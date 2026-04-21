package com.iamkaf.amber.api.core;

import net.minecraft.resources.ResourceLocation;

/**
 * @deprecated Use {@link com.iamkaf.amber.api.core.v2.AmberInitializer} instead.
 */
@Deprecated
public class AmberMod {
    private final String ID;

    public AmberMod(String modId) {
        this.ID = modId;
    }

    public ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(ID, path);
    }
}
