package com.iamkaf.amber.api.inventory;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ItemHelper {
    /**
     * Repairs the item by the given percentage of its maximum durability.
     * The percentage should be a value between 0 and 1 (e.g., 0.25 for 25%).
     * If the calculated repair amount exceeds the item's current damage, it will be fully repaired.
     *
     * @param item    The ItemStack to repair.
     * @param percent The percentage of the item's maximum durability to repair.
     */
    public static void repairBy(ItemStack item, float percent) {
        item.setDamageValue(Math.round(item.getDamageValue() - (float) item.getMaxDamage() * percent));
    }

    /**
     * Returns the display name(s) of the item(s) in the given ingredient.
     * If the ingredient contains only one item, it returns that item's display name.
     * If the ingredient contains multiple items, it returns a string like
     * "One of [display name], [display name], etc...".
     *
     * @param ingredient The Ingredient whose item display names are to be returned.
     * @return A string containing the display name(s) of the ingredient's items.
     */
    public static String getIngredientDisplayName(Ingredient ingredient) {
        ItemStack[] items = ingredient.getItems();

        if (items.length == 1) {
            return items[0].getDisplayName().getString();
        }

        String itemNames = Arrays.stream(items)
                .limit(3)
                .map(item -> item.getDisplayName().getString())
                .collect(Collectors.joining(", "));

        return "One of " + itemNames + ", etc...";
    }
}
