package com.iamkaf.amber.api.registry.v1;

import com.iamkaf.amber.platform.Services;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public final class RegistrarManager {
    private static final Map<String, RegistrarManager> MANAGER = new HashMap<>();

    private final String modId;
    private final Map<ResourceLocation, Registrar<?>> registrars = new HashMap<>();

    private RegistrarManager(String modId) {
        this.modId = modId;
    }

    public static RegistrarManager get(String modId) {
        return MANAGER.computeIfAbsent(modId, RegistrarManager::new);
    }

    @SuppressWarnings("unchecked")
    public <T> Registrar<T> get(Registry<T> key) {
        ResourceLocation registryId = Registry.REGISTRY.getKey((WritableRegistry<?>) key);
        return (Registrar<T>) registrars.computeIfAbsent(
                registryId,
                id -> Services.REGISTRAR_MANAGER.create(modId, id)
        );
    }

    public String getModId() {
        return modId;
    }
}
