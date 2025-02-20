package com.iamkaf.amber.event.neoforge;


import com.iamkaf.amber.api.event.v1.events.common.EntityEvent;
import com.iamkaf.amber.api.event.v1.events.common.LootEvents;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public class EventHandlerCommon {
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(LivingIncomingDamageEvent event) {
        EntityEvent.AFTER_DAMAGE.invoker().afterDamage(
                event.getEntity(),
                event.getSource(),
                event.getAmount(),
                event.getAmount(),
                event.getContainer().getBlockedDamage() > 0
        );
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(LootTableLoadEvent event) {
        LootEvents.MODIFY.invoker().modify(event.getName(), lootPool -> event.getTable().addPool(lootPool.build()));
    }
}
