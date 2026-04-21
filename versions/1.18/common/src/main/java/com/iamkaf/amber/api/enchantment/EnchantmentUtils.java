package com.iamkaf.amber.api.enchantment;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.core.Registry;

import java.util.Map;

/**
 * @deprecated This helper will be replaced by a versioned alternative in a future release.
 */
@Deprecated
public class EnchantmentUtils {
    public static boolean containsEnchantment(ItemStack stack, ResourceLocation enchantment) {
        if (!stack.isEnchanted()) {
            return false;
        }

        for (Map.Entry<Enchantment, Integer> entry : EnchantmentHelper.getEnchantments(stack).entrySet()) {
            if (Registry.ENCHANTMENT.getKey(entry.getKey()).equals(enchantment)) {
                return true;
            }
        }
        return false;
    }
}
