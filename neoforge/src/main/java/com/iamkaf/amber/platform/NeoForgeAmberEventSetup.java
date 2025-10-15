package com.iamkaf.amber.platform;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.Constants;
import com.iamkaf.amber.api.event.v1.events.common.*;
import com.iamkaf.amber.api.event.v1.events.common.client.ClientCommandEvents;
import com.iamkaf.amber.api.event.v1.events.common.client.ClientTickEvents;
import com.iamkaf.amber.api.event.v1.events.common.client.HudEvents;
import com.iamkaf.amber.api.keymapping.KeybindHelper;
import com.iamkaf.amber.platform.services.IAmberEventSetup;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraft.world.InteractionResult;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityStruckByLightningEvent;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.living.AnimalTameEvent;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import static net.minecraft.world.InteractionResult.CONSUME;
import static net.minecraft.world.InteractionResult.SUCCESS;

public class NeoForgeAmberEventSetup implements IAmberEventSetup {
    @Override
    public void registerCommon() {
        NeoForge.EVENT_BUS.register(EventHandlerCommonNeoForge.class);
        IEventBus bus = (IEventBus) AmberMod.getEventBus(Constants.MOD_ID);
        assert bus != null;
        bus.register(ModBusEventHandler.class);
    }

    @Override
    public void registerClient() {
        NeoForge.EVENT_BUS.register(EventHandlerClientNeoForge.class);
        IEventBus bus = (IEventBus) AmberMod.getEventBus(Constants.MOD_ID);
        assert bus != null;
        bus.register(EventHandlerClientMod.class);
    }

    @Override
    public void registerServer() {
        NeoForge.EVENT_BUS.register(EventHandlerServer.class);
    }

    static public class EventHandlerCommonNeoForge {
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
            CommandEvents.EVENT.invoker()
                    .register(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
        }

        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
            EntityEvent.ENTITY_SPAWN.invoker().onEntitySpawn(event.getEntity(), event.getLevel());
        }

        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onLivingDeath(LivingDeathEvent event) {
            EntityEvent.ENTITY_DEATH.invoker().onEntityDeath(event.getEntity(), event.getSource());
        }

        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
            InteractionResult result = EntityEvent.ENTITY_DAMAGE.invoker()
                    .onEntityDamage(event.getEntity(), event.getSource(), event.getAmount());
            if (result != InteractionResult.PASS) {
                event.setCanceled(true); // Cancel damage if not PASS
            }
        }

        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onWorldLoad(LevelEvent.Load event) {
            WorldEvents.WORLD_LOAD.invoker().onWorldLoad(event.getLevel().getServer(), event.getLevel());
        }

        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onWorldUnload(LevelEvent.Unload event) {
            WorldEvents.WORLD_UNLOAD.invoker().onWorldUnload(event.getLevel().getServer(), event.getLevel());
        }

        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onWorldSave(LevelEvent.Save event) {
            WorldEvents.WORLD_SAVE.invoker().onWorldSave(event.getLevel().getServer(), event.getLevel());
        }

        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onLightningStrike(EntityStruckByLightningEvent event) {
            InteractionResult result =
                    WeatherEvents.LIGHTNING_STRIKE.invoker().onLightningStrike(event.getEntity(), event.getLightning());
            if (result != InteractionResult.PASS) {
                event.setCanceled(true); // Cancel if not PASS
            }
        }

        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onBlockBreak(BlockEvent.BreakEvent event) {
            InteractionResult result = BlockEvents.BLOCK_BREAK_BEFORE.invoker().beforeBlockBreak(
                    event.getPlayer().level(),
                    event.getPlayer(),
                    event.getPos(),
                    event.getState(),
                    event.getLevel().getBlockEntity(event.getPos())
            );
            if (result != InteractionResult.PASS) {
                event.setCanceled(true); // Cancel break
                return;
            }

            // Fire after event (can't cancel)
            BlockEvents.BLOCK_BREAK_AFTER.invoker().afterBlockBreak(
                    event.getPlayer().level(),
                    event.getPlayer(),
                    event.getPos(),
                    event.getState(),
                    event.getLevel().getBlockEntity(event.getPos())
            );
        }

        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
            if (!(event.getEntity() instanceof net.minecraft.world.entity.player.Player player)) {
                return; // Only handle player placements
            }

            // Fire the unified BLOCK_PLACE event
            InteractionResult result = BlockEvents.BLOCK_PLACE.invoker()
                    .onBlockPlace(player.level(), player, event.getPos(), event.getPlacedBlock(), player.getMainHandItem());
            if (result != InteractionResult.PASS) {
                event.setCanceled(true); // Cancel placement
            }
        }

        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onBlockInteract(PlayerInteractEvent.RightClickBlock event) {
            InteractionResult result = BlockEvents.BLOCK_INTERACT.invoker().onBlockInteract(
                    event.getEntity(),
                    event.getEntity().level(),
                    event.getHand(),
                    new net.minecraft.world.phys.BlockHitResult(
                            event.getHitVec().getLocation(),
                            event.getFace(),
                            event.getPos(),
                            false
                    )
            );
            if (result != InteractionResult.PASS) {
                event.setCanceled(true); // Cancel if not PASS
            }
        }

        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onBlockClick(PlayerInteractEvent.LeftClickBlock event) {
            InteractionResult result = BlockEvents.BLOCK_CLICK.invoker()
                    .onBlockClick(
                            event.getEntity(),
                            event.getEntity().level(),
                            event.getHand(),
                            event.getPos(),
                            event.getFace()
                    );
            if (result != InteractionResult.PASS) {
                event.setCanceled(true); // Cancel if not PASS
            }
        }

        // Farming events - implemented via Mixins (BoneMealItemMixin, FarmBlockMixin, CropBlockMixin)
        // Note: BonemealEvent, FarmlandTrampleEvent, and BlockGrowFeatureEvent not available in NeoForge 1.21.10

        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onAnimalTame(AnimalTameEvent event) {
            if (event.getTamer() == null) {
                return; // No tamer, allow vanilla behavior
            }
            InteractionResult result = AnimalEvents.ANIMAL_TAME.invoker().onAnimalTame(event.getAnimal(), event.getTamer());
            if (result != InteractionResult.PASS) {
                event.setCanceled(true); // Cancel if not PASS
            }
        }

        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onAnimalBreed(BabyEntitySpawnEvent event) {
            if (event.getParentA() instanceof net.minecraft.world.entity.animal.Animal parentA && event.getParentB() instanceof net.minecraft.world.entity.animal.Animal parentB) {
                AnimalEvents.ANIMAL_BREED.invoker().onAnimalBreed(parentA, parentB, event.getChild());
            }
        }
    }
    
    /**
     * Event handler for mod bus events in NeoForge.
     * <p>
     * This class handles events that need to be registered on the mod bus,
     * such as BuildCreativeModeTabContentsEvent.
     */
    static public class ModBusEventHandler {
        /**
         * Handles the BuildCreativeModeTabContentsEvent from NeoForge.
         * <p>
         * This event is fired when items are being added to a creative mode tab,
         * which allows us to fire our unified MODIFY_ENTRIES event.
         *
         * @param event The NeoForge event
         */
        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void buildContents(BuildCreativeModeTabContentsEvent event) {
            CreativeModeTabEvents.MODIFY_ENTRIES.invoker()
                .modifyEntries(event.getTabKey(), event::accept);
        }
    }

    static public class EventHandlerClientNeoForge {
        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onCommandRegistration(RegisterClientCommandsEvent event) {
            ClientCommandEvents.EVENT.invoker().register(event.getDispatcher(), event.getBuildContext());
        }

        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void eventRenderGameOverlayEvent(RenderGuiEvent.Post event) {
            HudEvents.RENDER_HUD.invoker().onHudRender(event.getGuiGraphics(), event.getPartialTick());
        }

        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onClientTickEventPre(ClientTickEvent.Pre event) {
            ClientTickEvents.START_CLIENT_TICK.invoker().onStartTick();
        }

        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onClientTickEventPost(ClientTickEvent.Post event) {
            ClientTickEvents.END_CLIENT_TICK.invoker().onEndTick();
        }
    }

    // This is for the mod event bus
    static public class EventHandlerClientMod {
        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onKeyMappingRegistration(RegisterKeyMappingsEvent event) {
            for (var keyMapping : KeybindHelper.getKeybindings()) {
                event.register(keyMapping);
            }
            KeybindHelper.forgeEventAlreadyFired = true;
        }
    }

    static public class EventHandlerServer {
        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onServerTickEventPre(ServerTickEvent.Pre event) {
            ServerTickEvents.START_SERVER_TICK.invoker().onStartTick();
        }

        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onServerTickEventPost(ServerTickEvent.Post event) {
            ServerTickEvents.END_SERVER_TICK.invoker().onEndTick();
        }

        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
            if (event.getEntity() instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                PlayerEvents.PLAYER_JOIN.invoker().onPlayerJoin(serverPlayer);
            }
        }

        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
            if (event.getEntity() instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                PlayerEvents.PLAYER_LEAVE.invoker().onPlayerLeave(serverPlayer);
            }
        }

        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
            if (event.getEntity() instanceof net.minecraft.server.level.ServerPlayer newPlayer) {
                // In NeoForge, we don't have easy access to the old player, so we pass the same player twice
                // The 'alive' flag indicates if they respawned from death (false) or from End portal (true)
                PlayerEvents.PLAYER_RESPAWN.invoker().onPlayerRespawn(newPlayer, newPlayer, !event.isEndConquered());
            }
        }

        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onItemDrop(ItemTossEvent event) {
            // Fire the informational Amber item drop event (fires on both client and server)
            ItemEvents.ITEM_DROP.invoker().onItemDrop(event.getPlayer(), event.getEntity());
        }

        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onItemPickup(ItemEntityPickupEvent.Pre event) {
            // Don't fire if the item has pickup delay (e.g., just dropped)
            if (event.getItemEntity().hasPickUpDelay()) {
                return;
            }

            // Fire the informational Amber item pickup event
            ItemEvents.ITEM_PICKUP.invoker()
                    .onItemPickup(event.getPlayer(), event.getItemEntity(), event.getItemEntity().getItem());
        }
    }
}
