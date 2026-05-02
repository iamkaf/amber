package com.iamkaf.amber.util.compat;

import com.google.common.collect.Multimap;
import net.minecraft.core.NonNullList;
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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.List;

public final class ItemCompat {
    private ItemCompat() {}

    public static int inventorySize(Inventory inventory) {
        try {
            Object value = inventory.getClass().getMethod("getContainerSize").invoke(inventory);
            return value instanceof Integer size ? size : 0;
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve inventory size", exception);
        }
    }

    public static ItemStack inventoryItem(Inventory inventory, int slot) {
        try {
            return (ItemStack) inventory.getClass().getMethod("getItem", int.class).invoke(inventory, slot);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve inventory item at slot " + slot, exception);
        }
    }

    public static Item stackItem(ItemStack stack) {
        try {
            return (Item) stack.getClass().getMethod("getItem").invoke(stack);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve item stack item", exception);
        }
    }

    public static Item itemLikeItem(ItemLike item) {
        try {
            return (Item) item.getClass().getMethod("asItem").invoke(item);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve item-like item", exception);
        }
    }

    public static int stackCount(ItemStack stack) {
        try {
            Object value = stack.getClass().getMethod("getCount").invoke(stack);
            return value instanceof Integer count ? count : 0;
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve item stack count", exception);
        }
    }

    public static void shrinkStack(ItemStack stack, int amount) {
        try {
            stack.getClass().getMethod("shrink", int.class).invoke(stack, amount);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to shrink item stack", exception);
        }
    }

    public static int stackDamage(ItemStack stack) {
        try {
            Object value = stack.getClass().getMethod("getDamageValue").invoke(stack);
            return value instanceof Integer damage ? damage : 0;
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve item stack damage", exception);
        }
    }

    public static int stackMaxDamage(ItemStack stack) {
        try {
            Object value = stack.getClass().getMethod("getMaxDamage").invoke(stack);
            return value instanceof Integer maxDamage ? maxDamage : 0;
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve item stack max damage", exception);
        }
    }

    public static void setStackDamage(ItemStack stack, int damage) {
        try {
            stack.getClass().getMethod("setDamageValue", int.class).invoke(stack, damage);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to set item stack damage", exception);
        }
    }

    public static ItemStack[] ingredientItems(Ingredient ingredient) {
        try {
            return (ItemStack[]) ingredient.getClass().getMethod("getItems").invoke(ingredient);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve ingredient item stacks", exception);
        }
    }

    public static String displayNameString(ItemStack stack) {
        try {
            Object component = stack.getClass().getMethod("getDisplayName").invoke(stack);
            Object value = component.getClass().getMethod("getString").invoke(component);
            return value instanceof String text ? text : String.valueOf(component);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve item stack display name", exception);
        }
    }

    @SuppressWarnings("unchecked")
    public static Multimap<Attribute, AttributeModifier> stackAttributeModifiers(ItemStack stack, EquipmentSlot slot) {
        try {
            return (Multimap<Attribute, AttributeModifier>) stack.getClass()
                    .getMethod("getAttributeModifiers", EquipmentSlot.class)
                    .invoke(stack, slot);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve item stack attribute modifiers", exception);
        }
    }

    public static Attribute attackDamageAttribute() {
        try {
            return (Attribute) Attributes.class.getField("ATTACK_DAMAGE").get(null);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve attack damage attribute", exception);
        }
    }

    public static ItemStack itemDefaultInstance(Item item) {
        try {
            return (ItemStack) item.getClass().getMethod("getDefaultInstance").invoke(item);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve item default instance", exception);
        }
    }

    public static boolean stackIsEnchanted(ItemStack stack) {
        try {
            Object value = stack.getClass().getMethod("isEnchanted").invoke(stack);
            return value instanceof Boolean enchanted && enchanted;
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve item stack enchantment state", exception);
        }
    }

    public static List<?> stackEnchantmentTags(ItemStack stack) {
        try {
            return (List<?>) stack.getClass().getMethod("getEnchantmentTags").invoke(stack);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve item stack enchantment tags", exception);
        }
    }

    public static ItemStack playerItemBySlot(Player player, EquipmentSlot slot) {
        try {
            return (ItemStack) player.getClass().getMethod("getItemBySlot", EquipmentSlot.class).invoke(player, slot);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve player item in slot " + slot, exception);
        }
    }

    public static String tagString(Object tag, String key) {
        try {
            Object value = tag.getClass().getMethod("getString", String.class).invoke(tag, key);
            return value instanceof String text ? text : "";
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve string tag " + key, exception);
        }
    }

    public static int tagInt(Object tag, String key) {
        try {
            Object value = tag.getClass().getMethod("getInt", String.class).invoke(tag, key);
            return value instanceof Integer number ? number : 0;
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve int tag " + key, exception);
        }
    }

    public static int itemEnchantmentLevel(Enchantment enchantment, ItemStack stack) {
        try {
            Object value = net.minecraft.world.item.enchantment.EnchantmentHelper.class
                    .getMethod("getItemEnchantmentLevel", Enchantment.class, ItemStack.class)
                    .invoke(null, enchantment, stack);
            return value instanceof Integer level ? level : 0;
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve item enchantment level", exception);
        }
    }

    public static void addStackAttributeModifier(ItemStack stack, Attribute attribute,
            AttributeModifier modifier, EquipmentSlot slot) {
        try {
            stack.getClass()
                    .getMethod("addAttributeModifier", Attribute.class, AttributeModifier.class, EquipmentSlot.class)
                    .invoke(stack, attribute, modifier, slot);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to add item stack attribute modifier", exception);
        }
    }

    public static Object modifierTag(AttributeModifier modifier) {
        try {
            return modifier.getClass().getMethod("save").invoke(modifier);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to serialize attribute modifier", exception);
        }
    }

    public static ItemStack emptyStack() {
        try {
            return new ItemStack((ItemLike) Items.class.getField("AIR").get(null));
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resolve air item", exception);
        }
    }

    @SuppressWarnings("unchecked")
    public static NonNullList<ItemStack> nonNullListWithSize(int size, ItemStack defaultValue) {
        try {
            return (NonNullList<ItemStack>) NonNullList.class
                    .getMethod("withSize", int.class, Object.class)
                    .invoke(null, size, defaultValue);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to create non-null item list", exception);
        }
    }
}
