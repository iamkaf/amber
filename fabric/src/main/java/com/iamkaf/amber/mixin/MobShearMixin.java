//? if >=26.2 {
package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.event.EntityShearing;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Shears remove a mob's saddle, harness, or other equipment through {@code shearItem}.
 */
@Mixin(Mob.class)
public abstract class MobShearMixin {
    @Inject(method = "shearItem", at = @At("HEAD"))
    private void amber$reportEquipmentShear(CallbackInfo ci) {
        EntityShearing.sheared((Mob) (Object) this);
    }

    static {
        AmberMod.AMBER_MIXINS.add("MobShearMixin");
    }
}
//?}
