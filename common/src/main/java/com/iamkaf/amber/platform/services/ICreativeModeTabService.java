package com.iamkaf.amber.platform.services;

import com.iamkaf.amber.api.registry.v1.creativetabs.TabBuilder;
import net.minecraft.world.item.CreativeModeTab;

//? if <1.20 {
/*import java.util.Optional;
import java.util.function.Supplier;
*///?}

/**
 * Service providing loader-specific creative mode tab construction.
 */
public interface ICreativeModeTabService {
    //? if >=1.20 {
    /**
     * Builds the tab that Amber registers in the creative mode tab registry.
     */
    CreativeModeTab build(TabBuilder builder);
    //?} else {
    /*// Adds the tab to the loader's tab list. The lookup stays empty until the loader has created the tab.
    Supplier<Optional<CreativeModeTab>> register(TabBuilder builder);
    *///?}

    //? if <1.19.3 {
    /*// Index for a new CreativeModeTab, or -1 when the loader's constructor appends the tab itself.
    int legacyTabIndex();
    *///?}
}
