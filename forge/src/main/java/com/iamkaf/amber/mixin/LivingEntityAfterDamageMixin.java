package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.EntityEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LivingEntity.class)
public abstract class LivingEntityAfterDamageMixin {
    @Shadow
    protected float lastHurt;

    @Shadow
    public abstract boolean isDeadOrDying();

    @Unique
    private boolean amber$afterDamagePending;

    @Unique
    private float amber$baseDamageTaken;

    @Unique
    private float amber$damageTaken;

    @Unique
    private boolean amber$blocked;

    @Inject(method = "hurtServer", at = @At("HEAD"))
    private void amber$resetAfterDamageState(
            ServerLevel level,
            DamageSource source,
            float damage,
            CallbackInfoReturnable<Boolean> cir
    ) {
        this.amber$afterDamagePending = false;
        this.amber$baseDamageTaken = 0.0F;
        this.amber$damageTaken = 0.0F;
        this.amber$blocked = false;
    }

    @Inject(
            method = "hurtServer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;actuallyHurt(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)V",
                    ordinal = 0
            )
            //? if >=1.21.9
            , locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void amber$captureReducedDamage(
            ServerLevel level,
            DamageSource source,
            float damage,
            CallbackInfoReturnable<Boolean> cir
            //? if >=1.21.9 {
            ,
            ItemStack itemInUse,
            float damageBlocked
            //?}
    ) {
        //? if >=1.21.9
        this.amber$armAfterDamage(damage - this.lastHurt, damage, damageBlocked > 0.0F);
        //? if <1.21.9
        /*this.amber$armAfterDamage(damage - this.lastHurt, damage, false);*/
    }

    @Inject(
            method = "hurtServer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;actuallyHurt(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)V",
                    ordinal = 1
            )
            //? if >=1.21.9
            , locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void amber$captureFullDamage(
            ServerLevel level,
            DamageSource source,
            float damage,
            CallbackInfoReturnable<Boolean> cir
            //? if >=1.21.9 {
            ,
            ItemStack itemInUse,
            float damageBlocked
            //?}
    ) {
        //? if >=1.21.9
        this.amber$armAfterDamage(damage, damage, damageBlocked > 0.0F);
        //? if <1.21.9
        /*this.amber$armAfterDamage(damage, damage, false);*/
    }

    @Inject(method = "hurtServer", at = @At("TAIL"))
    private void amber$fireAfterDamage(
            ServerLevel level,
            DamageSource source,
            float damage,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (!this.amber$afterDamagePending || !cir.getReturnValueZ() || this.isDeadOrDying()) {
            return;
        }

        EntityEvent.AFTER_DAMAGE.invoker().afterDamage(
                (LivingEntity) (Object) this,
                source,
                this.amber$baseDamageTaken,
                this.amber$damageTaken,
                this.amber$blocked
        );
    }

    @Unique
    private void amber$armAfterDamage(float baseDamageTaken, float damageTaken, boolean blocked) {
        this.amber$afterDamagePending = true;
        this.amber$baseDamageTaken = baseDamageTaken;
        this.amber$damageTaken = damageTaken;
        this.amber$blocked = blocked;
    }

    static {
        AmberMod.AMBER_MIXINS.add("LivingEntityAfterDamageMixin");
    }
}
