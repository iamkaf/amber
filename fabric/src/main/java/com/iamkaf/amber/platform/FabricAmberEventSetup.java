package com.iamkaf.amber.platform;

import com.iamkaf.amber.api.event.v1.events.common.*;
import com.iamkaf.amber.api.event.v1.events.common.client.ClientCommandEvents;
import com.iamkaf.amber.api.event.v1.events.common.client.HudEvents;
import com.iamkaf.amber.api.creativetabs.CreativeModeTabRegistry;
import com.iamkaf.amber.api.event.v1.events.common.CreativeModeTabEvents;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
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
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.function.Consumer;

public class FabricAmberEventSetup implements IAmberEventSetup {

    /**
     * Fabric-specific wrapper for DefaultItemComponentEvents.ModifyContext.
     */
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

    @Override
    public void registerCommon() {
        LootTableEvents.MODIFY.register((ResourceKey<LootTable> resourceKey, LootTable.Builder builder,
                LootTableSource lootTableSource, HolderLookup.Provider provider) -> {
            LootEvents.MODIFY.invoker().modify(resourceKey.location(), builder::withPool);
        });
        UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            return PlayerEvents.ENTITY_INTERACT.invoker().interact(player, level, hand, entity);
        });
        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandBuildContext, commandSelection) -> {
            CommandEvents.EVENT.invoker().register(commandDispatcher, commandBuildContext, commandSelection);
        });

        // Entity lifecycle events
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            EntityEvent.ENTITY_DEATH.invoker().onEntityDeath(entity, source);
        });
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            InteractionResult result = EntityEvent.ENTITY_DAMAGE.invoker().onEntityDamage(entity, source, amount);
            return result == InteractionResult.PASS; // Only allow damage if PASS is returned
        });

        // Shield block events
        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamage, damageTaken, blocked) -> {
            if (entity instanceof net.minecraft.world.entity.player.Player player && blocked) {
                net.minecraft.world.item.ItemStack shield = findBlockingShield(player);
                if (!shield.isEmpty()) {
                    PlayerEvents.SHIELD_BLOCK.invoker().onShieldBlock(player, shield, baseDamage, source);
                }
            }
        });

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
        DefaultItemComponentEvents.MODIFY.register(modifyContext -> {
            ItemEvents.MODIFY_DEFAULT_COMPONENTS.invoker().modify(
                new FabricComponentModificationContext(modifyContext)
            );
        });

        // Creative mode tab events
        // Register for all existing vanilla tabs
        for (var tabKey : net.minecraft.core.registries.BuiltInRegistries.CREATIVE_MODE_TAB.registryKeySet()) {
            net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents.modifyEntriesEvent(tabKey).register((tab) -> {
                CreativeModeTabEvents.MODIFY_ENTRIES.invoker().modifyEntries(tabKey, tab);
            });
        }

        // Register for our custom tabs
        for (var builder : CreativeModeTabRegistry.getTabBuilders().values()) {
            var tabKey = net.minecraft.resources.ResourceKey.create(
                net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB,
                builder.getId()
            );
            net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents.modifyEntriesEvent(tabKey).register((tab) -> {
                CreativeModeTabEvents.MODIFY_ENTRIES.invoker().modifyEntries(tabKey, tab);
            });
        }
    }

    @Override
    public void registerClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            CommandDispatcher<CommandSourceStack> commandsTemp = new CommandDispatcher<>();
            ClientCommandEvents.EVENT.invoker().register(commandsTemp, registryAccess);
        });
        HudRenderCallback.EVENT.register((guiGraphics, tickDelta) -> {
            HudEvents.RENDER_HUD.invoker().onHudRender(guiGraphics, tickDelta);
        });
        ClientTickEvents.START_CLIENT_TICK.register(minecraft -> {
            com.iamkaf.amber.api.event.v1.events.common.client.ClientTickEvents.START_CLIENT_TICK.invoker().onStartTick();
        });
        ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
            com.iamkaf.amber.api.event.v1.events.common.client.ClientTickEvents.END_CLIENT_TICK.invoker().onEndTick();
        });
    }

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
        ServerWorldEvents.LOAD.register((server, world) -> {
            WorldEvents.WORLD_LOAD.invoker().onWorldLoad(server, world);
        });
        ServerWorldEvents.UNLOAD.register((server, world) -> {
            WorldEvents.WORLD_UNLOAD.invoker().onWorldUnload(server, world);
        });

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

    /**
     * Finds the shield a player is blocking with.
     * @param player The player to check
     * @return The shield ItemStack, or empty if not blocking with a shield
     */
    private static net.minecraft.world.item.ItemStack findBlockingShield(net.minecraft.world.entity.player.Player player) {
        if (!player.isBlocking()) {
            return net.minecraft.world.item.ItemStack.EMPTY;
        }

        // Check main hand first (most common)
        net.minecraft.world.item.ItemStack mainHand = player.getMainHandItem();
        if (mainHand.getItem() instanceof net.minecraft.world.item.ShieldItem) {
            return mainHand;
        }

        // Check off hand
        net.minecraft.world.item.ItemStack offHand = player.getOffhandItem();
        if (offHand.getItem() instanceof net.minecraft.world.item.ShieldItem) {
            return offHand;
        }

        return net.minecraft.world.item.ItemStack.EMPTY;
    }
}
