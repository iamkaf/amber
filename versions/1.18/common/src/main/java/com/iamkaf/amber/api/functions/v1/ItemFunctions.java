package com.iamkaf.amber.api.functions.v1;

import com.google.common.collect.Multimap;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ItemLike;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 1.21.9 still uses ResourceLocation in the newer Amber 10 item utility surface.
 */
public final class ItemFunctions {

    private ItemFunctions() {
    }

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

    public static boolean consumeIfAvailable(Inventory inventory, Tag<Item> tag) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.is(tag) && stack.getCount() > 0) {
                stack.shrink(1);
                return true;
            }
        }
        return false;
    }

    public static boolean consumeIfAvailable(Inventory inventory, Tag<Item> tag, int amount) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.is(tag) && stack.getCount() >= amount) {
                stack.shrink(amount);
                return true;
            }
        }
        return false;
    }

    public static boolean has(Inventory inventory, ItemLike item) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.getItem() == item.asItem() && stack.getCount() > 0) {
                return true;
            }
        }
        return false;
    }

    public static boolean has(Inventory inventory, Ingredient ingredient) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (ingredient.test(stack)) {
                return true;
            }
        }
        return false;
    }

    public static boolean has(Inventory inventory, Ingredient ingredient, int amount) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (ingredient.test(stack) && stack.getCount() >= amount) {
                return true;
            }
        }
        return false;
    }

    public static boolean has(Inventory inventory, Tag<Item> tag) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.is(tag) && stack.getCount() > 0) {
                return true;
            }
        }
        return false;
    }

    public static boolean has(Inventory inventory, Tag<Item> tag, int amount) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.is(tag) && stack.getCount() > amount) {
                return true;
            }
        }
        return false;
    }

    public static void forEach(Inventory inventory, Consumer<ItemStack> predicate) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            predicate.accept(inventory.getItem(i));
        }
    }

    public static NonNullList<ItemStack> getInventoryItems(Inventory inventory) {
        NonNullList<ItemStack> items = NonNullList.withSize(inventory.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            items.set(i, inventory.getItem(i));
        }
        return items;
    }

    public static List<ItemStack> getArmorSlots(Player player) {
        return List.of(
                player.getItemBySlot(EquipmentSlot.HEAD),
                player.getItemBySlot(EquipmentSlot.CHEST),
                player.getItemBySlot(EquipmentSlot.LEGS),
                player.getItemBySlot(EquipmentSlot.FEET)
        );
    }

    public static void repairBy(ItemStack item, float percent) {
        item.setDamageValue(Math.round(item.getDamageValue() - (float) item.getMaxDamage() * percent));
    }

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

    public static void addModifier(ItemStack stack, Attribute attribute, AttributeModifier modifier,
            EquipmentSlot slot) {
        stack.addAttributeModifier(attribute, modifier, slot);
    }

    public static boolean hasModifier(ItemStack stack, UUID id) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (stack.getAttributeModifiers(slot).values().stream().anyMatch(modifier -> modifier.getId().equals(id))) {
                return true;
            }
        }
        return false;
    }

    public static Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(ItemStack stack) {
        return stack.getItem().getDefaultAttributeModifiers(EquipmentSlot.MAINHAND);
    }

    public static boolean containsEnchantment(ItemStack stack, ResourceLocation enchantment) {
        for (Map.Entry<Enchantment, Integer> enchantmentEntry : EnchantmentHelper.getEnchantments(stack).entrySet()) {
            if (Registry.ENCHANTMENT.getKey(enchantmentEntry.getKey()).equals(enchantment)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsEnchantment(ItemStack stack, Enchantment enchantment) {
        if (enchantment == null) {
            return false;
        }

        for (Map.Entry<Enchantment, Integer> entry : EnchantmentHelper.getEnchantments(stack).entrySet()) {
            if (entry.getKey().equals(enchantment)) {
                return entry.getValue() > 0;
            }
        }
        return false;
    }

    public static int getEnchantmentLevel(ItemStack stack, Enchantment enchantment) {
        if (enchantment == null) {
            return 0;
        }

        for (Map.Entry<Enchantment, Integer> entry : EnchantmentHelper.getEnchantments(stack).entrySet()) {
            if (entry.getKey().equals(enchantment)) {
                return entry.getValue();
            }
        }
        return 0;
    }

    public static int getEnchantmentLevel(ItemStack stack, ResourceLocation enchantment) {
        for (Map.Entry<Enchantment, Integer> entry : EnchantmentHelper.getEnchantments(stack).entrySet()) {
            if (Registry.ENCHANTMENT.getKey(entry.getKey()).equals(enchantment)) {
                return entry.getValue();
            }
        }
        return 0;
    }

    public static boolean isEnchanted(ItemStack stack) {
        return stack.isEnchanted();
    }

    public static Map<Enchantment, Integer> getEnchantments(ItemStack stack) {
        return EnchantmentHelper.getEnchantments(stack);
    }

    public static boolean isTool(ItemStack stack) {
        return stack.getItem() instanceof DiggerItem;
    }

    public static boolean isTool(Item item) {
        return item instanceof DiggerItem;
    }

    public static boolean isWeapon(ItemStack stack) {
        if (stack.getItem() instanceof SwordItem || stack.getItem() instanceof TridentItem) {
            return true;
        }
        return stack.getAttributeModifiers(EquipmentSlot.MAINHAND).entries().stream()
                .anyMatch(entry -> entry.getKey().equals(Attributes.ATTACK_DAMAGE));
    }

    public static boolean isWeapon(Item item) {
        return isWeapon(item.getDefaultInstance());
    }

    public static boolean isArmor(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem;
    }

    public static boolean isArmor(Item item) {
        return item instanceof ArmorItem;
    }

    public static Supplier<Ingredient> createRepairIngredient(Supplier<Item> item) {
        return () -> Ingredient.of(item.get());
    }

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
