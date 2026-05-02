package com.iamkaf.amber.platform;

import com.iamkaf.amber.api.event.v1.events.common.*;
import com.iamkaf.amber.api.event.v1.events.common.client.ClientCommandEvents;
import com.iamkaf.amber.api.event.v1.events.common.client.ClientTickEvents;
import com.iamkaf.amber.api.event.v1.events.common.client.HudEvents;
import com.iamkaf.amber.api.event.v1.events.common.client.InputEvents;
import com.iamkaf.amber.api.event.v1.events.common.client.RenderEvents;
import com.iamkaf.amber.api.registry.v1.KeybindHelper;
import com.iamkaf.amber.platform.services.IAmberEventSetup;
import com.iamkaf.amber.api.event.v1.events.common.CreativeModeTabOutput;
import java.lang.reflect.Field;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
//? if >=1.20.5
import net.minecraft.core.component.DataComponentMap;
//? if >=1.20.5
import net.minecraft.core.component.DataComponentType;
//? if >=1.20.5
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.state.BlockState;
//? if >=1.20
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
//? if >=1.19.3 && <1.20
/*import net.minecraftforge.event.CreativeModeTabEvent;*/
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
//? if >=1.19
import net.minecraftforge.event.level.BlockEvent;
//? if <1.19
/*import net.minecraftforge.event.world.BlockEvent;*/
//? if >=1.19
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
//? if <1.19
/*import net.minecraftforge.event.entity.EntityJoinWorldEvent;*/
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.AnimalTameEvent;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
//? if >=1.18.1
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
//? if >=1.19
import net.minecraftforge.event.level.LevelEvent;
//? if <1.19
/*import net.minecraftforge.event.world.WorldEvent;*/
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
//? if <1.19
/*import net.minecraftforge.client.event.InputEvent;*/
//? if <1.19
/*import net.minecraftforge.client.event.DrawSelectionEvent;*/
//? if >=1.18.1
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
//? if >=1.19 && <1.20.5
import net.minecraftforge.client.event.RenderGuiEvent;
//? if <1.19
/*import net.minecraftforge.client.event.RenderGameOverlayEvent;*/
//? if >=1.19
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
//? if >=1.18 && <1.19
/*import net.minecraftforge.client.ClientRegistry;*/
//? if <1.18
/*import net.minecraftforge.fmlclient.registry.ClientRegistry;*/
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraft.world.InteractionResult;
//? if >=1.20.5
import net.minecraftforge.event.GatherComponentsEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
//? if <1.21.6
/*import net.minecraftforge.common.MinecraftForge;*/

import static net.minecraft.world.InteractionResult.CONSUME;
import static net.minecraft.world.InteractionResult.SUCCESS;

public class ForgeAmberEventSetup implements IAmberEventSetup {
    //? if >=1.20.5
    private static final Field ITEM_BUILT_COMPONENTS_FIELD = findItemBuiltComponentsField();
    //? if >=1.20.5
    private static volatile boolean itemComponentCacheDirty;

    /**
     * Forge-specific wrapper for GatherComponentsEvent.Item.
     */
    //? if >=1.20.5 {
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
    //?}
    @Override
    public void registerCommon() {
        //? if >=1.21.6 {
        LootTableLoadEvent.BUS.addListener(EventHandlerCommon::onLootTableEvent);
        PlayerInteractEvent.EntityInteractSpecific.BUS.addListener(EventHandlerCommon::onPlayerEntityInteract);
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
        //? if >=1.18.1
        ShieldBlockEvent.BUS.addListener(EventHandlerCommon::onShieldBlock);
        //?} else {
        /*MinecraftForge.EVENT_BUS.addListener(EventHandlerCommon::onLootTableEvent);
        MinecraftForge.EVENT_BUS.addListener(EventHandlerCommon::onPlayerEntityInteract);
        MinecraftForge.EVENT_BUS.addListener(EventHandlerCommon::onCommandRegistration);
        MinecraftForge.EVENT_BUS.addListener(EventHandlerCommon::onEntityJoinLevel);
        MinecraftForge.EVENT_BUS.addListener(EventHandlerCommon::onLivingDeath);
        MinecraftForge.EVENT_BUS.addListener(EventHandlerCommon::onLivingAttack);

        // World events
        MinecraftForge.EVENT_BUS.addListener(EventHandlerCommon::onWorldLoad);
        MinecraftForge.EVENT_BUS.addListener(EventHandlerCommon::onWorldUnload);
        MinecraftForge.EVENT_BUS.addListener(EventHandlerCommon::onWorldSave);

        // Weather events
        MinecraftForge.EVENT_BUS.addListener(EventHandlerCommon::onLightningStrike);

        // Block events
        MinecraftForge.EVENT_BUS.addListener(EventHandlerCommon::onBlockBreak);
        MinecraftForge.EVENT_BUS.addListener(EventHandlerCommon::onBlockPlace);
        MinecraftForge.EVENT_BUS.addListener(EventHandlerCommon::onBlockInteract);
        MinecraftForge.EVENT_BUS.addListener(EventHandlerCommon::onBlockClick);

        // Animal events
        MinecraftForge.EVENT_BUS.addListener(EventHandlerCommon::onAnimalTame);
        MinecraftForge.EVENT_BUS.addListener(EventHandlerCommon::onAnimalBreed);

        // Shield block events
        //? if >=1.18.1
        MinecraftForge.EVENT_BUS.addListener(EventHandlerCommon::onShieldBlock);*/
        //?}
        //? if <1.19
        /*MinecraftForge.EVENT_BUS.addListener(EventHandlerCommon::onItemCrafted);*/

        // Creative mode tab events (register with high priority)
        //? if >=1.21.10
        BuildCreativeModeTabContentsEvent.BUS.addListener(EventHandlerCommon::buildContents);
        //? if >=1.21.6 && <1.21.10
        /*BuildCreativeModeTabContentsEvent.getBus(FMLJavaModLoadingContext.get().getModBusGroup()).addListener(EventHandlerCommon::buildContents);*/
        //? if >=1.20 && <1.21.6
        /*FMLJavaModLoadingContext.get().getModEventBus().addListener(EventHandlerCommon::buildContents);*/

        // Default item components event
        //? if >=1.21.6
        GatherComponentsEvent.Item.BUS.addListener(EventHandlerCommon::onGatherComponents);
        //? if >=1.20.5 && <1.21.6
        /*MinecraftForge.EVENT_BUS.addListener(EventHandlerCommon::onGatherComponents);*/
        //? if >=1.20.5
        ItemEvents.setDefaultComponentListenerRegisteredHook(ForgeAmberEventSetup::invalidateItemComponentCache);
        //? if >=1.19.3 && <1.20
        /*FMLJavaModLoadingContext.get().getModEventBus().addListener(EventHandlerCommon::registerCreativeTabsLegacy);*/
        //? if >=1.19.3 && <1.20
        /*FMLJavaModLoadingContext.get().getModEventBus().addListener(EventHandlerCommon::buildContentsLegacy);*/
    }

    @Override
    public void registerClient() {
        //? if >=1.21.6
        RegisterClientCommandsEvent.BUS.addListener(EventHandlerClient::onCommandRegistration);
        //? if <1.21.6 && >=1.18.1
        /*MinecraftForge.EVENT_BUS.addListener(EventHandlerClient::onCommandRegistration);*/
        // mod bus events
        //? if >=1.21.10
        RegisterKeyMappingsEvent.BUS.addListener(EventHandlerClient::onKeybindRegistration);
        //? if >=1.21.6 && <1.21.10
        /*RegisterKeyMappingsEvent.getBus(FMLJavaModLoadingContext.get().getModBusGroup()).addListener(EventHandlerClient::onKeybindRegistration);*/
        //? if >=1.19 && <1.21.6
        /*FMLJavaModLoadingContext.get().getModEventBus().addListener(EventHandlerClient::onKeybindRegistration);*/
        //? if <1.19
        /*EventHandlerClient.onKeybindRegistration();*/
        //? if >=1.21.6 {
        TickEvent.ClientTickEvent.Pre.BUS.addListener(EventHandlerClient::onClientTickEventPre);
        TickEvent.ClientTickEvent.Post.BUS.addListener(EventHandlerClient::onClientTickEventPost);
        //?} else {
        /*MinecraftForge.EVENT_BUS.addListener(EventHandlerClient::onClientTickEventPre);
        MinecraftForge.EVENT_BUS.addListener(EventHandlerClient::onClientTickEventPost);*/
        //?}
        //? if >=1.19 && <1.20.5
        /*MinecraftForge.EVENT_BUS.addListener(EventHandlerClient::onRenderGuiPost);*/
        //? if <1.19
        /*MinecraftForge.EVENT_BUS.addListener(EventHandlerClient::onRenderGameOverlayPost);*/
        //? if <1.19
        /*MinecraftForge.EVENT_BUS.addListener(EventHandlerClient::onMouseScroll);*/
        //? if <1.19
        /*MinecraftForge.EVENT_BUS.addListener(EventHandlerClient::onBlockOutlineRender);*/
    }

    // FIXME: registerServer() called from common init due to EnvExecutor inconsistency
    // TODO: Move all server events to registerCommon() and sunset registerServer() methods
    @Override
    public void registerServer() {
        // Server tick events
        //? if >=1.21.6 {
        TickEvent.ServerTickEvent.Pre.BUS.addListener(EventHandlerServer::onServerTickEventPre);
        TickEvent.ServerTickEvent.Post.BUS.addListener(EventHandlerServer::onServerTickEventPost);

        // Player lifecycle events
        PlayerEvent.PlayerLoggedInEvent.BUS.addListener(EventHandlerCommon::onPlayerJoin);
        PlayerEvent.PlayerLoggedOutEvent.BUS.addListener(EventHandlerCommon::onPlayerLeave);
        PlayerEvent.PlayerRespawnEvent.BUS.addListener(EventHandlerCommon::onPlayerRespawn);

        // Item events
        ItemTossEvent.BUS.addListener(EventHandlerCommon::onItemDrop);
        EntityItemPickupEvent.BUS.addListener(EventHandlerCommon::onItemPickup);
        //?} else {
        /*MinecraftForge.EVENT_BUS.addListener(EventHandlerServer::onServerTickEventPre);
        MinecraftForge.EVENT_BUS.addListener(EventHandlerServer::onServerTickEventPost);

        // Player lifecycle events
        MinecraftForge.EVENT_BUS.addListener(EventHandlerCommon::onPlayerJoin);
        MinecraftForge.EVENT_BUS.addListener(EventHandlerCommon::onPlayerLeave);
        MinecraftForge.EVENT_BUS.addListener(EventHandlerCommon::onPlayerRespawn);

        // Item events
        MinecraftForge.EVENT_BUS.addListener(EventHandlerCommon::onItemDrop);
        MinecraftForge.EVENT_BUS.addListener(EventHandlerCommon::onItemPickup);*/
        //?}
    }

    static public class EventHandlerCommon {
        public static void onLootTableEvent(LootTableLoadEvent event) {
            LootEvents.MODIFY.invoker().modify(event.getName(), lootPool -> {
                //? if >=1.20.1
                event.getTable().addPool(lootPool.build());
                //? if <1.20.1
                /*addLootPool(event.getTable(), lootPool.build());*/
            });
        }

        //? if <1.20.1 {
        /*private static void addLootPool(net.minecraft.world.level.storage.loot.LootTable table, net.minecraft.world.level.storage.loot.LootPool pool) {
            try {
                java.lang.reflect.Field poolsField = net.minecraft.world.level.storage.loot.LootTable.class.getDeclaredField("pools");
                poolsField.setAccessible(true);
                net.minecraft.world.level.storage.loot.LootPool[] pools = (net.minecraft.world.level.storage.loot.LootPool[]) poolsField.get(table);
                net.minecraft.world.level.storage.loot.LootPool[] expanded = java.util.Arrays.copyOf(pools, pools.length + 1);
                expanded[pools.length] = pool;
                poolsField.set(table, expanded);
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Failed to modify loot table", e);
            }
        }*/
        //?}

        public static boolean onPlayerEntityInteract(PlayerInteractEvent.EntityInteractSpecific event) {
            InteractionResult result = PlayerEvents.ENTITY_INTERACT.invoker()
                    .interact(
                            //? if >=1.19
                            event.getEntity(),
                            //? if <1.19
                            /*event.getPlayer(),*/
                            //? if >=1.19
                            event.getLevel(),
                            //? if <1.19
                            /*event.getWorld(),*/
                            event.getHand(), event.getTarget());

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
                    .register(event.getDispatcher(),
                            //? if >=1.19
                            event.getBuildContext(), event.getCommandSelection()
	        //? if >=1.19 && <1.19
	        /*new net.minecraft.commands.CommandBuildContext(net.minecraft.core.RegistryAccess.BUILTIN.get()), net.minecraft.commands.Commands.CommandSelection.ALL*/
	                            //? if <1.19 && >=1.18.2
	                            /*legacyBuiltinRegistryAccess(), commandSelectionAll()*/
	                            //? if <1.18.2
	                            /*legacyBuiltinRegistryAccess(), commandSelectionAll()*/
	                    );
	        }

	        //? if <1.19 {
	        /*private static net.minecraft.core.RegistryAccess legacyBuiltinRegistryAccess() {
	            try {
	                Object supplier = net.minecraft.core.RegistryAccess.class.getField("BUILTIN").get(null);
	                if (supplier instanceof java.util.function.Supplier<?> registrySupplier) {
	                    return (net.minecraft.core.RegistryAccess) registrySupplier.get();
	                }
	            } catch (ReflectiveOperationException ignored) {
	            }

	            try {
	                return (net.minecraft.core.RegistryAccess) net.minecraft.core.RegistryAccess.class
	                        .getMethod("builtin")
	                        .invoke(null);
	            } catch (ReflectiveOperationException exception) {
	                throw new IllegalStateException("Unable to resolve builtin registry access", exception);
	            }
	        }

	        private static net.minecraft.commands.Commands.CommandSelection commandSelectionAll() {
	            return java.lang.Enum.valueOf(net.minecraft.commands.Commands.CommandSelection.class, "ALL");
	        }*/
	        //?}

        public static void onEntityJoinLevel(
                //? if >=1.19
                EntityJoinLevelEvent event
                //? if <1.19
                /*EntityJoinWorldEvent event*/
        ) {
            EntityEvent.ENTITY_SPAWN.invoker().onEntitySpawn(event.getEntity(),
                    //? if >=1.19
                    event.getLevel()
                    //? if <1.19
                    /*event.getWorld()*/
            );
        }

        public static void onLivingDeath(LivingDeathEvent event) {
            EntityEvent.ENTITY_DEATH.invoker().onEntityDeath(
                    //? if >=1.19
                    event.getEntity(),
                    //? if <1.19
                    /*event.getEntityLiving(),*/
                    event.getSource());
        }

        public static boolean onLivingAttack(LivingAttackEvent event) {
            if (
                    //? if >=1.20
                    event.getEntity().level().isClientSide()
                    //? if <1.20 && >=1.19
                    /*event.getEntity().level.isClientSide*/
                    //? if <1.19
                    /*legacyIsClientSide(event.getEntityLiving())*/
            ) {
                return false;
            }

            InteractionResult result = EntityEvent.ENTITY_DAMAGE.invoker()
                    .onEntityDamage(
                            //? if >=1.19
                            event.getEntity(),
                            //? if <1.19
                            /*event.getEntityLiving(),*/
                            event.getSource(), event.getAmount());
            if (result != InteractionResult.PASS) {
                return true;
            }

            //? if <1.18.1
            /*fireLegacyShieldBlock(event);*/

            return false;
        }

        //? if <1.18.1 {
        /*private static void fireLegacyShieldBlock(LivingAttackEvent event) {
            if (!(event.getEntityLiving() instanceof net.minecraft.world.entity.player.Player player)) {
                return;
            }

            if (!player.isDamageSourceBlocked(event.getSource())) {
                return;
            }

            ItemStack shield = player.getUseItem();
            if (shield.isEmpty() || !(shield.getItem() instanceof net.minecraft.world.item.ShieldItem)) {
                return;
            }

            PlayerEvents.SHIELD_BLOCK.invoker().onShieldBlock(player, shield, event.getAmount(), event.getSource());
        }
        *///?}

        public static boolean onBlockBreak(BlockEvent.BreakEvent event) {
            InteractionResult result = BlockEvents.BLOCK_BREAK_BEFORE.invoker().beforeBlockBreak(
                    //? if >=1.20
                    event.getPlayer().level(),
                    //? if <1.20
                    /*event.getPlayer().level,*/
                    event.getPlayer(),
                    event.getPos(),
                    event.getState(),
                    //? if >=1.19
                    event.getLevel().getBlockEntity(event.getPos())
                    //? if <1.19
                    /*event.getWorld().getBlockEntity(event.getPos())*/
            );
            if (result != InteractionResult.PASS) {
                return true; // Cancel break
            }

            // Fire after event (can't cancel)
            BlockEvents.BLOCK_BREAK_AFTER.invoker().afterBlockBreak(
                    //? if >=1.20
                    event.getPlayer().level(),
                    //? if <1.20
                    /*event.getPlayer().level,*/
                    event.getPlayer(),
                    event.getPos(),
                    event.getState(),
                    //? if >=1.19
                    event.getLevel().getBlockEntity(event.getPos())
                    //? if <1.19
                    /*event.getWorld().getBlockEntity(event.getPos())*/
            );
            return false; // Allow break
        }

        public static boolean onBlockPlace(BlockEvent.EntityPlaceEvent event) {
            if (!(event.getEntity() instanceof net.minecraft.world.entity.player.Player player)) {
                return false; // Only handle player placements
            }

            // Fire the unified BLOCK_PLACE event
            InteractionResult result = BlockEvents.BLOCK_PLACE.invoker()
                    //? if >=1.20
                    .onBlockPlace(player.level(), player, event.getPos(), event.getPlacedBlock(), player.getMainHandItem());
                    //? if <1.20
                    /*.onBlockPlace(player.level, player, event.getPos(), event.getPlacedBlock(), player.getMainHandItem());*/
            return result != InteractionResult.PASS; // Return true to cancel if not PASS
        }

        public static boolean onBlockInteract(PlayerInteractEvent.RightClickBlock event) {
            InteractionResult result = BlockEvents.BLOCK_INTERACT.invoker()
                    //? if >=1.20
                    .onBlockInteract(event.getEntity(), event.getEntity().level(), event.getHand(), event.getHitVec());
                    //? if <1.20 && >=1.19
                    /*.onBlockInteract(event.getEntity(), event.getEntity().level, event.getHand(), event.getHitVec());*/
                    //? if <1.19
                    /*.onBlockInteract(event.getPlayer(), event.getPlayer().level, event.getHand(), event.getHitVec());*/
            return result != InteractionResult.PASS; // Cancel if not PASS
        }

        public static boolean onBlockClick(PlayerInteractEvent.LeftClickBlock event) {
            InteractionResult result = BlockEvents.BLOCK_CLICK.invoker()
                    .onBlockClick(
                            //? if >=1.19
                            event.getEntity(),
                            //? if <1.19
                            /*event.getPlayer(),*/
                            //? if >=1.20
                            event.getEntity().level(),
                            //? if <1.20 && >=1.19
                            /*event.getEntity().level,*/
                            //? if <1.19
                            /*event.getPlayer().level,*/
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
            ItemEvents.ITEM_DROP.invoker().onItemDrop(event.getPlayer(),
                    //? if >=1.19
                    event.getEntity()
                    //? if <1.19
                    /*event.getEntityItem()*/
            );
        }

        public static void onItemPickup(EntityItemPickupEvent event) {
            // Don't fire if the item has pickup delay (e.g., just dropped)
            if (hasPickUpDelay(event.getItem())) {
                return;
            }

            // Fire the informational Amber item pickup event
            ItemEvents.ITEM_PICKUP.invoker().onItemPickup(
                    //? if >=1.19
                    event.getEntity(),
                    //? if <1.19
                    /*event.getPlayer(),*/
                    event.getItem(), itemStack(event.getItem()));
        }

        private static boolean hasPickUpDelay(ItemEntity item) {
            try {
                Object value = item.getClass().getMethod("hasPickUpDelay").invoke(item);
                return value instanceof Boolean delayed && delayed;
            } catch (ReflectiveOperationException exception) {
                throw new IllegalStateException("Unable to resolve item pickup delay", exception);
            }
        }

        private static ItemStack itemStack(ItemEntity item) {
            try {
                return (ItemStack) item.getClass().getMethod("getItem").invoke(item);
            } catch (ReflectiveOperationException exception) {
                throw new IllegalStateException("Unable to resolve item entity stack", exception);
            }
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

        //? if <1.19 {
        /*public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
            if (event.getPlayer() instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                PlayerEvents.CRAFT_ITEM.invoker().onCraftItem(serverPlayer, java.util.List.of(event.getCrafting()));
            }
        }
        *///?}

        //? if >=1.18.1 {
        public static void onShieldBlock(ShieldBlockEvent event) {
            if (event.getEntity() instanceof net.minecraft.world.entity.player.Player player) {
                //? if >=1.21.5
                net.minecraft.world.item.ItemStack shield = event.getBlockedWith();
                //? if <1.21.5
                /*net.minecraft.world.item.ItemStack shield = player.getUseItem();*/
                if (!shield.isEmpty()) {
                    PlayerEvents.SHIELD_BLOCK.invoker().onShieldBlock(
                        player, shield, event.getOriginalBlockedDamage(), event.getDamageSource()
                    );
                }
            }
        }
        //?}

        public static void onWorldLoad(
                //? if >=1.19
                LevelEvent.Load event
                //? if <1.19
                /*WorldEvent.Load event*/
        ) {
            //? if >=1.20.5
            rebuildItemComponentCacheIfReady();
	            WorldEvents.WORLD_LOAD.invoker().onWorldLoad(
	                    //? if >=1.19
	                    event.getLevel().getServer(), event.getLevel()
	                    //? if <1.19
	                    /*legacyServer(event.getWorld()), event.getWorld()*/
	            );
	        }

        public static void onWorldUnload(
                //? if >=1.19
                LevelEvent.Unload event
                //? if <1.19
                /*WorldEvent.Unload event*/
        ) {
	            WorldEvents.WORLD_UNLOAD.invoker().onWorldUnload(
	                    //? if >=1.19
	                    event.getLevel().getServer(), event.getLevel()
	                    //? if <1.19
	                    /*legacyServer(event.getWorld()), event.getWorld()*/
	            );
	        }

        public static void onWorldSave(
                //? if >=1.19
                LevelEvent.Save event
                //? if <1.19
                /*WorldEvent.Save event*/
        ) {
	            WorldEvents.WORLD_SAVE.invoker().onWorldSave(
	                    //? if >=1.19
	                    event.getLevel().getServer(), event.getLevel()
	                    //? if <1.19
	                    /*legacyServer(event.getWorld()), event.getWorld()*/
	            );
	        }

	        //? if <1.19 {
	        /*private static net.minecraft.server.MinecraftServer legacyServer(Object level) {
	            try {
	                return (net.minecraft.server.MinecraftServer) level.getClass().getMethod("getServer").invoke(level);
	            } catch (ReflectiveOperationException exception) {
	                throw new IllegalStateException("Unable to resolve server for Forge world event", exception);
	            }
	        }*/

	        /*private static boolean legacyIsClientSide(net.minecraft.world.entity.Entity entity) {
	            try {
	                Object level = net.minecraft.world.entity.Entity.class.getField("level").get(entity);
	                Object value = level.getClass().getField("isClientSide").get(level);
	                return value instanceof Boolean clientSide && clientSide;
	            } catch (ReflectiveOperationException exception) {
	                throw new IllegalStateException("Unable to resolve entity level side", exception);
	            }
	        }*/
	        //?}

        public static boolean onLightningStrike(EntityStruckByLightningEvent event) {
            InteractionResult result =
                    WeatherEvents.LIGHTNING_STRIKE.invoker().onLightningStrike(event.getEntity(), event.getLightning());
            return result != InteractionResult.PASS; // Return true to cancel if not PASS
        }
        
        //? if >=1.20 {
        /**
         * Handles the BuildCreativeModeTabContentsEvent from Forge.
         * <p>
         * This event is fired when items are being added to a creative mode tab,
         * which allows us to fire our unified MODIFY_ENTRIES event.
         *
         * @param event The Forge event
         */
        public static void buildContents(BuildCreativeModeTabContentsEvent event) {
            //? if >=1.20.5
            rebuildItemComponentCacheIfReady();
            // Add items from TabBuilder if this is a custom tab
            com.iamkaf.amber.api.registry.v1.creativetabs.TabBuilder tabBuilder =
                //? if >=1.21.11
                com.iamkaf.amber.api.registry.v1.creativetabs.CreativeModeTabRegistry.getTabBuilder(event.getTabKey().identifier());
                //? if <1.21.11
                /*com.iamkaf.amber.api.registry.v1.creativetabs.CreativeModeTabRegistry.getTabBuilder(event.getTabKey().location());*/
            if (tabBuilder != null) {
                for (var itemSupplier : tabBuilder.getItems()) {
                    event.accept(itemSupplier.get());
                }
            }

            CreativeModeTabOutput output = new CreativeModeTabOutput() {
                @Override
                public void accept(net.minecraft.world.item.ItemStack stack, CreativeModeTabOutput.TabVisibility visibility) {
                    net.minecraft.world.item.CreativeModeTab.TabVisibility mcVisibility = switch (visibility) {
                        case PARENT_AND_SEARCH_TABS -> net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS;
                        case PARENT_TAB_ONLY -> net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_TAB_ONLY;
                        case SEARCH_TAB_ONLY -> net.minecraft.world.item.CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY;
                    };
                    event.accept(stack, mcVisibility);
                }
            };
            CreativeModeTabEvents.MODIFY_ENTRIES.invoker()
                    .modifyEntries(event.getTabKey(), output);
        }
        //?}

        //? if >=1.19.3 && <1.20 {
        /*public static void registerCreativeTabsLegacy(CreativeModeTabEvent.Register event) {
            for (var builder : com.iamkaf.amber.api.registry.v1.creativetabs.CreativeModeTabRegistry.getTabBuilders().values()) {
                event.registerCreativeModeTab(builder.getId(), tabBuilder -> {
                    tabBuilder.title(builder.getTitle());
                    tabBuilder.icon(builder.getIcon());
                    if (!builder.shouldShowTitle()) {
                        tabBuilder.hideTitle();
                    }
                    if (!builder.canScroll()) {
                        tabBuilder.noScrollBar();
                    }
                });
            }
        }*/

        //?}

        //? if >=1.19.3 && <1.20 {
        /*public static void buildContentsLegacy(CreativeModeTabEvent.BuildContents event) {
            net.minecraft.resources.ResourceLocation tabId = net.minecraftforge.common.CreativeModeTabRegistry.getName(event.getTab());
            if (tabId == null) {
                return;
            }
            net.minecraft.resources.ResourceKey<net.minecraft.core.Registry<net.minecraft.world.item.CreativeModeTab>> registryKey =
                    net.minecraft.resources.ResourceKey.createRegistryKey(new net.minecraft.resources.ResourceLocation("minecraft", "creative_mode_tab"));
            net.minecraft.resources.ResourceKey<net.minecraft.world.item.CreativeModeTab> tabKey =
                    net.minecraft.resources.ResourceKey.create(registryKey, tabId);

            com.iamkaf.amber.api.registry.v1.creativetabs.TabBuilder tabBuilder =
                    com.iamkaf.amber.api.registry.v1.creativetabs.CreativeModeTabRegistry.getTabBuilder(tabId);
            //? if >=1.19.3 {
            if (tabBuilder != null) {
                for (var itemSupplier : tabBuilder.getItems()) {
                    event.getEntries().put(
                            new net.minecraft.world.item.ItemStack(itemSupplier.get()),
                            net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
                    );
                }
            }

            CreativeModeTabEvents.MODIFY_ENTRIES.invoker()
                    .modifyEntries(tabKey, new CreativeModeTabOutput() {
                        @Override
                        public void accept(net.minecraft.world.item.ItemStack stack, CreativeModeTabOutput.TabVisibility visibility) {
                            net.minecraft.world.item.CreativeModeTab.TabVisibility mcVisibility = switch (visibility) {
                                case PARENT_AND_SEARCH_TABS -> net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS;
                                case PARENT_TAB_ONLY -> net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_TAB_ONLY;
                                case SEARCH_TAB_ONLY -> net.minecraft.world.item.CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY;
                            };
                            event.getEntries().put(stack, mcVisibility);
                        }
                    });
            //?}
        }*/
        //?}

        //? if >=1.20.5 {
        /**
         * Handles GatherComponentsEvent.Item from Forge.
         */
        public static void onGatherComponents(GatherComponentsEvent.Item event) {
            ItemEvents.MODIFY_DEFAULT_COMPONENTS.invoker().modify(
                new ForgeComponentModificationContext(event)
            );
        }
        //?}
    }

    static public class EventHandlerClient {
        //? if >=1.19 && <1.20.5 {
        public static void onRenderGuiPost(RenderGuiEvent.Post event) {
            //? if >=1.20 {
            HudEvents.RENDER_HUD.invoker().onHudRender(event.getGuiGraphics(), event.getPartialTick());
            //?} else {
            /*HudEvents.RENDER_HUD.invoker().onHudRender(event.getPoseStack(), event.getPartialTick());*/
            //?}
        }

        //?}

        //? if <1.19 {
        /*public static void onRenderGameOverlayPost(RenderGameOverlayEvent.Post event) {
            if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
                HudEvents.RENDER_HUD.invoker().onHudRender(event.getMatrixStack(), event.getPartialTicks());
            }
        }

        *///?}

        //? if >=1.18.1 {
        public static void onCommandRegistration(RegisterClientCommandsEvent event) {
            ClientCommandEvents.EVENT.invoker().register(event.getDispatcher(),
                    //? if >=1.19
                    event.getBuildContext()
	                    //? if >=1.19 && <1.19
	                    /*new net.minecraft.commands.CommandBuildContext(net.minecraft.core.RegistryAccess.BUILTIN.get())*/
	                    //? if <1.19 && >=1.18.2
	                    /*EventHandlerCommon.legacyBuiltinRegistryAccess()*/
	                    //? if <1.18.2
	                    /*EventHandlerCommon.legacyBuiltinRegistryAccess()*/
	            );
	        }
        //?}

        public static void onKeybindRegistration(
                //? if >=1.19
                RegisterKeyMappingsEvent event
        ) {
            KeybindHelper.forgeEventAlreadyFired = true;
            for (var keyMapping : new ArrayList<>(KeybindHelper.getKeybindings())) {
                //? if >=1.19
                event.register(keyMapping);
                //? if <1.19
                /*ClientRegistry.registerKeyBinding(keyMapping);*/
            }
        }

        //? if >=1.20.4
        public static void onClientTickEventPre(TickEvent.ClientTickEvent.Pre pre) {
        //? if <1.20.4
        /*public static void onClientTickEventPre(TickEvent.ClientTickEvent event) { if (event.phase != TickEvent.Phase.START) return;*/
            ClientTickEvents.START_CLIENT_TICK.invoker().onStartTick();
        }

        //? if >=1.20.4
        public static void onClientTickEventPost(TickEvent.ClientTickEvent.Post post) {
        //? if <1.20.4
        /*public static void onClientTickEventPost(TickEvent.ClientTickEvent event) { if (event.phase != TickEvent.Phase.END) return;*/
            ClientTickEvents.END_CLIENT_TICK.invoker().onEndTick();
        }

        //? if <1.19 {
        /*public static void onMouseScroll(InputEvent.MouseScrollEvent event) {
            InteractionResult result = InputEvents.MOUSE_SCROLL_PRE.invoker()
                    .onMouseScrollPre(event.getMouseX(), event.getMouseY(), 0.0, event.getScrollDelta());
            if (result != InteractionResult.PASS) {
                event.setCanceled(true);
                return;
            }
            InputEvents.MOUSE_SCROLL_POST.invoker()
                    .onMouseScrollPost(event.getMouseX(), event.getMouseY(), 0.0, event.getScrollDelta());
        }
        *///?}

        //? if <1.19 {
        /*public static void onBlockOutlineRender(DrawSelectionEvent.HighlightBlock event) {
            BlockPos pos = event.getTarget().getBlockPos();
            BlockState state = Minecraft.getInstance().level.getBlockState(pos);
            InteractionResult result = RenderEvents.BLOCK_OUTLINE_RENDER.invoker().onBlockOutlineRender(
                    event.getInfo(),
                    event.getBuffers(),
                    event.getMatrix(),
                    event.getTarget(),
                    pos,
                    state
            );
            if (result != InteractionResult.PASS) {
                event.setCanceled(true);
            }
        }
        *///?}
    }

    static public class EventHandlerServer {
        //? if >=1.20.4
        public static void onServerTickEventPre(TickEvent.ServerTickEvent.Pre pre) {
        //? if <1.20.4
        /*public static void onServerTickEventPre(TickEvent.ServerTickEvent event) { if (event.phase != TickEvent.Phase.START) return;*/
            ServerTickEvents.START_SERVER_TICK.invoker().onStartTick();
        }

        //? if >=1.20.4
        public static void onServerTickEventPost(TickEvent.ServerTickEvent.Post post) {
        //? if <1.20.4
        /*public static void onServerTickEventPost(TickEvent.ServerTickEvent event) { if (event.phase != TickEvent.Phase.END) return;*/
            ServerTickEvents.END_SERVER_TICK.invoker().onEndTick();
        }
    }

    //? if >=1.20.5 {
    private static Field findItemBuiltComponentsField() {
        try {
            Field field = Item.class.getDeclaredField("builtComponents");
            field.setAccessible(true);
            return field;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to access Forge item component cache", e);
        }
    }

    private static void invalidateItemComponentCache() {
        itemComponentCacheDirty = true;
        rebuildItemComponentCacheIfReady();
    }

    private static void rebuildItemComponentCacheIfReady() {
        if (!itemComponentCacheDirty) {
            return;
        }
        //? if >=26.1 {
        if (!net.minecraft.world.item.Items.STICK.builtInRegistryHolder().areComponentsBound()) {
            return;
        }
        //?}

        for (Item item : net.minecraft.core.registries.BuiltInRegistries.ITEM) {
            try {
                ITEM_BUILT_COMPONENTS_FIELD.set(item, null);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Unable to invalidate Forge item component cache", e);
            }
        }
        //? if >=26.1 {
        for (Item item : net.minecraft.core.registries.BuiltInRegistries.ITEM) {
            item.builtInRegistryHolder().bindComponents(item.components());
        }
        //?}
        itemComponentCacheDirty = false;
    }
    //?}
}
