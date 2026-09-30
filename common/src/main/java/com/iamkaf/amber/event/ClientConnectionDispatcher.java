//? if >=1.21.11 {
package com.iamkaf.amber.event;

import com.iamkaf.amber.api.event.v1.events.common.client.ClientConnectionEvents;

/**
 * Turns loader connection hooks into strictly alternating {@link ClientConnectionEvents}.
 *
 * <p>Loader hooks can report a logout while no world is loaded, such as before a new
 * singleplayer world starts, and may report a second join when a server reconfigures the
 * client. Call both methods on the client thread.</p>
 */
public final class ClientConnectionDispatcher {
    private static boolean connected;

    private ClientConnectionDispatcher() {
    }

    public static void joined() {
        if (connected) {
            ClientConnectionEvents.DISCONNECT.invoker().onDisconnect();
        }
        connected = true;
        ClientConnectionEvents.JOIN.invoker().onJoin();
    }

    public static void disconnected() {
        if (!connected) {
            return;
        }
        connected = false;
        ClientConnectionEvents.DISCONNECT.invoker().onDisconnect();
    }
}
//?}
