package com.iamkaf.amber.api.inventory;

import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @deprecated This helper will be replaced by a versioned alternative in a future release.
 */
@Deprecated
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

    /**
     * Adds an attribute modifier to an {@code ItemStack} respecting default and already existing modifiers.
     *
     * @param stack     The {@code ItemStack} to add the modifier to.
     * @param attribute Which {@code Attribute} to add the modifier to.
     * @param modifier  Your attribute modifier.
     * @param slot      The {@code EquipmentSlot} the modifier is applicable for.
     * @see Attribute
     * @see AttributeModifier
     * @see EquipmentSlot
     */
    public static void addModifier(ItemStack stack, Holder<Attribute> attribute, AttributeModifier modifier,
            EquipmentSlot slot) {
        stack.addAttributeModifier(attribute.value(), modifier, slot);
    }

    /**
     * Checks if an {@code ItemStack} has an attribute modifier with the specified id.
     *
     * @param stack The {@code ItemStack} to check.
     * @param id    The {@code ResourceLocation} to check.
     * @return {@code} true if the modifier with the id is present.
     */
    public static boolean hasModifier(ItemStack stack, UUID id) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (stack.getAttributeModifiers(slot).values().stream().anyMatch(modifier -> modifier.getId().equals(id))) {
                return true;
            }
        }
        return false;
    }

    // TODO: add removeModifier method.

    public static Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(ItemStack stack) {
        Item item = stack.getItem();
        return item.getDefaultAttributeModifiers(EquipmentSlot.MAINHAND);
    }
}
