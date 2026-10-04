package com.iamkaf.amber.doctor;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.iamkaf.amber.Constants;
import com.iamkaf.amber.api.platform.v1.Platform;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicBoolean;

/** Uploads latest.log to mclo.gs after the player confirms, scrubbed the way Prism Launcher does. */
final class DoctorLogUpload {
    // mclo.gs keeps the first 25,000 lines. Keeping both ends preserves startup details and the latest errors.
    private static final int MAX_LINES = 25_000;
    private static final int HEAD_LINES = 10_000;
    private static final int TAIL_LINES = 14_900;
    private static final AtomicBoolean UPLOADING = new AtomicBoolean();

    private DoctorLogUpload() {}

    static int offer() {
        ClientDoctorCommands.message(DoctorText.translatable("amber.doctor.upload.notice").append(" ")
                .append(ClientDoctorCommands.button("amber.doctor.upload.confirm",
                        DoctorText.runCommand("/amber doctor upload confirm")).withStyle(style -> style.withHoverEvent(
                                DoctorText.showText(DoctorText.translatable("amber.doctor.upload.scrubbed"))))));
        return 1;
    }

    static int start() {
        if (!UPLOADING.compareAndSet(false, true)) {
            ClientDoctorCommands.message(DoctorText.translatable("amber.doctor.upload.running"));
            return 0;
        }
        ClientDoctorCommands.message(DoctorText.translatable("amber.doctor.upload.started").withStyle(ChatFormatting.GRAY));
        var client = Minecraft.getInstance();
        Map<String, String> secrets = secrets(client);
        var log = Platform.getLogsFolder().resolve("latest.log");
        CompletableFuture.supplyAsync(() -> {
                    try {
                        // A line being written while this reads may end mid-character, so decoding must not be strict.
                        return truncate(redact(new String(Files.readAllBytes(log), StandardCharsets.UTF_8), secrets));
                    } catch (IOException exception) {
                        throw new UncheckedIOException(exception);
                    }
                })
                .thenCompose(DoctorLogUpload::send)
                .whenComplete((url, error) -> client.execute(() -> {
                    UPLOADING.set(false);
                    if (error == null) {
                        client.keyboardHandler.setClipboard(url.toString());
                        ClientDoctorCommands.message(DoctorText.translatable("amber.doctor.upload.done",
                                DoctorText.literal(url.toString()).withStyle(style -> style.withColor(ChatFormatting.AQUA)
                                        .withUnderlined(true).withClickEvent(DoctorText.openUrl(url)))));
                    } else {
                        Throwable cause = error instanceof CompletionException && error.getCause() != null ? error.getCause() : error;
                        Constants.LOG.warn("Could not upload {}", log, cause);
                        ClientDoctorCommands.message(DoctorText.translatable("amber.doctor.upload.failed",
                                Objects.requireNonNullElse(cause.getMessage(), cause.getClass().getSimpleName())).withStyle(ChatFormatting.RED));
                    }
                }));
        return 1;
    }

    /** Values replaced before upload, read on the client thread. */
    private static Map<String, String> secrets(Minecraft client) {
        var secrets = new LinkedHashMap<String, String>();
        String token = client.getUser().getAccessToken();
        // Offline and development sessions use short placeholder tokens.
        if (token.length() >= 16) secrets.put(token, "<access token>");
        String home = System.getProperty("user.home", "");
        if (home.length() > 1) {
            secrets.put(home, "~");
            secrets.put(home.replace('\\', '/'), "~");
            secrets.put(home.replace('/', '\\'), "~");
        }
        return secrets;
    }

    private static String redact(String log, Map<String, String> secrets) {
        for (var secret : secrets.entrySet()) log = log.replace(secret.getKey(), secret.getValue());
        return log;
    }

    private static String truncate(String log) {
        String[] lines = log.split("\n", -1);
        if (lines.length <= MAX_LINES) return log;
        return String.join("\n", Arrays.copyOfRange(lines, 0, HEAD_LINES))
                + "\n\n--- Amber trimmed " + (lines.length - HEAD_LINES - TAIL_LINES) + " lines to fit mclo.gs ---\n\n"
                + String.join("\n", Arrays.copyOfRange(lines, lines.length - TAIL_LINES, lines.length));
    }

    private static CompletableFuture<URI> send(String log) {
        // Tests point this at a local server.
        String api = System.getProperty("amber.doctor.mclogs", "https://api.mclo.gs");
        var request = HttpRequest.newBuilder(URI.create(api + "/1/log"))
                .timeout(Duration.ofSeconds(60))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("User-Agent", "Amber/" + DoctorReports.amberInfo().version())
                .POST(HttpRequest.BodyPublishers.ofString("content=" + URLEncoder.encode(log, StandardCharsets.UTF_8)))
                .build();
        return HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build()
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    JsonObject body;
                    try {
                        //? if >=1.18
                        body = JsonParser.parseString(response.body()).getAsJsonObject();
                        //? if <1.18
                        /*body = new JsonParser().parse(response.body()).getAsJsonObject();*/
                    } catch (RuntimeException exception) {
                        throw new IllegalStateException("mclo.gs answered " + response.statusCode(), exception);
                    }
                    if (!body.has("success") || !body.get("success").getAsBoolean()) {
                        throw new IllegalStateException(body.has("error") ? body.get("error").getAsString()
                                : "mclo.gs answered " + response.statusCode());
                    }
                    return URI.create(body.get("url").getAsString());
                });
    }
}
