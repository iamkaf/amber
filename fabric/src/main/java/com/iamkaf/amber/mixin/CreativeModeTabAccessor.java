//? if <1.19.3 {
/*package com.iamkaf.amber.mixin;

import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

// Grows CreativeModeTab.TABS so a new tab has a free slot, as Fabric's own item group builder does.
@Mixin(CreativeModeTab.class)
public interface CreativeModeTabAccessor {
    @Mutable
    @Accessor("TABS")
    static void amber$setTabs(CreativeModeTab[] tabs) {
        throw new AssertionError();
    }
}
*///?}
