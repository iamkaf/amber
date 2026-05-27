package com.iamkaf.amber.platform.services;

import com.iamkaf.amber.api.registry.v1.creativetabs.TabBuilder;
import net.minecraft.world.item.CreativeModeTab;

/**
 * Service providing loader-specific creative mode tab construction.
 */
public interface ICreativeModeTabService {
    CreativeModeTab build(TabBuilder builder);
}
