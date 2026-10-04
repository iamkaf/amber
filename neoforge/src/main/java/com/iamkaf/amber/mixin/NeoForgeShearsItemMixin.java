package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.event.EntityShearing;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * NeoForge shears every {@code IShearable} a player uses shears on here, including modded ones.
 */
@Mixin(ShearsItem.class)
public abstract class NeoForgeShearsItemMixin {
    @Inject(
            method = "interactLivingEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/neoforged/neoforge/common/IShearable;onSheared(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Ljava/util/List;"
            )
    )
    private void amber$reportShear(ItemStack shears, Player player, LivingEntity entity, InteractionHand hand,
            CallbackInfoReturnable<InteractionResult> cir) {
        EntityShearing.sheared(entity);
    }

    static {
        AmberMod.AMBER_MIXINS.add("NeoForgeShearsItemMixin");
    }
}
