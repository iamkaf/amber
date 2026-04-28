package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.FarmingEvents;
import net.minecraft.core.BlockPos;
//? if >=1.15
import net.minecraft.server.level.ServerLevel;
//? if <1.15
/*import net.minecraft.world.level.Level;*/
//? if >=1.19
import net.minecraft.util.RandomSource;
//? if <1.19
/*import java.util.Random;*/
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CropBlock.class)
public abstract class CropBlockMixin {

    @Inject(
            //? if >=1.16
            method = "randomTick",
            //? if <1.16
            /*method = "tick",*/
            at = @At("HEAD"),
            cancellable = true
    )
    private void onCropGrow(BlockState state,
            //? if >=1.15
            ServerLevel level,
            //? if <1.15
            /*Level level,*/
            BlockPos pos,
            //? if >=1.19
            RandomSource random,
            //? if <1.19
            /*Random random,*/
            CallbackInfo ci) {
        InteractionResult result = FarmingEvents.CROP_GROW.invoker().onCropGrow(
                level, pos, state
        );

        if (result != InteractionResult.PASS) {
            ci.cancel();
        }
    }

    static {
        AmberMod.AMBER_MIXINS.add("CropBlockMixin");
    }
}
