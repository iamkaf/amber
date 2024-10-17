package com.iamkaf.amber.api.inventory;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class InventoryHelper {
    /**
     * Checks if the inventory contains the item and shrinks the stack by one.
     * Returns true if it does.
     */
    public static boolean consumeIfAvailable(Inventory inventory, ItemLike item) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.getItem() == item.asItem() && stack.getCount() > 0) {
                stack.shrink(1);
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the inventory contains the item and shrinks the stack by the amount specified.
     * Returns true if it does.
     */
    public static boolean consumeIfAvailable(Inventory inventory, ItemLike item, int amount) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.getItem() == item.asItem() && stack.getCount() >= amount) {
                stack.shrink(amount);
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the inventory contains the ingredient and shrinks the stack by the amount specified.
     * Returns true if it does.
     */
    public static boolean consumeIfAvailable(Inventory inventory, Ingredient ingredient, int amount) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (ingredient.test(stack) && stack.getCount() >= amount) {
                stack.shrink(amount);
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the inventory contains the item.
     * Returns true if it does.
     */
    public static boolean has(Inventory inventory, ItemLike item) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.getItem() == item.asItem() && stack.getCount() > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the inventory contains the ingredient.
     * Returns true if it does.
     */
    public static boolean has(Inventory inventory, Ingredient ingredient) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (ingredient.test(stack)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the inventory contains the ingredient in the amount specified.
     * Returns true if it does.
     */
    public static boolean has(Inventory inventory, Ingredient ingredient, int amount) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (ingredient.test(stack) && stack.getCount() >= amount) {
                return true;
            }
        }
        return false;
    }
}
