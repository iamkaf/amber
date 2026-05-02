package com.iamkaf.amber.api.registry.v1;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;

/**
 * Information about an entry that may not yet be registered.
 */
public interface DeferredSupplier<T> extends OptionalSupplier<T> {
    /**
     * Returns the identifier of the registry this supplier belongs to.
     */
    Identifier getRegistryId();

    /**
     * Returns the registry key for this supplier.
     */
    default ResourceKey<Registry<T>> getRegistryKey() {
        //? if >=1.19.3
        return ResourceKey.createRegistryKey(getRegistryId());
        //? if <1.19.3
        /*return legacyCreateRegistryKey(getRegistryId());*/
    }

    /**
     * Returns the identifier of the entry.
     */
    Identifier getId();

    /**
     * Returns the entry key.
     */
    default ResourceKey<T> getKey() {
        //? if >=1.19.3
        return ResourceKey.create(getRegistryKey(), getId());
        //? if <1.19.3
        /*return legacyCreate(getRegistryKey(), getId());*/
    }

    //? if <1.19.3 {
    /*@SuppressWarnings("unchecked")
    private static <T> ResourceKey<Registry<T>> legacyCreateRegistryKey(Identifier id) {
        try {
            return (ResourceKey<Registry<T>>) ResourceKey.class.getMethod("createRegistryKey", Identifier.class).invoke(null, id);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to create registry key for " + id, exception);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> ResourceKey<T> legacyCreate(ResourceKey<Registry<T>> registryKey, Identifier id) {
        try {
            return (ResourceKey<T>) ResourceKey.class.getMethod("create", ResourceKey.class, Identifier.class).invoke(null, registryKey, id);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to create registry entry key for " + id, exception);
        }
    }*/
    //?}
}
