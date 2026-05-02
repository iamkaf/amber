package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.PlayerEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.inventory.SmithingMenu")
public abstract class LegacySmithingMenuMixin {
    @Inject(method = "onTake", at = @At("RETURN"))
    private void amber$onSmithingTake(Player player, ItemStack craftedItem, CallbackInfoReturnable<ItemStack> cir) {
        if (!craftedItem.isEmpty() && player instanceof ServerPlayer serverPlayer) {
            PlayerEvents.CRAFT_ITEM.invoker().onCraftItem(serverPlayer, java.util.List.of(craftedItem));
        }
    }

    static {
        AmberMod.AMBER_MIXINS.add("LegacySmithingMenuMixin");
    }
}
