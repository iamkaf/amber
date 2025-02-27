package com.iamkaf.amber.event.forge;


import com.iamkaf.amber.api.event.v1.events.common.LootEvents;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventHandlerCommon {
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(LootTableLoadEvent event) {
        LootEvents.MODIFY.invoker().modify(event.getName(), lootPool -> event.getTable().addPool(lootPool.build()));
    }
}
