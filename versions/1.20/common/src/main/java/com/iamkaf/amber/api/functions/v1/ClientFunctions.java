package com.iamkaf.amber.api.functions.v1;

import com.iamkaf.amber.Constants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Consolidated utility class for client-side operations, including player feedback,
 * HUD rendering, tooltips, and text writing.
 * This class combines functionality from FeedbackHelper, CommonClientUtils, and SmartTooltip
 * that will be removed on Amber 10.
 *
 * @since 8.3.0
 */
public final class ClientFunctions {

    public static final int WHITE = 0xFFFFFFFF;
    public static final int BLACK = 0x000000FF;
    public static final int TRANSPARENT = 0x00000000;
    public static final int PACKED_LIGHT = 15728880;

    private ClientFunctions() {
    }

    public static boolean shouldRenderHud() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) {
            return false;
        }
        try {
            return !(mc.options.renderDebug || mc.options.hideGui || mc.level == null || mc.player == null);
        } catch (Exception e) {
            Constants.LOG.error("An error occurred while checking if the HUD should be rendered: {}", e.getMessage());
            return false;
        }
    }

    public static void renderText(GuiGraphics context, Font font, Component message, int x, int y, int color) {
        context.drawString(font, message, x, y, color);
    }

    public static void renderTooltip(GuiGraphics guiGraphics, ItemStack stack, int x, int y) {
        Minecraft mc = Minecraft.getInstance();

        if (mc == null || stack == null || stack.isEmpty()) {
            return;
        }

        List<Component> tooltipComponents = Screen.getTooltipFromItem(mc, stack);

        guiGraphics.renderTooltip(
                mc.font,
                tooltipComponents,
                Optional.<TooltipComponent>empty(),
                x,
                y
        );
    }

    public static final class SmartTooltip {
        private final List<Component> tooltipComponents = new ArrayList<>();

        public SmartTooltip add(Component component) {
            tooltipComponents.add(component);
            return this;
        }

        public SmartTooltip shift(Component component) {
            if (hasShiftDown()) {
                tooltipComponents.add(component);
            }
            return this;
        }

        public SmartTooltip keybind(KeyMapping keybind, Component component) {
            if (keybind.isDown()) {
                tooltipComponents.add(component);
            }
            return this;
        }

        public SmartTooltip shiftKeybind(KeyMapping keybind, Component component) {
            if (hasShiftDown() && keybind.isDown()) {
                tooltipComponents.add(component);
            }
            return this;
        }

        public void into(Consumer<Component> tooltipAdder) {
            for (Component component : tooltipComponents) {
                tooltipAdder.accept(component);
            }
        }

        private static boolean hasShiftDown() {
            return Screen.hasShiftDown();
        }
    }

    public static final class TextWriter {
        private final GuiGraphics context;
        private final Font font;
        private int cursorX = 0;
        private int cursorY = 0;

        public TextWriter(GuiGraphics context, Font font) {
            this.context = context;
            this.font = font;
        }

        public TextWriter(GuiGraphics context, Font font, int x, int y) {
            this(context, font);
            this.cursorX = x;
            this.cursorY = y;
        }

        public void write(Component message, int x, int y, int color) {
            cursorX = x;
            cursorY = y;
            renderText(context, font, message, cursorX, cursorY, color);
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
            renderText(context, font, message, cursorX, cursorY, color);
            cursorY += font.lineHeight;
        }

        public void writeLine(Component message) {
            writeLine(message, WHITE);
        }
    }
}
