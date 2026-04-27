package com.iamkaf.amber.api.event.v1.events.common;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import java.util.Collection;

/**
 * A public interface that mirrors the functionality of CreativeModeTab.Output.
 * <p>
 * This interface exists because CreativeModeTab.Output became protected in Minecraft 26.1,
 * making it impossible to use directly in public APIs. This interface provides the same
 * functionality and is implemented by platform-specific adapters.
 *
 * @since 10.0.0
 */
public interface CreativeModeTabOutput {
    /**
     * Visibility options for items in creative mode tabs.
     */
    enum TabVisibility {
        /**
         * Item appears in both the parent tab and the search tab.
         */
        PARENT_AND_SEARCH_TABS,
        
        /**
         * Item appears only in the parent tab.
         */
        PARENT_TAB_ONLY,
        
        /**
         * Item appears only in the search tab.
         */
        SEARCH_TAB_ONLY
    }

    /**
     * Accepts an item stack with the specified tab visibility.
     *
     * @param stack The item stack to add
     * @param tabVisibility Where the item should be visible
     */
    void accept(ItemStack stack, TabVisibility tabVisibility);

    /**
     * Accepts an item stack, making it visible in both the parent and search tabs.
     *
     * @param stack The item stack to add
     */
    default void accept(ItemStack stack) {
        this.accept(stack, TabVisibility.PARENT_AND_SEARCH_TABS);
    }

    /**
     * Accepts an item with the specified tab visibility.
     *
     * @param item The item to add
     * @param tabVisibility Where the item should be visible
     */
    default void accept(ItemLike item, TabVisibility tabVisibility) {
        this.accept(new ItemStack(item), tabVisibility);
    }

    /**
     * Accepts an item, making it visible in both the parent and search tabs.
     *
     * @param item The item to add
     */
    default void accept(ItemLike item) {
        this.accept(new ItemStack(item), TabVisibility.PARENT_AND_SEARCH_TABS);
    }

    /**
     * Accepts all item stacks with the specified tab visibility.
     *
     * @param stacks The item stacks to add
     * @param tabVisibility Where the items should be visible
     */
    default void acceptAll(Collection<ItemStack> stacks, TabVisibility tabVisibility) {
        stacks.forEach(stack -> this.accept(stack, tabVisibility));
    }

    /**
     * Accepts all item stacks, making them visible in both the parent and search tabs.
     *
     * @param stacks The item stacks to add
     */
    default void acceptAll(Collection<ItemStack> stacks) {
        this.acceptAll(stacks, TabVisibility.PARENT_AND_SEARCH_TABS);
    }
}
