package com.iamkaf.amber.platform;

import com.iamkaf.amber.api.event.v1.events.common.*;
import com.iamkaf.amber.api.event.v1.events.common.client.ClientCommandEvents;
import com.iamkaf.amber.api.event.v1.events.common.client.HudEvents;
import com.iamkaf.amber.api.registry.v1.creativetabs.CreativeModeTabRegistry;
import com.iamkaf.amber.api.event.v1.events.common.CreativeModeTabEvents;
import com.iamkaf.amber.api.event.v1.events.common.CreativeModeTabOutput;
import com.iamkaf.amber.Constants;
//? if >=1.20.6
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import com.iamkaf.amber.platform.services.IAmberEventSetup;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
//? if >=26.1
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
//? if <26.1
/*import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;*/
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
//? if >=1.19.2
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
//? if >=26.1
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
//? if <26.1
/*import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;*/
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
//? if >=1.21
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
//? if >=1.21
import net.fabricmc.fabric.api.loot.v3.LootTableSource;
//? if <1.21
/*import net.fabricmc.fabric.api.loot.v2.LootTableEvents;*/
//? if <1.21
/*import net.fabricmc.fabric.api.loot.v2.LootTableSource;*/
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.HolderLookup;
//? if >=1.20.5
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootTable;

//? if >=1.20.5
import java.util.function.Consumer;

public class FabricAmberEventSetup implements IAmberEventSetup {

    /**
     * Fabric-specific wrapper for DefaultItemComponentEvents.ModifyContext.
     */
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

    @Override
    public void registerCommon() {
        //? if >=1.20.5 {
        LootTableEvents.MODIFY.register((ResourceKey<LootTable> resourceKey, LootTable.Builder builder,
                LootTableSource lootTableSource
                //? if >=1.21
                , HolderLookup.Provider provider
        ) -> {
            //? if >=1.21.11
            LootEvents.MODIFY.invoker().modify(resourceKey.identifier(), builder::withPool);
            //? if <1.21.11 && >=1.21
            /*LootEvents.MODIFY.invoker().modify(resourceKey.location(), builder::withPool);*/
        });
        //?} else {
        /*LootTableEvents.MODIFY.register((resourceManager, lootManager, id, builder, lootTableSource) -> {
            LootEvents.MODIFY.invoker().modify(id, lootPool -> builder.withPool(lootPool));
        });*/
        //?}
        UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            return PlayerEvents.ENTITY_INTERACT.invoker().interact(player, level, hand, entity);
        });
        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandBuildContext, commandSelection) -> {
            CommandEvents.EVENT.invoker().register(commandDispatcher, commandBuildContext, commandSelection);
        });

        // Entity lifecycle events
        //? if >=1.19.2 {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            EntityEvent.ENTITY_DEATH.invoker().onEntityDeath(entity, source);
        });
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            InteractionResult result = EntityEvent.ENTITY_DAMAGE.invoker().onEntityDamage(entity, source, amount);
            return result == InteractionResult.PASS; // Only allow damage if PASS is returned
        });
        //?}

        // After-damage and shield block events are implemented by LivingEntityAfterDamageMixin.

        // Block events
        PlayerBlockBreakEvents.BEFORE.register((level, player, pos, state, blockEntity) -> {
            InteractionResult result =
                    BlockEvents.BLOCK_BREAK_BEFORE.invoker().beforeBlockBreak(level, player, pos, state, blockEntity);
            return result == InteractionResult.PASS; // Only allow break if PASS is returned
        });
        PlayerBlockBreakEvents.AFTER.register((level, player, pos, state, blockEntity) -> {
            BlockEvents.BLOCK_BREAK_AFTER.invoker().afterBlockBreak(level, player, pos, state, blockEntity);
        });
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            return BlockEvents.BLOCK_INTERACT.invoker().onBlockInteract(player, level, hand, hitResult);
        });
        AttackBlockCallback.EVENT.register((player, level, hand, pos, direction) -> {
            return BlockEvents.BLOCK_CLICK.invoker().onBlockClick(player, level, hand, pos, direction);
        });

        // Item events - implemented via Mixins (PlayerMixin, ItemEntityMixin)
        // Fabric doesn't have native item drop/pickup events: https://github.com/FabricMC/fabric/issues/1130

        // Farming events - implemented via Mixins (BoneMealItemMixin, FarmBlockMixin, CropBlockMixin)
        // Fabric doesn't have native farming events for bonemeal, farmland trampling, or crop growth

        // Animal events - implemented via Mixins (TamableAnimalMixin, AnimalMixin, VillagerMixin)
        // Fabric doesn't have native animal taming, breeding, or villager trade events
        
        // Default item components event
        //? if >=1.20.6 {
        DefaultItemComponentEvents.MODIFY.register(modifyContext -> {
            ItemEvents.MODIFY_DEFAULT_COMPONENTS.invoker().modify(
                new FabricComponentModificationContext(modifyContext)
            );
        });
        //?}

        // Creative mode tab events
        // Register for all existing vanilla tabs
        //? if >=1.20 {
        for (var tabKey : net.minecraft.core.registries.BuiltInRegistries.CREATIVE_MODE_TAB.registryKeySet()) {
            //? if >=26.1 {
            net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents.modifyOutputEvent(tabKey).register((output) -> {
                CreativeModeTabEvents.MODIFY_ENTRIES.invoker().modifyEntries(tabKey, new CreativeModeTabOutput() {
                    @Override
                    public void accept(net.minecraft.world.item.ItemStack stack, CreativeModeTabOutput.TabVisibility visibility) {
                        // For unobfuscated 26.x, CreativeModeTab.TabVisibility is protected
                        // We can only use the simple accept(stack) which defaults to PARENT_AND_SEARCH_TABS
                        // or accept(stack, item) which also defaults to PARENT_AND_SEARCH_TABS
                        // The visibility parameter is currently ignored due to Minecraft 26.x access restrictions
                        output.accept(stack);
                    }
                });
            });
            //?} else {
            /*net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents.modifyEntriesEvent(tabKey).register((tab) -> {
                CreativeModeTabEvents.MODIFY_ENTRIES.invoker().modifyEntries(tabKey, new CreativeModeTabOutput() {
                    @Override
                    public void accept(net.minecraft.world.item.ItemStack stack, CreativeModeTabOutput.TabVisibility visibility) {
                        tab.accept(stack);
                    }
                });
            });
            *///?}
        }
        //?}

        // Register for our custom tabs
        for (var builder : CreativeModeTabRegistry.getTabBuilders().values()) {
            //? if >=1.20 {
            net.minecraft.resources.ResourceKey<net.minecraft.world.item.CreativeModeTab> tabKey = net.minecraft.resources.ResourceKey.create(
                net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB,
                builder.getId()
            );
            //?} else {
            /*net.minecraft.resources.ResourceKey<net.minecraft.world.item.CreativeModeTab> tabKey = net.minecraft.resources.ResourceKey.create(
                net.minecraft.resources.ResourceKey.createRegistryKey(new net.minecraft.resources.ResourceLocation("minecraft", "creative_mode_tab")),
                builder.getId()
            );*/
            //?}
            //? if >=26.1 {
            net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents.modifyOutputEvent(tabKey).register((output) -> {
                // Add items from the TabBuilder
                for (var itemSupplier : builder.getItems()) {
                    output.accept(itemSupplier.get());
                }

                CreativeModeTabEvents.MODIFY_ENTRIES.invoker().modifyEntries(tabKey, new CreativeModeTabOutput() {
                    @Override
                    public void accept(net.minecraft.world.item.ItemStack stack, CreativeModeTabOutput.TabVisibility visibility) {
                        // For unobfuscated 26.x, CreativeModeTab.TabVisibility is protected
                        // We can only use the simple accept(stack) which defaults to PARENT_AND_SEARCH_TABS
                        // The visibility parameter is currently ignored due to Minecraft 26.x access restrictions
                        output.accept(stack);
                    }
                });
            });
            //?} else if >=1.20 {
            /*net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents.modifyEntriesEvent(tabKey).register((tab) -> {
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
            *///?} else if >=1.19.3 {
            /*net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents.modifyEntriesEvent(builder.getId()).register((tab) -> {
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

    @Override
    public void registerClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            CommandDispatcher<CommandSourceStack> commandsTemp = new CommandDispatcher<>();
            ClientCommandEvents.EVENT.invoker().register(commandsTemp, registryAccess);
        });
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
        ClientTickEvents.START_CLIENT_TICK.register(minecraft -> {
            com.iamkaf.amber.api.event.v1.events.common.client.ClientTickEvents.START_CLIENT_TICK.invoker().onStartTick();
        });
        ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
            com.iamkaf.amber.api.event.v1.events.common.client.ClientTickEvents.END_CLIENT_TICK.invoker().onEndTick();
        });
    }

        // FIXME: registerServer() called from common init due to EnvExecutor inconsistency
    // TODO: Move all server events to registerCommon() and sunset registerServer() methods
    @Override
    public void registerServer() {
        // Server tick events
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            com.iamkaf.amber.api.event.v1.events.common.ServerTickEvents.START_SERVER_TICK.invoker().onStartTick();
        });
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            com.iamkaf.amber.api.event.v1.events.common.ServerTickEvents.END_SERVER_TICK.invoker().onEndTick();
        });

        // World events
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

        // Player lifecycle events
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
