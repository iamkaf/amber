package com.iamkaf.amber.event.neoforge;


import com.iamkaf.amber.api.event.v1.events.common.LootEvents;
import com.iamkaf.amber.api.event.v1.events.common.PlayerEvents;
import net.minecraft.world.InteractionResult;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.neoforge.event.entity.EntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import static net.minecraft.world.InteractionResult.CONSUME;
import static net.minecraft.world.InteractionResult.SUCCESS;

public class EventHandlerCommon {
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(LootTableLoadEvent event) {
        LootEvents.MODIFY.invoker().modify(event.getName(), lootPool -> event.getTable().addPool(lootPool.build()));
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(PlayerInteractEvent.EntityInteract event) {
        InteractionResult result = PlayerEvents.ENTITY_INTERACT.invoker().interact(event.getEntity(), event.getLevel(), event.getHand(), event.getTarget());

        var side = event.getSide();

        // These checks make sure the event handling is equivalent to Fabric's.
        if (side.isClient()) {
            if (result == SUCCESS) {
                event.setCancellationResult(SUCCESS);
                event.setCanceled(true);
            } else if (result == CONSUME) {
                event.setCancellationResult(CONSUME);
                event.setCanceled(true);
            } else {
                // If the result is FAIL or any other value, cancel the event
                event.setCanceled(true);
            }
        } else {
            if (result != InteractionResult.PASS) {
                event.setCanceled(true);
            }
        }
    }
}
