package com.iamkaf.amber.api.event.v1.events.common;

import com.iamkaf.amber.api.event.v1.Event;
import com.iamkaf.amber.api.event.v1.EventFactory;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

public final class CreativeModeTabEvents {
    private CreativeModeTabEvents() {
    }

    public static final Event<ModifyEntries> MODIFY_ENTRIES = EventFactory.createArrayBacked(
            ModifyEntries.class, callbacks -> (tabKey, output) -> {
                for (ModifyEntries callback : callbacks) {
                    callback.modifyEntries(tabKey, output);
                }
            }
    );

    @FunctionalInterface
    public interface ModifyEntries {
        void modifyEntries(ResourceKey<CreativeModeTab> tabKey, CreativeModeTabOutput output);
    }
}
