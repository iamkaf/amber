package com.iamkaf.amber.platform.services;

import com.iamkaf.amber.api.registry.v1.Registrar;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public interface IRegistrarManager {
    <T> Registrar<T> create(String modId, ResourceLocation key);
}
