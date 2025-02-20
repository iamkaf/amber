package com.iamkaf.amber.event.neoforge;

import net.neoforged.neoforge.common.NeoForge;

public class AmberEventSetupImpl {
    public static void registerCommon() {
        NeoForge.EVENT_BUS.register(EventHandlerCommon.class);
    }

    public static void registerClient() {
        // No client events to register yet
    }
}
