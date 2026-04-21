package com.iamkaf.amber.api.registry.v1.creativetabs;

import com.iamkaf.amber.api.event.v1.events.common.CreativeModeTabEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ItemLike;

import java.util.function.Supplier;

public final class CreativeTabHelper {
    private CreativeTabHelper() {
    }

    public static void addItem(ResourceLocation tabId, Supplier<ItemLike> item) {
        CreativeModeTabEvents.MODIFY_ENTRIES.register((key, output) -> {
            if (key.equals(tabId)) {
                output.accept(item.get());
            }
        });
    }

    public static void addItem(ResourceLocation tabId, ItemLike item) {
        addItem(tabId, () -> item);
    }

    public static void addItems(ResourceLocation tabId, Supplier<ItemLike>... items) {
        CreativeModeTabEvents.MODIFY_ENTRIES.register((key, output) -> {
            if (key.equals(tabId)) {
                for (Supplier<ItemLike> item : items) {
                    output.accept(item.get());
                }
            }
        });
    }

    public static void addItems(ResourceLocation tabId, ItemLike... items) {
        CreativeModeTabEvents.MODIFY_ENTRIES.register((key, output) -> {
            if (key.equals(tabId)) {
                for (ItemLike item : items) {
                    output.accept(item);
                }
            }
        });
    }

    public static void addItemsToTab(ResourceLocation tabId, Supplier<ItemLike>... items) {
        CreativeModeTabEvents.MODIFY_ENTRIES.register((key, output) -> {
            if (key.equals(tabId)) {
                for (Supplier<ItemLike> item : items) {
                    output.accept(item.get());
                }
            }
        });
    }

    public static void addItemsToTab(ResourceLocation tabId, ItemLike... items) {
        CreativeModeTabEvents.MODIFY_ENTRIES.register((key, output) -> {
            if (key.equals(tabId)) {
                for (ItemLike item : items) {
                    output.accept(item);
                }
            }
        });
    }
}
