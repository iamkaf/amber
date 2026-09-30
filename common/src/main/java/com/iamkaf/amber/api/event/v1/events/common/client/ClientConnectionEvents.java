//? if >=1.21.11 {
package com.iamkaf.amber.api.event.v1.events.common.client;

import com.iamkaf.amber.api.event.v1.Event;
import com.iamkaf.amber.api.event.v1.EventFactory;

/**
 * Events for the client's connection to a server, including the integrated server of a
 * singleplayer or LAN world.
 *
 * <p>Both events run on the client thread. They always alternate: after a {@link #JOIN},
 * exactly one {@link #DISCONNECT} fires before the next {@code JOIN}. This makes them suitable
 * for creating and resetting per-server client state.</p>
 */
public final class ClientConnectionEvents {
    /**
     * Called after the client joins a server and enters play. The local player and client level
     * exist when this runs.
     */
    public static final Event<Join> JOIN = EventFactory.createArrayBacked(
            Join.class, callbacks -> () -> {
                for (Join callback : callbacks) {
                    callback.onJoin();
                }
            }
    );

    /**
     * Called when the client leaves the server it joined, whether it quit, was kicked, or lost
     * the connection. The local player and client level may already be gone, and no packets
     * should be sent.
     */
    public static final Event<Disconnect> DISCONNECT = EventFactory.createArrayBacked(
            Disconnect.class, callbacks -> () -> {
                for (Disconnect callback : callbacks) {
                    callback.onDisconnect();
                }
            }
    );

    private ClientConnectionEvents() {
    }

    @FunctionalInterface
    public interface Join {
        void onJoin();
    }

    @FunctionalInterface
    public interface Disconnect {
        void onDisconnect();
    }
}
//?}
