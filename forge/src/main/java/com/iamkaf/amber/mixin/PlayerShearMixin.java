package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.event.EntityShearing;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
//? if >=26.1
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * A player's interaction with an entity is one use of the item in hand, so shearing anything off the entity during
 * it reports one shear.
 */
@Mixin(Player.class)
public abstract class PlayerShearMixin {
    @Inject(method = "interactOn", at = @At("HEAD"))
    private void amber$beginShearUse(Entity entity, InteractionHand hand,
            //? if >=26.1
            Vec3 location,
            CallbackInfoReturnable<InteractionResult> cir) {
        EntityShearing.beginPlayerUse((Player) (Object) this, hand);
    }

    @Inject(method = "interactOn", at = @At("RETURN"))
    private void amber$endShearUse(CallbackInfoReturnable<InteractionResult> cir) {
        EntityShearing.end();
    }

    static {
        AmberMod.AMBER_MIXINS.add("PlayerShearMixin");
    }
}
