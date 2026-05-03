package com.iamkaf.amber.platform;

//? if >=1.20 {
import com.iamkaf.amber.api.event.v1.events.common.CreativeModeTabEvents;
import com.iamkaf.amber.api.event.v1.events.common.CreativeModeTabOutput;
//?}
import com.iamkaf.amber.api.registry.v1.creativetabs.TabBuilder;
//? if <1.19.3 {
/*import com.iamkaf.amber.api.event.v1.events.common.CreativeModeTabEvents;
import com.iamkaf.amber.api.event.v1.events.common.CreativeModeTabOutput;*/
//?}
import com.iamkaf.amber.platform.services.ICreativeModeTabService;
//? if >=1.20
import net.minecraft.core.registries.Registries;
//? if >=1.19.3 && <1.20
/*import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;*/
//? if >=1.20
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class FabricCreativeModeTabService implements ICreativeModeTabService {
    @Override
    public CreativeModeTab build(TabBuilder builder) {
        //? if >=1.20 {
        CreativeModeTab.Builder tabBuilder = CreativeModeTab.builder(builder.getRow(), builder.getColumn());
        tabBuilder.title(builder.getTitle());
        tabBuilder.icon(builder.getIcon());
        tabBuilder.displayItems((parameters, output) -> {
            for (var itemSupplier : builder.getItems()) {
                output.accept(new ItemStack(itemSupplier.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            }

            ResourceKey<CreativeModeTab> tabKey = ResourceKey.create(Registries.CREATIVE_MODE_TAB, builder.getId());
            CreativeModeTabEvents.MODIFY_ENTRIES.invoker().modifyEntries(tabKey, new CreativeModeTabOutput() {
                @Override
                public void accept(ItemStack stack, CreativeModeTabOutput.TabVisibility visibility) {
                    output.accept(stack, toMinecraftVisibility(visibility));
                }
            });
        });
        if (builder.isAlignedRight()) {
            tabBuilder.alignedRight();
        }
        if (!builder.shouldShowTitle()) {
            tabBuilder.hideTitle();
        }
        if (!builder.canScroll()) {
            tabBuilder.noScrollBar();
        }
        return tabBuilder.build();
        //?}
        //? if >=1.19.3 && <1.20 {
        /*CreativeModeTab.Builder tabBuilder = FabricItemGroup.builder(builder.getId());
        tabBuilder.title(builder.getTitle());
        tabBuilder.icon(builder.getIcon());
        if (builder.isAlignedRight()) {
            tabBuilder.alignedRight();
        }
        if (!builder.shouldShowTitle()) {
            tabBuilder.hideTitle();
        }
        if (!builder.canScroll()) {
            tabBuilder.noScrollBar();
        }
        return tabBuilder.build();*/
        //?}
        //? if <1.19.3
        /*return builder.build();*/
    }

    //? if >=1.20 {
    private static CreativeModeTab.TabVisibility toMinecraftVisibility(CreativeModeTabOutput.TabVisibility visibility) {
        return switch (visibility) {
            case PARENT_AND_SEARCH_TABS -> CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS;
            case PARENT_TAB_ONLY -> CreativeModeTab.TabVisibility.PARENT_TAB_ONLY;
            case SEARCH_TAB_ONLY -> CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY;
        };
    }
    //?}
}
