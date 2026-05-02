package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.CreativeModeTabEvents;
import com.iamkaf.amber.api.event.v1.events.common.CreativeModeTabOutput;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeTab.class)
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
        String path = switch (tab.getId()) {
            case 0 -> "building_blocks";
            case 1 -> "decorations";
            case 2 -> "redstone";
            case 3 -> "transportation";
            case 6 -> "ingredients";
            case 7 -> "food";
            case 8 -> "tools";
            case 9 -> "combat";
            case 10 -> "brewing";
            default -> null;
        };
        if (path == null) {
            return null;
        }

        ResourceKey<Registry<CreativeModeTab>> registryKey = ResourceKey.createRegistryKey(
                new ResourceLocation("minecraft", "creative_mode_tab")
        );
        return ResourceKey.create(registryKey, new ResourceLocation("minecraft", path));
    }

    static {
        AmberMod.AMBER_MIXINS.add("CreativeModeTabMixin");
    }
}
