package com.iamkaf.amber;

import com.iamkaf.amber.api.core.v2.AmberInitializer;
import com.iamkaf.amber.api.core.v2.AmberModInfo;
import com.iamkaf.amber.api.event.v1.events.common.ItemEvents;
import com.iamkaf.amber.api.event.v1.events.common.PlayerEvents;
import com.iamkaf.amber.api.platform.v1.Platform;
import com.iamkaf.amber.command.AmberCommands;
import com.iamkaf.amber.networking.v1.AmberNetworking;
import com.iamkaf.amber.platform.Services;
import com.iamkaf.amber.util.Env;
import com.iamkaf.amber.util.EnvExecutor;
import net.minecraft.world.InteractionResult;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Common entry point for the Amber mod.
 * Replace the contents with your own implementation.
 */
public class AmberMod {
    public static final ArrayList<AmberModInfo> AMBER_MODS = new ArrayList<>();
    public static final ArrayList<String> AMBER_MIXINS = new ArrayList<>();

    /**
     * Called during mod initialization for all loaders.
     */
    public static void init() {
        Constants.LOG.info("Initializing Everlasting Amber Dreams on {}...", Services.PLATFORM.getPlatformName());

        Services.AMBER_EVENT_SETUP.registerCommon();
        EnvExecutor.runInEnv(Env.CLIENT, () -> Services.AMBER_EVENT_SETUP::registerClient);
        EnvExecutor.runInEnv(Env.SERVER, () -> Services.AMBER_EVENT_SETUP::registerServer);

        // Initialize Amber's internal networking system
        AmberNetworking.initialize();
        // Register commands
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
