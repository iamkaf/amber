package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.FarmingEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
//? if >=26.1 {
import net.minecraft.world.level.block.FarmlandBlock;
//?} else
/*import net.minecraft.world.level.block.FarmBlock;*/
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if >=26.1 {
@Mixin(FarmlandBlock.class)
//?} else
/*@Mixin(FarmBlock.class)*/
public abstract class FarmlandBlockMixin extends Block {
    protected FarmlandBlockMixin(Properties properties) {
        super(properties);
    }

    //? if >=26.1 {
    @Inject(
            method = "fallOn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/FarmlandBlock;turnToDirt(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V"
            ),
            cancellable = true
    )
    private void amber$farmlandTrample(Level level, BlockState state, BlockPos pos, Entity entity,
            double fallDistance, CallbackInfo ci) {
        amber$handleFarmlandTrample(level, state, pos, entity, (float) fallDistance, ci);
        if (ci.isCancelled()) {
            super.fallOn(level, state, pos, entity, fallDistance);
        }
    }
    //?} else if >=1.21.5 {
    /*@Inject(
            method = "fallOn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/FarmBlock;turnToDirt(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V"
            ),
            cancellable = true
    )
    private void amber$farmlandTrample(Level level, BlockState state, BlockPos pos, Entity entity,
            double fallDistance, CallbackInfo ci) {
        amber$handleFarmlandTrample(level, state, pos, entity, (float) fallDistance, ci);
        if (ci.isCancelled()) {
            super.fallOn(level, state, pos, entity, fallDistance);
        }
    }
    */
    //?} else if >=1.19.4 {
    /*@Inject(
            method = "fallOn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/FarmBlock;turnToDirt(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V"
            ),
            cancellable = true
    )
    private void amber$farmlandTrample(Level level, BlockState state, BlockPos pos, Entity entity,
            float fallDistance, CallbackInfo ci) {
        amber$handleFarmlandTrample(level, state, pos, entity, fallDistance, ci);
        if (ci.isCancelled()) {
            super.fallOn(level, state, pos, entity, fallDistance);
        }
    }
    *///?} else if >=1.17 {
    /*@Inject(
            method = "fallOn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/FarmBlock;turnToDirt(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V"
            ),
            cancellable = true
    )
    private void amber$farmlandTrample(Level level, BlockState state, BlockPos pos, Entity entity,
            float fallDistance, CallbackInfo ci) {
        amber$handleFarmlandTrample(level, state, pos, entity, fallDistance, ci);
        if (ci.isCancelled()) {
            super.fallOn(level, state, pos, entity, fallDistance);
        }
    }
    *///?} else {
    /*@Inject(
            method = "fallOn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/FarmBlock;turnToDirt(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V"
            ),
            cancellable = true
    )
    private void amber$farmlandTrample(Level level, BlockPos pos, Entity entity, float fallDistance, CallbackInfo ci) {
        BlockState state = level.getBlockState(pos);
        amber$handleFarmlandTrample(level, state, pos, entity, fallDistance, ci);
        if (ci.isCancelled()) {
            super.fallOn(level, pos, entity, fallDistance);
        }
    }
    *///?}

    private void amber$handleFarmlandTrample(Level level, BlockState state, BlockPos pos, Entity entity,
            float fallDistance, CallbackInfo ci) {
        InteractionResult result = FarmingEvents.FARMLAND_TRAMPLE.invoker().onFarmlandTrample(
                level, pos, state, fallDistance, entity
        );
        if (result != InteractionResult.PASS) {
            ci.cancel();
        }
    }

    static {
        AmberMod.AMBER_MIXINS.add("FarmlandBlockMixin");
    }
}
