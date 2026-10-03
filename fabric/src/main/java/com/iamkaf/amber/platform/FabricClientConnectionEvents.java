package com.iamkaf.amber.platform;

import com.iamkaf.amber.event.ClientConnectionDispatcher;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

final class FabricClientConnectionEvents {
    private FabricClientConnectionEvents() {
    }

    static void register() {
        ClientPlayConnectionEvents.JOIN.register((listener, sender, client) -> ClientConnectionDispatcher.joined());
        // Fabric reports a singleplayer disconnect from the local network thread.
        ClientPlayConnectionEvents.DISCONNECT.register((listener, client) -> client.execute(ClientConnectionDispatcher::disconnected));
    }
}
