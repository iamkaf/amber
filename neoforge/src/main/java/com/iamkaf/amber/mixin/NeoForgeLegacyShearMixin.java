//? if <1.21.1 {
/*package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.EntityEvent;
import com.iamkaf.amber.platform.NeoForgeShearTargets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Bogged;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/^*
 * NeoForge 21.0 shears vanilla mobs in their own mobInteract instead of through IShearable, so capture the
 * drops there with the loader's drop capture.
 ^/
@Mixin({Sheep.class, MushroomCow.class, SnowGolem.class, Bogged.class})
public abstract class NeoForgeLegacyShearMixin {
    @Inject(
            method = "mobInteract",
            at = @At(value = "INVOKE", target = "shear(Lnet/minecraft/sounds/SoundSource;)V", shift = At.Shift.BEFORE)
    )
    private void amber$beginShearCapture(Player player, InteractionHand hand,
            CallbackInfoReturnable<InteractionResult> cir) {
        Entity entity = (Entity) (Object) this;
        if (!entity.level().isClientSide()) {
            entity.captureDrops(new ArrayList<>());
        }
    }

    @Inject(
            method = "mobInteract",
            at = @At(value = "INVOKE", target = "shear(Lnet/minecraft/sounds/SoundSource;)V", shift = At.Shift.AFTER)
    )
    private void amber$fireShear(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        Entity entity = (Entity) (Object) this;
        if (!(entity.level() instanceof ServerLevel level)) {
            return;
        }

        Collection<ItemEntity> captured = entity.captureDrops(null);
        List<ItemStack> drops = new ArrayList<>();
        if (captured != null) {
            for (ItemEntity drop : captured) {
                level.addFreshEntity(drop);
                drops.add(drop.getItem());
            }
        }

        EntityEvent.SHEAR.invoker().shear(
                new EntityEvent.SimpleShearingContext(
                        player instanceof ServerPlayer serverPlayer ? serverPlayer : null,
                        player.getItemInHand(hand),
                        entity,
                        level,
                        NeoForgeShearTargets.of(entity),
                        drops,
                        true,
                        EntityEvent.ShearSource.PLAYER
                )
        );
    }

    static {
        AmberMod.AMBER_MIXINS.add("NeoForgeLegacyShearMixin");
    }
}
*///?}
