package com.iamkaf.mochila;

import net.fabricmc.api.ModInitializer;

/**
 * Fabric entry point.
 */
public class MochilaFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        MochilaMod.init();
    }
}
