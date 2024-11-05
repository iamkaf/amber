package com.iamkaf.amber.api.item;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * A utility class for building smart tooltips that respond to player input.
 * This class allows adding tooltip components conditionally based on player
 * key presses or modifier keys.
 */
public class SmartTooltip {
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
        if (Screen.hasShiftDown()) {
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
        if (Screen.hasShiftDown() && keybind.isDown()) {
            tooltipComponents.add(component);
        }
        return this;
    }

    /**
     * Appends all accumulated components in this SmartTooltip to an external tooltip list.
     *
     * @param tooltipComponents The list to which the components are added.
     */
    public void into(List<Component> tooltipComponents) {
        tooltipComponents.addAll(this.tooltipComponents);
    }
}
