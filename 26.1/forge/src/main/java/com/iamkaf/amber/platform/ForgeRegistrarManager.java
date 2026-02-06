package com.iamkaf.amber.platform;

import com.iamkaf.amber.api.registry.v1.Registrar;
import com.iamkaf.amber.api.registry.v1.RegistrySupplier;
import com.iamkaf.amber.platform.services.IRegistrarManager;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class ForgeRegistrarManager implements IRegistrarManager {
    private final Map<String, Map<ResourceKey<? extends Registry<?>>, DeferredRegister<?>>> registers = new HashMap<>();

    @SuppressWarnings({"unchecked", "removal"})
    private <T> DeferredRegister<T> getRegister(String modId, ResourceKey<Registry<T>> key) {
        Map<ResourceKey<? extends Registry<?>>, DeferredRegister<?>> map = registers.computeIfAbsent(modId, m -> new HashMap<>());
        return (DeferredRegister<T>) map.computeIfAbsent(key, k -> {
            DeferredRegister<T> reg = DeferredRegister.create(key, modId);
            reg.register(FMLJavaModLoadingContext.get().getModBusGroup());
            return reg;
        });
    }

    @Override
    public <T> Registrar<T> create(String modId, ResourceKey<Registry<T>> key) {
        return new ForgeRegistrar<>(key, getRegister(modId, key));
    }

    private static class ForgeRegistrar<T> implements Registrar<T> {
        private final ResourceKey<Registry<T>> key;
        private final DeferredRegister<T> register;

        ForgeRegistrar(ResourceKey<Registry<T>> key, DeferredRegister<T> register) {
            this.key = key;
            this.register = register;
        }

        @Override
        public <R extends T> RegistrySupplier<R> register(Identifier id, Supplier<? extends R> supplier) {
            RegistryObject<R> obj = register.register(id.getPath(), supplier);
            return new ForgeRegistrySupplier<>(key.identifier(), id, obj);
        }

        @Override
        public ResourceKey<? extends Registry<T>> key() {
            return key;
        }

        @Override
        public Optional<Holder.Reference<T>> get(Identifier id) {
            return registry().get(id);
        }

        @SuppressWarnings("unchecked")
        private Registry<T> registry() {
            return (Registry<T>) BuiltInRegistries.REGISTRY.getValue(key.identifier());
        }
    }

    private static class ForgeRegistrySupplier<R> implements RegistrySupplier<R> {
        private final Identifier registryId;
        private final Identifier id;
        private final RegistryObject<R> obj;

        ForgeRegistrySupplier(Identifier registryId, Identifier id, RegistryObject<R> obj) {
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
        public Identifier getRegistryId() {
            return registryId;
        }

        @Override
        public Identifier getId() {
            return id;
        }
    }
}
