package com.iamkaf.mochila.network;

import com.iamkaf.amber.api.networking.v1.Packet;
import com.iamkaf.amber.api.networking.v1.PacketContext;
import com.iamkaf.mochila.item.BackpackItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class OpenBackpackPacket implements Packet<OpenBackpackPacket> {
    private final int button;

    public OpenBackpackPacket(int button) {
        this.button = button;
    }

    public int getButton() {
        return button;
    }

    public static void encode(OpenBackpackPacket packet, FriendlyByteBuf buffer) {
        buffer.writeVarInt(packet.button);
    }

    public static OpenBackpackPacket decode(FriendlyByteBuf buffer) {
        int button = buffer.readVarInt();
        return new OpenBackpackPacket(button);
    }

    public static void handle(OpenBackpackPacket packet, PacketContext context) {
        context.execute(() -> {
            ServerPlayer player = (ServerPlayer) context.getPlayer();
            BackpackItem.onBackpackKeybindPressed(player);
        });
    }
}
