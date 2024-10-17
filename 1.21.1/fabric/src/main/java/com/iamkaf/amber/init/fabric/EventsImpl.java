package com.iamkaf.amber.init.fabric;

import com.iamkaf.amber.api.item.BrewingHelper;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;

public class EventsImpl {
    public static void init() {
        registerPotionBrewingListeners();
    }

    private static void registerPotionBrewingListeners() {
        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
            var listeners = BrewingHelper.getListeners();
            for (var listener : listeners) {
                listener.accept(builder);
            }
        });
    }
}
