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
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
//? if <=1.18.2 {
/*import net.minecraftforge.registries.ForgeRegistries;*/
//?}
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class ForgeRegistrarManager implements IRegistrarManager {
    private final Map<String, Map<ResourceKey<? extends Registry<?>>, DeferredRegister<?>>> registers = new HashMap<>();

    //? if <=1.18.2 {
    /*@SuppressWarnings({"rawtypes", "unchecked"})
    private DeferredRegister getRegister(String modId, ResourceKey<? extends Registry<?>> key) {
        Map<ResourceKey<? extends Registry<?>>, DeferredRegister<?>> map = registers.computeIfAbsent(modId, m -> new HashMap<>());
        return (DeferredRegister) map.computeIfAbsent(key, k -> {
            DeferredRegister<?> reg;
            if (Registry.ITEM_REGISTRY.equals(key)) {
                reg = DeferredRegister.create(ForgeRegistries.ITEMS, modId);
            } else {
                throw new IllegalArgumentException("Unsupported Forge registry on 1.18: " + key.location());
            }
            reg.register(FMLJavaModLoadingContext.get().getModEventBus());
            return reg;
        });
    }
    *///?}

    //? if >1.18.2 && <=1.20.4 {
    @SuppressWarnings({"unchecked", "removal"})
    private <T> DeferredRegister<T> getRegister(String modId, ResourceKey<Registry<T>> key) {
        Map<ResourceKey<? extends Registry<?>>, DeferredRegister<?>> map = registers.computeIfAbsent(modId, m -> new HashMap<>());
        return (DeferredRegister<T>) map.computeIfAbsent(key, k -> {
            DeferredRegister<T> reg = DeferredRegister.create(key, modId);
            reg.register(FMLJavaModLoadingContext.get().getModEventBus());
            return reg;
        });
    }
    //?}

    //? if >1.20.4 {
    @SuppressWarnings({"unchecked", "removal"})
    private <T> DeferredRegister<T> getRegister(String modId, ResourceKey<Registry<T>> key) {
        Map<ResourceKey<? extends Registry<?>>, DeferredRegister<?>> map = registers.computeIfAbsent(modId, m -> new HashMap<>());
        return (DeferredRegister<T>) map.computeIfAbsent(key, k -> {
            DeferredRegister<T> reg = DeferredRegister.create(key, modId);
            reg.register(FMLJavaModLoadingContext.get().getModBusGroup());
            return reg;
        });
    }
    //?}

    @Override
    public <T> Registrar<T> create(String modId, ResourceKey<Registry<T>> key) {
        return new ForgeRegistrar<>(key, getRegister(modId, key));
    }

    private static class ForgeRegistrar<T> implements Registrar<T> {
        private final ResourceKey<Registry<T>> key;
        //? if <=1.18.2 {
        /*private final DeferredRegister register;*/
        //?} else {
        private final DeferredRegister<T> register;
        //?}

        //? if <=1.18.2 {
        /*ForgeRegistrar(ResourceKey<Registry<T>> key, DeferredRegister register) {*/
        //?} else {
        ForgeRegistrar(ResourceKey<Registry<T>> key, DeferredRegister<T> register) {
        //?}
            this.key = key;
            this.register = register;
        }

        @Override
        //? if <1.21.11 {
        /*public <R extends T> RegistrySupplier<R> register(ResourceLocation id, Supplier<? extends R> supplier) {*/
        //?} else {
        public <R extends T> RegistrySupplier<R> register(Identifier id, Supplier<? extends R> supplier) {
        //?}
            //? if <=1.18.2 {
            /*RegistryObject<?> obj = register.register(id.getPath(), (Supplier) supplier);
            return new ForgeRegistrySupplier<>(registryId(), id, obj);*/
            //?} else {
            RegistryObject<R> obj = register.register(id.getPath(), supplier);
            return new ForgeRegistrySupplier<>(registryId(), id, obj);
            //?}
        }

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
    }

    private static class ForgeRegistrySupplier<R> implements RegistrySupplier<R> {
        //? if <1.21.11 {
        /*private final ResourceLocation registryId;
        private final ResourceLocation id;*/
        //?} else {
        private final Identifier registryId;
        private final Identifier id;
        //?}
        //? if <=1.18.2 {
        /*private final RegistryObject<?> obj;*/
        //?} else {
        private final RegistryObject<R> obj;
        //?}

        //? if <=1.18.2 {
        /*ForgeRegistrySupplier(ResourceLocation registryId, ResourceLocation id, RegistryObject<?> obj) {*/
        //?} else if <1.21.11 {
        /*ForgeRegistrySupplier(ResourceLocation registryId, ResourceLocation id, RegistryObject<R> obj) {*/
        //?} else {
        ForgeRegistrySupplier(Identifier registryId, Identifier id, RegistryObject<R> obj) {
        //?}
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
            //? if <=1.18.2 {
            /*return (R) obj.get();*/
            //?} else {
            return obj.get();
            //?}
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
