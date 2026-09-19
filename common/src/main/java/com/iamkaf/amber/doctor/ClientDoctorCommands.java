//? if >=1.21.11 {
package com.iamkaf.amber.doctor;

import com.iamkaf.amber.api.doctor.v1.ClientDoctor;
import com.iamkaf.amber.platform.Services;
import com.iamkaf.amber.platform.services.IClientDoctorCommands;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.client.Minecraft;

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
        return 1;
    }

    private static void message(net.minecraft.network.chat.Component text) {
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
