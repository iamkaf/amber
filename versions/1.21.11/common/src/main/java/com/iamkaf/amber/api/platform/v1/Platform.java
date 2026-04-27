package com.iamkaf.amber.api.platform.v1;

import com.iamkaf.amber.platform.Services;
import com.iamkaf.amber.util.Env;
import org.jetbrains.annotations.Nullable;

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
     * Gets the name of the current platform.
     * This is usually "Forge", "Fabric", or "NeoForge".
     *
     * @return the name of the current platform
     */
    public static String getPlatformName() {
        return Services.PLATFORM.getPlatformName();
    }

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
     * Checks if the current runtime environment is the client.
     * This is a convenience method equivalent to {@code getEnvironment() == Env.CLIENT}.
     *
     * @return {@code true} if running on the client, {@code false} if running on the server
     */
    public static boolean isClient() {
        return getEnvironment() == Env.CLIENT;
    }

    /**
     * Checks if the current runtime environment is the server.
     * This is a convenience method equivalent to {@code getEnvironment() == Env.SERVER}.
     *
     * @return {@code true} if running on the server, {@code false} if running on the client
     */
    public static boolean isServer() {
        return getEnvironment() == Env.SERVER;
    }

    /**
     * Gets the current platform name as a string.
     * Common values include "Fabric", "Forge", "NeoForge".
     *
     * @return the name of the current platform
     */
    public static String getPlatform() {
        return getPlatformName();
    }

    /**
     * Checks if the current platform is Fabric.
     *
     * @return {@code true} if running on Fabric, {@code false} otherwise
     */
    public static boolean isFabric() {
        return "Fabric".equalsIgnoreCase(getPlatformName());
    }

    /**
     * Checks if the current platform is Forge.
     *
     * @return {@code true} if running on Forge, {@code false} otherwise
     */
    public static boolean isForge() {
        return "Forge".equalsIgnoreCase(getPlatformName());
    }

    /**
     * Checks if the current platform is NeoForge.
     *
     * @return {@code true} if running on NeoForge, {@code false} otherwise
     */
    public static boolean isNeoForge() {
        return "NeoForge".equalsIgnoreCase(getPlatformName());
    }

    /**
     * Gets the logs folder where game and mod logs are stored.
     * This is typically the "logs" folder inside your game directory.
     *
     * @return the path to the logs folder
     */
    public static Path getLogsFolder() {
        return getGameFolder().resolve("logs");
    }

    /**
     * Gets the screenshots folder where game screenshots are saved.
     * This is typically the "screenshots" folder inside your game directory.
     *
     * @return the path to the screenshots folder
     */
    public static Path getScreenshotsFolder() {
        return getGameFolder().resolve("screenshots");
    }

    /**
     * Gets the resource packs folder where custom resource packs are stored.
     * This is typically the "resourcepacks" folder inside your game directory.
     *
     * @return the path to the resource packs folder
     */
    public static Path getResourcePacksFolder() {
        return getGameFolder().resolve("resourcepacks");
    }

    /**
     * Gets the shader packs folder where custom shader packs are stored.
     * This is typically the "shaderpacks" folder inside your game directory.
     *
     * @return the path to the shader packs folder
     */
    public static Path getShaderPacksFolder() {
        return getGameFolder().resolve("shaderpacks");
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

    /**
     * Gets information about a mod.
     * This includes the mod ID, name, version, and description.
     *
     * @param modId the mod identifier to get information for
     * @return a ModInfo object containing the mod's details, or null if the mod is not found
     */
    public static @Nullable ModInfo getModInfo(String modId) {
        return Services.PLATFORM.getModInfo(modId);
    }

    /**
     * Gets the version of a specific mod if it's loaded.
     *
     * @param modId the mod identifier to check
     * @return the mod version as a string, or null if the mod is not loaded or version is unavailable
     */
    public static @Nullable String getModVersion(String modId) {
        ModInfo modInfo = getModInfo(modId);
        return modInfo != null ? modInfo.version() : null;
    }

    /**
     * Gets the display name of a specific mod if it's loaded.
     *
     * @param modId the mod identifier to check
     * @return the mod's display name, or null if the mod is not loaded
     */
    public static @Nullable String getModName(String modId) {
        ModInfo modInfo = getModInfo(modId);
        return modInfo != null ? modInfo.name() : null;
    }

    /**
     * Checks if any of the specified mods are loaded.
     * This is useful for checking alternative mods or dependencies.
     *
     * @param modIds the mod identifiers to check
     * @return {@code true} if at least one of the mods is loaded, {@code false} otherwise
     */
    public static boolean isAnyModLoaded(String... modIds) {
        for (String modId : modIds) {
            if (isModLoaded(modId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if all of the specified mods are loaded.
     * This is useful for checking required dependencies.
     *
     * @param modIds the mod identifiers to check
     * @return {@code true} if all mods are loaded, {@code false} otherwise
     */
    public static boolean areAllModsLoaded(String... modIds) {
        for (String modId : modIds) {
            if (!isModLoaded(modId)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets the saves folder where world saves are stored.
     * This is typically the "saves" folder inside your game directory.
     *
     * @return the path to the saves folder
     */
    public static Path getSavesFolder() {
        return getGameFolder().resolve("saves");
    }
}