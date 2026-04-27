package com.iamkaf.amber.api.functions.v1;

import com.iamkaf.amber.Constants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
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

    public static final int WHITE = 0xFFFFFFFF; // Default white color for text rendering
    public static final int BLACK = 0x000000FF; // Default black color for text rendering
    public static final int TRANSPARENT = 0x00000000; // Transparent color for text rendering
    public static final int PACKED_LIGHT = 15728880;

    private ClientFunctions() {
        // Utility class - prevent instantiation
    }

    // ==================== HUD RENDERING OPERATIONS ====================

    /**
     * Checks if HUDs should be rendered based on the current game state.
     * Takes into account whether the debug screen is shown, GUI is hidden, and the player is null.
     *
     * @return true if the HUD should be rendered, false otherwise.
     */
    public static boolean shouldRenderHud() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) {
            return false;
        }
        try {
            return !(mc.getDebugOverlay().showDebugScreen() || mc.options.hideGui || mc.level == null || mc.player == null);
        } catch (Exception e) {
            // Handle any exceptions that may occur
            Constants.LOG.error("An error occurred while checking if the HUD should be rendered: {}", e.getMessage());
            return false; // If an error occurs, do not render the HUD
        }
    }

    /**
     * Renders text at the specified coordinates with the given color.
     *
     * @param context The graphics context for rendering the GUI.
     * @param font    The font renderer to use for rendering text.
     * @param message The text component to render.
     * @param x       the x-coordinate where to start writing.
     * @param y       the y-coordinate where to start writing.
     * @param color   the color to use for rendering the text (specify an alpha value, or it will be invisible).
     */
    public static void renderText(GuiGraphics context, Font font, Component message, int x, int y, int color) {
        context.drawString(font, message, x, y, color);
    }

    /**
     * Renders a tooltip for the given item stack at the specified coordinates.
     *
     * @param guiGraphics The graphics context for rendering the GUI.
     * @param stack       The item stack for which to render the tooltip.
     * @param x           The x-coordinate for rendering the tooltip.
     * @param y           The y-coordinate for rendering the tooltip.
     */
    public static void renderTooltip(GuiGraphics guiGraphics, ItemStack stack, int x, int y) {
        Minecraft mc = Minecraft.getInstance();

        if (mc == null) {
            return;
        }

        if (stack == null || stack.isEmpty()) {
            return;
        }

        // converts a list of components to a list of ClientTooltipComponents
        List<ClientTooltipComponent> tooltipComponents = Screen.getTooltipFromItem(mc, stack)
                .stream()
                .map(Component::getVisualOrderText)
                .map(ClientTooltipComponent::create)
                .toList();

        guiGraphics.renderTooltip(
                mc.font,
                tooltipComponents,
                x,
                y,
                DefaultTooltipPositioner.INSTANCE,
                stack.get(DataComponents.TOOLTIP_STYLE)
        );
    }

    // ==================== SMART TOOLTIP OPERATIONS ====================

    /**
     * A utility class for building smart tooltips that respond to player input.
     * This class allows adding tooltip components conditionally based on player
     * key presses or modifier keys.
     */
    public static final class SmartTooltip {
        // List of tooltip components to display.
        private final List<Component> tooltipComponents = new ArrayList<>();

        /**
         * Constructs a SmartTooltip.
         */
        public SmartTooltip() {
        }

        /**
         * Adds a component to the tooltip unconditionally.
         *
         * @param component The component to add.
         * @return The current SmartTooltip instance for method chaining.
         */
        public SmartTooltip add(Component component) {
            tooltipComponents.add(component);
            return this;
        }

        /**
         * Adds a component to the tooltip if the player is holding the Shift key.
         *
         * @param component The component to add if Shift is held down.
         * @return The current SmartTooltip instance for method chaining.
         */
        public SmartTooltip shift(Component component) {
            if (hasShiftDown()) {
                tooltipComponents.add(component);
            }
            return this;
        }

        /**
         * Adds a component to the tooltip if a specific keybind is pressed.
         *
         * @param keybind   The key mapping to check.
         * @param component The component to add if the keybind is pressed.
         * @return The current SmartTooltip instance for method chaining.
         */
        public SmartTooltip keybind(KeyMapping keybind, Component component) {
            if (keybind.isDown()) {
                tooltipComponents.add(component);
            }
            return this;
        }

        /**
         * Adds a component to the tooltip if both the Shift key and a specific keybind are pressed.
         *
         * @param keybind   The key mapping to check.
         * @param component The component to add if both the Shift key and the keybind are pressed.
         * @return The current SmartTooltip instance for method chaining.
         */
        public SmartTooltip shiftKeybind(KeyMapping keybind, Component component) {
            if (hasShiftDown() && keybind.isDown()) {
                tooltipComponents.add(component);
            }
            return this;
        }

        /**
         * Appends all accumulated components in this SmartTooltip to an external tooltip list.
         *
         * @param tooltipAdder A consumer that accepts a Component and adds it to the tooltip list.
         */
        public void into(Consumer<Component> tooltipAdder) {
            for (Component component : tooltipComponents) {
                tooltipAdder.accept(component);
            }
        }

        /**
         * Helper method to check if Shift key is held down.
         *
         * @return true if the Shift key is held down.
         */
        private static boolean hasShiftDown() {
            return Minecraft.getInstance().hasShiftDown();
        }
    }

    // ==================== TEXT WRITER ====================

    /**
     * Helper class for writing text to the screen with a cursor position.
     * Maintains an internal cursor and supports writing at specific positions or sequential lines.
     *
     * <h2>Usage Examples</h2>
     * <pre><code>
     * // Example 1: Start at (20, 40) and write two lines
     * TextWriter writer = new TextWriter(graphics, font, 20, 40);
     * writer.writeLine(Component.literal("First line"), WHITE);
     * writer.writeLine(Component.literal("Second line"));
     *
     * // Example 2: Inline writing and repositioning
     * writer.write(Component.literal("Inline start"));
     * writer.write(Component.literal(" at x=100,y=100"), 100, 100);
     *
     * // Example 3: Using default color for lines
     * writer.writeLine(Component.literal("Default color line"));
     * </code></pre>
     */
    public static final class TextWriter {
        private final GuiGraphics context;
        private final Font font;

        private int cursorX = 0; // Current cursor X position
        private int cursorY = 0; // Current cursor Y position

        // TODO: Add line width and anchor to offer automatic wrapping and alignment.
        // See GuiGraphics.drawWordWrap()

        /**
         * Constructs a TextWriter with the specified graphics context and font.
         *
         * @param context the GuiGraphics context to draw text
         * @param font    the Font renderer
         */
        public TextWriter(GuiGraphics context, Font font) {
            this.context = context;
            this.font = font;
        }

        /**
         * Constructs a TextWriter with the specified graphics context, font, and initial cursor position.
         *
         * @param context the GuiGraphics context to draw text
         * @param font    the Font renderer
         * @param x       the initial X coordinate for the cursor
         * @param y       the initial Y coordinate for the cursor
         */
        public TextWriter(GuiGraphics context, Font font, int x, int y) {
            this(context, font);
            this.cursorX = x;
            this.cursorY = y;
        }

        /**
         * Writes the specified message at the given coordinates with the specified color.
         * Updates the internal cursor position to the provided coordinates.
         *
         * @param message the text component to render
         * @param x       the X coordinate where to start writing
         * @param y       the Y coordinate where to start writing
         * @param color   the color to use for rendering the text
         */
        public void write(Component message, int x, int y, int color) {
            cursorX = x;
            cursorY = y;
            renderText(context, font, message, cursorX, cursorY, color);
        }

        /**
         * Writes the specified message at the given coordinates using the default text color.
         * Updates the internal cursor position to the provided coordinates.
         *
         * @param message the text component to render
         * @param x       the X coordinate where to start writing
         * @param y       the Y coordinate where to start writing
         */
        public void write(Component message, int x, int y) {
            cursorX = x;
            cursorY = y;
            write(message, cursorX, cursorY, WHITE);
        }

        /**
         * Writes the specified message at the current cursor position using the default text color.
         *
         * @param message the text component to render
         */
        public void write(Component message) {
            write(message, cursorX, cursorY, WHITE);
        }

        /**
         * Writes the specified message at the current cursor position with the specified color,
         * then moves the cursor down by one line height.
         *
         * @param message the text component to render
         * @param color   the color to use for rendering the text
         */
        public void writeLine(Component message, int color) {
            renderText(context, font, message, cursorX, cursorY, color);
            cursorY += font.lineHeight; // Move cursor down by one line height
        }

        /**
         * Writes the specified message at the current cursor position using the default text color,
         * then moves the cursor down by one line height.
         *
         * @param message the text component to render
         */
        public void writeLine(Component message) {
            writeLine(message, WHITE);
        }
    }
}