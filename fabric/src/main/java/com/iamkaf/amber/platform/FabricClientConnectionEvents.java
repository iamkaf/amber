package com.iamkaf.amber.platform;

import com.iamkaf.amber.event.ClientConnectionDispatcher;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

import java.util.concurrent.atomic.AtomicBoolean;

final class FabricClientConnectionEvents {
    private static final AtomicBoolean PENDING_DISCONNECT = new AtomicBoolean();

    private FabricClientConnectionEvents() {
    }

    static void register() {
        ClientPlayConnectionEvents.JOIN.register((listener, sender, client) -> {
            deliverDisconnect();
            ClientConnectionDispatcher.joined();
        });
        // Fabric reports a singleplayer disconnect from the local network thread. Minecraft can drop
        // the queued task while it tears the level down, so the next client tick delivers it too.
        ClientPlayConnectionEvents.DISCONNECT.register((listener, client) -> {
            PENDING_DISCONNECT.set(true);
            client.execute(FabricClientConnectionEvents::deliverDisconnect);
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> deliverDisconnect());
    }

    private static void deliverDisconnect() {
        if (PENDING_DISCONNECT.getAndSet(false)) {
            ClientConnectionDispatcher.disconnected();
        }
    }
}
