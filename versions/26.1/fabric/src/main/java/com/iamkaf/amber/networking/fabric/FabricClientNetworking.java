package com.iamkaf.amber.networking.fabric;

import com.iamkaf.amber.api.networking.v1.Packet;
import com.iamkaf.amber.api.networking.v1.PacketHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Client-only networking functionality for Fabric.
 * This class is separated to avoid loading client-only classes on the server.
 */
@Environment(EnvType.CLIENT)
public class FabricClientNetworking {
    
    public static <T extends Packet<T>> void registerClientReceiver(
            CustomPacketPayload.Type<FabricNetworkChannelImpl.FabricPacketWrapper<T>> payloadType,
            PacketHandler<T> handler
    ) {
        ClientPlayNetworking.registerGlobalReceiver(payloadType, (payload, context) -> {
            FabricPacketContext packetContext = new FabricPacketContext(true, context.player());
            handler.handle(payload.packet, packetContext);
        });
    }
    
    public static <T extends Packet<T>> void sendToServer(FabricNetworkChannelImpl.FabricPacketWrapper<T> wrapper) {
        ClientPlayNetworking.send(wrapper);
    }
}