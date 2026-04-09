package com.iamkaf.amber.platform;

import com.iamkaf.amber.api.platform.v1.ModInfo;
import com.iamkaf.amber.platform.services.IPlatformHelper;
import com.iamkaf.amber.util.Env;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.LoadingModList;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Collectors;

public class ForgePlatformHelper implements IPlatformHelper {
    @Override
    public String getPlatformName() {

        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return LoadingModList.getModFileById(modId) != null;
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
        return LoadingModList.getMods().stream()
                .map(net.minecraftforge.fml.loading.moddiscovery.ModInfo::getModId)
                .collect(Collectors.toList());
    }

    @Override
    public @Nullable com.iamkaf.amber.api.platform.v1.ModInfo getModInfo(String modId) {
        net.minecraftforge.fml.loading.moddiscovery.ModInfo maybeInfo = LoadingModList.getMods().stream()
                .filter(c -> c.getModId().equals(modId))
                .findFirst()
                .orElse(null);
        if (maybeInfo != null) {
            return new ModInfo(
                    maybeInfo.getModId(),
                    maybeInfo.getDisplayName(),
                    maybeInfo.getVersion().toString(),
                    maybeInfo.getDescription()
            );
        }
        return null;
    }
}
