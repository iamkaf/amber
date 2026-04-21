package com.iamkaf.amber.api.registry.v1;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

/**
 * Information about an entry that may not yet be registered.
 */
public interface DeferredSupplier<T> extends OptionalSupplier<T> {
    /**
     * Returns the identifier of the registry this supplier belongs to.
     */
    ResourceLocation getRegistryId();

    /**
     * Returns the identifier of the entry.
     */
    ResourceLocation getId();
}
