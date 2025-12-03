package com.iamkaf.amber.platform;

import com.iamkaf.amber.api.event.v1.events.common.*;
import com.iamkaf.amber.api.event.v1.events.common.client.ClientCommandEvents;
import com.iamkaf.amber.api.event.v1.events.common.client.ClientTickEvents;
import com.iamkaf.amber.api.keymapping.KeybindHelper;
import com.iamkaf.amber.platform.services.IAmberEventSetup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.AnimalTameEvent;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraft.world.InteractionResult;
import net.minecraftforge.event.GatherComponentsEvent;

import static net.minecraft.world.InteractionResult.CONSUME;
import static net.minecraft.world.InteractionResult.SUCCESS;

public class ForgeAmberEventSetup implements IAmberEventSetup {

    /**
     * Forge-specific wrapper for GatherComponentsEvent.Item.
     */
    private static class ForgeComponentModificationContext implements ItemEvents.ComponentModificationContext {
        private final GatherComponentsEvent.Item event;

        ForgeComponentModificationContext(GatherComponentsEvent.Item event) {
            this.event = event;
        }

        @Override
        public void modify(Item item, java.util.function.Consumer<DataComponentMap.Builder> builderConsumer) {
            if (event.getOwner() == item) {
                DataComponentMap.Builder tempBuilder = DataComponentMap.builder();
                builderConsumer.accept(tempBuilder);

                DataComponentMap modifiedComponents = tempBuilder.build();
                for (TypedDataComponent<?> component : modifiedComponents) {
                    @SuppressWarnings("unchecked")
                    var compType = (DataComponentType<Object>) component.type();
                    event.register(compType, component.value());
                }
            }
        }
    }
    @Override
    public void registerCommon() {
        LootTableLoadEvent.BUS.addListener(EventHandlerCommon::onLootTableEvent);
        PlayerInteractEvent.EntityInteract.BUS.addListener(EventHandlerCommon::onPlayerEntityInteract);
        RegisterCommandsEvent.BUS.addListener(EventHandlerCommon::onCommandRegistration);
        EntityJoinLevelEvent.BUS.addListener(EventHandlerCommon::onEntityJoinLevel);
        LivingDeathEvent.BUS.addListener(EventHandlerCommon::onLivingDeath);
        LivingAttackEvent.BUS.addListener(EventHandlerCommon::onLivingAttack);

        // World events
        LevelEvent.Load.BUS.addListener(EventHandlerCommon::onWorldLoad);
        LevelEvent.Unload.BUS.addListener(EventHandlerCommon::onWorldUnload);
        LevelEvent.Save.BUS.addListener(EventHandlerCommon::onWorldSave);

        // Weather events
        EntityStruckByLightningEvent.BUS.addListener(EventHandlerCommon::onLightningStrike);

        // Block events
        BlockEvent.BreakEvent.BUS.addListener(EventHandlerCommon::onBlockBreak);
        BlockEvent.EntityPlaceEvent.BUS.addListener(EventHandlerCommon::onBlockPlace);
        PlayerInteractEvent.RightClickBlock.BUS.addListener(EventHandlerCommon::onBlockInteract);
        PlayerInteractEvent.LeftClickBlock.BUS.addListener(EventHandlerCommon::onBlockClick);

        // Animal events
        AnimalTameEvent.BUS.addListener(EventHandlerCommon::onAnimalTame);
        BabyEntitySpawnEvent.BUS.addListener(EventHandlerCommon::onAnimalBreed);

        // Shield block events
        ShieldBlockEvent.BUS.addListener(EventHandlerCommon::onShieldBlock);

        // Creative mode tab events (register with high priority)
        BuildCreativeModeTabContentsEvent.BUS.addListener(EventHandlerCommon::buildContents);

        // Default item components event
        GatherComponentsEvent.Item.BUS.addListener(EventHandlerCommon::onGatherComponents);
    }

    @Override
    public void registerClient() {
        RegisterClientCommandsEvent.BUS.addListener(EventHandlerClient::onCommandRegistration);
        // mod bus events
        RegisterKeyMappingsEvent.BUS.addListener(EventHandlerClient::onKeybindRegistration);
        TickEvent.ClientTickEvent.Pre.BUS.addListener(EventHandlerClient::onClientTickEventPre);
        TickEvent.ClientTickEvent.Post.BUS.addListener(EventHandlerClient::onClientTickEventPost);
    }

    @Override
    public void registerServer() {
        // Server tick events
        TickEvent.ServerTickEvent.Pre.BUS.addListener(EventHandlerServer::onServerTickEventPre);
        TickEvent.ServerTickEvent.Post.BUS.addListener(EventHandlerServer::onServerTickEventPost);

        // Player lifecycle events
        PlayerEvent.PlayerLoggedInEvent.BUS.addListener(EventHandlerCommon::onPlayerJoin);
        PlayerEvent.PlayerLoggedOutEvent.BUS.addListener(EventHandlerCommon::onPlayerLeave);
        PlayerEvent.PlayerRespawnEvent.BUS.addListener(EventHandlerCommon::onPlayerRespawn);

        // Item events
        ItemTossEvent.BUS.addListener(EventHandlerCommon::onItemDrop);
        EntityItemPickupEvent.BUS.addListener(EventHandlerCommon::onItemPickup);
    }

    static public class EventHandlerCommon {
        public static void onLootTableEvent(LootTableLoadEvent event) {
            LootEvents.MODIFY.invoker().modify(event.getName(), lootPool -> event.getTable().addPool(lootPool.build()));
        }

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

        public static void onCommandRegistration(RegisterCommandsEvent event) {
            CommandEvents.EVENT.invoker()
                    .register(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
        }

        public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
            EntityEvent.ENTITY_SPAWN.invoker().onEntitySpawn(event.getEntity(), event.getLevel());
        }

        public static void onLivingDeath(LivingDeathEvent event) {
            EntityEvent.ENTITY_DEATH.invoker().onEntityDeath(event.getEntity(), event.getSource());
        }

        public static boolean onLivingAttack(LivingAttackEvent event) {
            InteractionResult result = EntityEvent.ENTITY_DAMAGE.invoker()
                    .onEntityDamage(event.getEntity(), event.getSource(), event.getAmount());
            return result != InteractionResult.PASS; // Return true to cancel if not PASS
        }

        public static boolean onBlockBreak(BlockEvent.BreakEvent event) {
            InteractionResult result = BlockEvents.BLOCK_BREAK_BEFORE.invoker().beforeBlockBreak(
                    event.getPlayer().level(),
                    event.getPlayer(),
                    event.getPos(),
                    event.getState(),
                    event.getLevel().getBlockEntity(event.getPos())
            );
            if (result != InteractionResult.PASS) {
                return true; // Cancel break
            }

            // Fire after event (can't cancel)
            BlockEvents.BLOCK_BREAK_AFTER.invoker().afterBlockBreak(
                    event.getPlayer().level(),
                    event.getPlayer(),
                    event.getPos(),
                    event.getState(),
                    event.getLevel().getBlockEntity(event.getPos())
            );
            return false; // Allow break
        }

        public static boolean onBlockPlace(BlockEvent.EntityPlaceEvent event) {
            if (!(event.getEntity() instanceof net.minecraft.world.entity.player.Player player)) {
                return false; // Only handle player placements
            }

            // Fire the unified BLOCK_PLACE event
            InteractionResult result = BlockEvents.BLOCK_PLACE.invoker()
                    .onBlockPlace(player.level(), player, event.getPos(), event.getPlacedBlock(), player.getMainHandItem());
            return result != InteractionResult.PASS; // Return true to cancel if not PASS
        }

        public static boolean onBlockInteract(PlayerInteractEvent.RightClickBlock event) {
            InteractionResult result = BlockEvents.BLOCK_INTERACT.invoker()
                    .onBlockInteract(event.getEntity(), event.getEntity().level(), event.getHand(), event.getHitVec());
            return result != InteractionResult.PASS; // Cancel if not PASS
        }

        public static boolean onBlockClick(PlayerInteractEvent.LeftClickBlock event) {
            InteractionResult result = BlockEvents.BLOCK_CLICK.invoker()
                    .onBlockClick(
                            event.getEntity(),
                            event.getEntity().level(),
                            event.getHand(),
                            event.getPos(),
                            event.getFace()
                    );
            return result != InteractionResult.PASS; // Cancel if not PASS
        }

        public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
            if (event.getEntity() instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                PlayerEvents.PLAYER_JOIN.invoker().onPlayerJoin(serverPlayer);
            }
        }

        public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
            if (event.getEntity() instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                PlayerEvents.PLAYER_LEAVE.invoker().onPlayerLeave(serverPlayer);
            }
        }

        public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
            if (event.getEntity() instanceof net.minecraft.server.level.ServerPlayer newPlayer) {
                // In Forge, we don't have easy access to the old player, so we pass the same player twice
                // The 'alive' flag indicates if they respawned from death (false) or from End portal (true)
                PlayerEvents.PLAYER_RESPAWN.invoker().onPlayerRespawn(newPlayer, newPlayer, !event.isEndConquered());
            }
        }

        public static void onItemDrop(ItemTossEvent event) {
            // Fire the informational Amber item drop event (fires on both client and server)
            ItemEvents.ITEM_DROP.invoker().onItemDrop(event.getPlayer(), event.getEntity());
        }

        public static void onItemPickup(EntityItemPickupEvent event) {
            // Don't fire if the item has pickup delay (e.g., just dropped)
            if (event.getItem().hasPickUpDelay()) {
                return;
            }

            // Fire the informational Amber item pickup event
            ItemEvents.ITEM_PICKUP.invoker().onItemPickup(event.getEntity(), event.getItem(), event.getItem().getItem());
        }

        public static boolean onAnimalTame(AnimalTameEvent event) {
            if (event.getTamer() == null) {
                return false; // No tamer, allow vanilla behavior
            }
            InteractionResult result = AnimalEvents.ANIMAL_TAME.invoker().onAnimalTame(event.getAnimal(), event.getTamer());
            return result != InteractionResult.PASS; // Return true to cancel if not PASS
        }

        public static void onAnimalBreed(BabyEntitySpawnEvent event) {
            if (event.getParentA() instanceof net.minecraft.world.entity.animal.Animal parentA && event.getParentB() instanceof net.minecraft.world.entity.animal.Animal parentB) {
                AnimalEvents.ANIMAL_BREED.invoker().onAnimalBreed(parentA, parentB, event.getChild());
            }
        }

        public static void onShieldBlock(ShieldBlockEvent event) {
            if (event.getEntity() instanceof net.minecraft.world.entity.player.Player player) {
                net.minecraft.world.item.ItemStack shield = event.getBlockedWith();
                if (!shield.isEmpty()) {
                    PlayerEvents.SHIELD_BLOCK.invoker().onShieldBlock(
                        player, shield, event.getOriginalBlockedDamage(), event.getDamageSource()
                    );
                }
            }
        }

        public static void onWorldLoad(LevelEvent.Load event) {
            WorldEvents.WORLD_LOAD.invoker().onWorldLoad(event.getLevel().getServer(), event.getLevel());
        }

        public static void onWorldUnload(LevelEvent.Unload event) {
            WorldEvents.WORLD_UNLOAD.invoker().onWorldUnload(event.getLevel().getServer(), event.getLevel());
        }

        public static void onWorldSave(LevelEvent.Save event) {
            WorldEvents.WORLD_SAVE.invoker().onWorldSave(event.getLevel().getServer(), event.getLevel());
        }

        public static boolean onLightningStrike(EntityStruckByLightningEvent event) {
            InteractionResult result =
                    WeatherEvents.LIGHTNING_STRIKE.invoker().onLightningStrike(event.getEntity(), event.getLightning());
            return result != InteractionResult.PASS; // Return true to cancel if not PASS
        }
        
        /**
         * Handles the BuildCreativeModeTabContentsEvent from Forge.
         * <p>
         * This event is fired when items are being added to a creative mode tab,
         * which allows us to fire our unified MODIFY_ENTRIES event.
         *
         * @param event The Forge event
         */
        public static void buildContents(BuildCreativeModeTabContentsEvent event) {
            CreativeModeTabEvents.MODIFY_ENTRIES.invoker()
                .modifyEntries(event.getTabKey(), event::accept);
        }

        /**
         * Handles GatherComponentsEvent.Item from Forge.
         */
        public static void onGatherComponents(GatherComponentsEvent.Item event) {
            ItemEvents.MODIFY_DEFAULT_COMPONENTS.invoker().modify(
                new ForgeComponentModificationContext(event)
            );
        }
    }

    static public class EventHandlerClient {
        public static void onCommandRegistration(RegisterClientCommandsEvent event) {
            ClientCommandEvents.EVENT.invoker().register(event.getDispatcher(), event.getBuildContext());
        }

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
        public static void onServerTickEventPre(TickEvent.ServerTickEvent.Pre pre) {
            ServerTickEvents.START_SERVER_TICK.invoker().onStartTick();
        }

        public static void onServerTickEventPost(TickEvent.ServerTickEvent.Post post) {
            ServerTickEvents.END_SERVER_TICK.invoker().onEndTick();
        }
    }
}
