package com.iamkaf.amber.api.registry.v1;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Handles registration into a specific registry.
 */
public interface Registrar<T> {
    /**
     * Registers a new value.
     */
    <R extends T> RegistrySupplier<R> register(ResourceLocation id, Supplier<? extends R> supplier);

    ResourceLocation key();

    /**
     * Retrieves an already registered value.
     */
    Optional<T> get(ResourceLocation id);
}
