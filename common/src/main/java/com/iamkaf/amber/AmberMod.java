package com.iamkaf.amber;

import com.iamkaf.amber.api.core.v2.AmberInitializer;
import com.iamkaf.amber.api.core.v2.AmberModInfo;
import com.iamkaf.amber.api.platform.v1.Platform;
import com.iamkaf.amber.command.AmberCommands;
import com.iamkaf.amber.networking.v1.AmberNetworking;
import com.iamkaf.amber.platform.Services;
import com.iamkaf.amber.util.Env;
import com.iamkaf.amber.util.EnvExecutor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Common entry point for the Amber mod.
 */
public class AmberMod {
    public static final ArrayList<AmberModInfo> AMBER_MODS = new ArrayList<>();
    public static final ArrayList<String> AMBER_MIXINS = new ArrayList<>();

    /**
     * Called during mod initialization for all loaders.
     */
    public static void init() {
        Constants.LOG.info("Initializing Everlasting Amber Dreams on {}...", Services.PLATFORM.getPlatformName());

        // Hook events
        Services.AMBER_EVENT_SETUP.registerCommon();
        Services.AMBER_EVENT_SETUP.registerServer(); // TODO: move these to common
        EnvExecutor.runInEnv(Env.CLIENT, () -> Services.AMBER_EVENT_SETUP::registerClient);

        // Init Amber's own features
        AmberNetworking.initialize();
        AmberCommands.initialize();
    }

    /**
     * Gets the event bus for a specific Amber mod on Forge and NeoForge.
     *
     * @param modId the ID of the mod to get the event bus for
     * @return the event bus for the mod, or null if not found or on Fabric.
     */
    public static @Nullable Object getEventBus(String modId) {
        if (Platform.getPlatformName().equals("Fabric")) {
            return null;
        }

        return AmberInitializer.getEventBus(modId);
    }


}
