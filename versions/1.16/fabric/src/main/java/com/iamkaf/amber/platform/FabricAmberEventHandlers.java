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


import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;


import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;


import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;


import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;


import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;


import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.commands.CommandSourceStack;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootTable;


final class FabricAmberEventHandlers {
    private FabricAmberEventHandlers() {
    }


    private static net.minecraft.core.RegistryAccess commandRegistryAccess() {
        return net.minecraft.core.RegistryAccess.builtin();
    }


    static void registerModifyLootEvents() {


        LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, id, supplier, setter) -> {
            LootEvents.MODIFY.invoker().modify(id, supplier::pool);
        });


    }

    static void registerEntityInteractEvents() {
        UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            return PlayerEvents.ENTITY_INTERACT.invoker().interact(player, level, hand, entity);
        });
    }

    static void registerCommandEvents() {


        CommandRegistrationCallback.EVENT.register((commandDispatcher, dedicated) -> {
            CommandEvents.EVENT.invoker().register(
                    commandDispatcher,
                    commandRegistryAccess(),
                    dedicated ? net.minecraft.commands.Commands.CommandSelection.DEDICATED : net.minecraft.commands.Commands.CommandSelection.ALL
            );
        });


    }

    static void registerEntityDamageEvents() {


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


        for (var builder : CreativeModeTabRegistry.getTabBuilders().values()) {


            net.minecraft.resources.ResourceKey<net.minecraft.world.item.CreativeModeTab> tabKey = net.minecraft.resources.ResourceKey.create(
                net.minecraft.resources.ResourceKey.createRegistryKey(new net.minecraft.resources.ResourceLocation("minecraft", "creative_mode_tab")),
                builder.getId()
            );


        }
    }

    static void registerClientCommandEvents() {


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


            PlayerEvents.PLAYER_JOIN.invoker().onPlayerJoin(handler.player);
        });
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {


            PlayerEvents.PLAYER_LEAVE.invoker().onPlayerLeave(handler.player);
        });


    }

}
