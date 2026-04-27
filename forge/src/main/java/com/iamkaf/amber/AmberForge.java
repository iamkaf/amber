package com.iamkaf.amber;

import com.iamkaf.amber.api.core.v2.AmberInitializer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Constants.MOD_ID)
public class AmberForge {

    //? if <1.21.1 {
    /*public AmberForge() {
        this(FMLJavaModLoadingContext.get());
    }
    *///?}

    public AmberForge(FMLJavaModLoadingContext ctx) {
        AmberInitializer.initialize(Constants.MOD_ID);
        // Store the event bus internally for Amber's use
        //? if >=1.21.6
        AmberInitializer.setEventBus(Constants.MOD_ID, ctx.getModBusGroup());
        //? if <1.21.6
        /*AmberInitializer.setEventBus(Constants.MOD_ID, ctx.getModEventBus());*/
        
        // Register networking event listeners if needed in the future
        // This is a placeholder for potential future Forge networking event registration
        
        AmberMod.init();
    }
}
