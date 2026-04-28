package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.FarmingEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
//? if >=26.1
import net.minecraft.world.level.block.FarmlandBlock;
//? if <26.1
/*import net.minecraft.world.level.block.FarmBlock;*/
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if >=26.1
@Mixin(FarmlandBlock.class)
//? if <26.1
/*@Mixin(FarmBlock.class)*/
public abstract class FarmBlockMixin {

    //? if >=26.1
    @Inject(method = "fallOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/FarmlandBlock;turnToDirt(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V"), cancellable = true)
    //? if <26.1 && >=1.19.4
    /*@Inject(method = "fallOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/FarmBlock;turnToDirt(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V"), cancellable = true)*/
    //? if <1.19.4
    /*@Inject(method = "fallOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/FarmBlock;turnToDirt(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V"), cancellable = true)*/
    //? if >=1.21.5 {
    private void onFarmlandTrample(Level level, BlockState state, BlockPos pos, Entity entity, double fallDistance, CallbackInfo ci) {
        handleFarmlandTrample(level, state, pos, entity, (float) fallDistance, ci);
    }
    //?} else {
    /*private void onFarmlandTrample(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance, CallbackInfo ci) {
        handleFarmlandTrample(level, state, pos, entity, fallDistance, ci);
    }
    *///?}

    private void handleFarmlandTrample(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance, CallbackInfo ci) {
        InteractionResult result = FarmingEvents.FARMLAND_TRAMPLE.invoker().onFarmlandTrample(
                level, pos, state, fallDistance, entity
        );

        if (result != InteractionResult.PASS) {
            ci.cancel();
        }
    }

    static {
        AmberMod.AMBER_MIXINS.add("FarmBlockMixin");
    }
}
