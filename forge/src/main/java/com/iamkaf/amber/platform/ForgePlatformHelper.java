package com.iamkaf.amber.platform;

import com.iamkaf.amber.api.platform.v1.ModInfo;
import com.iamkaf.amber.platform.services.IPlatformHelper;
import com.iamkaf.amber.util.Env;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
//? if <=1.21.10 {
/*import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;*/
//?} else {
import net.minecraftforge.fml.loading.LoadingModList;
//?}
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Collection;
//? if <=1.21.10 {
/*import java.util.Optional;*/
//?}
import java.util.stream.Collectors;

public class ForgePlatformHelper implements IPlatformHelper {
    @Override
    public String getPlatformName() {

        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return isModLoadedByLoader(modId);
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
        return getModIdsFromLoader();
    }

    @Override
    public @Nullable com.iamkaf.amber.api.platform.v1.ModInfo getModInfo(String modId) {
        return getModInfoFromLoader(modId);
    }

    //? if <=1.21.10 {
    /*private boolean isModLoadedByLoader(String modId) {
        return ModList.get().isLoaded(modId);
    }

    private Collection<String> getModIdsFromLoader() {
        return ModList.get().getMods().stream().map(IModInfo::getModId).collect(Collectors.toList());
    }

    *///?}

    //? if <=1.18.2 {
    /*private @Nullable ModInfo getModInfoFromLoader(String modId) {
        return ModList.get().getModContainerById(modId)
                .map(container -> new ModInfo(
                        container.getModId(),
                        container.getModInfo().getDisplayName(),
                        container.getModInfo().getVersion().toString(),
                        container.getModInfo().getDescription()
                ))
                .orElse(null);
    }
    *///?}

    //? if >1.18.2 && <=1.20.4 {
    /*private @Nullable ModInfo getModInfoFromLoader(String modId) {
        return ModList.get().getModContainerById(modId)
                .map(container -> new ModInfo(
                        container.getModId(),
                        container.getModInfo().getDisplayName(),
                        container.getModInfo().getVersion().toString(),
                        container.getModInfo().getDescription()
                ))
                .orElse(null);
    }
    *///?}

    //? if >1.20.4 && <=1.21.10 {
    /*private @Nullable ModInfo getModInfoFromLoader(String modId) {
        Optional<ModContainer> maybeContainer =
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
        return null;
    }
    *///?}

    //? if >1.21.10 && <26.1 {
    private boolean isModLoadedByLoader(String modId) {
        return LoadingModList.get().getModFileById(modId) != null;
    }

    private Collection<String> getModIdsFromLoader() {
        return LoadingModList.get().getMods().stream()
                .map(net.minecraftforge.fml.loading.moddiscovery.ModInfo::getModId)
                .collect(Collectors.toList());
    }

    private @Nullable ModInfo getModInfoFromLoader(String modId) {
        net.minecraftforge.fml.loading.moddiscovery.ModInfo maybeInfo = LoadingModList.get().getMods().stream()
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
    //?}

    //? if >=26.1 {
    private boolean isModLoadedByLoader(String modId) {
        return LoadingModList.getModFileById(modId) != null;
    }

    private Collection<String> getModIdsFromLoader() {
        return LoadingModList.getMods().stream()
                .map(net.minecraftforge.fml.loading.moddiscovery.ModInfo::getModId)
                .collect(Collectors.toList());
    }

    private @Nullable ModInfo getModInfoFromLoader(String modId) {
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
    //?}
}
