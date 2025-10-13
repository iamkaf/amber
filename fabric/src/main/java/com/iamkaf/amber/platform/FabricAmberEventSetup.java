package com.iamkaf.amber.platform;

import com.iamkaf.amber.api.event.v1.events.common.BlockEvents;
import com.iamkaf.amber.api.event.v1.events.common.CommandEvents;
import com.iamkaf.amber.api.event.v1.events.common.EntityEvent;
import com.iamkaf.amber.api.event.v1.events.common.ItemEvents;
import com.iamkaf.amber.api.event.v1.events.common.LootEvents;
import com.iamkaf.amber.api.event.v1.events.common.PlayerEvents;
import com.iamkaf.amber.api.event.v1.events.common.client.ClientCommandEvents;
import com.iamkaf.amber.api.event.v1.events.common.client.HudEvents;
import com.iamkaf.amber.api.event.v1.events.common.client.RenderEvents;
import com.iamkaf.amber.platform.services.IAmberEventSetup;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.storage.loot.LootTable;

public class FabricAmberEventSetup implements IAmberEventSetup {
    @Override
    public void registerCommon() {
        LootTableEvents.MODIFY.register((ResourceKey<LootTable> resourceKey, LootTable.Builder builder, LootTableSource lootTableSource, HolderLookup.Provider provider) -> {
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
        
        // Block events
        PlayerBlockBreakEvents.BEFORE.register((level, player, pos, state, blockEntity) -> {
            InteractionResult result = BlockEvents.BLOCK_BREAK_BEFORE.invoker().beforeBlockBreak(level, player, pos, state, blockEntity);
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
