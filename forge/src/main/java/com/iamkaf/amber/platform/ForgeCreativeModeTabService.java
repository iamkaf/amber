package com.iamkaf.amber.platform;

import com.iamkaf.amber.api.registry.v1.creativetabs.TabBuilder;
import com.iamkaf.amber.platform.services.ICreativeModeTabService;
import net.minecraft.world.item.CreativeModeTab;

public class ForgeCreativeModeTabService implements ICreativeModeTabService {
    @Override
    public CreativeModeTab build(TabBuilder builder) {
        return builder.build();
    }
}
