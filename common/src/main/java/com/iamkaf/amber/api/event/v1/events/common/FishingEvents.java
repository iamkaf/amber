package com.iamkaf.amber.api.event.v1.events.common;

import com.iamkaf.amber.api.event.v1.Event;
import com.iamkaf.amber.api.event.v1.EventFactory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class FishingEvents {
    /**
     * Fired on the server when a player reels in a catch, before the hook pulls it in. Listeners may add, remove, or
     * change stacks in {@code drops}; the resulting stacks are pulled to the player as usual, with the catch's
     * experience, statistics, and advancement triggers. {@code rod} is the fishing rod being reeled in.
     */
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
