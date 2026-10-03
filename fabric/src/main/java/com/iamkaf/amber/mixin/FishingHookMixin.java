package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.event.v1.events.common.FishingEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.List;

@Mixin(targets =
        "net.minecraft.world.entity.projectile.FishingHook"
)
public abstract class FishingHookMixin {
    static {
        AmberMod.AMBER_MIXINS.add("FishingHookMixin");
    }

    @Shadow
    public abstract Player getPlayerOwner();

    @ModifyVariable(
            method = "retrieve(Lnet/minecraft/world/item/ItemStack;)I",
            at = @At("STORE"),
            ordinal = 0
    )
    private List<ItemStack> amber$modifyFishingCatch(List<ItemStack> drops, ItemStack rod) {
        return amber$modifyCatch(drops, rod);
    }

    private <T extends List<ItemStack>> T amber$modifyCatch(T drops, ItemStack rod) {
        Player owner = amber$getPlayerOwner();
        if (!(owner instanceof ServerPlayer serverPlayer)) {
            return drops;
        }

        List<ItemStack> mutableDrops = new ArrayList<>(drops);
        FishingEvents.MODIFY_CATCH.invoker().modify(serverPlayer, (Entity) (Object) this, rod, mutableDrops);
        drops.clear();
        drops.addAll(mutableDrops);
        return drops;
    }

    private Player amber$getPlayerOwner() {
        return getPlayerOwner();
    }
}
