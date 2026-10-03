package com.iamkaf.amber.platform;

import com.iamkaf.amber.api.registry.v1.creativetabs.TabBuilder;
import com.iamkaf.amber.platform.services.ICreativeModeTabService;
import net.minecraft.world.item.CreativeModeTab;

//? if <1.20 {
/*import java.util.Optional;
import java.util.function.Supplier;
*///?}

public class ForgeCreativeModeTabService implements ICreativeModeTabService {
    //? if >=1.20 {
    @Override
    public CreativeModeTab build(TabBuilder builder) {
        return builder.build();
    }
    //?} else if >=1.19.3 {
    /*// Forge creates the tab in CreativeModeTabEvent.Register from the registered builders.
    @Override
    public Supplier<Optional<CreativeModeTab>> register(TabBuilder builder) {
        return () -> Optional.ofNullable(net.minecraftforge.common.CreativeModeTabRegistry.getTab(builder.getId()));
    }
    *///?} else {
    /*@Override
    public Supplier<Optional<CreativeModeTab>> register(TabBuilder builder) {
        Optional<CreativeModeTab> tab = Optional.of(builder.build());
        return () -> tab;
    }

    @Override
    public int legacyTabIndex() {
        return -1;
    }
    *///?}
}
