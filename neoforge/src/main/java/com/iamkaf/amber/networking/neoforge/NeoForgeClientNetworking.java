package com.iamkaf.amber.networking.neoforge;

import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/** Client classes are loaded only after a physical-side check. */
final class NeoForgeClientNetworking {
    private NeoForgeClientNetworking() {}

    static boolean ready() {
        var client = Minecraft.getInstance();
        return client.getConnection() != null && client.player != null;
    }

    static boolean hasChannel(Identifier id) {
        var connection = Minecraft.getInstance().getConnection();
        return connection != null && connection.hasChannel(id);
    }

    static void send(CustomPacketPayload payload) {
        var connection = Minecraft.getInstance().getConnection();
        if (connection == null) throw new IllegalStateException("No server connection");
        connection.send(payload);
    }
}
