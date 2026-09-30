//? if >=1.21.11 {
package com.iamkaf.amber.platform;

import com.iamkaf.amber.event.ClientConnectionDispatcher;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.common.NeoForge;

final class NeoForgeClientConnectionEvents {
    private NeoForgeClientConnectionEvents() {
    }

    static void register() {
        NeoForge.EVENT_BUS.addListener(ClientPlayerNetworkEvent.LoggingIn.class, event -> ClientConnectionDispatcher.joined());
        NeoForge.EVENT_BUS.addListener(ClientPlayerNetworkEvent.LoggingOut.class, event -> ClientConnectionDispatcher.disconnected());
    }
}
//?}
