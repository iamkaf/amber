package com.iamkaf.amber.api.registry.v1.creativetabs;

import com.iamkaf.amber.api.event.v1.events.common.CreativeModeTabEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ItemLike;

import java.util.function.Supplier;

public final class CreativeTabHelper {
    private static final ResourceKey<net.minecraft.core.Registry<CreativeModeTab>> CREATIVE_MODE_TAB_REGISTRY =
            ResourceKey.createRegistryKey(new ResourceLocation("minecraft", "creative_mode_tab"));

    private CreativeTabHelper() {
    }

    public static void addItem(ResourceKey<CreativeModeTab> tabKey, Supplier<ItemLike> item) {
        CreativeModeTabEvents.MODIFY_ENTRIES.register((key, output) -> {
            if (key.equals(tabKey)) {
                output.accept(item.get());
            }
        });
    }

    public static void addItem(ResourceKey<CreativeModeTab> tabKey, ItemLike item) {
        addItem(tabKey, () -> item);
    }

    public static void addItems(ResourceKey<CreativeModeTab> tabKey, Supplier<ItemLike>... items) {
        CreativeModeTabEvents.MODIFY_ENTRIES.register((key, output) -> {
            if (key.equals(tabKey)) {
                for (Supplier<ItemLike> item : items) {
                    output.accept(item.get());
                }
            }
        });
    }

    public static void addItems(ResourceKey<CreativeModeTab> tabKey, ItemLike... items) {
        CreativeModeTabEvents.MODIFY_ENTRIES.register((key, output) -> {
            if (key.equals(tabKey)) {
                for (ItemLike item : items) {
                    output.accept(item);
                }
            }
        });
    }

    public static void addItemsToTab(ResourceLocation tabId, Supplier<ItemLike>... items) {
        ResourceKey<CreativeModeTab> tabKey = ResourceKey.create(CREATIVE_MODE_TAB_REGISTRY, tabId);
        CreativeModeTabEvents.MODIFY_ENTRIES.register((key, output) -> {
            if (key.equals(tabKey)) {
                for (Supplier<ItemLike> item : items) {
                    output.accept(item.get());
                }
            }
        });
    }

    public static void addItemsToTab(ResourceLocation tabId, ItemLike... items) {
        ResourceKey<CreativeModeTab> tabKey = ResourceKey.create(CREATIVE_MODE_TAB_REGISTRY, tabId);
        CreativeModeTabEvents.MODIFY_ENTRIES.register((key, output) -> {
            if (key.equals(tabKey)) {
                for (ItemLike item : items) {
                    output.accept(item);
                }
            }
        });
    }
}
