package com.iamkaf.mochila.network;

import com.iamkaf.amber.api.component.SimpleIntegerDataComponent;
import com.iamkaf.amber.api.networking.v1.Packet;
import com.iamkaf.amber.api.networking.v1.PacketContext;
import com.iamkaf.amber.api.player.FeedbackHelper;
import com.iamkaf.mochila.item.BackpackItem;
import com.iamkaf.mochila.item.backpack.QuickStash;
import com.iamkaf.mochila.registry.DataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class ChangeBackpackModePacket implements Packet<ChangeBackpackModePacket> {
    private final int button;

    public ChangeBackpackModePacket(int button) {
        this.button = button;
    }

    public int getButton() {
        return button;
    }

    public static void encode(ChangeBackpackModePacket packet, FriendlyByteBuf buffer) {
        buffer.writeVarInt(packet.button);
    }

    public static ChangeBackpackModePacket decode(FriendlyByteBuf buffer) {
        int button = buffer.readVarInt();
        return new ChangeBackpackModePacket(button);
    }

    public static void handle(ChangeBackpackModePacket packet, PacketContext context) {
        context.execute(() -> {
            ServerPlayer player = context.getServerPlayer();
            ItemStack stack = player.getMainHandItem();

            if (stack.getItem() instanceof BackpackItem) {
                int oldMode = stack.getOrDefault(
                                DataComponents.QUICKSTASH_MODE.get(),
                                SimpleIntegerDataComponent.empty()
                        )
                        .value();
                int newMode = oldMode == 0 ? 1 : 0;
                stack.set(DataComponents.QUICKSTASH_MODE.get(),
                        new SimpleIntegerDataComponent(newMode)
                );
                FeedbackHelper.actionBarMessage(player,
                        Component.translatable("mochila.keybind_mode_changed",
                                        "§e" + QuickStash.getMode(stack)
                                )
                                .withStyle(ChatFormatting.BLUE)
                );
            }
        });
    }
}
