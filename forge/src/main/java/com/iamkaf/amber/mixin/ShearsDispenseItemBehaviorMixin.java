package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.event.EntityShearing;
import net.minecraft.core.dispenser.ShearsDispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
//? if >=1.20.2
import net.minecraft.core.dispenser.BlockSource;
//? if <1.20.2
/*import net.minecraft.core.BlockSource;*/
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * One dispense is one use of the shears, so shearing an entity in front of the dispenser reports one shear.
 */
@Mixin(ShearsDispenseItemBehavior.class)
public abstract class ShearsDispenseItemBehaviorMixin {
    @Inject(method = "execute", at = @At("HEAD"))
    private void amber$beginShearUse(BlockSource source, ItemStack shears, CallbackInfoReturnable<ItemStack> cir) {
        //? if >=1.20.2
        EntityShearing.beginDispenserUse(source.level(), shears);
        //? if <1.20.2
        /*EntityShearing.beginDispenserUse(source.getLevel(), shears);*/
    }

    @Inject(method = "execute", at = @At("RETURN"))
    private void amber$endShearUse(CallbackInfoReturnable<ItemStack> cir) {
        EntityShearing.end();
    }

    static {
        AmberMod.AMBER_MIXINS.add("ShearsDispenseItemBehaviorMixin");
    }
}
