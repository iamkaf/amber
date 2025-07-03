package com.iamkaf.amber.platform;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.Constants;
import com.iamkaf.amber.api.event.v1.events.common.CommandEvents;
import com.iamkaf.amber.api.event.v1.events.common.LootEvents;
import com.iamkaf.amber.api.event.v1.events.common.PlayerEvents;
import com.iamkaf.amber.api.event.v1.events.common.client.ClientCommandEvents;
import com.iamkaf.amber.api.event.v1.events.common.client.ClientTickEvents;
import com.iamkaf.amber.api.keymapping.KeybindHelper;
import com.iamkaf.amber.platform.services.IAmberEventSetup;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionResult;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.listener.Priority;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

import static net.minecraft.world.InteractionResult.CONSUME;
import static net.minecraft.world.InteractionResult.SUCCESS;

public class ForgeAmberEventSetup implements IAmberEventSetup {
    @Override
    public void registerCommon() {
        LootTableLoadEvent.BUS.addListener(EventHandlerCommon::onLootTableEvent);
        PlayerInteractEvent.EntityInteract.BUS.addListener(EventHandlerCommon::onPlayerEntityInteract);
        RegisterCommandsEvent.BUS.addListener(EventHandlerCommon::onCommandRegistration);
    }

    @Override
    public void registerClient() {
        RegisterClientCommandsEvent.BUS.addListener(EventHandlerClient::onCommandRegistration);
        // mod bus events
        RegisterKeyMappingsEvent.getBus((BusGroup) AmberMod.getEventBus(Constants.MOD_ID))
                .addListener(EventHandlerClient::onKeybindRegistration);
        TickEvent.ClientTickEvent.Pre.BUS.addListener(EventHandlerClient::onClientTickEventPre);
        TickEvent.ClientTickEvent.Post.BUS.addListener(EventHandlerClient::onClientTickEventPost);
    }

    @Override
    public void registerServer() {

    }

    static public class EventHandlerCommon {
        @SubscribeEvent(priority = Priority.HIGH)
        public static void onLootTableEvent(LootTableLoadEvent event) {
            LootEvents.MODIFY.invoker().modify(event.getName(), lootPool -> event.getTable().addPool(lootPool.build()));
        }

        @SubscribeEvent(priority = Priority.HIGH)
        public static boolean onPlayerEntityInteract(PlayerInteractEvent.EntityInteract event) {
            InteractionResult result = PlayerEvents.ENTITY_INTERACT.invoker()
                    .interact(event.getEntity(), event.getLevel(), event.getHand(), event.getTarget());

            LogicalSide side = event.getSide();

            if (result.equals(InteractionResult.PASS)) {
                return false;
            }

            // These checks make sure the event handling is equivalent to Fabric's.
            if (side.isClient()) {
                if (result == SUCCESS) {
                    event.setCancellationResult(SUCCESS);
                    return true;
                } else if (result == CONSUME) {
                    event.setCancellationResult(CONSUME);
                    return true;
                } else {
                    // If the result is FAIL or any other value, cancel the event
                    return true;
                }
            }
            return false;
        }

        @SubscribeEvent(priority = Priority.HIGH)
        public static void onCommandRegistration(RegisterCommandsEvent event) {
            CommandEvents.EVENT.invoker()
                    .register(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
        }
    }

    static public class EventHandlerClient {
        @SubscribeEvent(priority = Priority.HIGH)
        public static void onCommandRegistration(RegisterClientCommandsEvent event) {
            ClientCommandEvents.EVENT.invoker().register(event.getDispatcher(), event.getBuildContext());
        }

        @SubscribeEvent(priority = Priority.HIGH)
        public static void onKeybindRegistration(RegisterKeyMappingsEvent event) {
            for (var keyMapping : KeybindHelper.getKeybindings()) {
                event.register(keyMapping);
            }
            KeybindHelper.forgeEventAlreadyFired = true;
        }

        public static void onClientTickEventPre(TickEvent.ClientTickEvent.Pre pre) {
            ClientTickEvents.START_CLIENT_TICK.invoker().onStartTick();
        }

        public static void onClientTickEventPost(TickEvent.ClientTickEvent.Post post) {
            ClientTickEvents.END_CLIENT_TICK.invoker().onEndTick();
        }
    }

    static public class EventHandlerServer {

    }
}
