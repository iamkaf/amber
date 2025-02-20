package com.iamkaf.amber.api.platform.v1.fabric;

import com.iamkaf.amber.util.Env;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;

import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Collectors;

public class PlatformImpl {
    public static Path getGameFolder() {
        return FabricLoader.getInstance().getGameDir().toAbsolutePath().normalize();
    }

    public static Path getConfigFolder() {
        return FabricLoader.getInstance().getConfigDir().toAbsolutePath().normalize();
    }

    public static Path getModsFolder() {
        return getGameFolder().resolve("mods");
    }

    public static Env getEnvironment() {
        return Env.fromPlatform(getEnv());
    }

    public static EnvType getEnv() {
        return FabricLoader.getInstance().getEnvironmentType();
    }

    public static boolean isModLoaded(String id) {
        return FabricLoader.getInstance().isModLoaded(id);
    }

    public static Collection<String> getModIds() {
        return FabricLoader.getInstance()
                .getAllMods()
                .stream()
                .map(ModContainer::getMetadata)
                .map(ModMetadata::getId)
                .collect(Collectors.toList());
    }

    public static boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }
}
