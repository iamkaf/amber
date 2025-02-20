package com.iamkaf.amber.event.fabric;

import com.iamkaf.amber.api.event.v1.events.common.EntityEvent;
import com.iamkaf.amber.api.event.v1.events.common.LootEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;

public class AmberEventSetupImpl {
    public static void registerCommon() {
        ServerLivingEntityEvents.AFTER_DAMAGE.register((livingEntity, damageSource, v, v1, b) -> {
            EntityEvent.AFTER_DAMAGE.invoker().afterDamage(livingEntity, damageSource, v, v1, b);
        });

        LootTableEvents.MODIFY.register((resourceKey, builder, lootTableSource, provider) -> {
            LootEvents.MODIFY.invoker().modify(resourceKey.location(), builder::withPool);
        });
    }

    public static void registerClient() {
        // No client events to register yet
    }
}
