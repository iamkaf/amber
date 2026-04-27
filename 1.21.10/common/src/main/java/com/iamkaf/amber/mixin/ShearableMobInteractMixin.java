package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.EntityEvent;
import com.iamkaf.amber.event.internal.CommonEventHooks;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.animal.coppergolem.CopperGolem;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.monster.Bogged;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Sheep.class, MushroomCow.class, SnowGolem.class, Bogged.class, CopperGolem.class})
public abstract class ShearableMobInteractMixin {
    @Unique
    private boolean amber$trackingShears;

    @Inject(method = "mobInteract", at = @At("HEAD"))
    private void amber$beforeMobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (player.getItemInHand(hand).is(Items.SHEARS)) {
            this.amber$trackingShears = true;
            CommonEventHooks.pushShearSource(player, player.getItemInHand(hand), EntityEvent.ShearSource.PLAYER);
        }
    }

    @Inject(method = "mobInteract", at = @At("RETURN"))
    private void amber$afterMobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (this.amber$trackingShears) {
            this.amber$trackingShears = false;
            CommonEventHooks.popShearSource();
        }
    }

    static {
        AmberMod.AMBER_MIXINS.add("ShearableMobInteractMixin");
    }
}
