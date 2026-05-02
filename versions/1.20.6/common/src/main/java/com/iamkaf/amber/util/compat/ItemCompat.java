package com.iamkaf.amber.util.compat;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public final class ItemCompat {
    private ItemCompat() {}

    public static int inventorySize(Inventory inventory) {
        return inventory.getContainerSize();
    }

    public static ItemStack inventoryItem(Inventory inventory, int slot) {
        return inventory.getItem(slot);
    }

    public static Item stackItem(ItemStack stack) {
        return stack.getItem();
    }

    public static Item itemLikeItem(ItemLike item) {
        return item.asItem();
    }

    public static int stackCount(ItemStack stack) {
        return stack.getCount();
    }

    public static void shrinkStack(ItemStack stack, int amount) {
        stack.shrink(amount);
    }

    public static int stackDamage(ItemStack stack) {
        return stack.getDamageValue();
    }

    public static int stackMaxDamage(ItemStack stack) {
        return stack.getMaxDamage();
    }

    public static void setStackDamage(ItemStack stack, int damage) {
        stack.setDamageValue(damage);
    }

    public static ItemStack[] ingredientItems(Ingredient ingredient) {
        return ingredient.getItems();
    }

    public static String displayNameString(ItemStack stack) {
        return stack.getDisplayName().getString();
    }

    public static boolean stackIsEnchanted(ItemStack stack) {
        return stack.isEnchanted();
    }

    public static ItemStack playerItemBySlot(Player player, EquipmentSlot slot) {
        return player.getItemBySlot(slot);
    }

    public static ItemStack emptyStack() {
        return new ItemStack(Items.AIR);
    }

    public static NonNullList<ItemStack> nonNullListWithSize(int size, ItemStack defaultValue) {
        return NonNullList.withSize(size, defaultValue);
    }
}
