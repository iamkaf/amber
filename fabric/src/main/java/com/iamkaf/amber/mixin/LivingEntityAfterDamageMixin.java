package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.EntityEvent;
import com.iamkaf.amber.api.event.v1.events.common.PlayerEvents;
import net.minecraft.server.level.ServerLevel;
//? if <1.19.2
/*import net.minecraft.world.InteractionResult;*/
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
//? if <1.19.2
/*import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;*/
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LivingEntity.class)
public abstract class LivingEntityAfterDamageMixin {
    @Shadow
    protected float lastHurt;

    //? if >=1.16
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

    @Unique
    private boolean amber$shieldBlockPending;

    @Unique
    private float amber$shieldBlockedDamage;

    //? if <1.19.2 {
    /*@Inject(
            method = "hurt",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSleeping()Z"),
            cancellable = true
    )
    private void amber$fireEntityDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        InteractionResult result = EntityEvent.ENTITY_DAMAGE.invoker()
                .onEntityDamage((LivingEntity) (Object) this, source, amount);
        if (result != InteractionResult.PASS) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "die",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;broadcastEntityEvent(Lnet/minecraft/world/entity/Entity;B)V"
            )
    )
    private void amber$fireEntityDeath(DamageSource source, CallbackInfo ci) {
        EntityEvent.ENTITY_DEATH.invoker().onEntityDeath((LivingEntity) (Object) this, source);
    }
    *///?}

    @Inject(
            //? if >=1.21.2
            method = "hurtServer",
            //? if <1.21.2
            /*method = "hurt",*/
            at = @At("HEAD")
    )
    private void amber$resetAfterDamageState(
            //? if >=1.21.2
            ServerLevel level,
            DamageSource source,
            float damage,
            CallbackInfoReturnable<Boolean> cir
    ) {
        this.amber$afterDamagePending = false;
        this.amber$baseDamageTaken = 0.0F;
        this.amber$damageTaken = 0.0F;
        this.amber$blocked = false;
        this.amber$shieldBlockPending = false;
        this.amber$shieldBlockedDamage = 0.0F;
    }

    //? if >=1.21.5 {
    @Inject(
            method = "applyItemBlocking",
            at = @At("RETURN")
    )
    private void amber$captureShieldBlock(
            ServerLevel level,
            DamageSource source,
            float damage,
            CallbackInfoReturnable<Float> cir
    ) {
        float damageBlocked = cir.getReturnValueF();
        this.amber$shieldBlockPending = damageBlocked > 0.0F;
        this.amber$shieldBlockedDamage = damage;
    }
    //?}

    //? if <1.21.5 && >=1.21.2 {
    /*@Inject(
            method = "hurtServer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;hurtCurrentlyUsedShield(F)V"
            )
    )
    private void amber$captureLegacyShieldBlock(
            ServerLevel level,
            DamageSource source,
            float damage,
            CallbackInfoReturnable<Boolean> cir
    ) {
        this.amber$shieldBlockPending = true;
        this.amber$shieldBlockedDamage = damage;
    }
    *///?}

    //? if <1.21.2 {
    /*@Inject(
            method = "hurt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;hurtCurrentlyUsedShield(F)V"
            )
    )
    private void amber$captureLegacyShieldBlock(
            DamageSource source,
            float damage,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if ((LivingEntity) (Object) this instanceof Player player) {
            ItemStack shield = amber$findCarriedShield(player);
            if (!shield.isEmpty()) {
                PlayerEvents.SHIELD_BLOCK.invoker().onShieldBlock(player, shield, damage, source);
            }
        }
    }
    *///?}

    @Inject(
            //? if >=1.21.2
            method = "hurtServer",
            //? if <1.21.2
            /*method = "hurt",*/
            at = @At(
                    value = "INVOKE",
                    target =
                    //? if >=1.21.2
                    "Lnet/minecraft/world/entity/LivingEntity;actuallyHurt(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)V",
                    //? if <1.21.2
                    /*"Lnet/minecraft/world/entity/LivingEntity;actuallyHurt(Lnet/minecraft/world/damagesource/DamageSource;F)V",*/
                    ordinal = 0
            )
            //? if >=1.21.9
            , locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void amber$captureReducedDamage(
            //? if >=1.21.2
            ServerLevel level,
            DamageSource source,
            float damage,
            CallbackInfoReturnable<Boolean> cir
            //? if >=1.21.9 {
            ,
            float reducedDamage,
            ItemStack itemInUse,
            float damageBlocked,
            boolean blocked
            //?}
    ) {
        //? if >=1.21.9
        this.amber$armAfterDamage(damage - this.lastHurt, damage, blocked);
        //? if <1.21.9
        /*this.amber$armAfterDamage(damage - this.lastHurt, damage, false);*/
    }

    @Inject(
            //? if >=1.21.2
            method = "hurtServer",
            //? if <1.21.2
            /*method = "hurt",*/
            at = @At(
                    value = "INVOKE",
                    target =
                    //? if >=1.21.2
                    "Lnet/minecraft/world/entity/LivingEntity;actuallyHurt(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)V",
                    //? if <1.21.2
                    /*"Lnet/minecraft/world/entity/LivingEntity;actuallyHurt(Lnet/minecraft/world/damagesource/DamageSource;F)V",*/
                    ordinal = 1
            )
            //? if >=1.21.9
            , locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void amber$captureFullDamage(
            //? if >=1.21.2
            ServerLevel level,
            DamageSource source,
            float damage,
            CallbackInfoReturnable<Boolean> cir
            //? if >=1.21.9 {
            ,
            float reducedDamage,
            ItemStack itemInUse,
            float damageBlocked,
            boolean blocked
            //?}
    ) {
        //? if >=1.21.9
        this.amber$armAfterDamage(damage, damage, blocked);
        //? if <1.21.9
        /*this.amber$armAfterDamage(damage, damage, false);*/
    }

    @Inject(
            //? if >=1.21.2
            method = "hurtServer",
            //? if <1.21.2
            /*method = "hurt",*/
            at = @At("TAIL")
    )
    private void amber$fireAfterDamage(
            //? if >=1.21.2
            ServerLevel level,
            DamageSource source,
            float damage,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (this.amber$isDeadOrDying()) {
            return;
        }

        LivingEntity entity = (LivingEntity) (Object) this;
        if (this.amber$afterDamagePending && cir.getReturnValueZ()) {
            EntityEvent.AFTER_DAMAGE.invoker().afterDamage(
                    entity,
                    source,
                    this.amber$baseDamageTaken,
                    this.amber$damageTaken,
                    this.amber$blocked
            );
        }

        if (entity instanceof Player player && this.amber$shieldBlockPending) {
            ItemStack shield = amber$findBlockingShield(player);
            if (!shield.isEmpty()) {
                PlayerEvents.SHIELD_BLOCK.invoker().onShieldBlock(player, shield, this.amber$shieldBlockedDamage, source);
            }
        }
    }

    @Unique
    private void amber$armAfterDamage(float baseDamageTaken, float damageTaken, boolean blocked) {
        this.amber$afterDamagePending = true;
        this.amber$baseDamageTaken = baseDamageTaken;
        this.amber$damageTaken = damageTaken;
        this.amber$blocked = blocked;
    }

    @Unique
    private boolean amber$isDeadOrDying() {
        //? if >=1.16
        return this.isDeadOrDying();
        //? if <1.16
        /*return !((LivingEntity) (Object) this).isAlive();*/
    }

    @Unique
    private static ItemStack amber$findBlockingShield(Player player) {
        if (!player.isBlocking()) {
            return ItemStack.EMPTY;
        }

        return amber$findCarriedShield(player);
    }

    @Unique
    private static ItemStack amber$findCarriedShield(Player player) {
        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.getItem() instanceof ShieldItem) {
            return mainHand;
        }

        ItemStack offHand = player.getOffhandItem();
        if (offHand.getItem() instanceof ShieldItem) {
            return offHand;
        }

        return ItemStack.EMPTY;
    }

    static {
        AmberMod.AMBER_MIXINS.add("LivingEntityAfterDamageMixin");
    }
}
