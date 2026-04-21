package com.iamkaf.amber.api.event.v1.events.common;

import com.iamkaf.amber.api.event.v1.Event;
import com.iamkaf.amber.api.event.v1.EventFactory;
import net.minecraft.resources.ResourceLocation;

public final class CreativeModeTabEvents {
    private CreativeModeTabEvents() {
    }

    public static final Event<ModifyEntries> MODIFY_ENTRIES = EventFactory.createArrayBacked(
            ModifyEntries.class, callbacks -> (tabId, output) -> {
                for (ModifyEntries callback : callbacks) {
                    callback.modifyEntries(tabId, output);
                }
            }
    );

    @FunctionalInterface
    public interface ModifyEntries {
        void modifyEntries(ResourceLocation tabId, CreativeModeTabOutput output);
    }
}
