package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.event.EntityShearing;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
//? if >=1.21.5
import net.minecraft.world.entity.animal.wolf.Wolf;
//? if <1.21.5
/*import net.minecraft.world.entity.animal.Wolf;*/
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Before shears could remove any mob's equipment, a wolf's owner could shear off its armor, which the wolf drops as
 * the only item it spawns in {@code mobInteract}. Applied only on those versions.
 */
@Mixin(Wolf.class)
public abstract class WolfShearMixin {
    @Inject(
            method = "mobInteract",
            at = @At(
                    value = "INVOKE",
                    //? if >=1.21.5
                    target = "Lnet/minecraft/world/entity/animal/wolf/Wolf;spawnAtLocation(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/item/ItemEntity;"
                    //? if >=1.21.2 && <1.21.5
                    /*target = "Lnet/minecraft/world/entity/animal/Wolf;spawnAtLocation(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/item/ItemEntity;"*/
                    //? if <1.21.2
                    /*target = "Lnet/minecraft/world/entity/animal/Wolf;spawnAtLocation(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/item/ItemEntity;"*/
            )
    )
    private void amber$reportArmorShear(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        EntityShearing.sheared((Wolf) (Object) this);
    }

    static {
        AmberMod.AMBER_MIXINS.add("WolfShearMixin");
    }
}
