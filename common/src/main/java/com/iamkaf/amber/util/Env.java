package com.iamkaf.amber.util;

import net.fabricmc.api.EnvType;

public enum Env {
    CLIENT,
    SERVER;

    /**
     * Converts platform-specific environment enum to platform-agnostic environment enum.
     *
     * @param type the platform-specific environment enum, could be {@link net.fabricmc.api.EnvType} or
     * {@link net.minecraftforge.api.distmarker.Dist}
     * @return the platform-agnostic environment enum
     */
    public static Env fromPlatform(Object type) {
        return type == EnvType.CLIENT ? CLIENT : type == EnvType.SERVER ? SERVER : null;
    }

    /**
     * Converts platform-agnostic environment enum to platform-specific environment enum.
     *
     * @return the platform-specific environment enum, could be {@link net.fabricmc.api.EnvType} or
     * {@link net.minecraftforge.api.distmarker.Dist}
     */
    public EnvType toPlatform() {
        return this == CLIENT ? EnvType.CLIENT : EnvType.SERVER;
    }
}