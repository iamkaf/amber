package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.CreativeModeTabEvents;
import com.iamkaf.amber.api.event.v1.events.common.CreativeModeTabOutput;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if <1.19.3 {
/*@Mixin(CreativeModeTab.class)
public abstract class CreativeModeTabMixin {
    @Inject(method = "fillItemList", at = @At("TAIL"))
    private void amber$modifyLegacyTabEntries(NonNullList<ItemStack> stacks, CallbackInfo ci) {
        ResourceKey<CreativeModeTab> tabKey = amber$tabKey((CreativeModeTab) (Object) this);
        if (tabKey == null) {
            return;
        }

        CreativeModeTabEvents.MODIFY_ENTRIES.invoker().modifyEntries(tabKey, new CreativeModeTabOutput() {
            @Override
            public void accept(ItemStack stack, CreativeModeTabOutput.TabVisibility visibility) {
                stacks.add(stack);
            }
        });
    }

    private static ResourceKey<CreativeModeTab> amber$tabKey(CreativeModeTab tab) {
        String path;
        if (tab == CreativeModeTab.TAB_BUILDING_BLOCKS) {
            path = "building_blocks";
        } else if (tab == CreativeModeTab.TAB_DECORATIONS) {
            path = "decorations";
        } else if (tab == CreativeModeTab.TAB_REDSTONE) {
            path = "redstone";
        } else if (tab == CreativeModeTab.TAB_TRANSPORTATION) {
            path = "transportation";
        } else if (tab == CreativeModeTab.TAB_MISC) {
            path = "ingredients";
        } else if (tab == CreativeModeTab.TAB_FOOD) {
            path = "food";
        } else if (tab == CreativeModeTab.TAB_TOOLS) {
            path = "tools";
        } else if (tab == CreativeModeTab.TAB_COMBAT) {
            path = "combat";
        } else if (tab == CreativeModeTab.TAB_BREWING) {
            path = "brewing";
        } else {
            return null;
        }

        return ResourceKey.create(
                ResourceKey.createRegistryKey(new net.minecraft.resources.ResourceLocation("minecraft", "creative_mode_tab")),
                new net.minecraft.resources.ResourceLocation("minecraft", path)
        );
    }

    static {
        AmberMod.AMBER_MIXINS.add("CreativeModeTabMixin");
    }
}
*///?}
