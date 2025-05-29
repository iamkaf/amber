package com.iamkaf.amber.event.fabric;

import com.iamkaf.amber.api.event.v1.events.common.LootEvents;
import com.iamkaf.amber.api.event.v1.events.common.PlayerEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;

public class AmberEventSetupImpl {
    public static void registerCommon() {
        LootTableEvents.MODIFY.register((resourceKey, builder, lootTableSource, provider) -> {
            LootEvents.MODIFY.invoker().modify(resourceKey.location(), builder::withPool);
        });
        UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            return PlayerEvents.ENTITY_INTERACT.invoker().interact(player, level, hand, entity);
        });
    }

    public static void registerClient() {
        // No client events to register yet
    }
}
