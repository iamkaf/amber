package com.iamkaf.amber.util.compat;

import com.google.common.collect.Multimap;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;

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

    public static Multimap<Attribute, AttributeModifier> stackAttributeModifiers(ItemStack stack, EquipmentSlot slot) {
        return stack.getAttributeModifiers(slot);
    }

    public static Attribute attackDamageAttribute() {
        return Attributes.ATTACK_DAMAGE;
    }

    public static ItemStack itemDefaultInstance(Item item) {
        return item.getDefaultInstance();
    }

    public static boolean stackIsEnchanted(ItemStack stack) {
        return stack.isEnchanted();
    }

    public static List<CompoundTag> stackEnchantmentTags(ItemStack stack) {
        ListTag enchantments = stack.getEnchantmentTags();
        List<CompoundTag> tags = new ArrayList<>(enchantments.size());
        for (int i = 0; i < enchantments.size(); i++) {
            tags.add(enchantments.getCompound(i));
        }
        return tags;
    }

    public static ItemStack playerItemBySlot(Player player, EquipmentSlot slot) {
        return player.getItemBySlot(slot);
    }

    public static String tagString(CompoundTag tag, String key) {
        return tag.getString(key);
    }

    public static int tagInt(CompoundTag tag, String key) {
        return tag.getInt(key);
    }

    public static int itemEnchantmentLevel(Enchantment enchantment, ItemStack stack) {
        return EnchantmentHelper.getItemEnchantmentLevel(enchantment, stack);
    }

    public static void addStackAttributeModifier(ItemStack stack, Attribute attribute,
            AttributeModifier modifier, EquipmentSlot slot) {
        stack.addAttributeModifier(attribute, modifier, slot);
    }

    public static CompoundTag modifierTag(AttributeModifier modifier) {
        return modifier.save();
    }

    public static String modifierIdentity(AttributeModifier modifier) {
        return modifier.save().getString("Name");
    }

    public static ItemStack emptyStack() {
        return new ItemStack(Items.AIR);
    }

    public static NonNullList<ItemStack> nonNullListWithSize(int size, ItemStack defaultValue) {
        return NonNullList.withSize(size, defaultValue);
    }
}
