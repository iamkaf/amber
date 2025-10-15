package com.iamkaf.amber;

import com.iamkaf.amber.api.core.v2.AmberInitializer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Constants.MOD_ID)
public class AmberForge {

    public AmberForge(FMLJavaModLoadingContext ctx) {
        AmberInitializer.initialize(Constants.MOD_ID);
        // Store the event bus internally for Amber's use
        AmberInitializer.setEventBus(Constants.MOD_ID, ctx.getModBusGroup());
        
        // Register networking event listeners if needed in the future
        // This is a placeholder for potential future Forge networking event registration
        
        AmberMod.init();
    }
}
