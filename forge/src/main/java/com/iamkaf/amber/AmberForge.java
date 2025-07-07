package com.iamkaf.amber;

import com.iamkaf.amber.api.core.v2.AmberInitializer;
import com.iamkaf.amber.api.core.v2.AmberModInfo;
import com.iamkaf.amber.api.platform.v1.ModInfo;
import com.iamkaf.amber.api.platform.v1.Platform;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Constants.MOD_ID)
public class AmberForge {

    public AmberForge(FMLJavaModLoadingContext ctx) {
        AmberInitializer.initialize(Constants.MOD_ID);
        // Store the event bus internally for Amber's use
        AmberInitializer.setEventBus(Constants.MOD_ID, ctx.getModBusGroup());
        AmberMod.init();
    }
}
