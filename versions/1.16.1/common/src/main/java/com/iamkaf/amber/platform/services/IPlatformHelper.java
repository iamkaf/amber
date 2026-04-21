package com.iamkaf.amber.platform.services;

import com.iamkaf.amber.api.platform.v1.ModInfo;
import com.iamkaf.amber.util.Env;

import java.util.Collection;

public interface IPlatformHelper {
    String getPlatformName();

    boolean isModLoaded(String modId);

    boolean isDevelopmentEnvironment();

    java.nio.file.Path getConfigDirectory();

    default String getEnvironmentName() {
        return isDevelopmentEnvironment() ? "development" : "production";
    }

    Env getEnvironment();

    Collection<String> getModIds();

    ModInfo getModInfo(String modId);
}
