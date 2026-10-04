package com.iamkaf.amber.platform;

import com.iamkaf.amber.api.event.v1.events.common.CreativeModeTabOutput;
import com.iamkaf.amber.api.registry.v1.creativetabs.TabBuilder;
import com.iamkaf.amber.platform.services.ICreativeModeTabService;
import net.minecraft.world.item.CreativeModeTab;

//? if <1.20 {
/*import java.util.Optional;
import java.util.function.Supplier;
*///?}
//? if >=1.19.3 && <1.20
/*import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;*/
//? if <1.19.3 {
/*import com.iamkaf.amber.mixin.CreativeModeTabAccessor;
import java.util.Arrays;
*///?}

public class FabricCreativeModeTabService implements ICreativeModeTabService {
    //? if >=1.20 {
    @Override
    public CreativeModeTab build(TabBuilder builder) {
        return builder.applyTo(CreativeModeTab.builder(builder.getRow(), builder.getColumn()))
                .displayItems((parameters, output) -> acceptItems(builder, output))
                .build();
    }
    //?} else if >=1.19.3 {
    /*@Override
    public Supplier<Optional<CreativeModeTab>> register(TabBuilder builder) {
        // FabricItemGroup adds the tab to the game when it is built.
        Optional<CreativeModeTab> tab = Optional.of(builder.applyTo(FabricItemGroup.builder(builder.getId()))
                //? if >=1.19.4
                .displayItems((parameters, output) -> acceptItems(builder, output))
                //? if <1.19.4
                //.displayItems((flags, output, hasPermissions) -> acceptItems(builder, output))
                .build());
        return () -> tab;
    }
    *///?} else {
    /*@Override
    public Supplier<Optional<CreativeModeTab>> register(TabBuilder builder) {
        Optional<CreativeModeTab> tab = Optional.of(builder.build());
        return () -> tab;
    }

    @Override
    public int legacyTabIndex() {
        CreativeModeTab[] tabs = CreativeModeTab.TABS;
        CreativeModeTabAccessor.amber$setTabs(Arrays.copyOf(tabs, tabs.length + 1));
        return tabs.length;
    }
    *///?}

    //? if >=1.19.3 {
    // The builder's own items. MODIFY_ENTRIES reaches every tab through Fabric's MODIFY_ENTRIES_ALL event.
    private static void acceptItems(TabBuilder builder, CreativeModeTab.Output output) {
        for (var item : builder.getItems()) {
            output.accept(item.get());
        }
    }

    static CreativeModeTab.TabVisibility toMinecraftVisibility(CreativeModeTabOutput.TabVisibility visibility) {
        return switch (visibility) {
            case PARENT_AND_SEARCH_TABS -> CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS;
            case PARENT_TAB_ONLY -> CreativeModeTab.TabVisibility.PARENT_TAB_ONLY;
            case SEARCH_TAB_ONLY -> CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY;
        };
    }
    //?}
}
