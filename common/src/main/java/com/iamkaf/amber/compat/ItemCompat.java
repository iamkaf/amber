package com.iamkaf.amber.compat;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
//? if <1.21.2
/*import net.minecraft.world.item.crafting.Ingredient;*/
import net.minecraft.world.level.ItemLike;
//? if >=1.18.2 && <1.20.5
/*import net.minecraft.core.Holder;*/
//? if >=1.20.5 && <1.21.2
/*import net.minecraft.world.item.Equipable;*/
//? if <1.20.5
/*import net.minecraft.world.entity.LivingEntity;*/
//? if >=1.19.3 && <1.20.5
/*import net.minecraft.core.registries.BuiltInRegistries;*/
//? if <1.19.3
/*import net.minecraft.core.Registry;*/
//? if <1.20.5 {
/*import com.google.common.collect.Multimap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.ArrayList;
import java.util.List;
*///?}

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

    //? if <1.21.2 {
    /*public static ItemStack[] ingredientItems(Ingredient ingredient) {
        return ingredient.getItems();
    }
    *///?}

    public static String displayNameString(ItemStack stack) {
        return stack.getDisplayName().getString();
    }

    public static boolean stackIsEnchanted(ItemStack stack) {
        return stack.isEnchanted();
    }

    public static ItemStack playerItemBySlot(Player player, EquipmentSlot slot) {
        return player.getItemBySlot(slot);
    }

    public static String modifierIdentity(AttributeModifier modifier) {
        //? if >=1.20.5
        return modifier.id().toString();
        //? if <1.20.5
        /*return modifier.save().getString("Name");*/
    }

    public static ItemStack emptyStack() {
        return new ItemStack(Items.AIR);
    }

    public static NonNullList<ItemStack> nonNullListWithSize(int size, ItemStack defaultValue) {
        return NonNullList.withSize(size, defaultValue);
    }

    //? if <1.20.5 {
    /*public static Multimap<Attribute, AttributeModifier> stackAttributeModifiers(ItemStack stack, EquipmentSlot slot) {
        return stack.getAttributeModifiers(slot);
    }

    public static Attribute attackDamageAttribute() {
        return Attributes.ATTACK_DAMAGE;
    }

    public static ItemStack itemDefaultInstance(Item item) {
        return item.getDefaultInstance();
    }

    public static List<CompoundTag> stackEnchantmentTags(ItemStack stack) {
        ListTag enchantments = stack.getEnchantmentTags();
        List<CompoundTag> tags = new ArrayList<>(enchantments.size());
        for (int i = 0; i < enchantments.size(); i++) {
            tags.add(enchantments.getCompound(i));
        }
        return tags;
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

    // Matches 1.20.5+ addModifier: the first stored modifier would hide the item's defaults, so copy them in first,
    // and a modifier with the same attribute and name replaces the old one.
    public static void addStackAttributeModifier(ItemStack stack, Attribute attribute,
            AttributeModifier modifier, EquipmentSlot slot) {
        if (!stack.hasTag() || !stack.getTag().contains("AttributeModifiers", Tag.TAG_LIST)) {
            for (EquipmentSlot defaultSlot : EquipmentSlot.values()) {
                stack.getItem().getDefaultAttributeModifiers(defaultSlot).forEach(
                        (defaultAttribute, defaultModifier) -> stack.addAttributeModifier(defaultAttribute, defaultModifier, defaultSlot));
            }
        }
        String attributeName = attributeName(attribute);
        String modifierName = modifierIdentity(modifier);
        stack.getOrCreateTag().getList("AttributeModifiers", Tag.TAG_COMPOUND).removeIf(entry ->
                entry instanceof CompoundTag tag
                        && tag.getString("AttributeName").equals(attributeName)
                        && tag.getString("Name").equals(modifierName));
        stack.addAttributeModifier(attribute, modifier, slot);
    }

    private static String attributeName(Attribute attribute) {
        //? if >=1.19.3
        return BuiltInRegistries.ATTRIBUTE.getKey(attribute).toString();
        //? if <1.19.3
        //return Registry.ATTRIBUTE.getKey(attribute).toString();
    }

    public static CompoundTag modifierTag(AttributeModifier modifier) {
        return modifier.save();
    }
    *///?}

    //? if >=1.20.5 && <1.21.2 {
    /*// The slot Minecraft equips the item into, for lines without the EQUIPPABLE component.
    public static EquipmentSlot equipmentSlot(ItemStack stack) {
        Equipable equipable = Equipable.get(stack);
        return equipable == null ? EquipmentSlot.MAINHAND : equipable.getEquipmentSlot();
    }
    *///?} else if <1.20.5 {
    /*// The slot Minecraft equips the item into, for lines without the EQUIPPABLE component.
    public static EquipmentSlot equipmentSlot(ItemStack stack) {
        return LivingEntity.getEquipmentSlotForItem(stack);
    }
    *///?}

    //? if >=1.18.2 && <1.20.5 {
    /*public static Attribute holderValue(Holder<Attribute> holder) {
        return holder.value();
    }
    *///?}
}
