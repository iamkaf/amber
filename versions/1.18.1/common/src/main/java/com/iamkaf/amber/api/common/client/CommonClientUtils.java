package com.iamkaf.amber.api.common.client;

import com.iamkaf.amber.Constants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public class CommonClientUtils {
    public static final int WHITE = 0xFFFFFFFF;
    public static final int BLACK = 0x000000FF;
    public static final int TRANSPARENT = 0x00000000;
    public static final int PACKED_LIGHT = 15728880;

    public static boolean shouldRender() {
        Minecraft mc = Minecraft.getInstance();
        try {
            return !(mc.options.renderDebug || mc.options.hideGui || mc.level == null || mc.player == null);
        } catch (Exception e) {
            Constants.LOG.error("An error occurred while checking if the HUD should be rendered: {}", e.getMessage());
            return false;
        }
    }

    public static void text(PoseStack context, Font font, Component message, int x, int y, int color) {
        GuiComponent.drawString(context, font, message, x, y, color);
    }

    public static void renderTooltip(PoseStack poseStack, ItemStack stack, int x, int y) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.screen == null || stack == null || stack.isEmpty()) {
            return;
        }

        List<Component> tooltipComponents = mc.screen.getTooltipFromItem(stack);
        mc.screen.renderTooltip(poseStack, tooltipComponents, Optional.empty(), x, y);
    }

    public static final class TextWriter {
        private final PoseStack context;
        private final Font font;
        private int cursorX = 0;
        private int cursorY = 0;

        public TextWriter(PoseStack context, Font font) {
            this.context = context;
            this.font = font;
        }

        public TextWriter(PoseStack context, Font font, int x, int y) {
            this(context, font);
            this.cursorX = x;
            this.cursorY = y;
        }

        public void write(Component message, int x, int y, int color) {
            cursorX = x;
            cursorY = y;
            text(context, font, message, cursorX, cursorY, color);
        }

        public void write(Component message, int x, int y) {
            cursorX = x;
            cursorY = y;
            write(message, cursorX, cursorY, WHITE);
        }

        public void write(Component message) {
            write(message, cursorX, cursorY, WHITE);
        }

        public void writeLine(Component message, int color) {
            text(context, font, message, cursorX, cursorY, color);
            cursorY += font.lineHeight;
        }

        public void writeLine(Component message) {
            writeLine(message, WHITE);
        }
    }
}
