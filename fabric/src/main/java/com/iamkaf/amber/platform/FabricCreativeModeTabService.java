package com.iamkaf.amber.platform;

import com.iamkaf.amber.api.registry.v1.creativetabs.TabBuilder;
//? if <1.19.3 {
/*import com.iamkaf.amber.api.event.v1.events.common.CreativeModeTabEvents;
import com.iamkaf.amber.api.event.v1.events.common.CreativeModeTabOutput;*/
//?}
import com.iamkaf.amber.platform.services.ICreativeModeTabService;
//? if >=1.19.3 && <1.20
/*import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;*/
//? if <1.19.3
/*import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;*/
import net.minecraft.world.item.CreativeModeTab;
//? if <1.19.3
/*import net.minecraft.world.item.ItemStack;*/

public class FabricCreativeModeTabService implements ICreativeModeTabService {
    @Override
    public CreativeModeTab build(TabBuilder builder) {
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
        //?} else if <1.19.3 {
        /*return FabricItemGroupBuilder.create(builder.getId())
                .icon(builder.getIcon())
                .appendItems(stacks -> {
                    for (var item : builder.getItems()) {
                        stacks.add(new ItemStack(item.get()));
                    }
                    net.minecraft.resources.ResourceKey<CreativeModeTab> tabKey = net.minecraft.resources.ResourceKey.create(
                            net.minecraft.resources.ResourceKey.createRegistryKey(new net.minecraft.resources.ResourceLocation("minecraft", "creative_mode_tab")),
                            builder.getId()
                    );
                    CreativeModeTabEvents.MODIFY_ENTRIES.invoker().modifyEntries(tabKey, new CreativeModeTabOutput() {
                        @Override
                        public void accept(ItemStack stack, CreativeModeTabOutput.TabVisibility visibility) {
                            stacks.add(stack);
                        }
                    });
                })
                .build();*/
        //?} else {
        return builder.build();
        //?}
    }
}
