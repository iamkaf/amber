package com.iamkaf.amber.api.event.v1.events.common;

import com.iamkaf.amber.api.event.v1.Event;
import com.iamkaf.amber.api.event.v1.EventFactory;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemEvents {
    public static final Event<ModifyDefaultComponents> MODIFY_DEFAULT_COMPONENTS = EventFactory.createArrayBacked(
            ModifyDefaultComponents.class, callbacks -> (context) -> {
                for (ModifyDefaultComponents callback : callbacks) {
                    callback.modify(context);
                }
            }
    );

    public static final Event<ItemDrop> ITEM_DROP = EventFactory.createArrayBacked(
            ItemDrop.class, callbacks -> (player, itemEntity) -> {
                for (ItemDrop callback : callbacks) {
                    callback.onItemDrop(player, itemEntity);
                }
            }
    );

    public static final Event<ItemPickup> ITEM_PICKUP = EventFactory.createArrayBacked(
            ItemPickup.class, callbacks -> (player, itemEntity, itemStack) -> {
                for (ItemPickup callback : callbacks) {
                    callback.onItemPickup(player, itemEntity, itemStack);
                }
            }
    );

    @FunctionalInterface
    public interface ItemDrop {
        void onItemDrop(Player player, ItemEntity itemEntity);
    }

    @FunctionalInterface
    public interface ItemPickup {
        void onItemPickup(Player player, ItemEntity itemEntity, ItemStack itemStack);
    }

    @FunctionalInterface
    public interface ModifyDefaultComponents {
        void modify(ComponentModificationContext context);
    }

    /**
     * Pre-1.20.5 has no item data-components system.
     * The event remains present for source compatibility, but the context is a no-op marker.
     */
    public interface ComponentModificationContext {
        default void modify(Item item, Runnable ignored) {
        }
    }
}
