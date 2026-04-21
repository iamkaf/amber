package com.iamkaf.amber.api.item;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * A utility class for building smart tooltips that respond to player input.
 * This class allows adding tooltip components conditionally based on player
 * key presses or modifier keys.
 */
public class SmartTooltip {
    private final List<Component> tooltipComponents = new ArrayList<>();

    public SmartTooltip() {
    }

    public SmartTooltip add(Component component) {
        tooltipComponents.add(component);
        return this;
    }

    public SmartTooltip shift(Component component) {
        if (Screen.hasShiftDown()) {
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
        if (Screen.hasShiftDown() && keybind.isDown()) {
            tooltipComponents.add(component);
        }
        return this;
    }

    public void into(List<Component> tooltipComponents) {
        tooltipComponents.addAll(this.tooltipComponents);
    }
}
