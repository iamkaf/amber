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
        String path = switch (amber$tabId(tab)) {
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

        return amber$resourceKey(
                amber$registryKey(new net.minecraft.resources.ResourceLocation("minecraft", "creative_mode_tab")),
                new net.minecraft.resources.ResourceLocation("minecraft", path)
        );
    }

    @SuppressWarnings("unchecked")
    private static <T> ResourceKey<T> amber$registryKey(net.minecraft.resources.ResourceLocation id) {
        try {
            return (ResourceKey<T>) ResourceKey.class.getMethod("createRegistryKey", net.minecraft.resources.ResourceLocation.class)
                    .invoke(null, id);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to create creative tab registry key", exception);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> ResourceKey<T> amber$resourceKey(ResourceKey<?> registryKey, net.minecraft.resources.ResourceLocation id) {
        try {
            return (ResourceKey<T>) ResourceKey.class
                    .getMethod("create", ResourceKey.class, net.minecraft.resources.ResourceLocation.class)
                    .invoke(null, registryKey, id);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to create creative tab resource key for " + id, exception);
        }
    }

    private static int amber$tabId(CreativeModeTab tab) {
        try {
            Object value = tab.getClass().getMethod("getId").invoke(tab);
            return value instanceof Integer id ? id : -1;
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve creative tab id", exception);
        }
    }

    static {
        AmberMod.AMBER_MIXINS.add("CreativeModeTabMixin");
    }
}
*///?}
