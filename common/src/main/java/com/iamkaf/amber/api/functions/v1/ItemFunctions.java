package com.iamkaf.amber.api.functions.v1;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.ItemLike;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Consolidated utility class for item, inventory, and armor operations.
 * This class combines functionality from the old InventoryHelper, ItemHelper, and ArmorTierHelper
 * that will be removed in Amber 10.
 *
 * @since 8.3.0
 */
public final class ItemFunctions {

    private ItemFunctions() {
        // Utility class - prevent instantiation
    }

    // ==================== INVENTORY OPERATIONS ====================

    /**
     * Checks if the inventory contains the specified item and consumes one from the first matching stack.
     *
     * @param inventory The inventory to search through.
     * @param item The item to consume.
     * @return true if the item was found and consumed, false otherwise.
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
     * Checks if the inventory contains the specified item and consumes the specified amount from the first matching stack.
     *
     * @param inventory The inventory to search through.
     * @param item The item to consume.
     * @param amount The amount to consume.
     * @return true if the item was found and consumed, false otherwise.
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
     * Checks if the inventory contains the item and shrinks the stack by one.
     * Returns true if it does.
     */
    public static boolean consumeIfAvailable(Inventory inventory, TagKey<Item> tag) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.is(tag) && stack.getCount() > 0) {
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
    public static boolean consumeIfAvailable(Inventory inventory, TagKey<Item> tag, int amount) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.is(tag) && stack.getCount() >= amount) {
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

    /**
     * Checks if the inventory contains the item.
     * Returns true if it does.
     */
    public static boolean has(Inventory inventory, TagKey<Item> tag) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.is(tag) && stack.getCount() > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the inventory contains the item.
     * Returns true if it does.
     */
    public static boolean has(Inventory inventory, TagKey<Item> tag, int amount) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.is(tag) && stack.getCount() > amount) {
                return true;
            }
        }
        return false;
    }

    /**
     * Executes a predicate on each ItemStack in the inventory.
     */
    public static void forEach(Inventory inventory, Consumer<ItemStack> predicate) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            predicate.accept(inventory.getItem(i));
        }
    }

    // ==================== ITEM OPERATIONS ====================

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
        ItemStack[] items = ingredient.items()
                .map(itemHolder -> itemHolder.value().getDefaultInstance())
                .toArray(ItemStack[]::new);

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
     * @param slotGroup The {@code EquipmentSlotGroup} the modifier is applicable for.
     * @see Attribute
     * @see AttributeModifier
     * @see EquipmentSlotGroup
     */
    public static void addModifier(ItemStack stack, Holder<Attribute> attribute, AttributeModifier modifier,
            EquipmentSlotGroup slotGroup) {
        ItemAttributeModifiers extraModifiers = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
        assert extraModifiers != null;
        var attributeBuilder = ItemAttributeModifiers.builder();
        var defaultModifiers = getDefaultAttributeModifiers(stack);
        Set<ResourceLocation> added = new HashSet<>();
        for (var mod : defaultModifiers.modifiers()) {
            if (!added.contains(mod.modifier().id())) {
                attributeBuilder.add(mod.attribute(), mod.modifier(), mod.slot());
                added.add(mod.modifier().id());
            }
        }
        for (var mod : extraModifiers.modifiers()) {
            if (mod.modifier().id().equals(modifier.id())) {
                // skipping so it can be overwritten
                continue;
            }
            // prevents duplicates
            if (!added.contains(mod.modifier().id())) {
                attributeBuilder.add(mod.attribute(), mod.modifier(), mod.slot());
                added.add(mod.modifier().id());
            }
        }
        attributeBuilder.add(attribute, modifier, slotGroup);
        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, attributeBuilder.build());
    }

    /**
     * Checks if an {@code ItemStack} has an attribute modifier with the specified id.
     *
     * @param stack The {@code ItemStack} to check.
     * @param id    The {@code ResourceLocation} to check.
     * @return {@code} true if the modifier with the id is present.
     */
    public static boolean hasModifier(ItemStack stack, ResourceLocation id) {
        if (!stack.has(DataComponents.ATTRIBUTE_MODIFIERS)) {
            return false;
        }
        var list = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
        assert list != null;
        return list.modifiers().stream().anyMatch(m -> m.modifier().id().equals(id));
    }

    /**
     * Gets the default attribute modifiers for the given ItemStack.
     *
     * @param stack The ItemStack to get default modifiers for.
     * @return The default attribute modifiers.
     */
    public static ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        Item item = stack.getItem();

        ItemStack defaultInstance = item.getDefaultInstance();

        return defaultInstance.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
    }

    // ==================== ARMOR TIER OPERATIONS ====================

    /**
     * Creates a repair ingredient supplier for the given item.
     *
     * @param item The item to create a repair ingredient for.
     * @return A supplier that returns an Ingredient for repairing with the given item.
     */
    public static Supplier<Ingredient> createRepairIngredient(Supplier<Item> item) {
        return () -> Ingredient.of(item.get());
    }

    // ==================== ARMOR TIER ENUMS ====================

    /**
     * Enum containing vanilla armor toughness values for different armor materials.
     */
    public enum VanillaArmorToughness {
        TURTLE_SHELL(0, 0, 0, 0, 0),
        LEATHER(0, 0, 0, 0, 0),
        GOLD(0, 0, 0, 0, 0),
        COPPER(0, 0, 0, 0, 0),
        CHAINMAIL(0, 0, 0, 0, 0),
        IRON(0, 0, 0, 0, 0),
        DIAMOND(8, 2, 2, 2, 2),
        NETHERITE(13, 3, 3, 3, 3);

        public final int fullSet;
        public final int helmet;
        public final int chestplate;
        public final int leggings;
        public final int boots;

        VanillaArmorToughness(int fullSet, int helmet, int chestplate, int leggings, int boots) {
            this.fullSet = fullSet;
            this.helmet = helmet;
            this.chestplate = chestplate;
            this.leggings = leggings;
            this.boots = boots;
        }
    }

    /**
     * Enum containing vanilla knockback resistance values for different armor materials.
     */
    public enum VanillaKnockbackResistance {
        TURTLE_SHELL(0.0f),
        LEATHER(0.0f),
        GOLD(0.0f),
        COPPER(0.0f),
        CHAINMAIL(0.0f),
        IRON(0.0f),
        DIAMOND(0.02f),
        NETHERITE(0.1f);

        public final float knockbackResistance;

        VanillaKnockbackResistance(float knockbackResistance) {
            this.knockbackResistance = knockbackResistance;
        }
    }

    /**
     * Enum containing vanilla enchantability values for different armor materials.
     */
    public enum VanillaEnchantability {
        TURTLE_SHELL(9),
        LEATHER(15),
        GOLD(25),
        COPPER(8),
        CHAINMAIL(12),
        IRON(9),
        DIAMOND(10),
        NETHERITE(15);

        public final int enchantability;

        VanillaEnchantability(int enchantability) {
            this.enchantability = enchantability;
        }
    }
}