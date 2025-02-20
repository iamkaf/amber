package com.iamkaf.amber.api.platform.v1.neoforge;

import com.iamkaf.amber.util.Env;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforgespi.language.IModInfo;

import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Collectors;

public class PlatformImpl {
    public static Path getGameFolder() {
        return FMLPaths.GAMEDIR.get();
    }

    public static Path getConfigFolder() {
        return FMLPaths.CONFIGDIR.get();
    }

    public static Path getModsFolder() {
        return FMLPaths.MODSDIR.get();
    }

    public static Env getEnvironment() {
        return Env.fromPlatform(getEnv());
    }

    public static Dist getEnv() {
        return FMLEnvironment.dist;
    }

    public static boolean isModLoaded(String id) {
        return ModList.get().isLoaded(id);
    }

    public static Collection<String> getModIds() {
        return ModList.get().getMods().stream().map(IModInfo::getModId).collect(Collectors.toList());
    }

    public static boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }
}
