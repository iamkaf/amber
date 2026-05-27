package com.iamkaf.amber.platform;

import com.iamkaf.amber.api.event.v1.events.common.*;
import com.iamkaf.amber.api.event.v1.events.common.client.ClientCommandEvents;
import com.iamkaf.amber.api.event.v1.events.common.client.ClientTickEvents;
import com.iamkaf.amber.api.event.v1.events.common.client.HudEvents;
import com.iamkaf.amber.api.event.v1.events.common.client.InputEvents;
import com.iamkaf.amber.api.event.v1.events.common.client.RenderEvents;
import com.iamkaf.amber.api.registry.v1.KeybindHelper;


import com.iamkaf.amber.mixin.LootTableAccessor;
import com.iamkaf.amber.api.event.v1.events.common.CreativeModeTabOutput;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;


import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.state.BlockState;


import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;

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

import net.minecraftforge.client.event.RenderGuiEvent;


import net.minecraftforge.client.event.RegisterKeyMappingsEvent;


import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraft.world.InteractionResult;


import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.minecraftforge.common.MinecraftForge;

import static net.minecraft.world.InteractionResult.CONSUME;
import static net.minecraft.world.InteractionResult.SUCCESS;

final class ForgeAmberEventHandlers {
    private ForgeAmberEventHandlers() {
    }

    static void registerModifyLootEvents() {


        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onLootTableEvent);

    }

    static void registerEntityInteractEvents() {


        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onPlayerEntityInteract);


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


        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onShieldBlock);

    }

    static void registerCraftItemEvents() {


    }

    static void registerCreativeTabEvents() {


        FMLJavaModLoadingContext.get().getModEventBus().addListener(ForgeAmberEventHandlers.EventHandlerCommon::registerCreativeTabsLegacy);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(ForgeAmberEventHandlers.EventHandlerCommon::buildContentsLegacy);
    }

    static void registerDefaultItemComponentEvents() {


    }

    static void registerClientCommandEvents() {


        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerClient::onCommandRegistration);
    }

    static void registerKeybindEvents() {


        FMLJavaModLoadingContext.get().getModEventBus().addListener(ForgeAmberEventHandlers.EventHandlerClient::onKeybindRegistration);


    }

    static void registerClientTickEvents() {


        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerClient::onClientTickEventPre);
        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerClient::onClientTickEventPost);

    }

    static void registerRenderGuiEvents() {

        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerClient::onRenderGuiPost);


    }

    static void registerMouseScrollEvents() {


    }

    static void registerBlockOutlineRenderEvents() {


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


    static public class EventHandlerCommon {
        public static void onLootTableEvent(LootTableLoadEvent event) {
            LootEvents.MODIFY.invoker().modify(event.getName(), lootPool -> {


                addLootPool(event.getTable(), lootPool.build());
            });
        }


        private static void addLootPool(net.minecraft.world.level.storage.loot.LootTable table, net.minecraft.world.level.storage.loot.LootPool pool) {
            LootTableAccessor accessor = (LootTableAccessor) table;
            java.util.List<net.minecraft.world.level.storage.loot.LootPool> pools = new java.util.ArrayList<>(accessor.amber$getPools());

            pools.add(pool);

            accessor.amber$setPools(pools);
        }
        public static void onItemFished(ItemFishedEvent event) {
            FishingEvents.MODIFY_CATCH.invoker().modify(
                    (net.minecraft.server.level.ServerPlayer) event.getEntity(),
                    event.getHookEntity(),
                    event.getEntity().getMainHandItem(),
                    event.getDrops()
            );
            event.setCanceled(true);
            for (ItemStack drop : event.getDrops()) {
                event.getEntity().addItem(drop.copy());
            }
        }




        public static boolean onPlayerEntityInteract(PlayerInteractEvent.EntityInteractSpecific event) {
            InteractionResult result = PlayerEvents.ENTITY_INTERACT.invoker()
                    .interact(

                            event.getEntity(),


                            event.getLevel(),


                            event.getHand(), event.getTarget());

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


        public static void onCommandRegistration(RegisterCommandsEvent event) {
            CommandEvents.EVENT.invoker()
                    .register(event.getDispatcher(),

                            event.getBuildContext(), event.getCommandSelection()


	                    );
	        }


        public static void onEntityJoinLevel(

                EntityJoinLevelEvent event


        ) {
            EntityEvent.ENTITY_SPAWN.invoker().onEntitySpawn(event.getEntity(),

                    event.getLevel()


            );
        }

        public static void onLivingDeath(LivingDeathEvent event) {
            EntityEvent.ENTITY_DEATH.invoker().onEntityDeath(

                    event.getEntity(),


                    event.getSource());
        }

        public static boolean onLivingAttack(LivingAttackEvent event) {
            if (


                    event.getEntity().level.isClientSide


            ) {
                return false;
            }

            InteractionResult result = EntityEvent.ENTITY_DAMAGE.invoker()
                    .onEntityDamage(

                            event.getEntity(),


                            event.getSource(), event.getAmount());
            if (result != InteractionResult.PASS) {
                return true;
            }


            return false;
        }


        public static boolean onBlockBreak(BlockEvent.BreakEvent event) {
            InteractionResult result = BlockEvents.BLOCK_BREAK_BEFORE.invoker().beforeBlockBreak(


                    event.getPlayer().level,
                    event.getPlayer(),
                    event.getPos(),
                    event.getState(),

                    event.getLevel().getBlockEntity(event.getPos())


            );
            if (result != InteractionResult.PASS) {
                return true;
            }


            BlockEvents.BLOCK_BREAK_AFTER.invoker().afterBlockBreak(


                    event.getPlayer().level,
                    event.getPlayer(),
                    event.getPos(),
                    event.getState(),

                    event.getLevel().getBlockEntity(event.getPos())


            );
            return false;
        }

        public static boolean onBlockPlace(BlockEvent.EntityPlaceEvent event) {
            if (!(event.getEntity() instanceof net.minecraft.world.entity.player.Player player)) {
                return false;
            }


            InteractionResult result = BlockEvents.BLOCK_PLACE.invoker()


                    .onBlockPlace(player.level, player, event.getPos(), event.getPlacedBlock(), player.getMainHandItem());
            return result != InteractionResult.PASS;
        }

        public static boolean onBlockInteract(PlayerInteractEvent.RightClickBlock event) {
            InteractionResult result = BlockEvents.BLOCK_INTERACT.invoker()


                    .onBlockInteract(event.getEntity(), event.getEntity().level, event.getHand(), event.getHitVec());


            return result != InteractionResult.PASS;
        }

        public static boolean onBlockClick(PlayerInteractEvent.LeftClickBlock event) {
            InteractionResult result = BlockEvents.BLOCK_CLICK.invoker()
                    .onBlockClick(

                            event.getEntity(),


                            event.getEntity().level,


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

                    event.getEntity()


            );
        }

        public static void onItemPickup(EntityItemPickupEvent event) {

            if (hasPickUpDelay(event.getItem())) {
                return;
            }


            ItemEvents.ITEM_PICKUP.invoker().onItemPickup(

                    event.getEntity(),


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


        public static void onShieldBlock(ShieldBlockEvent event) {
            if (event.getEntity() instanceof net.minecraft.world.entity.player.Player player) {


                net.minecraft.world.item.ItemStack shield = player.getUseItem();
                if (!shield.isEmpty()) {
                    PlayerEvents.SHIELD_BLOCK.invoker().onShieldBlock(
                        player, shield, event.getOriginalBlockedDamage(), event.getDamageSource()
                    );
                }
            }
        }


        public static void onWorldLoad(

                LevelEvent.Load event


        ) {


	            WorldEvents.WORLD_LOAD.invoker().onWorldLoad(

	                    event.getLevel().getServer(), event.getLevel()


	            );
	        }

        public static void onWorldUnload(

                LevelEvent.Unload event


        ) {
	            WorldEvents.WORLD_UNLOAD.invoker().onWorldUnload(

	                    event.getLevel().getServer(), event.getLevel()


	            );
	        }

        public static void onWorldSave(

                LevelEvent.Save event


        ) {
	            WorldEvents.WORLD_SAVE.invoker().onWorldSave(

	                    event.getLevel().getServer(), event.getLevel()


	            );
	        }


        public static boolean onLightningStrike(EntityStruckByLightningEvent event) {
            InteractionResult result =
                    WeatherEvents.LIGHTNING_STRIKE.invoker().onLightningStrike(event.getEntity(), event.getLightning());
            return result != InteractionResult.PASS;
        }


        public static void registerCreativeTabsLegacy(CreativeModeTabEvent.Register event) {
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


    }

    static public class EventHandlerClient {

        public static void onRenderGuiPost(RenderGuiEvent.Post event) {


            HudEvents.RENDER_HUD.invoker().onHudRender(event.getPoseStack(), event.getPartialTick());

        }


        public static void onCommandRegistration(RegisterClientCommandsEvent event) {
            ClientCommandEvents.EVENT.invoker().register(event.getDispatcher(),

                    event.getBuildContext()


	            );
	        }


        public static void onKeybindRegistration(

                RegisterKeyMappingsEvent event
        ) {
            KeybindHelper.forgeEventAlreadyFired = true;
            for (var keyMapping : new ArrayList<>(KeybindHelper.getKeybindings())) {

                event.register(keyMapping);


            }
        }


        public static void onClientTickEventPre(TickEvent.ClientTickEvent event) { if (event.phase != TickEvent.Phase.START) return;
            ClientTickEvents.START_CLIENT_TICK.invoker().onStartTick();
        }


        public static void onClientTickEventPost(TickEvent.ClientTickEvent event) { if (event.phase != TickEvent.Phase.END) return;
            ClientTickEvents.END_CLIENT_TICK.invoker().onEndTick();
        }


    }

    static public class EventHandlerServer {


        public static void onServerTickEventPre(TickEvent.ServerTickEvent event) { if (event.phase != TickEvent.Phase.START) return;
            ServerTickEvents.START_SERVER_TICK.invoker().onStartTick();
        }


        public static void onServerTickEventPost(TickEvent.ServerTickEvent event) { if (event.phase != TickEvent.Phase.END) return;
            ServerTickEvents.END_SERVER_TICK.invoker().onEndTick();
        }
    }


}
