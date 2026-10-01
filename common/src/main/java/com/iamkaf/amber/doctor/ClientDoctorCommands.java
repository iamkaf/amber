//? if >=1.21.11 {
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
import java.nio.file.Files;

/** Local callbacks do not assume the loader's command source type. */
public final class ClientDoctorCommands {
    private ClientDoctorCommands() {}

    public static void initialize() {
        ClientDoctor.register(DoctorReports.amberInfo(), (context, section) -> DoctorReports.amber(section));
        Services.load(IClientDoctorCommands.class).register();
    }

    public static <S> LiteralArgumentBuilder<S> root() {
        return LiteralArgumentBuilder.<S>literal("amber")
                .then(LiteralArgumentBuilder.<S>literal("doctor")
                        .executes(context -> local())
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
        return 1;
    }

    private static Component actions() {
        var actions = button("amber.doctor.open_game_folder", new ClickEvent.OpenFile(Platform.getGameFolder()));
        var log = Platform.getLogsFolder().resolve("latest.log");
        try {
            // Copies the log as it was when the report ran.
            String content = Files.readString(log);
            actions.append(" ").append(button("amber.doctor.copy_log", new ClickEvent.CopyToClipboard(content)));
        } catch (IOException exception) {
            Constants.LOG.warn("Could not read {} for the Doctor report", log, exception);
        }
        return actions;
    }

    private static MutableComponent button(String key, ClickEvent click) {
        return ComponentUtils.wrapInSquareBrackets(DoctorText.translatable(key))
                .withStyle(style -> style.withColor(ChatFormatting.AQUA).withClickEvent(click));
    }

    private static void message(Component text) {
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
        // The ordinary sendCommand method re-enters loader client command routing.
        connection.send(new net.minecraft.network.protocol.game.ServerboundChatCommandPacket(command));
        return 1;
    }
}
//?}
