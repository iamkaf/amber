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
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootTable;
//? if >=1.19 {
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
//?} else {
/*import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
*///?}
//? if >=1.19.2
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
//? if >=1.19.3 && <1.20 {
/*import net.fabricmc.fabric.api.itemgroup.v1.IdentifiableItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
*///?}
//? if >=1.21 {
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.minecraft.core.HolderLookup;
//?} else if >=1.18.2 {
/*import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableSource;
*///?} else {
/*import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
*///?}
//? if >=1.20.6 {
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.core.component.DataComponentMap;

import java.util.function.Consumer;
//?}
//? if >=26.1 {
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
//?} else {
/*import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
*///?}

final class FabricAmberEventHandlers {
    private FabricAmberEventHandlers() {
    }

    //? if >=1.20.6 {
    private static class FabricComponentModificationContext implements ItemEvents.ComponentModificationContext {
        private final DefaultItemComponentEvents.ModifyContext modifyContext;

        FabricComponentModificationContext(DefaultItemComponentEvents.ModifyContext modifyContext) {
            this.modifyContext = modifyContext;
        }

        @Override
        public void modify(Item item, Consumer<DataComponentMap.Builder> builderConsumer) {
            java.util.function.Predicate<Item> itemPredicate = testItem -> testItem == item;
            modifyContext.modify(itemPredicate, (builder, actualItem) -> {
                if (actualItem == item) {
                    builderConsumer.accept(builder);
                }
            });
        }
    }
    //?}

    //? if <1.19 {
    /*private static net.minecraft.core.RegistryAccess commandRegistryAccess() {
        //? if >=1.18.2
        return net.minecraft.core.RegistryAccess.BUILTIN.get();
        //? if <1.18.2
        //return net.minecraft.core.RegistryAccess.builtin();
    }
    *///?}

    static void registerModifyLootEvents() {
        //? if >=1.20.5 {
        LootTableEvents.MODIFY.register((ResourceKey<LootTable> resourceKey, LootTable.Builder builder,
                LootTableSource lootTableSource
                //? if >=1.21
                , HolderLookup.Provider provider
        ) -> {
            //? if >=1.21.11
            LootEvents.MODIFY.invoker().modify(resourceKey.identifier(), builder::withPool);
            //? if <1.21.11
            /*LootEvents.MODIFY.invoker().modify(resourceKey.location(), builder::withPool);*/
        });
        //?} else if >=1.18.2 {
        /*LootTableEvents.MODIFY.register((resourceManager, lootManager, id, builder, lootTableSource) -> {
            LootEvents.MODIFY.invoker().modify(id, lootPool -> builder.withPool(lootPool));
        });
        *///?} else {
        /*LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, id, supplier, setter) -> {
            LootEvents.MODIFY.invoker().modify(id, supplier::pool);
        });
        *///?}
    }

    static void registerEntityInteractEvents() {
        UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            return PlayerEvents.ENTITY_INTERACT.invoker().interact(player, level, hand, entity);
        });
    }

    static void registerCommandEvents() {
        //? if >=1.19 {
        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandBuildContext, commandSelection) -> {
            CommandEvents.EVENT.invoker().register(commandDispatcher, commandBuildContext, commandSelection);
        });
        //?} else {
        /*CommandRegistrationCallback.EVENT.register((commandDispatcher, dedicated) -> {
            CommandEvents.EVENT.invoker().register(
                    commandDispatcher,
                    commandRegistryAccess(),
                    dedicated ? net.minecraft.commands.Commands.CommandSelection.DEDICATED : net.minecraft.commands.Commands.CommandSelection.INTEGRATED
            );
        });
        *///?}
    }

    static void registerEntityDamageEvents() {
        //? if >=1.19.2 {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            EntityEvent.ENTITY_DEATH.invoker().onEntityDeath(entity, source);
        });
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            InteractionResult result = EntityEvent.ENTITY_DAMAGE.invoker().onEntityDamage(entity, source, amount);
            return result == InteractionResult.PASS;
        });
        //?}
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
        //? if >=1.20.6 {
        DefaultItemComponentEvents.MODIFY.register(modifyContext -> {
            ItemEvents.MODIFY_DEFAULT_COMPONENTS.invoker().modify(
                new FabricComponentModificationContext(modifyContext)
            );
        });
        //?}
    }

    static void registerCreativeTabEvents() {
        //? if >=26.1 {
        for (var tabKey : net.minecraft.core.registries.BuiltInRegistries.CREATIVE_MODE_TAB.registryKeySet()) {
            net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents.modifyOutputEvent(tabKey).register((output) -> {
                CreativeModeTabEvents.MODIFY_ENTRIES.invoker().modifyEntries(tabKey, new CreativeModeTabOutput() {
                    @Override
                    public void accept(net.minecraft.world.item.ItemStack stack, CreativeModeTabOutput.TabVisibility visibility) {
                        output.accept(stack);
                    }
                });
            });
        }
        //?} else if >=1.20 {
        /*for (var tabKey : net.minecraft.core.registries.BuiltInRegistries.CREATIVE_MODE_TAB.registryKeySet()) {
            net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents.modifyEntriesEvent(tabKey).register((tab) -> {
                CreativeModeTabEvents.MODIFY_ENTRIES.invoker().modifyEntries(tabKey, new CreativeModeTabOutput() {
                    @Override
                    public void accept(net.minecraft.world.item.ItemStack stack, CreativeModeTabOutput.TabVisibility visibility) {
                        tab.accept(stack);
                    }
                });
            });
        }
        *///?} else if >=1.19.3 {
        /*ItemGroupEvents.MODIFY_ENTRIES_ALL.register((tab, entries) -> {
            net.minecraft.resources.ResourceKey<net.minecraft.core.Registry<net.minecraft.world.item.CreativeModeTab>> registryKey =
                    net.minecraft.resources.ResourceKey.createRegistryKey(new Identifier("minecraft", "creative_mode_tab"));
            net.minecraft.resources.ResourceKey<net.minecraft.world.item.CreativeModeTab> tabKey =
                    net.minecraft.resources.ResourceKey.create(registryKey, ((IdentifiableItemGroup) tab).getId());
            CreativeModeTabEvents.MODIFY_ENTRIES.invoker().modifyEntries(tabKey, new CreativeModeTabOutput() {
                @Override
                public void accept(net.minecraft.world.item.ItemStack stack, CreativeModeTabOutput.TabVisibility visibility) {
                    entries.accept(stack);
                }
            });
        });
        *///?}

        for (var builder : CreativeModeTabRegistry.getTabBuilders().values()) {
            net.minecraft.resources.ResourceKey<net.minecraft.world.item.CreativeModeTab> tabKey = net.minecraft.resources.ResourceKey.create(
                //? if >=1.20
                net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB,
                //? if <1.20
                /*net.minecraft.resources.ResourceKey.createRegistryKey(new net.minecraft.resources.Identifier("minecraft", "creative_mode_tab")),*/
                builder.getId()
            );

            //? if >=26.1 {
            net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents.modifyOutputEvent(tabKey).register((output) -> {
                for (var itemSupplier : builder.getItems()) {
                    output.accept(itemSupplier.get());
                }

                CreativeModeTabEvents.MODIFY_ENTRIES.invoker().modifyEntries(tabKey, new CreativeModeTabOutput() {
                    @Override
                    public void accept(net.minecraft.world.item.ItemStack stack, CreativeModeTabOutput.TabVisibility visibility) {
                        output.accept(stack);
                    }
                });
            });
            //?} else if >=1.19.3 {
            /*//? if >=1.20
            net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents.modifyEntriesEvent(tabKey).register((tab) -> {
            //? if <1.20
            //net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents.modifyEntriesEvent(builder.getId()).register((tab) -> {
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
            *///?}
        }
    }

    static void registerClientCommandEvents() {
        //? if >=1.19 {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            CommandDispatcher<CommandSourceStack> commandsTemp =
                    (CommandDispatcher<CommandSourceStack>) (CommandDispatcher<?>) dispatcher;
            ClientCommandEvents.EVENT.invoker().register(commandsTemp, registryAccess);
        });
        //?} else {
        /*@SuppressWarnings("unchecked")
        CommandDispatcher<CommandSourceStack> commandsTemp = (CommandDispatcher<CommandSourceStack>) (CommandDispatcher<?>) ClientCommandManager.DISPATCHER;
        // Fabric's v1 dispatcher is static, so register once, after every mod's client initializer has run.
        boolean[] amber$clientCommandsRegistered = {false};
        ClientTickEvents.START_CLIENT_TICK.register((client) -> {
            if (amber$clientCommandsRegistered[0]) {
                return;
            }
            amber$clientCommandsRegistered[0] = true;
            ClientCommandEvents.EVENT.invoker().register(commandsTemp, commandRegistryAccess());
        });
        *///?}
    }

    static void registerRenderHudEvents() {
        //? if >=26.1 {
        HudElementRegistry.addLast(
                Identifier.fromNamespaceAndPath(Constants.MOD_ID, "render_hud"),
                (guiGraphics, tickDelta) -> HudEvents.RENDER_HUD.invoker().onHudRender(guiGraphics, tickDelta)
        );
        //?} else {
        /*HudRenderCallback.EVENT.register((guiGraphics, tickDelta) -> {
            HudEvents.RENDER_HUD.invoker().onHudRender(guiGraphics, tickDelta);
        });
        *///?}
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
        //? if >=26.1 {
        ServerLevelEvents.LOAD.register((server, level) -> {
            WorldEvents.WORLD_LOAD.invoker().onWorldLoad(server, level);
        });
        ServerLevelEvents.UNLOAD.register((server, level) -> {
            WorldEvents.WORLD_UNLOAD.invoker().onWorldUnload(server, level);
        });
        //?} else {
        /*ServerWorldEvents.LOAD.register((server, world) -> {
            WorldEvents.WORLD_LOAD.invoker().onWorldLoad(server, world);
        });
        ServerWorldEvents.UNLOAD.register((server, world) -> {
            WorldEvents.WORLD_UNLOAD.invoker().onWorldUnload(server, world);
        });
        *///?}
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
