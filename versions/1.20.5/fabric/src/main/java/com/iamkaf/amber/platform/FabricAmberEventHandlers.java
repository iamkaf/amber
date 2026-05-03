package com.iamkaf.amber.platform;

import com.iamkaf.amber.api.event.v1.events.common.*;
import com.iamkaf.amber.api.event.v1.events.common.client.ClientCommandEvents;
import com.iamkaf.amber.api.event.v1.events.common.client.HudEvents;
import com.iamkaf.amber.api.registry.v1.creativetabs.CreativeModeTabRegistry;
import com.iamkaf.amber.api.event.v1.events.common.CreativeModeTabEvents;
import com.iamkaf.amber.api.event.v1.events.common.CreativeModeTabOutput;
import com.iamkaf.amber.Constants;


import com.iamkaf.amber.platform.services.IAmberEventSetup;
import com.mojang.brigadier.CommandDispatcher;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;


import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;


import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;


import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;


import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;


import net.fabricmc.fabric.api.loot.v2.LootTableEvents;

import net.fabricmc.fabric.api.loot.v2.LootTableSource;


import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.commands.CommandSourceStack;

import net.minecraft.core.HolderLookup;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootTable;


import java.util.function.Consumer;

final class FabricAmberEventHandlers {
    private FabricAmberEventHandlers() {
    }


    static void registerModifyLootEvents() {

        LootTableEvents.MODIFY.register((ResourceKey<LootTable> resourceKey, LootTable.Builder builder,
                LootTableSource lootTableSource


        ) -> {


            LootEvents.MODIFY.invoker().modify(resourceKey.location(), builder::withPool);
        });


    }

    static void registerEntityInteractEvents() {
        UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            return PlayerEvents.ENTITY_INTERACT.invoker().interact(player, level, hand, entity);
        });
    }

    static void registerCommandEvents() {

        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandBuildContext, commandSelection) -> {
            CommandEvents.EVENT.invoker().register(commandDispatcher, commandBuildContext, commandSelection);
        });


    }

    static void registerEntityDamageEvents() {


        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            EntityEvent.ENTITY_DEATH.invoker().onEntityDeath(entity, source);
        });
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            InteractionResult result = EntityEvent.ENTITY_DAMAGE.invoker().onEntityDamage(entity, source, amount);
            return result == InteractionResult.PASS;
        });


    }

    static void registerBlockBreakEvents() {

        PlayerBlockBreakEvents.BEFORE.register((level, player, pos, state, blockEntity) -> {
            InteractionResult result =
                    BlockEvents.BLOCK_BREAK_BEFORE.invoker().beforeBlockBreak(level, player, pos, state, blockEntity);
            return result == InteractionResult.PASS;
        });
        PlayerBlockBreakEvents.AFTER.register((level, player, pos, state, blockEntity) -> {
            BlockEvents.BLOCK_BREAK_AFTER.invoker().afterBlockBreak(level, player, pos, state, blockEntity);
        });
    }

    static void registerBlockInteractionEvents() {
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            return BlockEvents.BLOCK_INTERACT.invoker().onBlockInteract(player, level, hand, hitResult);
        });
        AttackBlockCallback.EVENT.register((player, level, hand, pos, direction) -> {
            return BlockEvents.BLOCK_CLICK.invoker().onBlockClick(player, level, hand, pos, direction);
        });


    }

    static void registerDefaultItemComponentEvents() {


    }

    static void registerCreativeTabEvents() {


        for (var tabKey : net.minecraft.core.registries.BuiltInRegistries.CREATIVE_MODE_TAB.registryKeySet()) {


            net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents.modifyEntriesEvent(tabKey).register((tab) -> {
                CreativeModeTabEvents.MODIFY_ENTRIES.invoker().modifyEntries(tabKey, new CreativeModeTabOutput() {
                    @Override
                    public void accept(net.minecraft.world.item.ItemStack stack, CreativeModeTabOutput.TabVisibility visibility) {
                        tab.accept(stack);
                    }
                });
            });

        }


        for (var builder : CreativeModeTabRegistry.getTabBuilders().values()) {

            net.minecraft.resources.ResourceKey<net.minecraft.world.item.CreativeModeTab> tabKey = net.minecraft.resources.ResourceKey.create(
                net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB,
                builder.getId()
            );


            net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents.modifyEntriesEvent(tabKey).register((tab) -> {
                for (var itemSupplier : builder.getItems()) {
                    tab.accept(itemSupplier.get());
                }

                CreativeModeTabEvents.MODIFY_ENTRIES.invoker().modifyEntries(tabKey, new CreativeModeTabOutput() {
                    @Override
                    public void accept(net.minecraft.world.item.ItemStack stack, CreativeModeTabOutput.TabVisibility visibility) {
                        tab.accept(stack);
                    }
                });
            });


        }
    }

    static void registerClientCommandEvents() {

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            CommandDispatcher<CommandSourceStack> commandsTemp = new CommandDispatcher<>();
            ClientCommandEvents.EVENT.invoker().register(commandsTemp, registryAccess);
        });


    }

    static void registerRenderHudEvents() {


        HudRenderCallback.EVENT.register((guiGraphics, tickDelta) -> {
            HudEvents.RENDER_HUD.invoker().onHudRender(guiGraphics, tickDelta);
        });


    }

    static void registerStartClientTickEvents() {
        ClientTickEvents.START_CLIENT_TICK.register(minecraft -> {
            com.iamkaf.amber.api.event.v1.events.common.client.ClientTickEvents.START_CLIENT_TICK.invoker().onStartTick();
        });
    }

    static void registerEndClientTickEvents() {
        ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
            com.iamkaf.amber.api.event.v1.events.common.client.ClientTickEvents.END_CLIENT_TICK.invoker().onEndTick();
        });
    }


    static void registerStartServerTickEvents() {
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            com.iamkaf.amber.api.event.v1.events.common.ServerTickEvents.START_SERVER_TICK.invoker().onStartTick();
        });
    }

    static void registerEndServerTickEvents() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            com.iamkaf.amber.api.event.v1.events.common.ServerTickEvents.END_SERVER_TICK.invoker().onEndTick();
        });

    }

    static void registerWorldLifecycleEvents() {


        ServerWorldEvents.LOAD.register((server, world) -> {
            WorldEvents.WORLD_LOAD.invoker().onWorldLoad(server, world);
        });
        ServerWorldEvents.UNLOAD.register((server, world) -> {
            WorldEvents.WORLD_UNLOAD.invoker().onWorldUnload(server, world);
        });


    }

    static void registerPlayerLifecycleEvents() {

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {

            PlayerEvents.PLAYER_JOIN.invoker().onPlayerJoin(handler.getPlayer());


        });
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {

            PlayerEvents.PLAYER_LEAVE.invoker().onPlayerLeave(handler.getPlayer());


        });

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            PlayerEvents.PLAYER_RESPAWN.invoker().onPlayerRespawn(oldPlayer, newPlayer, alive);
        });

    }

}
