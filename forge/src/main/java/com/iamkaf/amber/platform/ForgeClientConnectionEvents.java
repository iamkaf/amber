//? if >=1.21.11 {
package com.iamkaf.amber.platform;

import com.iamkaf.amber.event.ClientConnectionDispatcher;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;

final class ForgeClientConnectionEvents {
    private ForgeClientConnectionEvents() {
    }

    static void register() {
        ClientPlayerNetworkEvent.LoggingIn.BUS.addListener(event -> ClientConnectionDispatcher.joined());
        ClientPlayerNetworkEvent.LoggingOut.BUS.addListener(event -> ClientConnectionDispatcher.disconnected());
    }
}
//?}
