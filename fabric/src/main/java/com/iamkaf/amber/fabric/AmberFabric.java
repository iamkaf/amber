package com.iamkaf.amber.fabric;

import net.fabricmc.api.ModInitializer;

import com.iamkaf.amber.Amber;

public final class AmberFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Amber.init();
    }
}
