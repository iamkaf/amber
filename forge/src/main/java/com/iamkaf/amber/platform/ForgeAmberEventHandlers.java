package com.iamkaf.amber.platform;

import com.iamkaf.amber.api.event.v1.events.common.*;
import com.iamkaf.amber.api.event.v1.events.common.client.ClientCommandEvents;
import com.iamkaf.amber.api.event.v1.events.common.client.ClientTickEvents;
import com.iamkaf.amber.api.event.v1.events.common.client.HudEvents;
import com.iamkaf.amber.api.event.v1.events.common.client.InputEvents;
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
import net.minecraftforge.fml.LogicalSide;
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
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.DrawSelectionEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
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
//? if <1.21.6
/*import net.minecraftforge.common.MinecraftForge;*/

import static net.minecraft.world.InteractionResult.CONSUME;
import static net.minecraft.world.InteractionResult.SUCCESS;

final class ForgeAmberEventHandlers {
    private ForgeAmberEventHandlers() {
    }

    //? if >=1.21.6 {
    static void registerModifyLootEvents() {
        LootTableLoadEvent.BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onLootTableEvent);
    }

    static void registerEntityInteractEvents() {
        PlayerInteractEvent.EntityInteractSpecific.BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onPlayerEntityInteract);
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

    static void registerCraftItemEvents() {
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

    static void registerMouseScrollEvents() {
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
        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onPlayerEntityInteract);
        //? if <1.19
        //MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onPlayerEntityInteractGeneral);
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
        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onLivingAttack);
    }

    static void registerWorldLifecycleEvents() {
        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onWorldLoad);
        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onWorldUnload);
        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onWorldSave);
    }

    static void registerLightningStrikeEvents() {
        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onLightningStrike);
    }

    static void registerBlockEvents() {
        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onBlockBreak);
        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onBlockPlace);
        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onBlockInteract);
        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onBlockClick);
    }

    static void registerAnimalEvents() {
        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onAnimalTame);
        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onAnimalBreed);
    }

    static void registerFishingEvents() {
        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onItemFished);
    }

    static void registerShieldBlockEvents() {
        //? if >=1.18.1
        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onShieldBlock);
    }

    static void registerCraftItemEvents() {
        //? if <1.18
        //MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onItemCrafted);
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
    }

    static void registerKeybindEvents() {
        //? if >=1.19
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ForgeAmberEventHandlers.EventHandlerClient::onKeybindRegistration);
        //? if <1.19
        //ForgeAmberEventHandlers.EventHandlerClient.onKeybindRegistration();
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

    static void registerMouseScrollEvents() {
        //? if <1.19
        //MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerClient::onMouseScroll);
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
        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onPlayerRespawn);
    }

    static void registerItemEvents() {
        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onItemDrop);
        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onItemPickup);
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

        public static boolean onPlayerEntityInteract(PlayerInteractEvent.EntityInteractSpecific event) {
            InteractionResult result = PlayerEvents.ENTITY_INTERACT.invoker()
                    //? if >=1.19
                    .interact(event.getEntity(), event.getLevel(), event.getHand(), event.getTarget());
                    //? if <1.19
                    /*.interact(event.getPlayer(), event.getWorld(), event.getHand(), event.getTarget());*/

            LogicalSide side = event.getSide();

            if (result.equals(InteractionResult.PASS)) {
                return false;
            }

            if (side.isClient()) {
                if (result == SUCCESS) {
                    event.setCancellationResult(SUCCESS);
                    return true;
                } else if (result == CONSUME) {
                    event.setCancellationResult(CONSUME);
                    return true;
                } else {
                    return true;
                }
            }
            return false;
        }

        //? if <1.19 {
        /*public static boolean onPlayerEntityInteractGeneral(PlayerInteractEvent.EntityInteract event) {
            InteractionResult result = PlayerEvents.ENTITY_INTERACT.invoker()
                    .interact(event.getPlayer(), event.getWorld(), event.getHand(), event.getTarget());

            LogicalSide side = event.getSide();

            if (result.equals(InteractionResult.PASS)) {
                return false;
            }

            event.setCancellationResult(result.equals(SUCCESS) ? SUCCESS : CONSUME);
            event.setCanceled(true);

            return side.isClient() && result.equals(SUCCESS);
        }
        *///?}

        public static void onCommandRegistration(RegisterCommandsEvent event) {
            CommandEvents.EVENT.invoker()
                    .register(event.getDispatcher(),
                            //? if >=1.19
                            event.getBuildContext(), event.getCommandSelection()
                            //? if <1.19
                            /*legacyBuiltinRegistryAccess(), commandSelectionAll()*/
                    );
        }

        //? if <1.19 {
        /*private static net.minecraft.core.RegistryAccess legacyBuiltinRegistryAccess() {
            //? if >=1.18.2
            return net.minecraft.core.RegistryAccess.BUILTIN.get();
            //? if <1.18.2
            //return net.minecraft.core.RegistryAccess.builtin();
        }

        private static net.minecraft.commands.Commands.CommandSelection commandSelectionAll() {
            return java.lang.Enum.valueOf(net.minecraft.commands.Commands.CommandSelection.class, "ALL");
        }
        *///?}

        //? if >=1.19 {
        public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
            EntityEvent.ENTITY_SPAWN.invoker().onEntitySpawn(event.getEntity(), event.getLevel());
        }

        public static void onLivingDeath(LivingDeathEvent event) {
            EntityEvent.ENTITY_DEATH.invoker().onEntityDeath(event.getEntity(), event.getSource());
        }
        //?} else {
        /*public static void onEntityJoinLevel(EntityJoinWorldEvent event) {
            EntityEvent.ENTITY_SPAWN.invoker().onEntitySpawn(event.getEntity(), event.getWorld());
        }

        public static void onLivingDeath(LivingDeathEvent event) {
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
            if (result != InteractionResult.PASS) {
                return true;
            }

            //? if <1.18
            /*fireLegacyShieldBlock(event);*/

            return false;
        }

        //? if <1.18.1 {
        /*private static void fireLegacyShieldBlock(LivingAttackEvent event) {
            if (!(event.getEntityLiving() instanceof net.minecraft.world.entity.player.Player player)) {
                return;
            }

            if (!legacyIsDamageSourceBlocked(player, event.getSource())) {
                return;
            }

            ItemStack shield = player.getUseItem();
            if (shield.isEmpty() || !legacyIsShieldItem(shield.getItem())) {
                return;
            }

            PlayerEvents.SHIELD_BLOCK.invoker().onShieldBlock(player, shield, event.getAmount(), event.getSource());
        }

        private static boolean legacyIsDamageSourceBlocked(net.minecraft.world.entity.player.Player player, net.minecraft.world.damagesource.DamageSource source) {
            return player.isDamageSourceBlocked(source);
        }

        private static boolean legacyIsShieldItem(Item item) {
            return item instanceof net.minecraft.world.item.ShieldItem;
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

        public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
            if (event.getEntity() instanceof net.minecraft.server.level.ServerPlayer newPlayer) {
                PlayerEvents.PLAYER_RESPAWN.invoker().onPlayerRespawn(newPlayer, newPlayer, !event.isEndConquered());
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

        public static void onBlockOutlineRender(DrawSelectionEvent.HighlightBlock event) {
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
