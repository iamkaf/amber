package com.iamkaf.amber.api.registry.v1;

import com.iamkaf.amber.platform.Services;
import net.minecraft.core.Registry;
//? if <=1.15.2 {
/*import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceLocation;*/
//?} else {
import net.minecraft.resources.ResourceKey;
//?}

import java.util.HashMap;
import java.util.Map;

/**
 * Simple manager for registry access.
 */
public final class RegistrarManager {
    private static final Map<String, RegistrarManager> MANAGER = new HashMap<>();

    private final String modId;
    //? if <=1.15.2 {
    /*private final Map<ResourceLocation, Registrar<?>> registrars = new HashMap<>();*/
    //?} else {
    private final Map<ResourceKey<? extends Registry<?>>, Registrar<?>> registrars = new HashMap<>();
    //?}

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
    //? if <=1.15.2 {
    /*public <T> Registrar<T> get(Registry<T> key) {
        ResourceLocation registryId = Registry.REGISTRY.getKey((WritableRegistry<?>) key);
        return (Registrar<T>) registrars.computeIfAbsent(
                registryId,
                id -> Services.REGISTRAR_MANAGER.create(modId, id)
        );
    }*/
    //?} else {
    public <T> Registrar<T> get(ResourceKey<Registry<T>> key) {
        return (Registrar<T>) registrars.computeIfAbsent(key, k -> Services.REGISTRAR_MANAGER.create(modId, key));
    }
    //?}

    public String getModId() {
        return modId;
    }

}
