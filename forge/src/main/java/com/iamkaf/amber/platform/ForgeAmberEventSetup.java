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
import com.iamkaf.amber.api.event.v1.events.common.client.InputEvents;
import com.iamkaf.amber.api.event.v1.events.common.client.RenderEvents;
import com.iamkaf.amber.api.keymapping.KeybindHelper;
import com.iamkaf.amber.platform.services.IAmberEventSetup;
import net.minecraft.world.InteractionResult;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.listener.Priority;
import net.minecraftforge.fml.LogicalSide;

import static net.minecraft.world.InteractionResult.CONSUME;
import static net.minecraft.world.InteractionResult.SUCCESS;

public class ForgeAmberEventSetup implements IAmberEventSetup {
    @Override
    public void registerCommon() {
        LootTableLoadEvent.BUS.addListener(EventHandlerCommon::onLootTableEvent);
        PlayerInteractEvent.EntityInteract.BUS.addListener(EventHandlerCommon::onPlayerEntityInteract);
        RegisterCommandsEvent.BUS.addListener(EventHandlerCommon::onCommandRegistration);
        EntityJoinLevelEvent.BUS.addListener(EventHandlerCommon::onEntityJoinLevel);
        LivingDeathEvent.BUS.addListener(EventHandlerCommon::onLivingDeath);
        LivingAttackEvent.BUS.addListener(EventHandlerCommon::onLivingAttack);
        
        // Block events
        BlockEvent.BreakEvent.BUS.addListener(EventHandlerCommon::onBlockBreak);
        BlockEvent.EntityPlaceEvent.BUS.addListener(EventHandlerCommon::onBlockPlace);
        PlayerInteractEvent.RightClickBlock.BUS.addListener(EventHandlerCommon::onBlockInteract);
        PlayerInteractEvent.LeftClickBlock.BUS.addListener(EventHandlerCommon::onBlockClick);
    }

    @Override
    public void registerClient() {
        RegisterClientCommandsEvent.BUS.addListener(EventHandlerClient::onCommandRegistration);
        // mod bus events
        RegisterKeyMappingsEvent.getBus((BusGroup) AmberMod.getEventBus(Constants.MOD_ID))
                .addListener(EventHandlerClient::onKeybindRegistration);
        TickEvent.ClientTickEvent.Pre.BUS.addListener(EventHandlerClient::onClientTickEventPre);
        TickEvent.ClientTickEvent.Post.BUS.addListener(EventHandlerClient::onClientTickEventPost);
        
        // Input and render events
        ScreenEvent.MouseScrolled.Pre.BUS.addListener(EventHandlerClient::onMouseScrollPre);
        ScreenEvent.MouseScrolled.Post.BUS.addListener(EventHandlerClient::onMouseScrollPost);
        RenderHighlightEvent.Block.BUS.addListener(EventHandlerClient::onBlockHighlight);
    }

    @Override
    public void registerServer() {

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
            InteractionResult result = EntityEvent.ENTITY_DAMAGE.invoker().onEntityDamage(event.getEntity(), event.getSource(), event.getAmount());
            return result != InteractionResult.PASS; // Return true to cancel if not PASS
        }
        
        public static boolean onBlockBreak(BlockEvent.BreakEvent event) {
            InteractionResult result = BlockEvents.BLOCK_BREAK_BEFORE.invoker().beforeBlockBreak(
                event.getPlayer().level(), event.getPlayer(), event.getPos(), event.getState(), 
                event.getLevel().getBlockEntity(event.getPos())
            );
            if (result != InteractionResult.PASS) {
                return true; // Cancel break
            }
            
            // Fire after event (can't cancel)
            BlockEvents.BLOCK_BREAK_AFTER.invoker().afterBlockBreak(
                event.getPlayer().level(), event.getPlayer(), event.getPos(), event.getState(),
                event.getLevel().getBlockEntity(event.getPos())
            );
            return false; // Allow break
        }
        
        public static boolean onBlockPlace(BlockEvent.EntityPlaceEvent event) {
            if (!(event.getEntity() instanceof net.minecraft.world.entity.player.Player player)) {
                return false; // Only handle player placements
            }
            
            InteractionResult result = BlockEvents.BLOCK_PLACE_BEFORE.invoker().beforeBlockPlace(
                player.level(), player, event.getPos(), event.getPlacedBlock(),
                player.getMainHandItem()
            );
            if (result != InteractionResult.PASS) {
                return true; // Cancel placement
            }
            
            // Fire after event (can't cancel)
            BlockEvents.BLOCK_PLACE_AFTER.invoker().afterBlockPlace(
                player.level(), player, event.getPos(), event.getPlacedBlock(),
                player.getMainHandItem()
            );
            return false; // Allow placement
        }
        
        public static boolean onBlockInteract(PlayerInteractEvent.RightClickBlock event) {
            InteractionResult result = BlockEvents.BLOCK_INTERACT.invoker().onBlockInteract(
                event.getEntity(), event.getEntity().level(), event.getHand(), 
                event.getHitVec()
            );
            return result != InteractionResult.PASS; // Cancel if not PASS
        }
        
        public static boolean onBlockClick(PlayerInteractEvent.LeftClickBlock event) {
            InteractionResult result = BlockEvents.BLOCK_CLICK.invoker().onBlockClick(
                event.getEntity(), event.getEntity().level(), event.getHand(), 
                event.getPos(), event.getFace()
            );
            return result != InteractionResult.PASS; // Cancel if not PASS
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
        
        public static boolean onMouseScrollPre(ScreenEvent.MouseScrolled.Pre event) {
            InteractionResult result = InputEvents.MOUSE_SCROLL_PRE.invoker().onMouseScrollPre(
                event.getMouseX(), event.getMouseY(), event.getDeltaX(), event.getDeltaY()
            );
            return result != InteractionResult.PASS; // Cancel if not PASS
        }
        
        public static void onMouseScrollPost(ScreenEvent.MouseScrolled.Post event) {
            InputEvents.MOUSE_SCROLL_POST.invoker().onMouseScrollPost(
                event.getMouseX(), event.getMouseY(), event.getDeltaX(), event.getDeltaY()
            );
        }
        
        public static boolean onBlockHighlight(RenderHighlightEvent.Block event) {
            InteractionResult result = RenderEvents.BLOCK_OUTLINE_RENDER.invoker().onBlockOutlineRender(
                event.getCamera(), event.getMultiBufferSource(), event.getPoseStack(),
                event.getTarget(), event.getTarget().getBlockPos(),
                event.getCamera().getEntity().level().getBlockState(event.getTarget().getBlockPos())
            );
            return result == InteractionResult.PASS; // Only render if PASS (opposite of cancel)
        }
    }

    static public class EventHandlerServer {

    }
}
