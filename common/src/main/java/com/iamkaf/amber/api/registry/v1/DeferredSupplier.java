package com.iamkaf.amber.api.registry.v1;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
//? if <1.21.11 {
/*import net.minecraft.resources.ResourceLocation;*/
//?} else {
import net.minecraft.resources.Identifier;
//?}

/**
 * Information about an entry that may not yet be registered.
 */
public interface DeferredSupplier<T> extends OptionalSupplier<T> {
    /**
     * Returns the identifier of the registry this supplier belongs to.
     */
    //? if <1.21.11 {
    /*ResourceLocation getRegistryId();*/
    //?} else {
    Identifier getRegistryId();
    //?}

    /**
     * Returns the registry key for this supplier.
     */
    default ResourceKey<Registry<T>> getRegistryKey() {
        return ResourceKey.createRegistryKey(getRegistryId());
    }

    /**
     * Returns the identifier of the entry.
     */
    //? if <1.21.11 {
    /*ResourceLocation getId();*/
    //?} else {
    Identifier getId();
    //?}

    /**
     * Returns the entry key.
     */
    default ResourceKey<T> getKey() {
        return ResourceKey.create(getRegistryKey(), getId());
    }
}
