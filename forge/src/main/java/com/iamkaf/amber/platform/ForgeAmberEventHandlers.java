package com.iamkaf.amber.platform;

import com.iamkaf.amber.api.event.v1.events.common.*;
import com.iamkaf.amber.api.event.v1.events.common.client.ClientCommandEvents;
import com.iamkaf.amber.api.event.v1.events.common.client.ClientTickEvents;
import com.iamkaf.amber.api.event.v1.events.common.client.HudEvents;
import com.iamkaf.amber.api.event.v1.events.common.client.RenderEvents;
import com.iamkaf.amber.api.registry.v1.KeybindHelper;
import com.iamkaf.amber.api.event.v1.events.common.CreativeModeTabOutput;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.AnimalTameEvent;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraft.world.InteractionResult;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
//? if >=1.19 {
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
//?} else {
/*import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.client.event.DrawSelectionEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
*///?}
//? if >=1.18 && <1.19
/*import net.minecraftforge.client.ClientRegistry;*/
//? if <1.18
/*import net.minecraftforge.fmlclient.registry.ClientRegistry;*/
//? if >=1.18.1 {
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
//?}
//? if >=1.19 && <1.20.6
/*import net.minecraftforge.client.event.RenderGuiEvent;*/
//? if >=1.19.3 && <1.20
/*import net.minecraftforge.event.CreativeModeTabEvent;*/
//? if >=1.20
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
//? if <1.20.1
/*import com.iamkaf.amber.mixin.LootTableAccessor;*/
//? if >=1.20.6 {
import com.iamkaf.amber.mixin.ItemAccessor;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraftforge.event.GatherComponentsEvent;
//?}
//? if >=1.21.6
import net.minecraftforge.common.util.Result;
//? if <1.21.6 {
/*import java.util.function.Predicate;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
*///?}

final class ForgeAmberEventHandlers {
    private ForgeAmberEventHandlers() {
    }

    //? if >=1.21.6 {
    static void registerModifyLootEvents() {
        LootTableLoadEvent.BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onLootTableEvent);
    }

    static void registerEntityInteractEvents() {
        //? if >=26.1
        PlayerInteractEvent.EntityInteractSpecific.BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onPlayerEntityInteract);
        //? if <26.1
        /*PlayerInteractEvent.EntityInteract.BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onPlayerEntityInteract);*/
    }

    static void registerCommandEvents() {
        RegisterCommandsEvent.BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onCommandRegistration);
    }

    static void registerEntitySpawnEvents() {
        EntityJoinLevelEvent.BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onEntityJoinLevel);
    }

    static void registerEntityDeathEvents() {
        LivingDeathEvent.BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onLivingDeath);
    }

    static void registerEntityDamageEvents() {
        LivingAttackEvent.BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onLivingAttack);
    }

    static void registerWorldLifecycleEvents() {
        LevelEvent.Load.BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onWorldLoad);
        LevelEvent.Unload.BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onWorldUnload);
        LevelEvent.Save.BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onWorldSave);
    }

    static void registerLightningStrikeEvents() {
        EntityStruckByLightningEvent.BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onLightningStrike);
    }

    static void registerBlockEvents() {
        BlockEvent.BreakEvent.BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onBlockBreak);
        BlockEvent.EntityPlaceEvent.BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onBlockPlace);
        PlayerInteractEvent.RightClickBlock.BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onBlockInteract);
        PlayerInteractEvent.LeftClickBlock.BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onBlockClick);
    }

    static void registerAnimalEvents() {
        AnimalTameEvent.BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onAnimalTame);
        BabyEntitySpawnEvent.BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onAnimalBreed);
    }

    static void registerFishingEvents() {
        ItemFishedEvent.BUS.addListener(true, ForgeAmberEventHandlers.EventHandlerCommon::onItemFished);
    }

    static void registerShieldBlockEvents() {
        ShieldBlockEvent.BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onShieldBlock);
    }

    static void registerCreativeTabEvents() {
        //? if >=1.21.10
        BuildCreativeModeTabContentsEvent.BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::buildContents);
        //? if <1.21.10
        /*BuildCreativeModeTabContentsEvent.getBus(FMLJavaModLoadingContext.get().getModBusGroup()).addListener(ForgeAmberEventHandlers.EventHandlerCommon::buildContents);*/
    }

    static void registerDefaultItemComponentEvents() {
        GatherComponentsEvent.Item.BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onGatherComponents);
        ItemEvents.setDefaultComponentListenerRegisteredHook(ForgeAmberEventHandlers::invalidateItemComponentCache);
    }

    static void registerClientCommandEvents() {
        RegisterClientCommandsEvent.BUS.addListener(ForgeAmberEventHandlers.EventHandlerClient::onCommandRegistration);
    }

    static void registerKeybindEvents() {
        //? if >=1.21.10
        RegisterKeyMappingsEvent.BUS.addListener(ForgeAmberEventHandlers.EventHandlerClient::onKeybindRegistration);
        //? if <1.21.10
        /*RegisterKeyMappingsEvent.getBus(FMLJavaModLoadingContext.get().getModBusGroup()).addListener(ForgeAmberEventHandlers.EventHandlerClient::onKeybindRegistration);*/
    }

    static void registerClientTickEvents() {
        TickEvent.ClientTickEvent.Pre.BUS.addListener(ForgeAmberEventHandlers.EventHandlerClient::onClientTickEventPre);
        TickEvent.ClientTickEvent.Post.BUS.addListener(ForgeAmberEventHandlers.EventHandlerClient::onClientTickEventPost);
    }

    static void registerRenderGuiEvents() {
    }

    static void registerBlockOutlineRenderEvents() {
    }

    static void registerServerTickEvents() {
        TickEvent.ServerTickEvent.Pre.BUS.addListener(ForgeAmberEventHandlers.EventHandlerServer::onServerTickEventPre);
        TickEvent.ServerTickEvent.Post.BUS.addListener(ForgeAmberEventHandlers.EventHandlerServer::onServerTickEventPost);
    }

    static void registerPlayerLifecycleEvents() {
        PlayerEvent.PlayerLoggedInEvent.BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onPlayerJoin);
        PlayerEvent.PlayerLoggedOutEvent.BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onPlayerLeave);
        PlayerEvent.Clone.BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onPlayerClone);
        PlayerEvent.PlayerRespawnEvent.BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onPlayerRespawn);
    }

    static void registerItemEvents() {
        ItemTossEvent.BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onItemDrop);
        EntityItemPickupEvent.BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onItemPickup);
    }
    //?} else {
    /*static void registerModifyLootEvents() {
        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onLootTableEvent);
    }

    static void registerEntityInteractEvents() {
        addCancellableListener(PlayerInteractEvent.EntityInteract.class, ForgeAmberEventHandlers.EventHandlerCommon::onPlayerEntityInteract);
    }

    static void registerCommandEvents() {
        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onCommandRegistration);
    }

    static void registerEntitySpawnEvents() {
        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onEntityJoinLevel);
    }

    static void registerEntityDeathEvents() {
        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onLivingDeath);
    }

    static void registerEntityDamageEvents() {
        addCancellableListener(LivingAttackEvent.class, ForgeAmberEventHandlers.EventHandlerCommon::onLivingAttack);
    }

    static void registerWorldLifecycleEvents() {
        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onWorldLoad);
        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onWorldUnload);
        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onWorldSave);
    }

    static void registerLightningStrikeEvents() {
        addCancellableListener(EntityStruckByLightningEvent.class, ForgeAmberEventHandlers.EventHandlerCommon::onLightningStrike);
    }

    static void registerBlockEvents() {
        addCancellableListener(BlockEvent.BreakEvent.class, ForgeAmberEventHandlers.EventHandlerCommon::onBlockBreak);
        addCancellableListener(BlockEvent.EntityPlaceEvent.class, ForgeAmberEventHandlers.EventHandlerCommon::onBlockPlace);
        addCancellableListener(PlayerInteractEvent.RightClickBlock.class, ForgeAmberEventHandlers.EventHandlerCommon::onBlockInteract);
        addCancellableListener(PlayerInteractEvent.LeftClickBlock.class, ForgeAmberEventHandlers.EventHandlerCommon::onBlockClick);
    }

    static void registerAnimalEvents() {
        addCancellableListener(AnimalTameEvent.class, ForgeAmberEventHandlers.EventHandlerCommon::onAnimalTame);
        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onAnimalBreed);
    }

    static void registerFishingEvents() {
        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onItemFished);
    }

    static void registerShieldBlockEvents() {
        //? if >=1.18.1
        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onShieldBlock);
    }

    static void registerCreativeTabEvents() {
        //? if >=1.20 {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ForgeAmberEventHandlers.EventHandlerCommon::buildContents);
        //?} else if >=1.19.3 {
        /^FMLJavaModLoadingContext.get().getModEventBus().addListener(ForgeAmberEventHandlers.EventHandlerCommon::registerCreativeTabsLegacy);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ForgeAmberEventHandlers.EventHandlerCommon::buildContentsLegacy);
        ^///?}
    }

    static void registerDefaultItemComponentEvents() {
        //? if >=1.20.6 {
        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onGatherComponents);
        ItemEvents.setDefaultComponentListenerRegisteredHook(ForgeAmberEventHandlers::invalidateItemComponentCache);
        //?}
    }

    static void registerClientCommandEvents() {
        //? if >=1.18.1
        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerClient::onCommandRegistration);
        // Forge 1.18.1 and 1.18.2 keep their command event for suggestions; these run the commands.
        //? if <1.19
        //ForgeChatClientCommands.register();
    }

    static void registerKeybindEvents() {
        //? if >=1.19
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ForgeAmberEventHandlers.EventHandlerClient::onKeybindRegistration);
        // Forge has no key mapping registration event before 1.19, so register them during client setup.
        //? if <1.19
        //FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.NORMAL, false, FMLClientSetupEvent.class, event -> event.enqueueWork(ForgeAmberEventHandlers.EventHandlerClient::onKeybindRegistration));
    }

    static void registerClientTickEvents() {
        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerClient::onClientTickEventPre);
        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerClient::onClientTickEventPost);
    }

    static void registerRenderGuiEvents() {
        //? if >=1.19 && <1.20.6
        //MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerClient::onRenderGuiPost);
        //? if <1.19
        //MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerClient::onRenderGameOverlayPost);
    }

    static void registerBlockOutlineRenderEvents() {
        //? if <1.19
        //MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerClient::onBlockOutlineRender);
    }

    static void registerServerTickEvents() {
        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerServer::onServerTickEventPre);
        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerServer::onServerTickEventPost);
    }

    static void registerPlayerLifecycleEvents() {
        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onPlayerJoin);
        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onPlayerLeave);
        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onPlayerClone);
        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onPlayerRespawn);
    }

    static void registerItemEvents() {
        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onItemDrop);
        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onItemPickup);
    }

    // Before EventBus 7, a listener's return value is ignored, so cancel the event explicitly.
    private static <T extends Event> void addCancellableListener(Class<T> type, Predicate<T> handler) {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, type, event -> {
            if (handler.test(event)) {
                event.setCanceled(true);
            }
        });
    }
    *///?}

    //? if >=1.20.6 {
    private static volatile boolean itemComponentCacheDirty;

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
            LootTableAccessor accessor = (LootTableAccessor) table;
            java.util.List<net.minecraft.world.level.storage.loot.LootPool> pools = new java.util.ArrayList<>(accessor.amber$getPools());
            pools.add(pool);
            accessor.amber$setPools(pools);
        }
        *///?}

        public static void onItemFished(ItemFishedEvent event) {
            //? if >=1.19 {
            FishingEvents.MODIFY_CATCH.invoker().modify(
                    (net.minecraft.server.level.ServerPlayer) event.getEntity(),
                    event.getHookEntity(),
                    event.getEntity().getMainHandItem(),
                    event.getDrops()
            );
            //? if <1.21.6
            /*event.setCanceled(true);*/
            for (ItemStack drop : event.getDrops()) {
                event.getEntity().addItem(drop.copy());
            }
            //?} else {
            /*FishingEvents.MODIFY_CATCH.invoker().modify(
                    (net.minecraft.server.level.ServerPlayer) event.getPlayer(),
                    event.getHookEntity(),
                    event.getPlayer().getMainHandItem(),
                    event.getDrops()
            );
            event.setCanceled(true);
            for (ItemStack drop : event.getDrops()) {
                event.getPlayer().addItem(drop.copy());
            }
            *///?}
        }

        //? if >=26.1 {
        // The client posts the interaction from its game mode and again from Player.interactOn with the same
        // location instance. Remember a passed interaction so listeners run once per click on each side.
        private static net.minecraft.world.phys.Vec3 passedClientInteractLocation;

        public static boolean onPlayerEntityInteract(PlayerInteractEvent.EntityInteractSpecific event) {
            if (event.getSide().isClient()) {
                boolean repeated = event.getLocalPos() == passedClientInteractLocation;
                passedClientInteractLocation = null;
                if (repeated) {
                    return false;
                }
            }
        //?} else {
        /*public static boolean onPlayerEntityInteract(PlayerInteractEvent.EntityInteract event) {
        *///?}
            InteractionResult result = PlayerEvents.ENTITY_INTERACT.invoker()
                    //? if >=1.19
                    .interact(event.getEntity(), event.getLevel(), event.getHand(), event.getTarget());
                    //? if <1.19
                    /*.interact(event.getPlayer(), event.getWorld(), event.getHand(), event.getTarget());*/

            if (result == InteractionResult.PASS) {
                //? if >=26.1 {
                if (event.getSide().isClient()) {
                    passedClientInteractLocation = event.getLocalPos();
                }
                //?}
                return false;
            }

            event.setCancellationResult(result);
            return true;
        }

        public static void onCommandRegistration(RegisterCommandsEvent event) {
            CommandEvents.EVENT.invoker()
                    .register(event.getDispatcher(),
                            //? if >=1.19
                            event.getBuildContext(), event.getCommandSelection()
                            //? if <1.19
                            /*legacyBuiltinRegistryAccess(), event.getEnvironment()*/
                    );
        }

        //? if <1.19 {
        /*static net.minecraft.core.RegistryAccess legacyBuiltinRegistryAccess() {
            //? if >=1.18.2
            return net.minecraft.core.RegistryAccess.BUILTIN.get();
            //? if <1.18.2
            //return net.minecraft.core.RegistryAccess.builtin();
        }
        *///?}

        //? if >=1.19 {
        public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
            EntityEvent.ENTITY_SPAWN.invoker().onEntitySpawn(event.getEntity(), event.getLevel());
        }

        public static void onLivingDeath(LivingDeathEvent event) {
            // Clients also run death for the entity event the server sends; Amber reports the server's.
            //? if >=1.20
            if (event.getEntity().level().isClientSide()) return;
            //? if <1.20
            /*if (event.getEntity().level.isClientSide) return;*/
            EntityEvent.ENTITY_DEATH.invoker().onEntityDeath(event.getEntity(), event.getSource());
        }
        //?} else {
        /*public static void onEntityJoinLevel(EntityJoinWorldEvent event) {
            EntityEvent.ENTITY_SPAWN.invoker().onEntitySpawn(event.getEntity(), event.getWorld());
        }

        public static void onLivingDeath(LivingDeathEvent event) {
            if (legacyIsClientSide(event.getEntityLiving())) return;
            EntityEvent.ENTITY_DEATH.invoker().onEntityDeath(event.getEntityLiving(), event.getSource());
        }
        *///?}

        public static boolean onLivingAttack(LivingAttackEvent event) {
            if (
                    //? if >=1.20
                    event.getEntity().level().isClientSide()
                    //? if >=1.19 && <1.20
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
            return result != InteractionResult.PASS;
        }

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
                //? if >=1.21.6 {
                // Forge checks the result, not cancellation, before breaking the block.
                event.setResult(Result.DENY);
                //?}
                return true;
            }

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
            return false;
        }

        public static boolean onBlockPlace(BlockEvent.EntityPlaceEvent event) {
            if (!(event.getEntity() instanceof net.minecraft.world.entity.player.Player player)) {
                return false;
            }

            InteractionResult result = BlockEvents.BLOCK_PLACE.invoker()
                    //? if >=1.20
                    .onBlockPlace(player.level(), player, event.getPos(), event.getPlacedBlock(), player.getMainHandItem());
                    //? if <1.20
                    /*.onBlockPlace(player.level, player, event.getPos(), event.getPlacedBlock(), player.getMainHandItem());*/
            return result != InteractionResult.PASS;
        }

        public static boolean onBlockInteract(PlayerInteractEvent.RightClickBlock event) {
            InteractionResult result = BlockEvents.BLOCK_INTERACT.invoker()
                    //? if >=1.20
                    .onBlockInteract(event.getEntity(), event.getEntity().level(), event.getHand(), event.getHitVec());
                    //? if >=1.19 && <1.20
                    /*.onBlockInteract(event.getEntity(), event.getEntity().level, event.getHand(), event.getHitVec());*/
                    //? if <1.19
                    /*.onBlockInteract(event.getPlayer(), event.getPlayer().level, event.getHand(), event.getHitVec());*/
            return result != InteractionResult.PASS;
        }

        public static boolean onBlockClick(PlayerInteractEvent.LeftClickBlock event) {
            InteractionResult result = BlockEvents.BLOCK_CLICK.invoker()
                    .onBlockClick(
                            //? if >=1.20 {
                            event.getEntity(),
                            event.getEntity().level(),
                            //?} else if >=1.19 {
                            /*event.getEntity(),
                            event.getEntity().level,
                            *///?} else {
                            /*event.getPlayer(),
                            event.getPlayer().level,
                            *///?}
                            event.getHand(),
                            event.getPos(),
                            event.getFace()
                    );
            return result != InteractionResult.PASS;
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

        // Forge's respawn event carries only the new player; the clone event just before it has the old one.
        private static final java.util.Map<net.minecraft.world.entity.player.Player, net.minecraft.server.level.ServerPlayer> RESPAWN_ORIGINALS =
                java.util.Collections.synchronizedMap(new java.util.WeakHashMap<>());

        public static void onPlayerClone(PlayerEvent.Clone event) {
            if (event.getOriginal() instanceof net.minecraft.server.level.ServerPlayer original) {
                //? if >=1.19
                RESPAWN_ORIGINALS.put(event.getEntity(), original);
                //? if <1.19
                /*RESPAWN_ORIGINALS.put(event.getPlayer(), original);*/
            }
        }

        public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
            //? if >=1.19
            net.minecraft.world.entity.player.Player player = event.getEntity();
            //? if <1.19
            /*net.minecraft.world.entity.player.Player player = event.getPlayer();*/
            net.minecraft.server.level.ServerPlayer original = RESPAWN_ORIGINALS.remove(player);
            if (player instanceof net.minecraft.server.level.ServerPlayer newPlayer) {
                PlayerEvents.PLAYER_RESPAWN.invoker().onPlayerRespawn(original != null ? original : newPlayer, newPlayer, event.isEndConquered());
            }
        }

        public static void onItemDrop(ItemTossEvent event) {
            ItemEvents.ITEM_DROP.invoker().onItemDrop(event.getPlayer(),
                    //? if >=1.19
                    event.getEntity()
                    //? if <1.19
                    /*event.getEntityItem()*/
            );
        }

        public static void onItemPickup(EntityItemPickupEvent event) {
            if (hasPickUpDelay(event.getItem())) {
                return;
            }

            ItemEvents.ITEM_PICKUP.invoker().onItemPickup(
                    //? if >=1.19
                    event.getEntity(),
                    //? if <1.19
                    /*event.getPlayer(),*/
                    event.getItem(), itemStack(event.getItem()));
        }

        private static boolean hasPickUpDelay(ItemEntity item) {
            return item.hasPickUpDelay();
        }

        private static ItemStack itemStack(ItemEntity item) {
            return item.getItem();
        }

        public static boolean onAnimalTame(AnimalTameEvent event) {
            if (event.getTamer() == null) {
                return false;
            }
            InteractionResult result = AnimalEvents.ANIMAL_TAME.invoker().onAnimalTame(event.getAnimal(), event.getTamer());
            return result != InteractionResult.PASS;
        }

        public static void onAnimalBreed(BabyEntitySpawnEvent event) {
            if (event.getParentA() instanceof net.minecraft.world.entity.animal.Animal parentA && event.getParentB() instanceof net.minecraft.world.entity.animal.Animal parentB) {
                AnimalEvents.ANIMAL_BREED.invoker().onAnimalBreed(parentA, parentB, event.getChild());
            }
        }

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

        //? if >=1.19 {
        public static void onWorldLoad(LevelEvent.Load event) {
            //? if >=1.20.6
            rebuildItemComponentCacheIfReady();
            net.minecraft.server.MinecraftServer server = event.getLevel().getServer();
            if (server == null) {
                return;
            }
            WorldEvents.WORLD_LOAD.invoker().onWorldLoad(server, event.getLevel());
        }

        public static void onWorldUnload(LevelEvent.Unload event) {
            net.minecraft.server.MinecraftServer server = event.getLevel().getServer();
            if (server == null) {
                return;
            }
            WorldEvents.WORLD_UNLOAD.invoker().onWorldUnload(server, event.getLevel());
        }

        public static void onWorldSave(LevelEvent.Save event) {
            net.minecraft.server.MinecraftServer server = event.getLevel().getServer();
            if (server == null) {
                return;
            }
            WorldEvents.WORLD_SAVE.invoker().onWorldSave(server, event.getLevel());
        }
        //?} else {
        /*public static void onWorldLoad(WorldEvent.Load event) {
            net.minecraft.server.MinecraftServer server = legacyServer(event.getWorld());
            if (server == null) {
                return;
            }
            WorldEvents.WORLD_LOAD.invoker().onWorldLoad(server, event.getWorld());
        }

        public static void onWorldUnload(WorldEvent.Unload event) {
            net.minecraft.server.MinecraftServer server = legacyServer(event.getWorld());
            if (server == null) {
                return;
            }
            WorldEvents.WORLD_UNLOAD.invoker().onWorldUnload(server, event.getWorld());
        }

        public static void onWorldSave(WorldEvent.Save event) {
            net.minecraft.server.MinecraftServer server = legacyServer(event.getWorld());
            if (server == null) {
                return;
            }
            WorldEvents.WORLD_SAVE.invoker().onWorldSave(server, event.getWorld());
        }

        private static net.minecraft.server.MinecraftServer legacyServer(Object level) {
            return ((net.minecraft.world.level.Level) level).getServer();
        }

        private static boolean legacyIsClientSide(net.minecraft.world.entity.Entity entity) {
            return entity.level.isClientSide;
        }
        *///?}

        public static boolean onLightningStrike(EntityStruckByLightningEvent event) {
            InteractionResult result =
                    WeatherEvents.LIGHTNING_STRIKE.invoker().onLightningStrike(event.getEntity(), event.getLightning());
            return result != InteractionResult.PASS;
        }

        //? if >=1.20 {
        public static void buildContents(BuildCreativeModeTabContentsEvent event) {
            //? if >=1.20.6
            rebuildItemComponentCacheIfReady();
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
        //?} else if >=1.19.3 {
        /*public static void registerCreativeTabsLegacy(CreativeModeTabEvent.Register event) {
            for (var builder : com.iamkaf.amber.api.registry.v1.creativetabs.CreativeModeTabRegistry.getTabBuilders().values()) {
                event.registerCreativeModeTab(builder.getId(), builder::applyTo);
            }
        }

        public static void buildContentsLegacy(CreativeModeTabEvent.BuildContents event) {
            net.minecraft.resources.Identifier tabId = net.minecraftforge.common.CreativeModeTabRegistry.getName(event.getTab());
            if (tabId == null) {
                return;
            }
            net.minecraft.resources.ResourceKey<net.minecraft.core.Registry<net.minecraft.world.item.CreativeModeTab>> registryKey =
                    net.minecraft.resources.ResourceKey.createRegistryKey(new net.minecraft.resources.Identifier("minecraft", "creative_mode_tab"));
            net.minecraft.resources.ResourceKey<net.minecraft.world.item.CreativeModeTab> tabKey =
                    net.minecraft.resources.ResourceKey.create(registryKey, tabId);
            com.iamkaf.amber.api.registry.v1.creativetabs.TabBuilder tabBuilder =
                    com.iamkaf.amber.api.registry.v1.creativetabs.CreativeModeTabRegistry.getTabBuilder(tabId);
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
        }
        *///?}

        //? if >=1.20.6 {
        public static void onGatherComponents(GatherComponentsEvent.Item event) {
            ItemEvents.MODIFY_DEFAULT_COMPONENTS.invoker().modify(
                new ForgeComponentModificationContext(event)
            );
        }
        //?}
    }

    static public class EventHandlerClient {
        //? if >=1.19 && <1.20.6 {
        /*public static void onRenderGuiPost(RenderGuiEvent.Post event) {
            //? if >=1.20
            HudEvents.RENDER_HUD.invoker().onHudRender(event.getGuiGraphics(), event.getPartialTick());
            //? if <1.20
            //HudEvents.RENDER_HUD.invoker().onHudRender(event.getPoseStack(), event.getPartialTick());
        }
        *///?} else if <1.19 {
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
                    //? if <1.19
                    /*EventHandlerCommon.legacyBuiltinRegistryAccess()*/
            );
        }
        //?}

        //? if >=1.19 {
        public static void onKeybindRegistration(RegisterKeyMappingsEvent event) {
            KeybindHelper.forgeEventAlreadyFired = true;
            for (var keyMapping : new ArrayList<>(KeybindHelper.getKeybindings())) {
                event.register(keyMapping);
            }
        }
        //?} else {
        /*public static void onKeybindRegistration() {
            KeybindHelper.forgeEventAlreadyFired = true;
            for (var keyMapping : new ArrayList<>(KeybindHelper.getKeybindings())) {
                ClientRegistry.registerKeyBinding(keyMapping);
            }
        }
        *///?}

        //? if >=1.20.4 {
        public static void onClientTickEventPre(TickEvent.ClientTickEvent.Pre pre) {
            ClientTickEvents.START_CLIENT_TICK.invoker().onStartTick();
        }

        public static void onClientTickEventPost(TickEvent.ClientTickEvent.Post post) {
            ClientTickEvents.END_CLIENT_TICK.invoker().onEndTick();
        }
        //?} else {
        /*public static void onClientTickEventPre(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.START) return;
            ClientTickEvents.START_CLIENT_TICK.invoker().onStartTick();
        }

        public static void onClientTickEventPost(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;
            ClientTickEvents.END_CLIENT_TICK.invoker().onEndTick();
        }
        *///?}

        //? if <1.19 {
        /*public static void onBlockOutlineRender(DrawSelectionEvent.HighlightBlock event) {
            BlockPos pos = event.getTarget().getBlockPos();
            BlockState state = Minecraft.getInstance().level.getBlockState(pos);
            InteractionResult result = RenderEvents.BLOCK_OUTLINE_RENDER.invoker().onBlockOutlineRender(
                    //? if >=1.18 {
                    event.getCamera(),
                    event.getMultiBufferSource(),
                    event.getPoseStack(),
                    //?} else {
                    /^event.getInfo(),
                    event.getBuffers(),
                    event.getMatrix(),
                    ^///?}
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
        //? if >=1.20.4 {
        public static void onServerTickEventPre(TickEvent.ServerTickEvent.Pre pre) {
            ServerTickEvents.START_SERVER_TICK.invoker().onStartTick();
        }

        public static void onServerTickEventPost(TickEvent.ServerTickEvent.Post post) {
            ServerTickEvents.END_SERVER_TICK.invoker().onEndTick();
        }
        //?} else {
        /*public static void onServerTickEventPre(TickEvent.ServerTickEvent event) {
            if (event.phase != TickEvent.Phase.START) return;
            ServerTickEvents.START_SERVER_TICK.invoker().onStartTick();
        }

        public static void onServerTickEventPost(TickEvent.ServerTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;
            ServerTickEvents.END_SERVER_TICK.invoker().onEndTick();
        }
        *///?}
    }

    //? if >=1.20.6 {
    static void invalidateItemComponentCache() {
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
            ((ItemAccessor) item).amber$setBuiltComponents(null);
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
