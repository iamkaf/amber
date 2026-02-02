package com.iamkaf.mochila;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class MochilaNeoForge {
    public MochilaNeoForge(IEventBus eventBus) {
        MochilaMod.init();
    }
}