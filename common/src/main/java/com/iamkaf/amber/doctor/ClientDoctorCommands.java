package com.iamkaf.amber.doctor;

import com.iamkaf.amber.Constants;
import com.iamkaf.amber.api.doctor.v1.ClientDoctor;
import com.iamkaf.amber.api.platform.v1.Platform;
import com.iamkaf.amber.platform.Services;
import com.iamkaf.amber.platform.services.IClientDoctorCommands;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

/** Local callbacks do not assume the loader's command source type. */
public final class ClientDoctorCommands {
    private static final URI DISCORD = URI.create("https://kaf.sh/discord");
    //? if <1.20.5
    /*private static boolean forwarding;*/

    private ClientDoctorCommands() {}

    public static void initialize() {
        ClientDoctor.register(DoctorReports.amberInfo(), (context, section) -> DoctorReports.amber(section));
        Services.load(IClientDoctorCommands.class).register();
    }

    public static <S> LiteralArgumentBuilder<S> root() {
        return LiteralArgumentBuilder.<S>literal("amber")
                //? if <1.20.5
                /*.requires(source -> !forwarding)*/
                .then(LiteralArgumentBuilder.<S>literal("doctor")
                        .executes(context -> local())
                        .then(LiteralArgumentBuilder.<S>literal("upload")
                                .executes(context -> DoctorLogUpload.offer())
                                .then(LiteralArgumentBuilder.<S>literal("confirm")
                                        .executes(context -> DoctorLogUpload.start())))
                        .then(LiteralArgumentBuilder.<S>literal("server")
                                .executes(context -> server(""))
                                .then(RequiredArgumentBuilder.<S, String>argument("player", StringArgumentType.word())
                                        .executes(context -> server(StringArgumentType.getString(context, "player"))))));
    }

    private static int local() {
        var client = Minecraft.getInstance();
        DoctorReports.render(ClientDoctor.inspect(new ClientDoctor.Context(client)),
                ClientDoctorCommands::message);
        message(actions());
        message(help());
        return 1;
    }

    private static Component actions() {
        var folders = button("amber.doctor.open_game_folder", DoctorText.openFile(Platform.getGameFolder()))
                .append(" ")
                .append(button("amber.doctor.open_logs_folder", DoctorText.openFile(Platform.getLogsFolder())));
        Path crashReports = Platform.getGameFolder().resolve("crash-reports");
        if (hasFiles(crashReports)) {
            folders.append(" ").append(button("amber.doctor.open_crash_reports", DoctorText.openFile(crashReports)));
        }
        return folders;
    }

    private static Component help() {
        return DoctorText.translatable("amber.doctor.need_help").withStyle(ChatFormatting.GRAY)
                .append(" ")
                .append(button("amber.doctor.upload_log", DoctorText.runCommand("/amber doctor upload")))
                .append(" ")
                .append(button("amber.doctor.discord", DoctorText.openUrl(DISCORD)));
    }

    private static boolean hasFiles(Path folder) {
        if (!Files.isDirectory(folder)) return false;
        try (var files = Files.list(folder)) {
            return files.findAny().isPresent();
        } catch (IOException exception) {
            Constants.LOG.warn("Could not list {} for the Doctor report", folder, exception);
            return false;
        }
    }

    static MutableComponent button(String key, ClickEvent click) {
        return ComponentUtils.wrapInSquareBrackets(DoctorText.translatable(key))
                .withStyle(style -> style.withColor(ChatFormatting.AQUA).withClickEvent(click));
    }

    static void message(Component text) {
        var player = Minecraft.getInstance().player;
        if (player != null) com.iamkaf.amber.api.functions.v1.PlayerFunctions.sendMessage(player, text);
    }

    private static int server(String player) {
        var client = Minecraft.getInstance();
        var connection = client.getConnection();
        if (connection == null || com.iamkaf.amber.networking.v1.AmberNetworking.CHANNEL.serverAvailability()
                != com.iamkaf.amber.api.networking.v1.PeerAvailability.SUPPORTED) {
            message(DoctorText.translatable("amber.doctor.server_unavailable"));
            return 0;
        }
        String command = "amber doctor server" + (player.isEmpty() ? "" : " " + player);
        //? if >=1.20.5 {
        // The ordinary sendCommand method re-enters loader client command routing.
        connection.send(new net.minecraft.network.protocol.game.ServerboundChatCommandPacket(command));
        //?} else {
        /*// Older command packets carry chat acknowledgements that only vanilla's own sender can fill in.
        // Hiding the client root while it runs makes every loader pass the command on to the server.
        forwarding = true;
        try {
            //? if >=1.19.3
            connection.sendUnsignedCommand(command);
            //? if >=1.19.1 && <1.19.3
            //client.player.commandUnsigned(command);
            //? if >=1.19 && <1.19.1
            //client.player.command(command);
            //? if <1.19
            //client.player.chat("/" + command);
        } finally {
            forwarding = false;
        }
        *///?}
        return 1;
    }
}
