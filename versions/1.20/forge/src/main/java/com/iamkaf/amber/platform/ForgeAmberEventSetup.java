package com.iamkaf.amber.platform;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.Constants;
import com.iamkaf.amber.api.event.v1.events.common.CommandEvents;
import com.iamkaf.amber.api.event.v1.events.common.LootEvents;
import com.iamkaf.amber.api.event.v1.events.common.PlayerEvents;
import com.iamkaf.amber.api.event.v1.events.common.client.ClientCommandEvents;
import com.iamkaf.amber.api.keymapping.KeybindHelper;
import com.iamkaf.amber.platform.services.IAmberEventSetup;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.LogicalSide;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.List;

import static net.minecraft.world.InteractionResult.CONSUME;
import static net.minecraft.world.InteractionResult.SUCCESS;

public class ForgeAmberEventSetup implements IAmberEventSetup {
    @Override
    public void registerCommon() {
        MinecraftForge.EVENT_BUS.addListener(EventHandlerCommon::onLootTableEvent);
        MinecraftForge.EVENT_BUS.addListener(EventHandlerCommon::onPlayerEntityInteract);
        MinecraftForge.EVENT_BUS.addListener(EventHandlerCommon::onCommandRegistration);
    }

    @Override
    public void registerClient() {
        MinecraftForge.EVENT_BUS.addListener(EventHandlerClient::onCommandRegistration);
        // mod bus events
        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventHandlerClient::onKeybindRegistration);
    }

    @Override
    public void registerServer() {

    }

    static public class EventHandlerCommon {
        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onLootTableEvent(LootTableLoadEvent event) {
            LootEvents.MODIFY.invoker().modify(event.getName(), lootPool -> event.setTable(withAdditionalPool(event.getTable(), lootPool.build())));
        }

        @SubscribeEvent(priority = EventPriority.HIGH)
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

        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onCommandRegistration(RegisterCommandsEvent event) {
            CommandEvents.EVENT.invoker()
                    .register(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
        }
    }

    static public class EventHandlerClient {
        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onCommandRegistration(RegisterClientCommandsEvent event) {
            ClientCommandEvents.EVENT.invoker().register(event.getDispatcher(), event.getBuildContext());
        }

        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onKeybindRegistration(RegisterKeyMappingsEvent event) {
            Constants.LOG.info("Registering Amber keybindings for Forge...");
            for (var keyMapping : KeybindHelper.getKeybindings()) {
                event.register(keyMapping);
            }
            KeybindHelper.forgeEventAlreadyFired = true;
        }
    }

    static public class EventHandlerServer {

    }

    private static LootTable withAdditionalPool(LootTable original, LootPool additionalPool) {
        try {
            LootTable.Builder builder = LootTable.lootTable().setParamSet(original.getParamSet());

            Field randomSequenceField = LootTable.class.getDeclaredField("randomSequence");
            randomSequenceField.setAccessible(true);
            Object randomSequence = randomSequenceField.get(original);
            if (randomSequence != null) {
                builder.setRandomSequence((net.minecraft.resources.ResourceLocation) randomSequence);
            }

            Field builderPoolsField = LootTable.Builder.class.getDeclaredField("pools");
            builderPoolsField.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<LootPool> builderPools = (List<LootPool>) builderPoolsField.get(builder);

            Field originalPoolsField = LootTable.class.getDeclaredField("pools");
            originalPoolsField.setAccessible(true);
            copyFieldContents(builderPools, originalPoolsField.get(original), LootPool.class);
            builderPools.add(additionalPool);

            Field builderFunctionsField = LootTable.Builder.class.getDeclaredField("functions");
            builderFunctionsField.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<LootItemFunction> builderFunctions = (List<LootItemFunction>) builderFunctionsField.get(builder);

            Field originalFunctionsField = LootTable.class.getDeclaredField("functions");
            originalFunctionsField.setAccessible(true);
            copyFieldContents(builderFunctions, originalFunctionsField.get(original), LootItemFunction.class);

            return builder.build();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to append loot pool on Forge 1.20", e);
        }
    }

    private static <T> void copyFieldContents(List<T> target, Object source, Class<T> type) {
        if (source == null) {
            return;
        }
        if (source instanceof List<?> list) {
            for (Object element : list) {
                target.add(type.cast(element));
            }
            return;
        }
        if (source.getClass().isArray()) {
            int length = Array.getLength(source);
            for (int i = 0; i < length; i++) {
                target.add(type.cast(Array.get(source, i)));
            }
            return;
        }
        throw new IllegalStateException("Unsupported loot field container: " + source.getClass().getName());
    }
}
