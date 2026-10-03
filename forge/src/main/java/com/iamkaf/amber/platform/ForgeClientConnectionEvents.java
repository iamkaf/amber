package com.iamkaf.amber.platform;

import com.iamkaf.amber.event.ClientConnectionDispatcher;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
//? if <1.21.6
/*import net.minecraftforge.common.MinecraftForge;*/

final class ForgeClientConnectionEvents {
    private ForgeClientConnectionEvents() {
    }

    static void register() {
        //? if >=1.21.6 {
        ClientPlayerNetworkEvent.LoggingIn.BUS.addListener(event -> ClientConnectionDispatcher.joined());
        ClientPlayerNetworkEvent.LoggingOut.BUS.addListener(event -> ClientConnectionDispatcher.disconnected());
        //?} else if >=1.19 {
        /*MinecraftForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingIn event) -> ClientConnectionDispatcher.joined());
        MinecraftForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingOut event) -> ClientConnectionDispatcher.disconnected());
        *///?} else {
        /*MinecraftForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggedInEvent event) -> ClientConnectionDispatcher.joined());
        MinecraftForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggedOutEvent event) -> ClientConnectionDispatcher.disconnected());
        *///?}
    }
}
