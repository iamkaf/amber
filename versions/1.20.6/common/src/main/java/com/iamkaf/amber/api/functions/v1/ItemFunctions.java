package com.iamkaf.amber.api.functions.v1;

import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.ItemLike;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    public static boolean has(Inventory inventory, TagKey<Item> tag) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.is(tag) && stack.getCount() > 0) {
                return true;
            }
        }
        return false;
    }

    public static boolean has(Inventory inventory, TagKey<Item> tag, int amount) {
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

    public static void addModifier(ItemStack stack, Holder<Attribute> attribute, AttributeModifier modifier,
            EquipmentSlotGroup slotGroup) {
        ItemAttributeModifiers extraModifiers = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
        assert extraModifiers != null;
        var attributeBuilder = ItemAttributeModifiers.builder();
        var defaultModifiers = getDefaultAttributeModifiers(stack);
        Set<UUID> added = new HashSet<>();
        for (var mod : defaultModifiers.modifiers()) {
            if (!added.contains(mod.modifier().id())) {
                attributeBuilder.add(mod.attribute(), mod.modifier(), mod.slot());
                added.add(mod.modifier().id());
            }
        }
        for (var mod : extraModifiers.modifiers()) {
            if (mod.modifier().id().equals(modifier.id())) {
                continue;
            }
            if (!added.contains(mod.modifier().id())) {
                attributeBuilder.add(mod.attribute(), mod.modifier(), mod.slot());
                added.add(mod.modifier().id());
            }
        }
        attributeBuilder.add(attribute, modifier, slotGroup);
        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, attributeBuilder.build());
    }

    public static boolean hasModifier(ItemStack stack, UUID id) {
        if (!stack.has(DataComponents.ATTRIBUTE_MODIFIERS)) {
            return false;
        }
        var list = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
        assert list != null;
        return list.modifiers().stream().anyMatch(m -> m.modifier().id().equals(id));
    }

    public static ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        Item item = stack.getItem();
        ItemStack defaultInstance = item.getDefaultInstance();
        return defaultInstance.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
    }

    public static boolean containsEnchantment(ItemStack stack, ResourceLocation enchantment) {
        ItemEnchantments enchantments = stack.getEnchantments();
        if (enchantments.isEmpty()) {
            return false;
        }

        for (var enchantmentEntry : enchantments.entrySet()) {
            Holder<Enchantment> enchantmentHolder = enchantmentEntry.getKey();
            if (enchantmentHolder.is(enchantment)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsEnchantment(ItemStack stack, Enchantment enchantment) {
        if (enchantment == null) {
            return false;
        }

        ItemEnchantments enchantments = stack.getEnchantments();

        for (var entry : enchantments.entrySet()) {
            Holder<Enchantment> enchantmentHolder = entry.getKey();
            if (enchantmentHolder.value().equals(enchantment)) {
                return entry.getIntValue() > 0;
            }
        }
        return false;
    }

    public static int getEnchantmentLevel(ItemStack stack, Enchantment enchantment) {
        if (enchantment == null) {
            return 0;
        }

        ItemEnchantments enchantments = stack.getEnchantments();
        for (var entry : enchantments.entrySet()) {
            Holder<Enchantment> enchantmentHolder = entry.getKey();
            if (enchantmentHolder.value().equals(enchantment)) {
                return entry.getIntValue();
            }
        }
        return 0;
    }

    public static int getEnchantmentLevel(ItemStack stack, ResourceLocation enchantment) {
        ItemEnchantments enchantments = stack.getEnchantments();
        if (enchantments.isEmpty()) {
            return 0;
        }

        for (var entry : enchantments.entrySet()) {
            Holder<Enchantment> enchantmentHolder = entry.getKey();
            if (enchantmentHolder.is(enchantment)) {
                return entry.getIntValue();
            }
        }
        return 0;
    }

    public static boolean isEnchanted(ItemStack stack) {
        return stack.isEnchanted();
    }

    public static ItemEnchantments getEnchantments(ItemStack stack) {
        return stack.getEnchantments();
    }

    public static boolean isTool(ItemStack stack) {
        return stack.has(DataComponents.TOOL);
    }

    public static boolean isTool(Item item) {
        return item.getDefaultInstance().has(DataComponents.TOOL);
    }

    public static boolean isWeapon(ItemStack stack) {
        ItemAttributeModifiers modifiers = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        return modifiers.modifiers().stream()
                .anyMatch(modifier -> modifier.attribute().is(Attributes.ATTACK_DAMAGE));
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
