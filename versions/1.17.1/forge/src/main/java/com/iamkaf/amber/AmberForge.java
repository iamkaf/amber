package com.iamkaf.amber;

import com.iamkaf.amber.api.core.v2.AmberInitializer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Constants.MOD_ID)
public class AmberForge {

    public AmberForge() {
        AmberInitializer.initialize(Constants.MOD_ID);
        AmberInitializer.setEventBus(Constants.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        AmberMod.init();
    }
}
