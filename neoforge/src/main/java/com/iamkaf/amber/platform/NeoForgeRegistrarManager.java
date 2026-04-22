package com.iamkaf.amber.platform;

import com.iamkaf.amber.api.registry.v1.Registrar;
import com.iamkaf.amber.api.registry.v1.RegistrySupplier;
import com.iamkaf.amber.platform.services.IRegistrarManager;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
//? if <1.21.11 {
/*import net.minecraft.resources.ResourceLocation;*/
//?} else {
import net.minecraft.resources.Identifier;
//?}
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public class NeoForgeRegistrarManager implements IRegistrarManager {
    private final Map<String, Map<ResourceKey<? extends Registry<?>>, DeferredRegister<?>>> registers = new HashMap<>();

    @SuppressWarnings("unchecked")
    private <T> DeferredRegister<T> getRegister(String modId, ResourceKey<Registry<T>> key) {
        Map<ResourceKey<? extends Registry<?>>, DeferredRegister<?>> map =
                registers.computeIfAbsent(modId, m -> new HashMap<>());
        return (DeferredRegister<T>) map.computeIfAbsent(
                key, k -> {
                    DeferredRegister<T> reg = DeferredRegister.create(key, modId);
                    reg.register(Objects.requireNonNull(ModLoadingContext.get().getActiveContainer().getEventBus()));
                    return reg;
                }
        );
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
        //? if <1.21.11 {
        /*public <R extends T> RegistrySupplier<R> register(ResourceLocation id, Supplier<? extends R> supplier) {*/
        //?} else {
        public <R extends T> RegistrySupplier<R> register(Identifier id, Supplier<? extends R> supplier) {
        //?}
            DeferredHolder<T, R> holder = register.register(id.getPath(), supplier);
            return new NeoForgeRegistrySupplier<>(registryId(), id, holder);
        }

        @Override
        public ResourceKey<? extends Registry<T>> key() {
            return key;
        }

        @Override
        //? if <1.21.11 {
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

        //? if <1.21.11 {
        /*@SuppressWarnings("unchecked")
        private Registry<T> registry() {
            return (Registry<T>) BuiltInRegistries.REGISTRY.getValue(key.location());
        }
        *///?}

        //? if >=1.21.11 {
        @SuppressWarnings("unchecked")
        private Registry<T> registry() {
            return (Registry<T>) BuiltInRegistries.REGISTRY.getValue(key.identifier());
        }
        //?}
    }

    private static class NeoForgeRegistrySupplier<T, R extends T> implements RegistrySupplier<R> {
        //? if <1.21.11 {
        /*private final ResourceLocation registryId;
        private final ResourceLocation id;*/
        //?} else {
        private final Identifier registryId;
        private final Identifier id;
        //?}
        private final DeferredHolder<T, R> holder;

        //? if <1.21.11 {
        /*NeoForgeRegistrySupplier(ResourceLocation registryId, ResourceLocation id, DeferredHolder<T, R> holder) {*/
        //?} else {
        NeoForgeRegistrySupplier(Identifier registryId, Identifier id, DeferredHolder<T, R> holder) {
        //?}
            this.registryId = registryId;
            this.id = id;
            this.holder = holder;
        }

        @Override
        public boolean isPresent() {
            return holder.isBound();
        }

        @Override
        public R get() {
            return holder.get();
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
