package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.PlayerEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
//? if <1.21.5
/*import net.minecraft.world.level.Level;*/
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Item.class)
public abstract class CraftedItemMixin {
    @Inject(method = "onCraftedBy", at = @At("TAIL"))
    private void amber$onCraftedBy(
            ItemStack itemStack,
            //? if <1.21.5
            /*Level level,*/
            Player player,
            CallbackInfo ci
    ) {
        if (!itemStack.isEmpty() && player instanceof ServerPlayer serverPlayer) {
            PlayerEvents.CRAFT_ITEM.invoker().onCraftItem(serverPlayer, java.util.List.of(itemStack));
        }
    }

    static {
        AmberMod.AMBER_MIXINS.add("CraftedItemMixin");
    }
}
