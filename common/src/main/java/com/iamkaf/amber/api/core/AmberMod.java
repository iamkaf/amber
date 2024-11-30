package com.iamkaf.amber.api.core;

import net.minecraft.resources.ResourceLocation;

public class AmberMod {
    private final String ID;

    public AmberMod(String modId) {
        this.ID = modId;
    }

    public ResourceLocation id(String path) {
        return new ResourceLocation(ID, path);
    }
}
