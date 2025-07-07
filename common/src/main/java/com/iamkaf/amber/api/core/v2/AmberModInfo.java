package com.iamkaf.amber.api.core.v2;

import org.jetbrains.annotations.Nullable;

/**
 * Represents information about an Amber mod.
 *
 * @param id      The unique identifier of the mod.
 * @param name    The human-readable name of the mod.
 * @param version The version of the mod.
 */
public record AmberModInfo(String id, String name, String version) {
}
