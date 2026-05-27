package com.iamkaf.amber.api.event.v1.events.common;

import com.iamkaf.amber.api.event.v1.Event;
import com.iamkaf.amber.api.event.v1.EventFactory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class FishingEvents {
    public static final Event<ModifyCatch> MODIFY_CATCH = EventFactory.createArrayBacked(
            ModifyCatch.class,
            callbacks -> (player, hook, rod, drops) -> {
                for (ModifyCatch callback : callbacks) {
                    callback.modify(player, hook, rod, drops);
                }
            }
    );

    private FishingEvents() {
    }

    @FunctionalInterface
    public interface ModifyCatch {
        void modify(ServerPlayer player, Entity hook, ItemStack rod, List<ItemStack> drops);
    }
}
