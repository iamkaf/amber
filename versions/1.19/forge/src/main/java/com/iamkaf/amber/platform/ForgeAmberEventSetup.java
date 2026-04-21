package com.iamkaf.amber.platform;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.Constants;
import com.iamkaf.amber.api.event.v1.events.common.CommandEvents;
import com.iamkaf.amber.api.event.v1.events.common.LootEvents;
import com.iamkaf.amber.api.event.v1.events.common.PlayerEvents;
import com.iamkaf.amber.api.event.v1.events.common.client.ClientCommandEvents;
import com.iamkaf.amber.api.keymapping.KeybindHelper;
import com.iamkaf.amber.platform.services.IAmberEventSetup;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionResult;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.LogicalSide;

import static net.minecraft.world.InteractionResult.CONSUME;
import static net.minecraft.world.InteractionResult.SUCCESS;

public class ForgeAmberEventSetup implements IAmberEventSetup {
    @Override
    public void registerCommon() {
        MinecraftForge.EVENT_BUS.addListener(EventHandlerCommon::onLootTableEvent);
        MinecraftForge.EVENT_BUS.addListener(EventHandlerCommon::onPlayerEntityInteract);
        MinecraftForge.EVENT_BUS.addListener(EventHandlerCommon::onCommandRegistration);
    }

    @Override
    public void registerClient() {
        MinecraftForge.EVENT_BUS.addListener(EventHandlerClient::onCommandRegistration);
        // mod bus events
        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventHandlerClient::onKeybindRegistration);
    }

    @Override
    public void registerServer() {

    }

    static public class EventHandlerCommon {
        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onLootTableEvent(LootTableLoadEvent event) {
            LootEvents.MODIFY.invoker().modify(event.getName(), lootPool -> event.getTable().addPool(lootPool.build()));
        }

        @SubscribeEvent(priority = EventPriority.HIGH)
        public static boolean onPlayerEntityInteract(PlayerInteractEvent.EntityInteract event) {
            InteractionResult result = PlayerEvents.ENTITY_INTERACT.invoker()
                    .interact(event.getPlayer(), event.getPlayer().level, event.getHand(), event.getTarget());

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

        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onCommandRegistration(RegisterCommandsEvent event) {
            CommandEvents.EVENT.invoker()
                    .register(event.getDispatcher(), null, event.getEnvironment());
        }
    }

    static public class EventHandlerClient {
        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onCommandRegistration(RegisterClientCommandsEvent event) {
            ClientCommandEvents.EVENT.invoker().register(event.getDispatcher(), null);
        }

        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onKeybindRegistration(FMLClientSetupEvent event) {
            Constants.LOG.info("Registering Amber keybindings for Forge...");
            for (var keyMapping : KeybindHelper.getKeybindings()) {
                ClientRegistry.registerKeyBinding(keyMapping);
            }
            KeybindHelper.forgeEventAlreadyFired = true;
        }
    }

    static public class EventHandlerServer {

    }
}
