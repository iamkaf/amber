package com.iamkaf.amber.mixin;

import com.iamkaf.amber.api.event.v1.events.common.EntityEvent;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow
    protected float lastHurt;

    @Shadow
    public abstract boolean isDeadOrDying();

    @Shadow
    public abstract boolean isDamageSourceBlocked(DamageSource damageSource);

    @Inject(method = "hurtServer", at = @At("TAIL"))
    private void amber$afterDamage(ServerLevel level, DamageSource source, float amount,
            CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 1) float f) {
        if (!isDeadOrDying()) {
            EntityEvent.AFTER_DAMAGE.invoker()
                    .afterDamage((LivingEntity) (Object) this, source, f, amount, this.isDamageSourceBlocked(source));
        }
    }
}
