package com.iamkaf.amber.api.event.v1.events.common;

import com.iamkaf.amber.api.event.v1.Event;
import com.iamkaf.amber.api.event.v1.EventFactory;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;

public interface CommandEvents {
    Event<CommandEvents> EVENT = EventFactory.createArrayBacked(
            CommandEvents.class, callbacks -> (dispatcher, registryAccess, dedicated) -> {
                for (CommandEvents callback : callbacks) {
                    callback.register(dispatcher, registryAccess, dedicated);
                }
            }
    );

    void register(CommandDispatcher<CommandSourceStack> dispatcher, Object registryAccess, boolean dedicated);
}
