package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.event.EntityShearing;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.ShearsDispenseItemBehavior;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.IShearable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * One dispense is one use of the shears, so shearing an entity in front of the dispenser reports one shear.
 * NeoForge shears every {@link IShearable} here, including modded ones.
 */
@Mixin(ShearsDispenseItemBehavior.class)
public abstract class ShearsDispenseItemBehaviorMixin {
    @Inject(method = "execute", at = @At("HEAD"))
    private void amber$beginShearUse(BlockSource source, ItemStack shears, CallbackInfoReturnable<ItemStack> cir) {
        EntityShearing.beginDispenserUse(source.level(), shears);
    }

    @Inject(method = "execute", at = @At("RETURN"))
    private void amber$endShearUse(CallbackInfoReturnable<ItemStack> cir) {
        EntityShearing.end();
    }

    @WrapOperation(
            //? if >=1.21.6
            method = "tryShearEntity",
            //? if <1.21.6
            /*method = "tryShearLivingEntity",*/
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/neoforged/neoforge/common/IShearable;onSheared(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Ljava/util/List;"
            )
    )
    private static List<ItemStack> amber$reportShear(IShearable shearable, Player player, ItemStack shears, Level level,
            BlockPos pos, Operation<List<ItemStack>> original) {
        if (shearable instanceof Entity entity) {
            EntityShearing.sheared(entity);
        }
        return original.call(shearable, player, shears, level, pos);
    }

    static {
        AmberMod.AMBER_MIXINS.add("ShearsDispenseItemBehaviorMixin");
    }
}
