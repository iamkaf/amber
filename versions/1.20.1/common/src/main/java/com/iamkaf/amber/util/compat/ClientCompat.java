package com.iamkaf.amber.util.compat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

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

    public static void renderText(GuiGraphics context, Font font, Component message, int x, int y, int color) {
        context.drawString(font, message, x, y, color);
    }

    public static void renderTooltip(GuiGraphics guiGraphics, ItemStack stack, int x, int y) {
        Minecraft minecraft = minecraft();
        if (minecraft != null) {
            guiGraphics.renderTooltip(minecraft.font, stack, x, y);
        }
    }
}
