package com.iamkaf.amber.networking.forge;

import com.iamkaf.amber.api.networking.v1.Packet;
import com.iamkaf.amber.api.networking.v1.PacketHandler;

/**
 * Legacy placeholder for the pre-payload Forge networking band.
 * Client registration and sending are handled through the SimpleChannel directly.
 */
public class ForgeClientNetworking {
    public static <T extends Packet<T>> void registerClientReceiver(
            Object ignored,
            PacketHandler<T> handler
    ) {
    }

    public static <T extends Packet<T>> void sendToServer(ForgePacketWrapper<T> wrapper) {
    }
}
