package com.iamkaf.amber.platform;

import com.iamkaf.amber.api.platform.v1.ModInfo;
import com.iamkaf.amber.platform.services.IPlatformHelper;
import com.iamkaf.amber.util.Env;
//? if <26.1 {
/*import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;
*///?}
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
//? if >=26.1
import net.minecraftforge.fml.loading.LoadingModList;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Collection;
//? if <26.1
/*import java.util.Optional;*/
import java.util.stream.Collectors;

public class ForgePlatformHelper implements IPlatformHelper {
    @Override
    public String getPlatformName() {

        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        //? if >=26.1
        return LoadingModList.getModFileById(modId) != null;
        //? if <26.1
        /*return ModList.get().isLoaded(modId);*/
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
        //? if >=26.1 {
        return LoadingModList.getMods().stream()
                .map(net.minecraftforge.fml.loading.moddiscovery.ModInfo::getModId)
                .collect(Collectors.toList());
        //?} else {
        /*return ModList.get().getMods().stream().map(IModInfo::getModId).collect(Collectors.toList());*/
        //?}
    }

    @Override
    public @Nullable com.iamkaf.amber.api.platform.v1.ModInfo getModInfo(String modId) {
        //? if >=26.1 {
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
        //?} else {
        /*Optional<ModContainer> maybeContainer =
                ModList.get().getLoadedMods().stream().filter(c -> c.getModId().equals(modId)).findFirst();
        if (maybeContainer.isPresent()) {
            ModContainer container = maybeContainer.get();
            return new ModInfo(
                    container.getModId(),
                    container.getModInfo().getDisplayName(),
                    container.getModInfo().getVersion().toString(),
                    container.getModInfo().getDescription()
            );
        }
        return null;*/
        //?}
    }
}
