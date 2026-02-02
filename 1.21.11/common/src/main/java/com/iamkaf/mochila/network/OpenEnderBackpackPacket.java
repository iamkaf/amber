package com.iamkaf.mochila.network;

import com.iamkaf.amber.api.networking.v1.Packet;
import com.iamkaf.amber.api.networking.v1.PacketContext;
import com.iamkaf.mochila.item.EnderBackpackItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class OpenEnderBackpackPacket implements Packet<OpenEnderBackpackPacket> {
    public OpenEnderBackpackPacket() {
    }

    public static void encode(OpenEnderBackpackPacket packet, FriendlyByteBuf buffer) {
    }

    public static OpenEnderBackpackPacket decode(FriendlyByteBuf buffer) {
        return new OpenEnderBackpackPacket();
    }

    public static void handle(OpenEnderBackpackPacket packet, PacketContext context) {
        context.execute(() -> {
            ServerPlayer player = (ServerPlayer) context.getPlayer();
            EnderBackpackItem.onEnderBackpackKeybindPressed(player);
        });
    }
}
