package com.iamkaf.amber.api.registry.v1;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Simple manager for registry access.
 */
public final class RegistrarManager {
    private static final Map<String, RegistrarManager> MANAGER = new HashMap<>();

    private final String modId;
    private final Map<ResourceKey<? extends Registry<?>>, Registrar<?>> registrars = new HashMap<>();

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
        return (Registrar<T>) registrars.computeIfAbsent(key, k -> new SimpleRegistrar<>(key));
    }

    public String getModId() {
        return modId;
    }

    private static class SimpleRegistrar<T> implements Registrar<T> {
        private final ResourceKey<Registry<T>> key;

        SimpleRegistrar(ResourceKey<Registry<T>> key) {
            this.key = key;
        }

        @Override
        public <R extends T> RegistrySupplier<R> register(ResourceLocation id, Supplier<? extends R> supplier) {
            Registry<T> registry = registry();
            R value = supplier.get();
            Registry.register(registry, id, value);
            return new SimpleRegistrySupplier<>(key.location(), id, value);
        }

        @SuppressWarnings("unchecked")
        private Registry<T> registry() {
//            return (Registry<T>) BuiltInRegistries.REGISTRY.get(key.location()).get();
            return (Registry<T>) BuiltInRegistries.REGISTRY.getValue(key.location());
        }

        @Override
        public ResourceKey<? extends Registry<T>> key() {
            return key;
        }

        @Override
        public Optional<Holder.Reference<T>> get(ResourceLocation id) {
            return registry().get(id);
        }
    }

    private static class SimpleRegistrySupplier<R> implements RegistrySupplier<R> {
        private final ResourceLocation registryId;
        private final ResourceLocation id;
        private final R value;

        SimpleRegistrySupplier(ResourceLocation registryId, ResourceLocation id, R value) {
            this.registryId = registryId;
            this.id = id;
            this.value = value;
        }

        @Override
        public boolean isPresent() {
            return true;
        }

        @Override
        public R get() {
            return value;
        }

        @Override
        public ResourceLocation getRegistryId() {
            return registryId;
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }
    }
}
