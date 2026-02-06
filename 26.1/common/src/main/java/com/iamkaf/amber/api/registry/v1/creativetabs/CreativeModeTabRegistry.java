package com.iamkaf.amber.api.registry.v1.creativetabs;

import com.iamkaf.amber.Constants;
import com.iamkaf.amber.api.registry.v1.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Registry for creating and managing custom creative mode tabs.
 * <p>
 * This provides a unified way to register creative mode tabs that works
 * across all mod loaders (Fabric, Forge, NeoForge).
 * <p>
 * Example usage:
 * <pre>{@code
 * public static final RegistrySupplier<CreativeModeTab> MY_TAB = 
 *     CreativeModeTabRegistry.register(
 *         CreativeModeTabRegistry.builder("my_tab")
 *             .title(Component.translatable("itemGroup.mymod.my_tab"))
 *             .icon(MyItems.EXAMPLE_ITEM)
 *             .addItem(MyItems.EXAMPLE_ITEM)
 *     );
 * }</pre>
 */
public final class CreativeModeTabRegistry {
    private static final Map<Identifier, TabBuilder> TAB_BUILDERS = new HashMap<>();
    
    private CreativeModeTabRegistry() {}
    
    /**
     * Creates a new tab builder for registering a custom creative mode tab.
     * <p>
     * The tab ID will be namespaced with your mod ID automatically.
     * 
     * @param id The tab ID (without namespace)
     * @return A tab builder
     */
    public static TabBuilder builder(String id) {
        return builder(Identifier.fromNamespaceAndPath(Constants.MOD_ID, id));
    }
    
    /**
     * Creates a new tab builder for registering a custom creative mode tab.
     * <p>
     * Use this method if you need to specify a custom namespace.
     * 
     * @param id The tab ID (with namespace)
     * @return A tab builder
     */
    public static TabBuilder builder(Identifier id) {
        return new TabBuilder(id);
    }
    
    /**
     * Registers a tab builder.
     * <p>
     * This should be called during your mod's initialization phase.
     * The tab will be registered with the appropriate registry for the current loader.
     * 
     * @param builder The tab builder to register
     * @return A RegistrySupplier for the tab
     */
    public static RegistrySupplier<CreativeModeTab> register(TabBuilder builder) {
        TAB_BUILDERS.put(builder.getId(), builder);
        return com.iamkaf.amber.api.registry.v1.RegistrarManager.get(Constants.MOD_ID)
            .get(Registries.CREATIVE_MODE_TAB)
            .register(builder.getId(), builder::build);
    }
    
    /**
     * Registers a tab with a custom name.
     * <p>
     * This is a shortcut for {@code register(builder(id))}.
     * 
     * @param id The tab ID (without namespace)
     * @return A RegistrySupplier for the tab
     */
    public static RegistrySupplier<CreativeModeTab> register(String id) {
        return register(builder(id));
    }
    
    /**
     * Registers a tab with a custom name and namespace.
     * <p>
     * This is a shortcut for {@code register(builder(id))}.
     * 
     * @param id The tab ID (with namespace)
     * @return A RegistrySupplier for the tab
     */
    public static RegistrySupplier<CreativeModeTab> register(Identifier id) {
        return register(builder(id));
    }
    
    /**
     * Internal method to get all registered tab builders.
     * <p>
     * This is used by platform-specific implementations to access
     * the tab builders for event registration.
     * 
     * @return An unmodifiable map of all registered tab builders
     */
    public static Map<Identifier, TabBuilder> getTabBuilders() {
        return Collections.unmodifiableMap(TAB_BUILDERS);
    }
    
    /**
     * Gets a tab builder by ID.
     * 
     * @param id The tab ID
     * @return The tab builder, or null if not found
     */
    public static TabBuilder getTabBuilder(Identifier id) {
        return TAB_BUILDERS.get(id);
    }
    
    /**
     * Checks if a tab is registered.
     * 
     * @param id The tab ID
     * @return True if the tab is registered, false otherwise
     */
    public static boolean isTabRegistered(Identifier id) {
        return TAB_BUILDERS.containsKey(id);
    }
}