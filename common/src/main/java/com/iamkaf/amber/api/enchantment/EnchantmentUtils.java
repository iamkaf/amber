package com.iamkaf.amber.api.enchantment;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class EnchantmentUtils {
    public static boolean containsEnchantment(ItemStack stack, Enchantment enchantment) {
        if (!stack.isEnchanted()) {
            return false;
        }

        return EnchantmentHelper.getItemEnchantmentLevel(enchantment, stack) > 0;
    }
}
