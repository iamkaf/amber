package com.iamkaf.amber.api.registry.v1.creativetabs;

import com.iamkaf.amber.api.event.v1.events.common.CreativeModeTabEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.ItemLike;

import java.util.function.Supplier;

/**
 * Helper class for easily adding items to creative mode tabs.
 * <p>
 * This provides simple methods to add items to existing tabs (vanilla or from other mods)
 * without needing to directly work with the event system.
 * <p>
 * Example usage:
 * <pre>{@code
 * // Add to a vanilla tab
 * CreativeTabHelper.addItem(CreativeModeTabs.INGREDIENTS, MyItems.SPECIAL_INGREDIENT);
 * 
 * // Add to another mod's tab
 * ResourceKey<CreativeModeTab> otherModTab = 
 *     ResourceKey.create(Registries.CREATIVE_MODE_TAB, 
 *         Identifier.fromNamespaceAndPath("othermod", "tab"));
 * CreativeTabHelper.addItem(otherModTab, MyItems.COMPAT_ITEM);
 * 
 * // Add multiple items
 * CreativeTabHelper.addItems(CreativeModeTabs.FUNCTIONAL_BLOCKS, 
 *     MyBlocks.CUSTOM_BLOCK, MyBlocks.OTHER_BLOCK);
 * }</pre>
 */
public final class CreativeTabHelper {
    private CreativeTabHelper() {}

    /**
     * Adds an item to an existing creative mode tab.
     * <p>
     * This works for vanilla tabs and tabs from other mods.
     * 
     * @param tabKey The resource key of the tab
     * @param item The item to add
     */
    public static void addItem(ResourceKey<CreativeModeTab> tabKey, Supplier<ItemLike> item) {
        CreativeModeTabEvents.MODIFY_ENTRIES.register((key, output) -> {
            if (key.equals(tabKey)) {
                output.accept(item.get());
            }
        });
    }

    /**
     * Adds an item to an existing creative mode tab.
     * <p>
     * This works for vanilla tabs and tabs from other mods.
     * 
     * @param tabKey The resource key of the tab
     * @param item The item to add
     */
    public static void addItem(ResourceKey<CreativeModeTab> tabKey, ItemLike item) {
        addItem(tabKey, () -> item);
    }


    /**
     * Adds multiple items to an existing creative mode tab.
     * <p>
     * This works for vanilla tabs and tabs from other mods.
     * 
     * @param tabKey The resource key of the tab
     * @param items The items to add
     */
    public static void addItems(ResourceKey<CreativeModeTab> tabKey, Supplier<ItemLike>... items) {
        CreativeModeTabEvents.MODIFY_ENTRIES.register((key, output) -> {
            if (key.equals(tabKey)) {
                for (Supplier<ItemLike> item : items) {
                    output.accept(item.get());
                }
            }
        });
    }

    /**
     * Adds multiple items to an existing creative mode tab.
     * <p>
     * This works for vanilla tabs and tabs from other mods.
     * 
     * @param tabKey The resource key of the tab
     * @param items The items to add
     */
    public static void addItems(ResourceKey<CreativeModeTab> tabKey, ItemLike... items) {
        CreativeModeTabEvents.MODIFY_ENTRIES.register((key, output) -> {
            if (key.equals(tabKey)) {
                for (ItemLike item : items) {
                    output.accept(item);
                }
            }
        });
    }


    /**
     * Adds items to a tab when it's built.
     * <p>
     * This is useful for adding items to your own tabs during registration.
     * The items will be added when the tab's contents are built.
     * 
     * @param tabId The tab ID to add items to
     * @param items The items to add
     */
    public static void addItemsToTab(Identifier tabId, Supplier<ItemLike>... items) {
        ResourceKey<CreativeModeTab> tabKey = ResourceKey.create(Registries.CREATIVE_MODE_TAB, tabId);
        CreativeModeTabEvents.MODIFY_ENTRIES.register((key, output) -> {
            if (key.equals(tabKey)) {
                for (Supplier<ItemLike> item : items) {
                    output.accept(item.get());
                }
            }
        });
    }

    /**
     * Adds items to a tab when it's built.
     * <p>
     * This is useful for adding items to your own tabs during registration.
     * The items will be added when the tab's contents are built.
     * 
     * @param tabId The tab ID to add items to
     * @param items The items to add
     */
    public static void addItemsToTab(Identifier tabId, ItemLike... items) {
        ResourceKey<CreativeModeTab> tabKey = ResourceKey.create(Registries.CREATIVE_MODE_TAB, tabId);
        CreativeModeTabEvents.MODIFY_ENTRIES.register((key, output) -> {
            if (key.equals(tabKey)) {
                for (ItemLike item : items) {
                    output.accept(item);
                }
            }
        });
    }
}