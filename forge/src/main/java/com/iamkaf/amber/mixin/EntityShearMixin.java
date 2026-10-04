//? if >=1.21.6 {
package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.event.EntityShearing;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
//? if <26.2 {
/*import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
*///?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Shears cut an entity's leads, and until 26.2 they remove a mob's saddle, harness, or other equipment here.
 */
@Mixin(Entity.class)
public abstract class EntityShearMixin {
    @Inject(method = "shearOffAllLeashConnections", at = @At("RETURN"))
    private void amber$reportLeashShear(@Nullable Player player, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            EntityShearing.sheared((Entity) (Object) this);
        }
    }

    //? if <26.2 {
    /*@Inject(method = "attemptToShearEquipment", at = @At("RETURN"))
    private void amber$reportEquipmentShear(Player player, InteractionHand hand, ItemStack shears, Mob mob,
            CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            EntityShearing.sheared(mob);
        }
    }
    *///?}

    static {
        AmberMod.AMBER_MIXINS.add("EntityShearMixin");
    }
}
//?}
