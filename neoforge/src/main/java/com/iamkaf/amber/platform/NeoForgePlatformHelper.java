package com.iamkaf.amber.platform;

import com.iamkaf.amber.platform.services.IPlatformHelper;
import com.iamkaf.amber.util.Env;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforgespi.language.IModInfo;

import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Collectors;

public class NeoForgePlatformHelper implements IPlatformHelper {
    @Override
    public String getPlatformName() {

        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.isProduction();
    }

    @Override
    public Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public Env getEnvironment() {
        switch (FMLLoader.getDist()) {
            case CLIENT -> {
                return Env.CLIENT;
            }
            case DEDICATED_SERVER -> {
                return Env.SERVER;
            }
            default -> {
                throw new IllegalStateException("Unknown environment type: " + FMLLoader.getDist());
            }
        }
    }

    @Override
    public Collection<String> getModIds() {
        return ModList.get().getMods().stream().map(IModInfo::getModId).collect(Collectors.toList());
    }
}