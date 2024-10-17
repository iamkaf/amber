package com.iamkaf.amber.fabric;

import net.fabricmc.api.ModInitializer;

import com.iamkaf.amber.Amber;

public final class AmberFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        Amber.init();
    }
}
