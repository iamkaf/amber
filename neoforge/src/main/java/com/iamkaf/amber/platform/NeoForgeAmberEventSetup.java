package com.iamkaf.amber.platform;

import com.iamkaf.amber.api.event.v1.events.common.CommandEvents;
import com.iamkaf.amber.api.event.v1.events.common.LootEvents;
import com.iamkaf.amber.api.event.v1.events.common.PlayerEvents;
import com.iamkaf.amber.api.event.v1.events.common.client.ClientCommandEvents;
import com.iamkaf.amber.platform.services.IAmberEventSetup;
import net.minecraft.world.InteractionResult;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import static net.minecraft.world.InteractionResult.CONSUME;
import static net.minecraft.world.InteractionResult.SUCCESS;

public class NeoForgeAmberEventSetup implements IAmberEventSetup {
    @Override
    public void registerCommon() {
        NeoForge.EVENT_BUS.register(EventHandlerCommon.class);
    }

    @Override
    public void registerClient() {
        NeoForge.EVENT_BUS.register(EventHandlerClient.class);
    }

    @Override
    public void registerServer() {
        NeoForge.EVENT_BUS.register(EventHandlerServer.class);
    }

    static public class EventHandlerCommon {
        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void event(LootTableLoadEvent event) {
            LootEvents.MODIFY.invoker().modify(event.getName(), lootPool -> event.getTable().addPool(lootPool.build()));
        }

        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void event(PlayerInteractEvent.EntityInteract event) {
            InteractionResult result = PlayerEvents.ENTITY_INTERACT.invoker()
                    .interact(event.getEntity(), event.getLevel(), event.getHand(), event.getTarget());

            LogicalSide side = event.getSide();

            if (result.equals(InteractionResult.PASS)) {
                return;
            }

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
            }
        }

        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onCommandRegistration(RegisterCommandsEvent event) {
            CommandEvents.EVENT.invoker().register(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
        }
    }

    static public class EventHandlerClient {
        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onCommandRegistration(RegisterClientCommandsEvent event) {
            ClientCommandEvents.EVENT.invoker().register(event.getDispatcher(), event.getBuildContext());
        }
    }

    static public class EventHandlerServer {

    }
}
