package com.iamkaf.amber.api.registry.v1;

import com.iamkaf.amber.platform.Services;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple manager for registry access.
 */
public final class RegistrarManager {
    // Forge and NeoForge construct mods in parallel.
    private static final Map<String, RegistrarManager> MANAGER = new ConcurrentHashMap<>();

    private final String modId;
    private final Map<ResourceKey<? extends Registry<?>>, Registrar<?>> registrars = new ConcurrentHashMap<>();

    private RegistrarManager(String modId) {
        this.modId = modId;
    }

    /**
     * Obtains the manager for the given mod id.
     */
    public static RegistrarManager get(String modId) {
        return MANAGER.computeIfAbsent(modId, RegistrarManager::new);
    }

    @SuppressWarnings("unchecked")
    public <T> Registrar<T> get(ResourceKey<Registry<T>> key) {
        return (Registrar<T>) registrars.computeIfAbsent(key, k -> Services.REGISTRAR_MANAGER.create(modId, key));
    }

    public String getModId() {
        return modId;
    }

}
