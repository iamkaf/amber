package com.iamkaf.mochila;

import com.iamkaf.amber.api.core.v2.AmberInitializer;
import com.iamkaf.amber.util.Env;
import com.iamkaf.amber.util.EnvExecutor;
import com.iamkaf.mochila.network.MochilaNetworking;
import com.iamkaf.mochila.platform.Services;
import com.iamkaf.mochila.registry.*;

/**
 * Common entry point for the Mochila mod.
 */
public class MochilaMod {

    /**
     * Called during mod initialization for all loaders.
     */
    public static void init() {
        Constants.LOG.info("Initializing {} on {}...", Constants.MOD_NAME, Services.PLATFORM.getPlatformName());

        AmberInitializer.initialize(Constants.MOD_ID);

        // Registries
        DataComponents.init();
        Items.init();
        CreativeModeTabs.init();
        RecipeSerializers.init();
        MochilaNetworking.init();
        EnvExecutor.runInEnv(Env.CLIENT, () -> Keybinds::init);

        Constants.LOG.info("Bag 'em up!");
    }
}
