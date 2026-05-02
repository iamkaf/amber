package com.iamkaf.amber.util.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.stream.Collectors;

public final class ClientCompat {
    private ClientCompat() {}

    public static Minecraft minecraft() {
        return Minecraft.getInstance();
    }

    public static boolean shouldRenderHud() {
        Minecraft minecraft = minecraft();
        return minecraft != null
                && !minecraft.options.renderDebug
                && !minecraft.options.hideGui
                && minecraft.level != null
                && minecraft.player != null;
    }

    public static boolean isEmpty(ItemStack stack) {
        return stack.isEmpty();
    }

    public static void renderText(PoseStack context, Font font, Component message, int x, int y, int color) {
        font.draw(message.getString(), x, y, color);
    }

    public static void renderTooltip(PoseStack guiGraphics, ItemStack stack, int x, int y) {
        Minecraft minecraft = minecraft();
        Screen screen = minecraft == null ? null : minecraft.screen;
        if (screen != null) {
            List<String> lines = stack.getTooltipLines(minecraft.player, minecraft.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL)
                    .stream()
                    .map(Component::getString)
                    .collect(Collectors.toList());
            screen.renderTooltip(lines, x, y);
        }
    }
}
