package com.iamkaf.amber.platform;

import com.iamkaf.amber.api.registry.v1.Registrar;
import com.iamkaf.amber.api.registry.v1.RegistrySupplier;
import com.iamkaf.amber.platform.services.IRegistrarManager;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class NeoForgeRegistrarManager implements IRegistrarManager {
    private final Map<String, Map<ResourceKey<? extends Registry<?>>, DeferredRegister<?>>> registers = new HashMap<>();

    @SuppressWarnings("unchecked")
    private <T> DeferredRegister<T> getRegister(String modId, ResourceKey<Registry<T>> key) {
        Map<ResourceKey<? extends Registry<?>>, DeferredRegister<?>> map = registers.computeIfAbsent(modId, m -> new HashMap<>());
        return (DeferredRegister<T>) map.computeIfAbsent(key, k -> {
            DeferredRegister<T> reg = DeferredRegister.create(key, modId);
            reg.register(FMLJavaModLoadingContext.get().getModEventBus());
            return reg;
        });
    }

    @Override
    public <T> Registrar<T> create(String modId, ResourceKey<Registry<T>> key) {
        return new NeoForgeRegistrar<>(key, getRegister(modId, key));
    }

    private static class NeoForgeRegistrar<T> implements Registrar<T> {
        private final ResourceKey<Registry<T>> key;
        private final DeferredRegister<T> register;

        NeoForgeRegistrar(ResourceKey<Registry<T>> key, DeferredRegister<T> register) {
            this.key = key;
            this.register = register;
        }

        @Override
        public <R extends T> RegistrySupplier<R> register(ResourceLocation id, Supplier<? extends R> supplier) {
            RegistryObject<R> obj = register.register(id.getPath(), supplier);
            return new NeoForgeRegistrySupplier<>(key.location(), id, obj);
        }

        @Override
        public ResourceKey<? extends Registry<T>> key() {
            return key;
        }

        @Override
        public Optional<Holder.Reference<T>> get(ResourceLocation id) {
            return registry().get(id);
        }

        @SuppressWarnings("unchecked")
        private Registry<T> registry() {
            return (Registry<T>) BuiltInRegistries.REGISTRY.getValue(key.location());
        }
    }

    private static class NeoForgeRegistrySupplier<R> implements RegistrySupplier<R> {
        private final ResourceLocation registryId;
        private final ResourceLocation id;
        private final RegistryObject<R> obj;

        NeoForgeRegistrySupplier(ResourceLocation registryId, ResourceLocation id, RegistryObject<R> obj) {
            this.registryId = registryId;
            this.id = id;
            this.obj = obj;
        }

        @Override
        public boolean isPresent() {
            return obj.isPresent();
        }

        @Override
        public R get() {
            return obj.get();
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
