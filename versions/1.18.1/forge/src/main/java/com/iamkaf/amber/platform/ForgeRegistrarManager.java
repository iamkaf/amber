package com.iamkaf.amber.platform;

import com.iamkaf.amber.api.registry.v1.Registrar;
import com.iamkaf.amber.api.registry.v1.RegistrySupplier;
import com.iamkaf.amber.platform.services.IRegistrarManager;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class ForgeRegistrarManager implements IRegistrarManager {
    private final Map<String, Map<ResourceKey<? extends Registry<?>>, DeferredRegister<?>>> registers = new HashMap<>();

    @SuppressWarnings({"rawtypes", "unchecked"})
    private DeferredRegister getRegister(String modId, ResourceKey<? extends Registry<?>> key) {
        Map<ResourceKey<? extends Registry<?>>, DeferredRegister<?>> map = registers.computeIfAbsent(modId, m -> new HashMap<>());
        return (DeferredRegister) map.computeIfAbsent(key, k -> {
            DeferredRegister<?> reg;
            if (Registry.ITEM_REGISTRY.equals(key)) {
                reg = DeferredRegister.create(ForgeRegistries.ITEMS, modId);
            } else {
                throw new IllegalArgumentException("Unsupported Forge registry on 1.18.1: " + key.location());
            }
            reg.register(FMLJavaModLoadingContext.get().getModEventBus());
            return reg;
        });
    }

    @Override
    public <T> Registrar<T> create(String modId, ResourceKey<Registry<T>> key) {
        return new ForgeRegistrar<>(key, getRegister(modId, key));
    }

    private static class ForgeRegistrar<T> implements Registrar<T> {
        private final ResourceKey<Registry<T>> key;
        private final DeferredRegister register;

        ForgeRegistrar(ResourceKey<Registry<T>> key, DeferredRegister register) {
            this.key = key;
            this.register = register;
        }

        @Override
        @SuppressWarnings({"unchecked", "rawtypes"})
        public <R extends T> RegistrySupplier<R> register(ResourceLocation id, Supplier<? extends R> supplier) {
            RegistryObject<?> obj = register.register(id.getPath(), (Supplier) supplier);
            return new ForgeRegistrySupplier<>(key.location(), id, obj);
        }

        @SuppressWarnings("unchecked")
        private Registry<T> registry() {
            return (Registry<T>) Registry.REGISTRY.get(key.location());
        }

        @Override
        public ResourceKey<? extends Registry<T>> key() {
            return key;
        }

        @Override
        public Optional<T> get(ResourceLocation id) {
            return registry().getOptional(id);
        }
    }

    private static class ForgeRegistrySupplier<R> implements RegistrySupplier<R> {
        private final ResourceLocation registryId;
        private final ResourceLocation id;
        private final RegistryObject<?> obj;

        ForgeRegistrySupplier(ResourceLocation registryId, ResourceLocation id, RegistryObject<?> obj) {
            this.registryId = registryId;
            this.id = id;
            this.obj = obj;
        }

        @Override
        public boolean isPresent() {
            return obj.isPresent();
        }

        @Override
        @SuppressWarnings("unchecked")
        public R get() {
            return (R) obj.get();
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
