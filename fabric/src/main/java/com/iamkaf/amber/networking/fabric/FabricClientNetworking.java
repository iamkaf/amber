package com.iamkaf.amber.networking.fabric;

import com.iamkaf.amber.api.networking.v1.Packet;
import com.iamkaf.amber.api.networking.v1.PacketHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
//? if >=1.20.5 {
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else {
/*import com.iamkaf.amber.api.networking.v1.PacketDecoder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
*///?}
//? if <1.20.2 {
/*import net.fabricmc.fabric.api.client.networking.v1.C2SPlayChannelEvents;
import net.minecraft.client.multiplayer.ClientPacketListener;
*///?}

/**
 * Client-only networking functionality for Fabric.
 * This class is separated to avoid loading client-only classes on the server.
 */
@Environment(EnvType.CLIENT)
public class FabricClientNetworking {
    //? if <1.20.2 {
    /*// Before 1.20.2 the server's play channel list arrives after join; until then support is unknown.
    private static volatile ClientPacketListener registeredListener;

    static void trackChannelRegistration() {
        C2SPlayChannelEvents.REGISTER.register((listener, sender, client, channels) -> registeredListener = listener);
        net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.DISCONNECT.register(
                (listener, client) -> registeredListener = null);
    }
    *///?}

    //? if >=1.20.5 {
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
    //?} else {
    /*static <T extends Packet<T>> void registerClientReceiver(Identifier packetId, PacketDecoder<T> decoder,
                                                            PacketHandler<T> handler) {
        // Raw channel handlers run on the network thread: decode there, then hop to the client thread.
        ClientPlayNetworking.registerGlobalReceiver(packetId, (client, listener, buffer, responseSender) -> {
            T packet = decoder.decode(buffer);
            client.execute(() -> {
                // Match 1.20.5+, where vanilla drops packets queued after the connection closed.
                if (listener.getConnection().isConnected()) {
                    handler.handle(packet, new FabricPacketContext(true, client.player));
                }
            });
        });
    }

    static void sendToServer(Identifier packetId, FriendlyByteBuf buffer) {
        ClientPlayNetworking.send(packetId, buffer);
    }
    *///?}

    static boolean ready() {
        var client = net.minecraft.client.Minecraft.getInstance();
        var connection = client.getConnection();
        if (connection == null || client.player == null) return false;
        //? if <1.20.2 {
        /*return connection == registeredListener;
        *///?} else {
        return true;
        //?}
    }

    static boolean canSend(net.minecraft.resources.Identifier id) {
        return ClientPlayNetworking.canSend(id);
    }

}
