package com.iamkaf.amber.platform;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.Constants;
import com.iamkaf.amber.api.event.v1.events.common.BlockEvents;
import com.iamkaf.amber.api.event.v1.events.common.CommandEvents;
import com.iamkaf.amber.api.event.v1.events.common.EntityEvent;
import com.iamkaf.amber.api.event.v1.events.common.LootEvents;
import com.iamkaf.amber.api.event.v1.events.common.PlayerEvents;
import com.iamkaf.amber.api.event.v1.events.common.client.ClientCommandEvents;
import com.iamkaf.amber.api.event.v1.events.common.client.ClientTickEvents;
import com.iamkaf.amber.api.event.v1.events.common.client.HudEvents;
import com.iamkaf.amber.api.event.v1.events.common.client.InputEvents;
import com.iamkaf.amber.api.event.v1.events.common.client.RenderEvents;
import com.iamkaf.amber.api.keymapping.KeybindHelper;
import com.iamkaf.amber.platform.services.IAmberEventSetup;
import net.minecraft.world.InteractionResult;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

import static net.minecraft.world.InteractionResult.CONSUME;
import static net.minecraft.world.InteractionResult.SUCCESS;

public class NeoForgeAmberEventSetup implements IAmberEventSetup {
    @Override
    public void registerCommon() {
        NeoForge.EVENT_BUS.register(EventHandlerCommonNeoForge.class);
//        IEventBus bus = (IEventBus) AmberMod.getEventBus(Constants.MOD_ID);
//        assert bus != null;
//        bus.register(EventHandlerCommonNeoForge.EventHandlerCommonMod.class);
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
//        NeoForge.EVENT_BUS.register(EventHandlerServer.class);
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
            InteractionResult result = EntityEvent.ENTITY_DAMAGE.invoker().onEntityDamage(event.getEntity(), event.getSource(), event.getAmount());
            if (result != InteractionResult.PASS) {
                event.setCanceled(true); // Cancel damage if not PASS
            }
        }
        
        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onBlockBreak(BlockEvent.BreakEvent event) {
            InteractionResult result = BlockEvents.BLOCK_BREAK_BEFORE.invoker().beforeBlockBreak(
                event.getLevel(), event.getPlayer(), event.getPos(), event.getState(), 
                event.getLevel().getBlockEntity(event.getPos())
            );
            if (result != InteractionResult.PASS) {
                event.setCanceled(true); // Cancel break
                return;
            }
            
            // Fire after event (can't cancel)
            BlockEvents.BLOCK_BREAK_AFTER.invoker().afterBlockBreak(
                event.getLevel(), event.getPlayer(), event.getPos(), event.getState(),
                event.getLevel().getBlockEntity(event.getPos())
            );
        }
        
        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
            if (!(event.getEntity() instanceof net.minecraft.world.entity.player.Player player)) {
                return; // Only handle player placements
            }
            
            InteractionResult result = BlockEvents.BLOCK_PLACE_BEFORE.invoker().beforeBlockPlace(
                event.getLevel(), player, event.getPos(), event.getPlacedBlock(),
                player.getMainHandItem()
            );
            if (result != InteractionResult.PASS) {
                event.setCanceled(true); // Cancel placement
                return;
            }
            
            // Fire after event (can't cancel)
            BlockEvents.BLOCK_PLACE_AFTER.invoker().afterBlockPlace(
                event.getLevel(), player, event.getPos(), event.getPlacedBlock(),
                player.getMainHandItem()
            );
        }
        
        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onBlockInteract(PlayerInteractEvent.RightClickBlock event) {
            InteractionResult result = BlockEvents.BLOCK_INTERACT.invoker().onBlockInteract(
                event.getEntity(), event.getLevel(), event.getHand(), 
                new net.minecraft.world.phys.BlockHitResult(
                    event.getHitVec(), event.getFace(), event.getPos(), false
                )
            );
            if (result != InteractionResult.PASS) {
                event.setCanceled(true); // Cancel if not PASS
            }
        }
        
        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onBlockClick(PlayerInteractEvent.LeftClickBlock event) {
            InteractionResult result = BlockEvents.BLOCK_CLICK.invoker().onBlockClick(
                event.getEntity(), event.getLevel(), event.getHand(), 
                event.getPos(), event.getFace()
            );
            if (result != InteractionResult.PASS) {
                event.setCanceled(true); // Cancel if not PASS
            }
        }

        static public class EventHandlerCommonMod {
            // TODO: Implement mod-specific event handling if needed
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
        
        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
            InteractionResult result = InputEvents.MOUSE_SCROLL.invoker().onMouseScroll(
                event.getMouseX(), event.getMouseY(), event.getScrollDeltaX(), event.getScrollDeltaY()
            );
            if (result != InteractionResult.PASS) {
                event.setCanceled(true); // Cancel if not PASS
            }
        }
        
        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onBlockHighlight(RenderHighlightEvent.Block event) {
            InteractionResult result = RenderEvents.BLOCK_OUTLINE_RENDER.invoker().onBlockOutlineRender(
                event.getCamera(), event.getMultiBufferSource(), event.getPoseStack(),
                event.getTarget(), event.getTarget().getBlockPos(),
                event.getCamera().getEntity().level().getBlockState(event.getTarget().getBlockPos())
            );
            if (result != InteractionResult.PASS) {
                event.setCanceled(true); // Cancel rendering if not PASS
            }
        }
    }

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

    }
}
