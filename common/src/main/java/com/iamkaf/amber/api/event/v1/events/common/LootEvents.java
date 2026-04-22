package com.iamkaf.amber.api.event.v1.events.common;

import com.iamkaf.amber.api.event.v1.Event;
import com.iamkaf.amber.api.event.v1.EventFactory;
//? if <1.21.11 {
/*import net.minecraft.resources.ResourceLocation;*/
//?} else {
import net.minecraft.resources.Identifier;
//?}
import net.minecraft.world.level.storage.loot.LootPool;

import java.util.function.Consumer;

public class LootEvents {
    /**
     * An event that is called to modify a loot table before it is loaded.
     */
    public static final Event<ModifyLootTable> MODIFY = EventFactory.createArrayBacked(
            //? if <1.21.11 {
            /*ModifyLootTable.class, callbacks -> (ResourceLocation lootTable, Consumer<LootPool.Builder> add) -> {*/
            //?} else {
            ModifyLootTable.class, callbacks -> (Identifier lootTable, Consumer<LootPool.Builder> add) -> {
            //?}
                for (ModifyLootTable callback : callbacks) {
                    callback.modify(lootTable, add);
                }
            }
    );

    @FunctionalInterface
    public interface ModifyLootTable {
        //? if <1.21.11 {
        /*void modify(ResourceLocation lootTable, Consumer<LootPool.Builder> add);*/
        //?} else {
        void modify(Identifier lootTable, Consumer<LootPool.Builder> add);
        //?}
    }
}
