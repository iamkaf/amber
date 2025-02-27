package com.iamkaf.amber.api.platform.v1;

import com.iamkaf.amber.util.Env;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.fabricmc.api.EnvType;

import java.nio.file.Path;
import java.util.Collection;

public class Platform {
    @ExpectPlatform
    public static Path getGameFolder() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Path getConfigFolder() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Path getModsFolder() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Env getEnvironment() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static EnvType getEnv() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isModLoaded(String id) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Collection<String> getModIds() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isDevelopmentEnvironment() {
        throw new AssertionError();
    }
}
