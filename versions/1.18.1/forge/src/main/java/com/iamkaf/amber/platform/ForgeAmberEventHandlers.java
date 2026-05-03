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


import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;


import net.minecraftforge.event.world.BlockEvent;


import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.AnimalTameEvent;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import net.minecraftforge.event.entity.living.ShieldBlockEvent;


import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;

import net.minecraftforge.client.event.InputEvent;

import net.minecraftforge.client.event.DrawSelectionEvent;


import net.minecraftforge.client.event.RegisterClientCommandsEvent;


import net.minecraftforge.client.event.RenderGameOverlayEvent;


import net.minecraftforge.client.ClientRegistry;


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

        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onPlayerEntityInteractGeneral);

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

    static void registerShieldBlockEvents() {


        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerCommon::onShieldBlock);

    }

    static void registerCraftItemEvents() {


    }

    static void registerCreativeTabEvents() {


    }

    static void registerDefaultItemComponentEvents() {


    }

    static void registerClientCommandEvents() {


        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerClient::onCommandRegistration);
    }

    static void registerKeybindEvents() {


        ForgeAmberEventHandlers.EventHandlerClient.onKeybindRegistration();
    }

    static void registerClientTickEvents() {


        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerClient::onClientTickEventPre);
        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerClient::onClientTickEventPost);

    }

    static void registerRenderGuiEvents() {


        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerClient::onRenderGameOverlayPost);
    }

    static void registerMouseScrollEvents() {

        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerClient::onMouseScroll);
    }

    static void registerBlockOutlineRenderEvents() {

        MinecraftForge.EVENT_BUS.addListener(ForgeAmberEventHandlers.EventHandlerClient::onBlockOutlineRender);
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
            net.minecraft.world.level.storage.loot.LootPool[] pools = accessor.amber$getPools();
            net.minecraft.world.level.storage.loot.LootPool[] expanded = java.util.Arrays.copyOf(pools, pools.length + 1);
            expanded[pools.length] = pool;
            accessor.amber$setPools(expanded);
        }


        public static boolean onPlayerEntityInteract(PlayerInteractEvent.EntityInteractSpecific event) {
            InteractionResult result = PlayerEvents.ENTITY_INTERACT.invoker()
                    .interact(


                            event.getPlayer(),


                            event.getWorld(),
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


        public static boolean onPlayerEntityInteractGeneral(PlayerInteractEvent.EntityInteract event) {
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


        public static void onCommandRegistration(RegisterCommandsEvent event) {
            CommandEvents.EVENT.invoker()
                    .register(event.getDispatcher(),


	                            legacyBuiltinRegistryAccess(), commandSelectionAll()
	                    );
	        }


	        private static net.minecraft.core.RegistryAccess legacyBuiltinRegistryAccess() {


	            return net.minecraft.core.RegistryAccess.builtin();
	        }

	        private static net.minecraft.commands.Commands.CommandSelection commandSelectionAll() {
	            return java.lang.Enum.valueOf(net.minecraft.commands.Commands.CommandSelection.class, "ALL");
	        }


        public static void onEntityJoinLevel(


                EntityJoinWorldEvent event
        ) {
            EntityEvent.ENTITY_SPAWN.invoker().onEntitySpawn(event.getEntity(),


                    event.getWorld()
            );
        }

        public static void onLivingDeath(LivingDeathEvent event) {
            EntityEvent.ENTITY_DEATH.invoker().onEntityDeath(


                    event.getEntityLiving(),
                    event.getSource());
        }

        public static boolean onLivingAttack(LivingAttackEvent event) {
            if (


                    legacyIsClientSide(event.getEntityLiving())
            ) {
                return false;
            }

            InteractionResult result = EntityEvent.ENTITY_DAMAGE.invoker()
                    .onEntityDamage(


                            event.getEntityLiving(),
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


                    event.getWorld().getBlockEntity(event.getPos())
            );
            if (result != InteractionResult.PASS) {
                return true;
            }


            BlockEvents.BLOCK_BREAK_AFTER.invoker().afterBlockBreak(


                    event.getPlayer().level,
                    event.getPlayer(),
                    event.getPos(),
                    event.getState(),


                    event.getWorld().getBlockEntity(event.getPos())
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


                    .onBlockInteract(event.getPlayer(), event.getPlayer().level, event.getHand(), event.getHitVec());
            return result != InteractionResult.PASS;
        }

        public static boolean onBlockClick(PlayerInteractEvent.LeftClickBlock event) {
            InteractionResult result = BlockEvents.BLOCK_CLICK.invoker()
                    .onBlockClick(


                            event.getPlayer(),


                            event.getPlayer().level,
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


                    event.getEntityItem()
            );
        }

        public static void onItemPickup(EntityItemPickupEvent event) {

            if (hasPickUpDelay(event.getItem())) {
                return;
            }


            ItemEvents.ITEM_PICKUP.invoker().onItemPickup(


                    event.getPlayer(),
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


        public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
            if (event.getPlayer() instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                PlayerEvents.CRAFT_ITEM.invoker().onCraftItem(serverPlayer, java.util.List.of(event.getCrafting()));
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


                WorldEvent.Load event
        ) {


	            WorldEvents.WORLD_LOAD.invoker().onWorldLoad(


	                    legacyServer(event.getWorld()), event.getWorld()
	            );
	        }

        public static void onWorldUnload(


                WorldEvent.Unload event
        ) {
	            WorldEvents.WORLD_UNLOAD.invoker().onWorldUnload(


	                    legacyServer(event.getWorld()), event.getWorld()
	            );
	        }

        public static void onWorldSave(


                WorldEvent.Save event
        ) {
	            WorldEvents.WORLD_SAVE.invoker().onWorldSave(


	                    legacyServer(event.getWorld()), event.getWorld()
	            );
	        }


	        private static net.minecraft.server.MinecraftServer legacyServer(Object level) {
	            return ((net.minecraft.world.level.Level) level).getServer();
	        }

	        private static boolean legacyIsClientSide(net.minecraft.world.entity.Entity entity) {
	            return entity.level.isClientSide;
	        }


        public static boolean onLightningStrike(EntityStruckByLightningEvent event) {
            InteractionResult result =
                    WeatherEvents.LIGHTNING_STRIKE.invoker().onLightningStrike(event.getEntity(), event.getLightning());
            return result != InteractionResult.PASS;
        }


    }

    static public class EventHandlerClient {


        public static void onRenderGameOverlayPost(RenderGameOverlayEvent.Post event) {
            if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
                HudEvents.RENDER_HUD.invoker().onHudRender(event.getMatrixStack(), event.getPartialTicks());
            }
        }


        public static void onCommandRegistration(RegisterClientCommandsEvent event) {
            ClientCommandEvents.EVENT.invoker().register(event.getDispatcher(),


	                    EventHandlerCommon.legacyBuiltinRegistryAccess()
	            );
	        }


        public static void onKeybindRegistration(


        ) {
            KeybindHelper.forgeEventAlreadyFired = true;
            for (var keyMapping : new ArrayList<>(KeybindHelper.getKeybindings())) {


                ClientRegistry.registerKeyBinding(keyMapping);
            }
        }


        public static void onClientTickEventPre(TickEvent.ClientTickEvent event) { if (event.phase != TickEvent.Phase.START) return;
            ClientTickEvents.START_CLIENT_TICK.invoker().onStartTick();
        }


        public static void onClientTickEventPost(TickEvent.ClientTickEvent event) { if (event.phase != TickEvent.Phase.END) return;
            ClientTickEvents.END_CLIENT_TICK.invoker().onEndTick();
        }


        public static void onMouseScroll(InputEvent.MouseScrollEvent event) {
            InteractionResult result = InputEvents.MOUSE_SCROLL_PRE.invoker()
                    .onMouseScrollPre(event.getMouseX(), event.getMouseY(), 0.0, event.getScrollDelta());
            if (result != InteractionResult.PASS) {
                event.setCanceled(true);
                return;
            }
            InputEvents.MOUSE_SCROLL_POST.invoker()
                    .onMouseScrollPost(event.getMouseX(), event.getMouseY(), 0.0, event.getScrollDelta());
        }


        public static void onBlockOutlineRender(

                DrawSelectionEvent.HighlightBlock event


        ) {
            BlockPos pos = event.getTarget().getBlockPos();
            BlockState state = Minecraft.getInstance().level.getBlockState(pos);
            InteractionResult result = RenderEvents.BLOCK_OUTLINE_RENDER.invoker().onBlockOutlineRender(

                    event.getCamera(),


                    event.getMultiBufferSource(),


                    event.getPoseStack(),


                    event.getTarget(),
                    pos,
                    state
            );
            if (result != InteractionResult.PASS) {
                event.setCanceled(true);
            }
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
