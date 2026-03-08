package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.BlockEvents;
import com.iamkaf.amber.event.internal.CommonEventHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FireBlock.class)
public abstract class FireBlockMixin {
    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
                    ordinal = 1
            )
    )
    private boolean amber$onTickSpread(ServerLevel level, BlockPos pos, BlockState state, int flags) {
        boolean result = level.setBlock(pos, state, flags);
        if (result) {
            CommonEventHooks.fireBlockIgnite(
                    level,
                    pos,
                    state,
                    null,
                    net.minecraft.world.item.ItemStack.EMPTY,
                    BlockEvents.BlockIgnitionSource.FIRE_SPREAD
            );
        }
        return result;
    }

    @Redirect(
            method = "checkBurnOut",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
            )
    )
    private boolean amber$onBurnOutSpread(Level level, BlockPos pos, BlockState state, int flags) {
        boolean result = level.setBlock(pos, state, flags);
        if (result) {
            CommonEventHooks.fireBlockIgnite(
                    level,
                    pos,
                    state,
                    null,
                    net.minecraft.world.item.ItemStack.EMPTY,
                    BlockEvents.BlockIgnitionSource.FIRE_SPREAD
            );
        }
        return result;
    }

    static {
        AmberMod.AMBER_MIXINS.add("FireBlockMixin");
    }
}
