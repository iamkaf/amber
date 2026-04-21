package com.iamkaf.amber.api.registry.v1.creativetabs;

import com.iamkaf.amber.Constants;
import com.iamkaf.amber.api.registry.v1.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class CreativeModeTabRegistry {
    private static final Map<ResourceLocation, TabBuilder> TAB_BUILDERS = new HashMap<>();

    private CreativeModeTabRegistry() {
    }

    public static TabBuilder builder(String id) {
        return builder(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, id));
    }

    public static TabBuilder builder(ResourceLocation id) {
        return new TabBuilder(id);
    }

    public static RegistrySupplier<CreativeModeTab> register(TabBuilder builder) {
        TAB_BUILDERS.put(builder.getId(), builder);
        return com.iamkaf.amber.api.registry.v1.RegistrarManager.get(Constants.MOD_ID)
                .get(Registries.CREATIVE_MODE_TAB)
                .register(builder.getId(), builder::build);
    }

    public static RegistrySupplier<CreativeModeTab> register(String id) {
        return register(builder(id));
    }

    public static RegistrySupplier<CreativeModeTab> register(ResourceLocation id) {
        return register(builder(id));
    }

    public static Map<ResourceLocation, TabBuilder> getTabBuilders() {
        return Collections.unmodifiableMap(TAB_BUILDERS);
    }

    public static TabBuilder getTabBuilder(ResourceLocation id) {
        return TAB_BUILDERS.get(id);
    }

    public static boolean isTabRegistered(ResourceLocation id) {
        return TAB_BUILDERS.containsKey(id);
    }
}
