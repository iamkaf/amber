package com.iamkaf.amber.api.event.v1.events.common.client;

import com.iamkaf.amber.api.event.v1.Event;
import com.iamkaf.amber.api.event.v1.EventFactory;

/**
 * Events that occur during the client tick.
 * Matches the Forge/NeoForge events.
 *
 * @since 6.0.7
 */
public final class ClientTickEvents {
    /**
     * Called at the start of the client tick.
     */
    public static final Event<StartTick> START_CLIENT_TICK = EventFactory.createArrayBacked(
            StartTick.class, callbacks -> () -> {
                for (StartTick event : callbacks) {
                    event.onStartTick();
                }
            }
    );
    /**
     * Called at the end of the client tick.
     */
    public static final Event<EndTick> END_CLIENT_TICK = EventFactory.createArrayBacked(
            EndTick.class, callbacks -> () -> {
                for (EndTick event : callbacks) {
                    event.onEndTick();
                }
            }
    );

    private ClientTickEvents() {
    }

    @FunctionalInterface
    public interface StartTick {
        void onStartTick();
    }

    @FunctionalInterface
    public interface EndTick {
        void onEndTick();
    }
}
