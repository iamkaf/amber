package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.FishingEvents;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Hands listeners the catch as soon as the loot table rolls it, so the items they leave are the ones the hook pulls
 * to the player, with the usual experience, statistics, and advancement triggers.
 */
@Mixin(FishingHook.class)
public abstract class FishingHookMixin {
    @Shadow
    public abstract Player getPlayerOwner();

    @ModifyVariable(method = "retrieve(Lnet/minecraft/world/item/ItemStack;)I", at = @At("STORE"), ordinal = 0)
    private List<ItemStack> amber$modifyCatch(List<ItemStack> drops, ItemStack rod) {
        if (getPlayerOwner() instanceof ServerPlayer player) {
            List<ItemStack> modified = new ArrayList<>(drops);
            FishingEvents.MODIFY_CATCH.invoker().modify(player, (FishingHook) (Object) this, rod, modified);
            drops.clear();
            drops.addAll(modified);
        }
        return drops;
    }

    static {
        AmberMod.AMBER_MIXINS.add("FishingHookMixin");
    }
}
