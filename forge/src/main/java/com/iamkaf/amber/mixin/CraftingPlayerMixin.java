package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.PlayerEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ResultSlot.class)
public abstract class CraftingPlayerMixin {

    @Shadow
    @Final
    private Player player;

    @Shadow
    @Final
    private CraftingContainer craftSlots;

    @Shadow
    private int removeCount;

    @Unique
    private int amber$removeCountBeforeCheck;

    @Inject(method = "checkTakeAchievements", at = @At("HEAD"))
    private void amber$captureRemoveCount(ItemStack craftedItem, CallbackInfo ci) {
        amber$removeCountBeforeCheck = removeCount;
    }

    @Inject(method = "onTake", at = @At(value = "INVOKE",
            //? if >=1.17
            target = "Lnet/minecraft/world/inventory/ResultSlot;checkTakeAchievements(Lnet/minecraft/world/item/ItemStack;)V",
            //? if <1.17
            /*target = "Lnet/minecraft/inventory/container/CraftingResultSlot;checkTakeAchievements(Lnet/minecraft/item/ItemStack;)V",*/
            shift = At.Shift.AFTER))
    private void amber$onResultTaken(Player player, ItemStack carried,
            //? if >=1.17
            CallbackInfo ci
            //? if <1.17
            /*CallbackInfoReturnable<ItemStack> cir*/
    ) {
        //? if >=1.19 {
        if (amber$removeCountBeforeCheck <= 0) {
            amber$fireCraftEvent(carried);
        }
        amber$removeCountBeforeCheck = 0;
        //?}
    }

    @Unique
    private void amber$fireCraftEvent(ItemStack craftedItem) {
        if (craftedItem.isEmpty() || !(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        PlayerEvents.CRAFT_ITEM.invoker().onCraftItem(serverPlayer, java.util.List.of(craftedItem));
    }

    static {
        AmberMod.AMBER_MIXINS.add("CraftingPlayerMixin");
    }
}
