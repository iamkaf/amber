package com.iamkaf.amber.networking.fabric;

import com.iamkaf.amber.api.networking.v1.Packet;
import com.iamkaf.amber.api.networking.v1.PacketDecoder;
import com.iamkaf.amber.api.networking.v1.PacketEncoder;
import com.iamkaf.amber.api.networking.v1.PacketHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class FabricClientNetworking {
    public static <T extends Packet<T>> void registerClientReceiver(
            ResourceLocation channelId,
            PacketDecoder<T> decoder,
            PacketHandler<T> handler
    ) {
        ClientPlayNetworking.registerGlobalReceiver(channelId, (client, networkHandler, buffer, responseSender) -> {
            T packet = decoder.decode(buffer);
            client.execute(() -> {
                FabricPacketContext packetContext = new FabricPacketContext(true, client.player);
                handler.handle(packet, packetContext);
            });
        });
    }

    public static <T extends Packet<T>> void sendToServer(
            ResourceLocation channelId,
            T packet,
            PacketEncoder<T> encoder
    ) {
        FriendlyByteBuf buffer = PacketByteBufs.create();
        encoder.encode(packet, buffer);
        ClientPlayNetworking.send(channelId, buffer);
    }
}
