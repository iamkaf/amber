package com.iamkaf.amber.platform;

import com.iamkaf.amber.api.registry.v1.Registrar;
import com.iamkaf.amber.api.registry.v1.RegistrySupplier;
import com.iamkaf.amber.platform.services.IRegistrarManager;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
//? if >1.20.4 {
import net.minecraft.core.Holder;
//?}
//? if >1.18.2 {
import net.minecraft.core.registries.BuiltInRegistries;
//?}
//? if <1.21.11 {
/*import net.minecraft.resources.ResourceLocation;*/
//?} else {
import net.minecraft.resources.Identifier;
//?}

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
        //? if <1.21.11 {
        /*public <R extends T> RegistrySupplier<R> register(ResourceLocation id, Supplier<? extends R> supplier) {*/
        //?} else {
        public <R extends T> RegistrySupplier<R> register(Identifier id, Supplier<? extends R> supplier) {
        //?}
            Registry<T> registry = registry();
            R value = supplier.get();
            Registry.register(registry, id, value);
            return new FabricRegistrySupplier<>(registryId(), id, value);
        }

        //? if <=1.18.2 {
        /*@SuppressWarnings("unchecked")
        private Registry<T> registry() {
            return (Registry<T>) Registry.REGISTRY.get(key.location());
        }
        *///?}

        //? if >1.18.2 && <=1.20.4 {
        @SuppressWarnings("unchecked")
        private Registry<T> registry() {
            return (Registry<T>) BuiltInRegistries.REGISTRY.get(key.location());
        }
        //?}

        //? if >1.20.4 && <1.21.11 {
        @SuppressWarnings("unchecked")
        private Registry<T> registry() {
            return (Registry<T>) BuiltInRegistries.REGISTRY.getValue(key.location());
        }
        //?}

        //? if >=1.21.11 {
        @SuppressWarnings("unchecked")
        private Registry<T> registry() {
            return (Registry<T>) BuiltInRegistries.REGISTRY.getValue(key.identifier());
        }
        //?}

        @Override
        public ResourceKey<? extends Registry<T>> key() {
            return key;
        }

        @Override
        //? if <=1.20.4 {
        /*public Optional<T> get(ResourceLocation id) {
            return registry().getOptional(id);*/
        //?} else if <1.21.11 {
        /*public Optional<Holder.Reference<T>> get(ResourceLocation id) {
            return registry().get(id);*/
        //?} else {
        public Optional<Holder.Reference<T>> get(Identifier id) {
            return registry().get(id);
        //?}
        }

        //? if <1.21.11 {
        /*private ResourceLocation registryId() {
            return key.location();*/
        //?} else {
        private Identifier registryId() {
            return key.identifier();
        //?}
        }
    }

    private static class FabricRegistrySupplier<R> implements RegistrySupplier<R> {
        //? if <1.21.11 {
        /*private final ResourceLocation registryId;
        private final ResourceLocation id;*/
        //?} else {
        private final Identifier registryId;
        private final Identifier id;
        //?}
        private final R value;

        //? if <1.21.11 {
        /*FabricRegistrySupplier(ResourceLocation registryId, ResourceLocation id, R value) {*/
        //?} else {
        FabricRegistrySupplier(Identifier registryId, Identifier id, R value) {
        //?}
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
        //? if <1.21.11 {
        /*public ResourceLocation getRegistryId() {*/
        //?} else {
        public Identifier getRegistryId() {
        //?}
            return registryId;
        }

        @Override
        //? if <1.21.11 {
        /*public ResourceLocation getId() {*/
        //?} else {
        public Identifier getId() {
        //?}
            return id;
        }
    }
}
