package com.iamkaf.amber.mixin;

import com.iamkaf.amber.api.event.v1.events.common.EntityEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;


@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow
    public abstract boolean isDeadOrDying();

    @Shadow
    public abstract boolean isDamageSourceBlocked(DamageSource damageSource);

    @Inject(method = "hurt", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void amber$afterDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir, float f) {
        if (!isDeadOrDying()) {
            EntityEvent.AFTER_DAMAGE.invoker()
                    .afterDamage((LivingEntity) (Object) this, source, f, amount, this.isDamageSourceBlocked(source));
        }
    }
}
