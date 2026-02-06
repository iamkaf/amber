package com.iamkaf.amber.api.event.v1.events.common;

import com.iamkaf.amber.api.event.v1.Event;
import com.iamkaf.amber.api.event.v1.EventFactory;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public class ItemEvents {
    /**
     * An event that is fired when default data components for items are being registered.
     * <p>
     * This event is fired during the registry bootstrap process when items are being finalized.
     * It allows mods to modify the default components that items have when created.
     * </p>
     * <p>
     * This event is fired once during startup on both client and server. Modifications are
     * applied to all subsequent instances of the modified items.
     * </p>
     * <p>
     * <b>This event is informational only and cannot be cancelled.</b>
     * </p>
     */
    public static final Event<ModifyDefaultComponents> MODIFY_DEFAULT_COMPONENTS = EventFactory.createArrayBacked(
            ModifyDefaultComponents.class, callbacks -> (context) -> {
                for (ModifyDefaultComponents callback : callbacks) {
                    callback.modify(context);
                }
            }
    );

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

    @FunctionalInterface
    public interface ModifyDefaultComponents {
        /**
         * Called when default data components for items are being registered.
         * <p>
         * This allows mods to modify the default components that items have when created.
         * The context provides access to modify specific items' component builders.
         * </p>
         *
         * @param context the modification context containing item and builder access
         */
        void modify(ComponentModificationContext context);
    }

    /**
     * Interface providing access to modify default data components for items.
     * <p>
     * This context is provided during the MODIFY_DEFAULT_COMPONENTS event and allows
     * mods to safely modify the default components that items will have when created.
     * </p>
     */
    public interface ComponentModificationContext {
        /**
         * Modifies the default components for the specified item.
         * <p>
         * The consumer will receive a DataComponentMap.Builder that can be used
         * to add, remove, or modify components for the item.
         * </p>
         *
         * @param item the item to modify components for
         * @param builderConsumer a consumer that accepts the component builder
         */
        void modify(Item item, Consumer<DataComponentMap.Builder> builderConsumer);
    }
}
