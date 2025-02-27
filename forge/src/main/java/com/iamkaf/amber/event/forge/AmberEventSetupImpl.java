package com.iamkaf.amber.event.forge;


import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class AmberEventSetupImpl {
    public static void registerCommon() {
//        FMLJavaModLoadingContext.get().getModEventBus().register(EventHandlerCommon.class);
        MinecraftForge.EVENT_BUS.register(EventHandlerCommon.class);
    }

    public static void registerClient() {
        // No client events to register yet
    }
}
