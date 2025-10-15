package com.iamkaf.amber;

import com.iamkaf.amber.api.core.v2.AmberInitializer;
import com.iamkaf.amber.api.core.v2.AmberModInfo;
import com.iamkaf.amber.api.event.v1.events.common.AnimalEvents;
import com.iamkaf.amber.api.event.v1.events.common.FarmingEvents;
import com.iamkaf.amber.api.event.v1.events.common.ItemEvents;
import com.iamkaf.amber.api.event.v1.events.common.PlayerEvents;
import com.iamkaf.amber.api.event.v1.events.common.examples.ServerTickExample;
import com.iamkaf.amber.api.platform.v1.Platform;
import com.iamkaf.amber.command.AmberCommands;
import com.iamkaf.amber.networking.v1.AmberNetworking;
import com.iamkaf.amber.platform.Services;
import com.iamkaf.amber.util.Env;
import com.iamkaf.amber.util.EnvExecutor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Pig;
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

        registerTestEvents();
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

    // These events are for testing the implementation across mod loaders
    // and should be removed before publishing.
    // - Kaf, 2025-10-09
    private static void registerTestEvents() {
        FarmingEvents.BONEMEAL_USE.register((level, pos, state, stack, entity) -> {
            Constants.LOG.info("Bonemeal used at {} in dimension {} with item {} by {}",
                    pos, level.dimension(), stack, entity.getDisplayName().getString());

            // test cancelling
            if (entity.isCrouching()) {
                Constants.LOG.info("Bonemeal use cancelled");
                return InteractionResult.FAIL;
            }

            return InteractionResult.PASS;
        });

        FarmingEvents.FARMLAND_TRAMPLE.register((level, pos, state, fallDistance, entity) -> {
            Constants.LOG.info("Farmland trampled at {} in dimension {} by {} falling {} blocks",
                    pos, level.dimension(), entity.getDisplayName().getString(), fallDistance);

            // test cancelling
            if (entity.isCrouching()) {
                Constants.LOG.info("Farmland trampling cancelled");
                return InteractionResult.FAIL;
            }

            return InteractionResult.PASS;
        });

        FarmingEvents.CROP_GROW.register((level, pos, state) -> {
            Constants.LOG.info("Crop grown at {} in dimension {}", pos, level.dimension());

            if (pos.equals(new BlockPos(0, 64, 0))) {
                Constants.LOG.info("Crop growth cancelled");
                return InteractionResult.FAIL;
            }

            return InteractionResult.PASS;
        });

        AnimalEvents.ANIMAL_TAME.register((animal, player) -> {
            Constants.LOG.info("Animal tamed: {} by {}",
                    animal.getDisplayName().getString(),
                    player.getDisplayName().getString());

            if (animal.getDisplayName().getString().equals("Pig")) {
                Constants.LOG.info("Animal taming cancelled");
                return InteractionResult.FAIL;
            }

            return InteractionResult.PASS;
        });

        AnimalEvents.ANIMAL_BREED.register((parentA, parentB, baby) -> {
            Constants.LOG.info("Animal bred: {} + {} -> {}",
                    parentA.getDisplayName().getString(),
                    parentB.getDisplayName().getString(),
                    baby.getDisplayName().getString());
        });

        ServerTickExample.register();

    }

}
