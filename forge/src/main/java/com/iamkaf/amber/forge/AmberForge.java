package com.iamkaf.amber.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import com.iamkaf.amber.Amber;

@Mod(Amber.MOD_ID)
public final class AmberForge {
    public AmberForge() {
        EventBuses.registerModEventBus(Amber.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        Amber.init();
    }
}
