package com.iamkaf.amber.platform;

import com.iamkaf.amber.api.registry.v1.Registrar;
import com.iamkaf.amber.api.registry.v1.RegistrySupplier;
import com.iamkaf.amber.platform.services.IRegistrarManager;
//? if >=1.18.2
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
//? if >=1.19.3
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
//? if <1.18.2
/*import net.minecraftforge.registries.IForgeRegistry;*/
//? if <1.18.2
/*import net.minecraftforge.registries.RegistryManager;*/
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class ForgeRegistrarManager implements IRegistrarManager {
    private final Map<String, Map<ResourceKey<? extends Registry<?>>, DeferredRegister<?>>> registers = new HashMap<>();

    @SuppressWarnings({"unchecked", "removal"})
    private DeferredRegister getRegister(String modId, ResourceKey<? extends Registry<?>> key) {
        Map<ResourceKey<? extends Registry<?>>, DeferredRegister<?>> map = registers.computeIfAbsent(modId, m -> new HashMap<>());
        return map.computeIfAbsent(key, k -> {
            DeferredRegister reg = DeferredRegister.create(
                //? if >=1.18.2
                (ResourceKey) key,
                //? if <1.18.2
                /*(IForgeRegistry) RegistryManager.ACTIVE.getRegistry(key.location()),*/
                modId
            );
            //? if >=1.21.6
            reg.register(FMLJavaModLoadingContext.get().getModBusGroup());
            //? if <1.21.6
            /*reg.register(FMLJavaModLoadingContext.get().getModEventBus());*/
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
        @SuppressWarnings("unchecked")
        public <R extends T> RegistrySupplier<R> register(Identifier id, Supplier<? extends R> supplier) {
            RegistryObject obj = register.register(id.getPath(), supplier);
            //? if >=1.21.11
            return new ForgeRegistrySupplier<>(key.identifier(), id, obj);
            //? if <1.21.11
            /*return new ForgeRegistrySupplier<>(key.location(), id, obj);*/
        }

        @Override
        public ResourceKey<? extends Registry<T>> key() {
            return key;
        }

        @Override
        //? if >=1.18.2 {
        public Optional<Holder.Reference<T>> get(Identifier id) {
            //? if >=1.21.2
            return registry().get(id);
            //? if <1.21.2 && >=1.19.3
            /*return registry().getHolder(ResourceKey.create(key, id));*/
            //? if <1.19.3 && >=1.18.2
            /*return Optional.empty();*/
        }
        //?}
        //? if <1.18.2
        /*public Optional<T> get(Identifier id) {
            return Optional.ofNullable(registry().get(id));
        }*/

        @SuppressWarnings("unchecked")
        private Registry<T> registry() {
            //? if >=1.21.11
            return (Registry<T>) BuiltInRegistries.REGISTRY.getValue(key.identifier());
            //? if <1.21.11 && >=1.21.2
            /*return (Registry<T>) BuiltInRegistries.REGISTRY.getValue(key.location());*/
            //? if <1.21.2 && >=1.19.3
            /*return (Registry<T>) BuiltInRegistries.REGISTRY.get(key.location());*/
            //? if <1.19.3
            /*return (Registry<T>) Registry.REGISTRY.get(key.location());*/
        }
    }

    private static class ForgeRegistrySupplier<R> implements RegistrySupplier<R> {
        private final Identifier registryId;
        private final Identifier id;
        private final RegistryObject obj;

        ForgeRegistrySupplier(Identifier registryId, Identifier id, RegistryObject obj) {
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
        public Identifier getRegistryId() {
            return registryId;
        }

        @Override
        public Identifier getId() {
            return id;
        }
    }
}
