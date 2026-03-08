package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.EntityEvent;
import com.iamkaf.amber.event.internal.CommonEventHooks;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Creeper.class)
public abstract class CreeperMixin {
    @Inject(
            method = "mobInteract",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/monster/Creeper;ignite()V",
                    shift = At.Shift.AFTER
            )
    )
    private void amber$onCreeperIgnite(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack stack = player.getItemInHand(hand);
        EntityEvent.EntityIgnitionSource source = stack.is(Items.FIRE_CHARGE)
                ? EntityEvent.EntityIgnitionSource.FIRE_CHARGE
                : EntityEvent.EntityIgnitionSource.FLINT_AND_STEEL;

        CommonEventHooks.pushEntityIgnitionSource(player, stack, source, false);
        try {
            CommonEventHooks.fireEntityIgnite((Creeper) (Object) this);
        } finally {
            CommonEventHooks.popEntityIgnitionSource();
        }
    }

    static {
        AmberMod.AMBER_MIXINS.add("CreeperMixin");
    }
}
