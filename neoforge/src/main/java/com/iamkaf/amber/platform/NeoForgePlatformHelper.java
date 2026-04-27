package com.iamkaf.amber.platform;

import com.iamkaf.amber.api.platform.v1.ModInfo;
import com.iamkaf.amber.platform.services.IPlatformHelper;
import com.iamkaf.amber.util.Env;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforgespi.language.IModInfo;
import org.jetbrains.annotations.Nullable;

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

        //? if >=1.21.9
        return !FMLLoader.getCurrent().isProduction();
        //? if <1.21.9
        /*return !FMLLoader.isProduction();*/
    }

    @Override
    public Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public Env getEnvironment() {
        //? if >=1.21.9
        switch (FMLLoader.getCurrent().getDist()) {
        //? if <1.21.9
        /*switch (net.neoforged.fml.loading.FMLEnvironment.dist) {*/
            case CLIENT -> {
                return Env.CLIENT;
            }
            case DEDICATED_SERVER -> {
                return Env.SERVER;
            }
            default -> {
                //? if >=1.21.9
                throw new IllegalStateException("Unknown environment type: " + FMLLoader.getCurrent().getDist());
                //? if <1.21.9
                /*throw new IllegalStateException("Unknown environment type: " + net.neoforged.fml.loading.FMLEnvironment.dist);*/
            }
        }
    }

    @Override
    public Collection<String> getModIds() {
        return ModList.get().getMods().stream().map(IModInfo::getModId).collect(Collectors.toList());
    }

    @Override
    public @Nullable ModInfo getModInfo(String modId) {
        return ModList.get().getModContainerById(modId).map(container -> new ModInfo(
                container.getModId(),
                container.getModInfo().getDisplayName(),
                container.getModInfo().getVersion().toString(),
                container.getModInfo().getDescription()
        )).orElse(null);
    }
}
