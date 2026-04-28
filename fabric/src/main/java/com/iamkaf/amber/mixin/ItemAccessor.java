package com.iamkaf.amber.mixin;

//? if >=1.20.5 && <1.20.6
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
//? if >=1.20.5 && <1.20.6
import org.spongepowered.asm.mixin.Mutable;
//? if >=1.20.5 && <1.20.6
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Item.class)
public interface ItemAccessor {
    //? if >=1.20.5 && <1.20.6 {
    @Accessor("components")
    @Mutable
    void amber$setComponents(DataComponentMap components);
    //?}
}
