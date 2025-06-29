package com.iamkaf.amber.api.platform.v1;

import com.iamkaf.amber.platform.Services;
import com.iamkaf.amber.util.Env;

import java.nio.file.Path;
import java.util.Collection;

/**
 * Platform utility class for getting Minecraft mod environment information.
 * This class works with different mod loaders (Forge, Fabric, etc.) and handles
 * the differences between them so you don't have to.
 *
 * <p>All methods are static and work by calling the platform service behind the scenes.</p>
 */
public class Platform {

    /**
     * Gets the main Minecraft game folder path.
     * This is usually the ".minecraft" folder on your computer.
     *
     * @return the path to the Minecraft game folder
     */
    public static Path getGameFolder() {
        return Services.PLATFORM.getConfigDirectory().getParent();
    }

    /**
     * Gets the config folder where mod settings files are stored.
     * This is usually the "config" folder inside your game directory.
     *
     * @return the path to the config folder
     */
    public static Path getConfigFolder() {
        return Services.PLATFORM.getConfigDirectory();
    }

    /**
     * Gets the mods folder where mod JAR files are stored.
     * This is the "mods" folder inside your game directory.
     *
     * @return the path to the mods folder
     */
    public static Path getModsFolder() {
        return getGameFolder().resolve("mods");
    }

    /**
     * Gets the current runtime environment type (client, server).
     *
     * @return the current environment type
     * @see Env
     */
    public static Env getEnvironment() {
        return Services.PLATFORM.getEnvironment();
    }

    /**
     * Checks if a specific mod is currently loaded and available.
     *
     * @param id the mod identifier to check for (e.g., "amber", "liteminer", "fabric-api")
     * @return {@code true} if the mod with the given ID is loaded, {@code false} otherwise
     */
    public static boolean isModLoaded(String id) {
        return Services.PLATFORM.isModLoaded(id);
    }

    /**
     * Gets a collection of all currently loaded mod identifiers.
     * This includes both the base game and all loaded mods.
     *
     * @return an unmodifiable collection containing the IDs of all loaded mods
     */
    public static Collection<String> getModIds() {
        return Services.PLATFORM.getModIds();
    }

    /**
     * Checks if the current runtime environment is a development environment.
     * This is typically {@code true} when running from an IDE or development workspace,
     * and {@code false} when running from a production/distributed installation.
     *
     * @return {@code true} if running in a development environment, {@code false} otherwise
     */
    public static boolean isDevelopmentEnvironment() {
        return Services.PLATFORM.isDevelopmentEnvironment();
    }
}