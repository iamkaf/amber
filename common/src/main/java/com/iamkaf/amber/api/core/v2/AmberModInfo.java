package com.iamkaf.amber.api.core.v2;

import org.jetbrains.annotations.Nullable;

/**
 * Represents information about an Amber mod.
 *
 * @param id      The unique identifier of the mod.
 * @param name    The human-readable name of the mod.
 * @param version The version of the mod.
 * @param side    The side the mod is intended for (common, client, or server).
 * @param eventBus An optional event bus object for the mod, can be null.
 */
public record AmberModInfo(String id, String name, String version,
                           com.iamkaf.amber.api.core.v2.AmberModInfo.AmberModSide side, @Nullable Object eventBus) {

    public enum AmberModSide {
        COMMON,
        CLIENT,
        SERVER
    }
}
