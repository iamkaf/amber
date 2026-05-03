package com.iamkaf.amber.platform;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.Constants;
import com.iamkaf.amber.api.event.v1.events.common.*;
import com.iamkaf.amber.api.event.v1.events.common.client.ClientCommandEvents;
import com.iamkaf.amber.api.event.v1.events.common.client.ClientTickEvents;
import com.iamkaf.amber.api.event.v1.events.common.client.HudEvents;
import com.iamkaf.amber.api.registry.v1.KeybindHelper;
import com.iamkaf.amber.api.event.v1.events.common.CreativeModeTabOutput;
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
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityStruckByLightningEvent;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.living.AnimalTameEvent;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;


import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;

import static net.minecraft.world.InteractionResult.CONSUME;
import static net.minecraft.world.InteractionResult.SUCCESS;

final class NeoForgeAmberEventHandlers {


    private static class NeoForgeComponentModificationContext implements ItemEvents.ComponentModificationContext {
        private final ModifyDefaultComponentsEvent event;

        NeoForgeComponentModificationContext(ModifyDefaultComponentsEvent event) {
            this.event = event;
        }

        @Override
        @SuppressWarnings({"unchecked", "rawtypes"})
        public void modify(Item item, java.util.function.Consumer<DataComponentMap.Builder> builderConsumer) {
            event.modify(item, patchBuilder -> {
                DataComponentMap.Builder tempBuilder = DataComponentMap.builder();
                builderConsumer.accept(tempBuilder);

                DataComponentMap modifiedComponents = tempBuilder.build();
                for (TypedDataComponent component : modifiedComponents) {
                    patchBuilder.set(component.type(), component.value());
                }
            });
        }
    }

    private static IEventBus modBus() {
        IEventBus bus = (IEventBus) AmberMod.getEventBus(Constants.MOD_ID);
        assert bus != null;
        return bus;
    }

    static void registerModifyLootEvents() {
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, EventHandlerCommonNeoForge::onLootTableEvent);
    }

    static void registerEntityInteractEvents() {
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, EventHandlerCommonNeoForge::onPlayerEntityInteract);
    }

    static void registerCommandEvents() {
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, EventHandlerCommonNeoForge::onCommandRegistration);
    }

    static void registerEntitySpawnEvents() {
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, EventHandlerCommonNeoForge::onEntityJoinLevel);
    }

    static void registerEntityDeathEvents() {
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, EventHandlerCommonNeoForge::onLivingDeath);
    }

    static void registerEntityDamageEvents() {
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, EventHandlerCommonNeoForge::onLivingIncomingDamage);
    }

    static void registerWorldLifecycleEvents() {
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, EventHandlerCommonNeoForge::onWorldLoad);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, EventHandlerCommonNeoForge::onWorldUnload);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, EventHandlerCommonNeoForge::onWorldSave);
    }

    static void registerLightningStrikeEvents() {
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, EventHandlerCommonNeoForge::onLightningStrike);
    }

    static void registerBlockEvents() {
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, EventHandlerCommonNeoForge::onBlockBreak);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, EventHandlerCommonNeoForge::onBlockPlace);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, EventHandlerCommonNeoForge::onBlockInteract);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, EventHandlerCommonNeoForge::onBlockClick);
    }

    static void registerAnimalEvents() {
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, EventHandlerCommonNeoForge::onAnimalTame);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, EventHandlerCommonNeoForge::onAnimalBreed);
    }

    static void registerShieldBlockEvents() {
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, EventHandlerCommonNeoForge::onShieldBlock);
    }

    static void registerCreativeTabEvents() {
        modBus().addListener(EventPriority.HIGH, ModBusEventHandler::buildContents);
    }

    static void registerDefaultItemComponentEvents() {
        modBus().addListener(EventPriority.HIGH, ModBusEventHandler::onModifyDefaultComponents);
    }

    static void registerClientCommandEvents() {
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, EventHandlerClientNeoForge::onCommandRegistration);
    }

    static void registerRenderGuiEvents() {
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, EventHandlerClientNeoForge::eventRenderGameOverlayEvent);
    }

    static void registerClientTickEvents() {
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, EventHandlerClientNeoForge::onClientTickEventPre);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, EventHandlerClientNeoForge::onClientTickEventPost);
    }

    static void registerKeybindEvents() {
        modBus().addListener(EventPriority.HIGH, EventHandlerClientMod::onKeyMappingRegistration);
    }


    static void registerServerTickEvents() {
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, EventHandlerServer::onServerTickEventPre);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, EventHandlerServer::onServerTickEventPost);
    }

    static void registerPlayerLifecycleEvents() {
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, EventHandlerServer::onPlayerJoin);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, EventHandlerServer::onPlayerLeave);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, EventHandlerServer::onPlayerRespawn);
    }

    static void registerItemEvents() {
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, EventHandlerServer::onItemDrop);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, EventHandlerServer::onItemPickup);
    }

    static public class EventHandlerCommonNeoForge {
        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onLootTableEvent(LootTableLoadEvent event) {
            LootEvents.MODIFY.invoker().modify(event.getName(), lootPool -> event.getTable().addPool(lootPool.build()));
        }

        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onPlayerEntityInteract(PlayerInteractEvent.EntityInteract event) {
            InteractionResult result = PlayerEvents.ENTITY_INTERACT.invoker()
                    .interact(event.getEntity(), event.getLevel(), event.getHand(), event.getTarget());

            LogicalSide side = event.getSide();

            if (result.equals(InteractionResult.PASS)) {
                return;
            }


            if (side.isClient()) {
                if (result == SUCCESS) {
                    event.setCancellationResult(SUCCESS);
                    event.setCanceled(true);
                } else if (result == CONSUME) {
                    event.setCancellationResult(CONSUME);
                    event.setCanceled(true);
                } else {

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
                event.setCanceled(true);
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
                event.setCanceled(true);
            }
        }

        @SubscribeEvent(priority = EventPriority.HIGH)


        public static void onBlockBreak(BlockEvent.BreakEvent event) {
            if (event.getPlayer().level().isClientSide()) {
                return;
            }

            InteractionResult result = BlockEvents.BLOCK_BREAK_BEFORE.invoker().beforeBlockBreak(
                    event.getPlayer().level(),
                    event.getPlayer(),
                    event.getPos(),
                    event.getState(),
                    event.getLevel().getBlockEntity(event.getPos())
            );
            if (result != InteractionResult.PASS) {
                event.setCanceled(true);


                return;
            }


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
                return;
            }


            InteractionResult result = BlockEvents.BLOCK_PLACE.invoker()
                    .onBlockPlace(player.level(), player, event.getPos(), event.getPlacedBlock(), player.getMainHandItem());
            if (result != InteractionResult.PASS) {
                event.setCanceled(true);
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
                event.setCanceled(true);
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
                event.setCanceled(true);
            }
        }


        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onAnimalTame(AnimalTameEvent event) {
            if (event.getTamer() == null) {
                return;
            }
            InteractionResult result = AnimalEvents.ANIMAL_TAME.invoker().onAnimalTame(event.getAnimal(), event.getTamer());
            if (result != InteractionResult.PASS) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onAnimalBreed(BabyEntitySpawnEvent event) {
            if (event.getParentA() instanceof net.minecraft.world.entity.animal.Animal parentA && event.getParentB() instanceof net.minecraft.world.entity.animal.Animal parentB) {
                AnimalEvents.ANIMAL_BREED.invoker().onAnimalBreed(parentA, parentB, event.getChild());
            }
        }

        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onShieldBlock(LivingShieldBlockEvent event) {
            if (event.getEntity() instanceof net.minecraft.world.entity.player.Player player && event.getBlocked()) {
                net.minecraft.world.item.ItemStack shield = player.getUseItem();
                if (!shield.isEmpty()) {
                    PlayerEvents.SHIELD_BLOCK.invoker().onShieldBlock(
                        player, shield, event.getOriginalBlockedDamage(), event.getDamageSource()
                    );
                }
            }
        }
    }


    static public class ModBusEventHandler {


        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void buildContents(BuildCreativeModeTabContentsEvent event) {

            com.iamkaf.amber.api.registry.v1.creativetabs.TabBuilder tabBuilder =


                com.iamkaf.amber.api.registry.v1.creativetabs.CreativeModeTabRegistry.getTabBuilder(event.getTabKey().location());
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


        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onModifyDefaultComponents(ModifyDefaultComponentsEvent event) {
            ItemEvents.MODIFY_DEFAULT_COMPONENTS.invoker().modify(
                new NeoForgeComponentModificationContext(event)
            );
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


    static public class EventHandlerClientMod {
        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onKeyMappingRegistration(RegisterKeyMappingsEvent event) {
            KeybindHelper.forgeEventAlreadyFired = true;
            for (var keyMapping : new ArrayList<>(KeybindHelper.getKeybindings())) {
                event.register(keyMapping);
            }
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


                PlayerEvents.PLAYER_RESPAWN.invoker().onPlayerRespawn(newPlayer, newPlayer, !event.isEndConquered());
            }
        }

        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onItemDrop(ItemTossEvent event) {

            ItemEvents.ITEM_DROP.invoker().onItemDrop(event.getPlayer(), event.getEntity());
        }

        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onItemPickup(ItemEntityPickupEvent.Pre event) {

            if (event.getItemEntity().hasPickUpDelay()) {
                return;
            }


            ItemEvents.ITEM_PICKUP.invoker()
                    .onItemPickup(event.getPlayer(), event.getItemEntity(), event.getItemEntity().getItem());
        }
    }
}
