package com.iamkaf.amber;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class AmberNeoForge {
    public AmberNeoForge(IEventBus eventBus) {
        AmberMod.init();
    }
}