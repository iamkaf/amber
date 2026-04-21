package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.PlayerEvents;
import com.iamkaf.amber.event.internal.CommonEventHooks;
import com.iamkaf.amber.event.internal.FishingHookEventBridge;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FishingHook.class)
public abstract class FishingHookMixin implements FishingHookEventBridge {
    @Shadow
    public abstract @Nullable Player getPlayerOwner();

    @Shadow
    @Nullable
    private Entity hookedIn;

    @Unique
    private List<ItemStack> amber$caughtItems = new ArrayList<>();

    @Unique
    private int amber$capturedExperience;

    @Unique
    private boolean amber$successfulCatch;

    @Unique
    private boolean amber$stopFired;

    @Inject(method = "retrieve", at = @At("HEAD"))
    private void amber$beforeRetrieve(ItemStack rod, CallbackInfoReturnable<Integer> cir) {
        this.amber$caughtItems = new ArrayList<>();
        this.amber$capturedExperience = 0;
        this.amber$successfulCatch = false;
    }

    @Redirect(
            method = "retrieve",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"
            )
    )
    private boolean amber$captureFishingRewards(Level level, Entity entity) {
        boolean spawned = level.addFreshEntity(entity);
        if (spawned) {
            if (entity instanceof ItemEntity itemEntity) {
                this.amber$caughtItems.add(itemEntity.getItem().copy());
            } else if (entity instanceof ExperienceOrb experienceOrb) {
                this.amber$capturedExperience += experienceOrb.getValue();
            }
        }
        return spawned;
    }

    @Inject(method = "retrieve", at = @At("RETURN"))
    private void amber$afterRetrieve(ItemStack rod, CallbackInfoReturnable<Integer> cir) {
        if (!(this.getPlayerOwner() instanceof ServerPlayer serverPlayer)) {
            return;
        }

        int resultCode = cir.getReturnValue();
        if (resultCode == 1 && !this.amber$caughtItems.isEmpty()) {
            this.amber$successfulCatch = true;
            CommonEventHooks.fireFishingSuccess(
                    serverPlayer,
                    rod,
                    (FishingHook) (Object) this,
                    this.amber$caughtItems.get(0),
                    null,
                    this.amber$capturedExperience
            );
        } else if ((resultCode == 3 || resultCode == 5) && this.hookedIn != null) {
            this.amber$successfulCatch = true;
            CommonEventHooks.fireFishingSuccess(serverPlayer, rod, (FishingHook) (Object) this, ItemStack.EMPTY, this.hookedIn, 0);
        }

        if (resultCode != 0) {
            this.amber$fireStopInternal(serverPlayer, rod, PlayerEvents.FishingStopReason.REELED_IN, this.amber$successfulCatch);
        }
    }

    @Inject(
            method = "catchingFish",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/syncher/SynchedEntityData;set(Lnet/minecraft/network/syncher/EntityDataAccessor;Ljava/lang/Object;)V",
                    ordinal = 1,
                    shift = At.Shift.AFTER
            )
    )
    private void amber$onFishingBite(BlockPos pos, CallbackInfo ci) {
        if (this.getPlayerOwner() instanceof ServerPlayer serverPlayer) {
            CommonEventHooks.fireFishingBite(serverPlayer, CommonEventHooks.findFishingRod(serverPlayer), (FishingHook) (Object) this);
        }
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/projectile/FishingHook;discard()V",
                    ordinal = 1,
                    shift = At.Shift.BEFORE
            )
    )
    private void amber$onFishingTimeout(CallbackInfo ci) {
        if (this.getPlayerOwner() instanceof ServerPlayer serverPlayer) {
            this.amber$fireStopInternal(
                    serverPlayer,
                    CommonEventHooks.findFishingRod(serverPlayer),
                    PlayerEvents.FishingStopReason.TIME_OUT,
                    false
            );
        }
    }

    @Inject(method = "shouldStopFishing", at = @At("RETURN"))
    private void amber$onShouldStopFishing(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && player instanceof ServerPlayer serverPlayer) {
            ItemStack rod = CommonEventHooks.findFishingRod(serverPlayer);
            PlayerEvents.FishingStopReason reason = rod.isEmpty()
                    ? PlayerEvents.FishingStopReason.ROD_BROKE
                    : PlayerEvents.FishingStopReason.HOOK_ESCAPED;
            this.amber$fireStopInternal(serverPlayer, rod, reason, false);
        }
    }

    @Override
    public void amber$fireStop(PlayerEvents.FishingStopReason reason, boolean wasSuccessful) {
        if (this.getPlayerOwner() instanceof ServerPlayer serverPlayer) {
            this.amber$fireStopInternal(serverPlayer, CommonEventHooks.findFishingRod(serverPlayer), reason, wasSuccessful);
        }
    }

    @Unique
    private void amber$fireStopInternal(
            ServerPlayer player,
            ItemStack rod,
            PlayerEvents.FishingStopReason reason,
            boolean wasSuccessful
    ) {
        if (this.amber$stopFired) {
            return;
        }

        this.amber$stopFired = true;
        CommonEventHooks.fireFishingStop(player, rod, (FishingHook) (Object) this, reason, wasSuccessful);
    }

    static {
        AmberMod.AMBER_MIXINS.add("FishingHookMixin");
    }
}
