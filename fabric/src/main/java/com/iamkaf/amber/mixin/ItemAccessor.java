package com.iamkaf.amber.mixin;

//? if >=1.20.5
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
//? if >=1.20.5
import org.spongepowered.asm.mixin.Mutable;
//? if >=1.20.5
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Item.class)
public interface ItemAccessor {
    //? if >=1.20.5 {
    @Accessor("components")
    @Mutable
    void amber$setComponents(DataComponentMap components);
    //?}
}
