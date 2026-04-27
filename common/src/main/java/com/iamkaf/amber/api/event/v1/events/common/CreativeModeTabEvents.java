package com.iamkaf.amber.api.event.v1.events.common;

import com.iamkaf.amber.api.event.v1.Event;
import com.iamkaf.amber.api.event.v1.EventFactory;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ItemLike;

/**
 * Events related to creative mode tabs.
 * <p>
 * These events allow mods to modify existing creative mode tabs
 * and add items to them in a unified way across all loaders.
 */
public final class CreativeModeTabEvents {
    private CreativeModeTabEvents() {}

    /**
     * Event fired when items are being added to a creative mode tab.
     * <p>
     * This event allows mods to add their items to existing tabs (vanilla or from other mods).
     * It is fired for all tabs, including custom tabs registered through Amber's API.
     * <p>
     * The event is fired during the tab's content building phase, which happens
     * when the creative inventory is opened or needs to be refreshed.
     */
    public static final Event<ModifyEntries> MODIFY_ENTRIES = EventFactory.createArrayBacked(
        ModifyEntries.class, callbacks -> (tabKey, output) -> {
            for (ModifyEntries callback : callbacks) {
                callback.modifyEntries(tabKey, output);
            }
        }
    );

    /**
     * Functional interface for handling creative mode tab entry modification.
     */
    @FunctionalInterface
    public interface ModifyEntries {
        /**
         * Called when items are being added to a creative mode tab.
         * <p>
         * Use the provided output interface to add items to the tab. You can add
         * items conditionally based on the tab key, feature flags, or other factors.
         *
         * @param tabKey The resource key of the tab being modified
         * @param output Output interface to add items to the tab
         */
        void modifyEntries(ResourceKey<CreativeModeTab> tabKey, CreativeModeTabOutput output);
    }
}