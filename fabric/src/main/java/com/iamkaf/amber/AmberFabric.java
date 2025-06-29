package com.iamkaf.amber;

import net.fabricmc.api.ModInitializer;

/**
 * Fabric entry point.
 */
public class AmberFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        AmberMod.init();
    }
}
