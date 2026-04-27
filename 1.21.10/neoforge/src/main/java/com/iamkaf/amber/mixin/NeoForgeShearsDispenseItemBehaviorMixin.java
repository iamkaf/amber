package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.EntityEvent;
import com.iamkaf.amber.event.internal.CommonEventHooks;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.ShearsDispenseItemBehavior;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.IShearable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ShearsDispenseItemBehavior.class)
public abstract class NeoForgeShearsDispenseItemBehaviorMixin {
    @Redirect(
            method = "tryShearEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/neoforged/neoforge/common/IShearable;onSheared(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Ljava/util/List;"
            )
    )
    private static List<ItemStack> amber$onDispenseShear(
            IShearable shearable,
            Player player,
            ItemStack stack,
            Level level,
            BlockPos pos
    ) {
        CommonEventHooks.pushShearSource(null, stack, EntityEvent.ShearSource.DISPENSER);
        try {
            return shearable.onSheared(player, stack, level, pos);
        } finally {
            CommonEventHooks.popShearSource();
        }
    }

    static {
        AmberMod.AMBER_MIXINS.add("NeoForgeShearsDispenseItemBehaviorMixin");
    }
}
