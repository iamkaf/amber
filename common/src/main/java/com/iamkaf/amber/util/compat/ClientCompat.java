package com.iamkaf.amber.util.compat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class ClientCompat {
    private ClientCompat() {}

    public static Minecraft minecraft() {
        return Minecraft.getInstance();
    }

    public static boolean shouldRenderHud() {
        Minecraft minecraft = minecraft();
        return minecraft != null
                && !minecraft.getDebugOverlay().showDebugScreen()
                && !minecraft.options.hideGui
                && minecraft.level != null
                && minecraft.player != null;
    }

    public static boolean isEmpty(ItemStack stack) {
        return stack.isEmpty();
    }

    public static void renderText(GuiGraphicsExtractor context, Font font, Component message, int x, int y, int color) {
        context.text(font, message, x, y, color);
    }

    public static void renderTooltip(GuiGraphicsExtractor guiGraphics, ItemStack stack, int x, int y) {
        Minecraft minecraft = minecraft();
        if (minecraft == null) {
            return;
        }

        List<ClientTooltipComponent> tooltipComponents = Screen.getTooltipFromItem(minecraft, stack)
                .stream()
                .map(Component::getVisualOrderText)
                .map(ClientTooltipComponent::create)
                .toList();
        guiGraphics.tooltip(
                minecraft.font,
                tooltipComponents,
                x,
                y,
                DefaultTooltipPositioner.INSTANCE,
                stack.get(DataComponents.TOOLTIP_STYLE)
        );
    }
}
