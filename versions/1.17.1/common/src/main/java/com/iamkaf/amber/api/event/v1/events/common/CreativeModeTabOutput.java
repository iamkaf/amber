package com.iamkaf.amber.api.event.v1.events.common;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Collection;

public interface CreativeModeTabOutput {
    enum TabVisibility {
        PARENT_AND_SEARCH_TABS,
        PARENT_TAB_ONLY,
        SEARCH_TAB_ONLY
    }

    void accept(ItemStack stack, TabVisibility tabVisibility);

    default void accept(ItemStack stack) {
        this.accept(stack, TabVisibility.PARENT_AND_SEARCH_TABS);
    }

    default void accept(ItemLike item, TabVisibility tabVisibility) {
        this.accept(new ItemStack(item), tabVisibility);
    }

    default void accept(ItemLike item) {
        this.accept(new ItemStack(item), TabVisibility.PARENT_AND_SEARCH_TABS);
    }

    default void acceptAll(Collection<ItemStack> stacks, TabVisibility tabVisibility) {
        stacks.forEach(stack -> this.accept(stack, tabVisibility));
    }

    default void acceptAll(Collection<ItemStack> stacks) {
        this.acceptAll(stacks, TabVisibility.PARENT_AND_SEARCH_TABS);
    }
}
