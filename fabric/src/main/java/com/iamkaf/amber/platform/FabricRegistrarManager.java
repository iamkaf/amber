package com.iamkaf.amber.platform;

import com.iamkaf.amber.api.registry.v1.Registrar;
import com.iamkaf.amber.api.registry.v1.RegistrySupplier;
import com.iamkaf.amber.platform.services.IRegistrarManager;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;

import java.util.Optional;
import java.util.function.Supplier;

public class FabricRegistrarManager implements IRegistrarManager {
    @Override
    public <T> Registrar<T> create(String modId, ResourceKey<Registry<T>> key) {
        return new FabricRegistrar<>(key);
    }

    private static class FabricRegistrar<T> implements Registrar<T> {
        private final ResourceKey<Registry<T>> key;

        FabricRegistrar(ResourceKey<Registry<T>> key) {
            this.key = key;
        }

        @Override
        public <R extends T> RegistrySupplier<R> register(Identifier id, Supplier<? extends R> supplier) {
            Registry<T> registry = registry();
            R value = supplier.get();
            Registry.register(registry, id, value);
            //? if >=1.21.11
            return new FabricRegistrySupplier<>(key.identifier(), id, value);
            //? if <1.21.11
            /*return new FabricRegistrySupplier<>(key.location(), id, value);*/
        }

        @SuppressWarnings("unchecked")
        private Registry<T> registry() {
            //? if >=1.21.11
            return (Registry<T>) BuiltInRegistries.REGISTRY.getValue(key.identifier());
            //? if <1.21.11 && >=1.21.2
            /*return (Registry<T>) BuiltInRegistries.REGISTRY.getValue(key.location());*/
            //? if <1.21.2
            /*return (Registry<T>) BuiltInRegistries.REGISTRY.get(key.location());*/
        }

        @Override
        public ResourceKey<? extends Registry<T>> key() {
            return key;
        }

        @Override
        public Optional<Holder.Reference<T>> get(Identifier id) {
            //? if >=1.21.2
            return registry().get(id);
            //? if <1.21.2
            /*return registry().getHolder(ResourceKey.create(key, id));*/
        }
    }

    private static class FabricRegistrySupplier<R> implements RegistrySupplier<R> {
        private final Identifier registryId;
        private final Identifier id;
        private final R value;

        FabricRegistrySupplier(Identifier registryId, Identifier id, R value) {
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
        public Identifier getRegistryId() {
            return registryId;
        }

        @Override
        public Identifier getId() {
            return id;
        }
    }
}
