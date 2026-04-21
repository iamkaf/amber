package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.Constants;
import com.iamkaf.amber.api.event.v1.events.common.ItemEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin {

    // Player owns the item-drop method on the legacy and transition-era lines.
    //? if >=1.21.5 {
    @Inject(method = "drop(Lnet/minecraft/world/item/ItemStack;Z)Lnet/minecraft/world/entity/item/ItemEntity;",
            at = @At("RETURN"))
    private void onItemDrop(ItemStack stack, boolean includeThrower, CallbackInfoReturnable<ItemEntity> cir) {
    //?} else {
    @Inject(method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;",
            at = @At("RETURN"))
    private void onItemDrop(ItemStack stack, boolean randomizeMotion, boolean includeThrower, CallbackInfoReturnable<ItemEntity> cir) {
    //?}
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
