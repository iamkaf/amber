package com.iamkaf.amber.platform;

import com.iamkaf.amber.api.event.v1.events.common.CommandEvents;
import com.iamkaf.amber.api.event.v1.events.common.LootEvents;
import com.iamkaf.amber.api.event.v1.events.common.PlayerEvents;
import com.iamkaf.amber.api.event.v1.events.common.client.HudEvents;
import com.iamkaf.amber.platform.services.IAmberEventSetup;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;

public class FabricAmberEventSetup implements IAmberEventSetup {
    @Override
    public void registerCommon() {
        LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, resourceLocation, supplier, setter) -> {
            LootEvents.MODIFY.invoker().modify(resourceLocation, supplier::withPool);
        });
        UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            return PlayerEvents.ENTITY_INTERACT.invoker().interact(player, level, hand, entity);
        });
        CommandRegistrationCallback.EVENT.register((commandDispatcher, dedicated) -> {
            CommandEvents.EVENT.invoker().register(
                    commandDispatcher,
                    null,
                    dedicated
            );
        });
    }

    @Override
    public void registerClient() {
        HudRenderCallback.EVENT.register((tickDelta) -> {
            HudEvents.RENDER_HUD.invoker().onHudRender(tickDelta);
        });
    }

    @Override
    public void registerServer() {

    }
}
