package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.BlockEvents;
import com.iamkaf.amber.event.internal.CommonEventHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.hurtingprojectile.SmallFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TntBlock.class)
public abstract class TntBlockMixin {
    @Unique
    private static final ThreadLocal<ItemStack> AMBER_TNT_IGNITION_ITEM = new ThreadLocal<>();

    @Unique
    private static final ThreadLocal<BlockEvents.BlockIgnitionSource> AMBER_TNT_IGNITION_SOURCE = new ThreadLocal<>();

    @Inject(method = "useItemOn", at = @At("HEAD"))
    private void amber$captureIgniter(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult,
            CallbackInfoReturnable<InteractionResult> cir
    ) {
        if (stack.is(Items.FLINT_AND_STEEL)) {
            AMBER_TNT_IGNITION_ITEM.set(stack.copyWithCount(1));
            AMBER_TNT_IGNITION_SOURCE.set(BlockEvents.BlockIgnitionSource.FLINT_AND_STEEL);
        } else if (stack.is(Items.FIRE_CHARGE)) {
            AMBER_TNT_IGNITION_ITEM.set(stack.copyWithCount(1));
            AMBER_TNT_IGNITION_SOURCE.set(BlockEvents.BlockIgnitionSource.FIRE_CHARGE);
        }
    }

    @Inject(method = "useItemOn", at = @At("RETURN"))
    private void amber$onUseItemOn(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult,
            CallbackInfoReturnable<InteractionResult> cir
    ) {
        try {
            if (!level.isClientSide() && cir.getReturnValue().consumesAction()) {
                ItemStack ignitionItem = AMBER_TNT_IGNITION_ITEM.get();
                BlockEvents.BlockIgnitionSource source = AMBER_TNT_IGNITION_SOURCE.get();
                if (source != null && ignitionItem != null && !ignitionItem.isEmpty()) {
                    CommonEventHooks.fireBlockIgnite(level, pos, state, player, ignitionItem, source);
                }
            }
        } finally {
            AMBER_TNT_IGNITION_ITEM.remove();
            AMBER_TNT_IGNITION_SOURCE.remove();
        }
    }

    @Inject(
            method = "onProjectileHit",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z",
                    shift = At.Shift.BEFORE
            )
    )
    private void amber$onProjectileHit(Level level, BlockState state, BlockHitResult hitResult, Projectile projectile, CallbackInfo ci) {
        if (level.isClientSide()) {
            return;
        }

        @Nullable Player player = projectile.getOwner() instanceof Player owner ? owner : null;
        ItemStack ignitionItem = projectile instanceof SmallFireball ? new ItemStack(Items.FIRE_CHARGE) : ItemStack.EMPTY;
        BlockEvents.BlockIgnitionSource source = projectile instanceof SmallFireball
                ? BlockEvents.BlockIgnitionSource.FIRE_CHARGE
                : projectile instanceof AbstractArrow
                        ? BlockEvents.BlockIgnitionSource.FIRE_ARROW
                        : BlockEvents.BlockIgnitionSource.ENVIRONMENTAL;

        CommonEventHooks.fireBlockIgnite(level, hitResult.getBlockPos(), state, player, ignitionItem, source);
    }

    static {
        AmberMod.AMBER_MIXINS.add("TntBlockMixin");
    }
}
