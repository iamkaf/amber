//? if <26.2 {
/*package com.iamkaf.amber.mixin;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.event.EntityShearing;
import java.util.List;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/^*
 * Before 26.2 Forge shears every {@code IForgeShearable} a player uses shears on here, including modded ones and,
 * from 1.20.6, a wolf's armor. A shearable that gives nothing, such as a wolf clicked by someone other than its
 * owner, was not sheared.
 ^/
@Mixin(ShearsItem.class)
public abstract class ForgeShearsItemMixin {
    @ModifyVariable(method = "interactLivingEntity", at = @At("STORE"), ordinal = 0)
    private List<ItemStack> amber$reportShear(List<ItemStack> drops, ItemStack shears, Player player,
            LivingEntity entity, InteractionHand hand) {
        if (!drops.isEmpty()) {
            EntityShearing.sheared(entity);
        }
        return drops;
    }

    static {
        AmberMod.AMBER_MIXINS.add("ForgeShearsItemMixin");
    }
}
*///?}
