package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.EntityEvent;
import com.iamkaf.amber.event.internal.CommonEventHooks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin {
    @Unique
    private @Nullable Entity amber$pendingIgnitedEntity;

    @Unique
    private int amber$pendingPreviousFireTicks;

    @Redirect(
            method = "onHitEntity",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;igniteForSeconds(F)V")
    )
    private void amber$captureArrowIgnition(Entity entity, float seconds) {
        this.amber$pendingIgnitedEntity = entity;
        this.amber$pendingPreviousFireTicks = entity.getRemainingFireTicks();
        CommonEventHooks.pushEntityIgnitionSource(
                this.amber$getPlayerOwner(),
                ItemStack.EMPTY,
                EntityEvent.EntityIgnitionSource.FIRE_ARROW,
                false
        );
        try {
            entity.igniteForSeconds(seconds);
        } finally {
            CommonEventHooks.popEntityIgnitionSource();
        }
    }

    @Inject(method = "onHitEntity", at = @At("RETURN"))
    private void amber$onArrowHitEntity(EntityHitResult hitResult, CallbackInfo ci) {
        if (this.amber$pendingIgnitedEntity != null
                && !this.amber$pendingIgnitedEntity.level().isClientSide()
                && this.amber$pendingPreviousFireTicks <= 0
                && this.amber$pendingIgnitedEntity.getRemainingFireTicks() > this.amber$pendingPreviousFireTicks) {
            CommonEventHooks.pushEntityIgnitionSource(
                    this.amber$getPlayerOwner(),
                    ItemStack.EMPTY,
                    EntityEvent.EntityIgnitionSource.FIRE_ARROW,
                    false
            );
            try {
                CommonEventHooks.fireEntityIgnite(this.amber$pendingIgnitedEntity);
            } finally {
                CommonEventHooks.popEntityIgnitionSource();
            }
        }

        this.amber$pendingIgnitedEntity = null;
        this.amber$pendingPreviousFireTicks = 0;
    }

    @Unique
    private @Nullable Player amber$getPlayerOwner() {
        return ((AbstractArrow) (Object) this).getOwner() instanceof Player player ? player : null;
    }

    static {
        AmberMod.AMBER_MIXINS.add("AbstractArrowMixin");
    }
}
