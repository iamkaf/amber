package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.EntityEvent;
import com.iamkaf.amber.event.internal.CommonEventHooks;
import net.minecraft.core.dispenser.ShearsDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ShearsDispenseItemBehavior.class)
public abstract class ShearsDispenseItemBehaviorMixin {
    @Redirect(
            method = "tryShearEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Shearable;shear(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/sounds/SoundSource;Lnet/minecraft/world/item/ItemStack;)V"
            )
    )
    private static void amber$onDispenseShear(Shearable shearable, ServerLevel level, SoundSource soundSource, ItemStack stack) {
        CommonEventHooks.pushShearSource(null, stack, EntityEvent.ShearSource.DISPENSER);
        try {
            shearable.shear(level, soundSource, stack);
        } finally {
            CommonEventHooks.popShearSource();
        }
    }

    static {
        AmberMod.AMBER_MIXINS.add("ShearsDispenseItemBehaviorMixin");
    }
}
