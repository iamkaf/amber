package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.ItemEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {

    @Inject(method = "playerTouch", at = @At("HEAD"))
    private void onItemPickup(Player player, CallbackInfo ci) {
        ItemEntity itemEntity = (ItemEntity) (Object) this;

        // Don't fire if the item can't be picked up yet (e.g., just dropped)
        if (itemEntity.hasPickUpDelay()) {
            return;
        }

        // Fire the informational Amber item pickup event (fires on both client and server)
        ItemEvents.ITEM_PICKUP.invoker().onItemPickup(player, itemEntity, itemEntity.getItem());
    }

    static {
        AmberMod.AMBER_MIXINS.add("ItemEntityMixin");
    }
}
