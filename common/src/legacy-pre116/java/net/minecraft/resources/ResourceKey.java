package net.minecraft.resources;

import java.util.Objects;

public final class ResourceKey<T> {
    private final ResourceKey<?> registry;
    private final ResourceLocation location;

    private ResourceKey(ResourceKey<?> registry, ResourceLocation location) {
        this.registry = registry;
        this.location = Objects.requireNonNull(location);
    }

    public static <T> ResourceKey<T> create(ResourceKey<?> registry, ResourceLocation location) {
        return new ResourceKey<>(registry, location);
    }

    public static <T> ResourceKey<T> createRegistryKey(ResourceLocation location) {
        return new ResourceKey<>(null, location);
    }

    public ResourceKey<?> registry() {
        return registry;
    }

    public ResourceLocation location() {
        return location;
    }

    public ResourceLocation identifier() {
        return location;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ResourceKey)) return false;
        ResourceKey<?> other = (ResourceKey<?>) obj;
        return Objects.equals(registry, other.registry) && location.equals(other.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(registry, location);
    }

    @Override
    public String toString() {
        return "ResourceKey[" + location + "]";
    }
}
