package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.BlockEvents;
import com.iamkaf.amber.event.internal.CommonEventHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LightningBolt.class)
public abstract class LightningBoltIgnitionMixin {
    @Redirect(
            method = "spawnFire",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z",
                    ordinal = 0
            )
    )
    private boolean amber$onPrimaryLightningFire(ServerLevel level, BlockPos pos, BlockState state) {
        boolean result = level.setBlockAndUpdate(pos, state);
        if (result) {
            CommonEventHooks.fireBlockIgnite(
                    level,
                    pos,
                    state,
                    null,
                    net.minecraft.world.item.ItemStack.EMPTY,
                    BlockEvents.BlockIgnitionSource.LIGHTNING
            );
        }
        return result;
    }

    @Redirect(
            method = "spawnFire",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z",
                    ordinal = 1
            )
    )
    private boolean amber$onSecondaryLightningFire(ServerLevel level, BlockPos pos, BlockState state) {
        boolean result = level.setBlockAndUpdate(pos, state);
        if (result) {
            CommonEventHooks.fireBlockIgnite(
                    level,
                    pos,
                    state,
                    null,
                    net.minecraft.world.item.ItemStack.EMPTY,
                    BlockEvents.BlockIgnitionSource.LIGHTNING
            );
        }
        return result;
    }

    static {
        AmberMod.AMBER_MIXINS.add("LightningBoltIgnitionMixin");
    }
}
