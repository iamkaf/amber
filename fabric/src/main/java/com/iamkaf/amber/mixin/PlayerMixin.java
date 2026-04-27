package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.Constants;
import com.iamkaf.amber.api.event.v1.events.common.ItemEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class PlayerMixin {

    // Target the 3-parameter drop method in LivingEntity which is used by Player
    @Inject(method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;",
            at = @At("RETURN"))
    private void onItemDrop(ItemStack stack, boolean randomizeMotion, boolean includeThrower, CallbackInfoReturnable<ItemEntity> cir) {
        // Only handle drops from players
        if (!((Object) this instanceof Player)) {
            return;
        }

        ItemEntity itemEntity = cir.getReturnValue();

        // Only proceed if an item was actually dropped
        if (itemEntity == null) {
            return;
        }

        Player player = (Player) (Object) this;

        // Fire the informational Amber item drop event (fires on both client and server)
        ItemEvents.ITEM_DROP.invoker().onItemDrop(player, itemEntity);
    }

    static {
        AmberMod.AMBER_MIXINS.add("PlayerMixin");
    }
}
