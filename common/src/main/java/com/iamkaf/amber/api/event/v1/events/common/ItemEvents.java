package com.iamkaf.amber.api.event.v1.events.common;

import com.iamkaf.amber.api.event.v1.Event;
import com.iamkaf.amber.api.event.v1.EventFactory;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ItemEvents {
    /**
     * An event that is fired when a player drops an item.
     * <p>
     * This event is fired on both the client and server side when a player tosses/drops an item from their inventory.
     * </p>
     * <p>
     * <b>This event is informational only and cannot be cancelled.</b>
     * </p>
     */
    public static final Event<ItemDrop> ITEM_DROP = EventFactory.createArrayBacked(
            ItemDrop.class, callbacks -> (player, itemEntity) -> {
                for (ItemDrop callback : callbacks) {
                    callback.onItemDrop(player, itemEntity);
                }
            }
    );

    /**
     * An event that is fired when a player picks up an item.
     * <p>
     * This event is fired on both the client and server side when a player collects an item from the world.
     * </p>
     * <p>
     * <b>This event is informational only and cannot be cancelled.</b>
     * </p>
     */
    public static final Event<ItemPickup> ITEM_PICKUP = EventFactory.createArrayBacked(
            ItemPickup.class, callbacks -> (player, itemEntity, itemStack) -> {
                for (ItemPickup callback : callbacks) {
                    callback.onItemPickup(player, itemEntity, itemStack);
                }
            }
    );

    @FunctionalInterface
    public interface ItemDrop {
        /**
         * Called when a player drops an item.
         * <p>
         * This is an informational event only - you cannot cancel the drop.
         * </p>
         *
         * @param player the player who dropped the item
         * @param itemEntity the item entity that was dropped
         */
        void onItemDrop(Player player, ItemEntity itemEntity);
    }

    @FunctionalInterface
    public interface ItemPickup {
        /**
         * Called when a player picks up an item.
         * <p>
         * This is an informational event only - you cannot cancel the pickup.
         * </p>
         *
         * @param player the player who picked up the item
         * @param itemEntity the item entity that was picked up
         * @param itemStack the item stack that was picked up
         */
        void onItemPickup(Player player, ItemEntity itemEntity, ItemStack itemStack);
    }
}
