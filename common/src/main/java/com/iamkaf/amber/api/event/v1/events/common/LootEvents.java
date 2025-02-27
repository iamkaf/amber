package com.iamkaf.amber.api.event.v1.events.common;

import com.iamkaf.amber.api.event.v1.Event;
import com.iamkaf.amber.api.event.v1.EventFactory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;

import java.util.function.Consumer;

public class LootEvents {
    public static final Event<ModifyLootTable> MODIFY = EventFactory.createArrayBacked(
            ModifyLootTable.class, callbacks -> (ResourceLocation lootTable, Consumer<LootPool.Builder> add) -> {
                for (ModifyLootTable callback : callbacks) {
                    callback.modify(lootTable, add);
                }
            }
    );

    @FunctionalInterface
    public interface ModifyLootTable {
        void modify(ResourceLocation lootTable, Consumer<LootPool.Builder> add);
    }
}
