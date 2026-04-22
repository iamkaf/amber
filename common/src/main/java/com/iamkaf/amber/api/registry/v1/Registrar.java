package com.iamkaf.amber.api.registry.v1;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
//? if >1.20.4 {
import net.minecraft.core.Holder;
//?}
//? if <1.21.11 {
/*import net.minecraft.resources.ResourceLocation;*/
//?} else {
import net.minecraft.resources.Identifier;
//?}

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Handles registration into a specific registry.
 */
public interface Registrar<T> {
    /**
     * Registers a new value.
     */
    //? if <1.21.11 {
    /*<R extends T> RegistrySupplier<R> register(ResourceLocation id, Supplier<? extends R> supplier);*/
    //?} else {
    <R extends T> RegistrySupplier<R> register(Identifier id, Supplier<? extends R> supplier);
    //?}

    /**
     * Returns the key of the backing registry.
     */
    ResourceKey<? extends Registry<T>> key();

    /**
     * Retrieves an already registered value.
     */
    //? if <=1.20.4 {
    /*Optional<T> get(ResourceLocation id);*/
    //?} else if <1.21.11 {
    /*Optional<Holder.Reference<T>> get(ResourceLocation id);*/
    //?} else {
    Optional<Holder.Reference<T>> get(Identifier id);
    //?}
}
