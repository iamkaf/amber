package com.iamkaf.amber.event.fabric;

import com.iamkaf.amber.api.event.v1.events.common.LootEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;

public class AmberEventSetupImpl {
    public static void registerCommon() {
        LootTableEvents.MODIFY.register((resourceManager, lootDataManager, resourceLocation, provider, lootTableSource) -> {
            LootEvents.MODIFY.invoker().modify(resourceLocation, provider::withPool);
        });
    }

    public static void registerClient() {
        // No client events to register yet
    }
}
