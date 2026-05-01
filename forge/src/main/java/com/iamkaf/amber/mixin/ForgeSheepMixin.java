package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.EntityEvent;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
//? if >=1.21.5
import net.minecraft.world.entity.animal.sheep.Sheep;
//? if <1.21.5 && >=1.17
/*import net.minecraft.world.entity.animal.Sheep;*/
//? if <1.17
/*import net.minecraft.entity.passive.SheepEntity;*/
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//? if >=1.17
@Mixin(Sheep.class)
//? if <1.17
/*@Mixin(SheepEntity.class)*/
public abstract class ForgeSheepMixin {
    @Inject(
            method = "onSheared",
            at = @At("RETURN")
    )
    private void amber$fireShear(Player player, ItemStack stack, Level level, BlockPos pos, int fortune,
            CallbackInfoReturnable<List<ItemStack>> cir) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        List<ItemStack> drops = cir.getReturnValue();
        EntityEvent.SHEAR.invoker().shear(
                new EntityEvent.SimpleShearingContext(
                        player instanceof ServerPlayer serverPlayer ? serverPlayer : null,
                        stack,
                        //? if >=1.17
                        (Sheep) (Object) this,
                        //? if <1.17
                        /*(SheepEntity) (Object) this,*/
                        serverLevel,
                        EntityEvent.ShearTarget.SHEEP,
                        drops,
                        !drops.isEmpty(),
                        player == null ? EntityEvent.ShearSource.DISPENSER : EntityEvent.ShearSource.PLAYER
                )
        );
    }

    static {
        AmberMod.AMBER_MIXINS.add("ForgeSheepMixin");
    }
}
