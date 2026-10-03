package com.iamkaf.amber.compat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
//? if >=26.1
import net.minecraft.client.gui.GuiGraphicsExtractor;
//? if >=1.20 && <26.1
/*import net.minecraft.client.gui.GuiGraphics;*/
//? if <1.20 {
/*import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
*///?}
//? if >=1.21.6 {
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.core.component.DataComponents;
//?}
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

//? if >=1.21.6
import java.util.List;

public final class ClientCompat {
    private ClientCompat() {}

    public static Minecraft minecraft() {
        return Minecraft.getInstance();
    }

    public static boolean shouldRenderHud() {
        Minecraft minecraft = minecraft();
        return minecraft != null
                //? if >=1.20.2
                && !minecraft.getDebugOverlay().showDebugScreen()
                //? if <1.20.2
                /*&& !minecraft.options.renderDebug*/
                //? if >=26.2
                && !minecraft.gui.hud.isHidden()
                //? if <26.2
                /*&& !minecraft.options.hideGui*/
                && minecraft.level != null
                && minecraft.player != null;
    }

    public static boolean isEmpty(ItemStack stack) {
        return stack.isEmpty();
    }

    //? if >=26.1 {
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
                //? if >=26.3 {
                stack.get(DataComponents.TOOLTIP_STYLE),
                true
                //?} else {
                /*stack.get(DataComponents.TOOLTIP_STYLE)
                *///?}
        );
    }
    //?} else if >=1.21.6 {
    /*public static void renderText(GuiGraphics context, Font font, Component message, int x, int y, int color) {
        context.drawString(font, message, x, y, color);
    }

    public static void renderTooltip(GuiGraphics guiGraphics, ItemStack stack, int x, int y) {
        Minecraft minecraft = minecraft();
        if (minecraft == null) {
            return;
        }

        List<ClientTooltipComponent> tooltipComponents = Screen.getTooltipFromItem(minecraft, stack)
                .stream()
                .map(Component::getVisualOrderText)
                .map(ClientTooltipComponent::create)
                .toList();
        guiGraphics.renderTooltip(
                minecraft.font,
                tooltipComponents,
                x,
                y,
                DefaultTooltipPositioner.INSTANCE,
                stack.get(DataComponents.TOOLTIP_STYLE)
        );
    }
    *///?} else if >=1.20 {
    /*public static void renderText(GuiGraphics context, Font font, Component message, int x, int y, int color) {
        context.drawString(font, message, x, y, color);
    }

    public static void renderTooltip(GuiGraphics guiGraphics, ItemStack stack, int x, int y) {
        Minecraft minecraft = minecraft();
        if (minecraft != null) {
            guiGraphics.renderTooltip(minecraft.font, stack, x, y);
        }
    }
    *///?} else {
    /*public static void renderText(PoseStack context, Font font, Component message, int x, int y, int color) {
        font.draw(context, message, x, y, color);
    }

    public static void renderTooltip(PoseStack guiGraphics, ItemStack stack, int x, int y) {
        Minecraft minecraft = minecraft();
        if (minecraft != null) {
            Screen screen = tooltipScreen(minecraft);
            screen.renderComponentTooltip(guiGraphics, screen.getTooltipFromItem(stack), x, y);
        }
    }

    private static Screen hudTooltipScreen;

    // Tooltips need a Screen before GuiGraphics, so the HUD borrows a blank one sized to the window.
    private static Screen tooltipScreen(Minecraft minecraft) {
        if (minecraft.screen != null) {
            return minecraft.screen;
        }
        if (hudTooltipScreen == null) {
            hudTooltipScreen = new Screen(Component.nullToEmpty("")) {};
        }
        int width = minecraft.getWindow().getGuiScaledWidth();
        int height = minecraft.getWindow().getGuiScaledHeight();
        if (hudTooltipScreen.width != width || hudTooltipScreen.height != height) {
            hudTooltipScreen.init(minecraft, width, height);
        }
        return hudTooltipScreen;
    }
    *///?}
}
