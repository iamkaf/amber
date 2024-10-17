package com.iamkaf.amber.api.item;


import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ArmorTierHelper {
    public static Map<ArmorItem.Type, Integer> defense(int boots, int leggings, int chestplate, int helmet,
            int body) {
        return Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
            map.put(ArmorItem.Type.BOOTS, boots);
            map.put(ArmorItem.Type.LEGGINGS, leggings);
            map.put(ArmorItem.Type.CHESTPLATE, chestplate);
            map.put(ArmorItem.Type.HELMET, helmet);
            map.put(ArmorItem.Type.BODY, body);
        });
    }

    public static List<ArmorMaterial.Layer> genericLayers(String modId, String name) {
        List<ArmorMaterial.Layer> layers = new ArrayList<>();

        // Creates a new armor texture that will be located at:
        // - 'assets/amberdreams/textures/models/armor/name_layer_1.png' for the outer texture
        // - 'assets/amberdreams/textures/models/armor/name_layer_2.png' for the inner texture (only legs)
        layers.add(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(modId, name)));

        return layers;
    }

    public static List<ArmorMaterial.Layer> genericLayers(String modId, String name, boolean dyeable) {
        List<ArmorMaterial.Layer> layers = new ArrayList<>();

        // Creates a new armor texture that will be located at:
        // - 'assets/amberdreams/textures/models/armor/name_layer_1.png' for the outer texture
        // - 'assets/amberdreams/textures/models/armor/name_layer_2.png' for the inner texture (only legs)
        layers.add(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(modId, name)));

        if (dyeable) {
            // Creates a new armor texture that will be rendered on top of the previous at:
            // - 'assets/amberdreams/textures/models/armor/name_layer_1_overlay.png' for the outer texture
            // - 'assets/amberdreams/textures/models/armor/name_layer_2_overlay.png' for the inner texture
            // (only legs)
            // 'true' means that the armor material is dyeable; however, the item must also be added to the
            // 'minecraft:dyeable' tag
            layers.add(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(modId, name), "_overlay", true));
        }

        return layers;
    }


    public static Supplier<Ingredient> repair(Supplier<Item> item) {
        return () -> Ingredient.of(item.get());
    }

    public enum VanillaToughness {
        TURTLE_SHELL(0, 0, 0, 0, 0),
        LEATHER(0, 0, 0, 0, 0),
        GOLD(0, 0, 0, 0, 0),
        CHAINMAIL(0, 0, 0, 0, 0),
        IRON(0, 0, 0, 0, 0),
        DIAMOND(8, 2, 2, 2, 2),
        NETHERITE(13, 3, 3, 3, 3);

        public final int fullSet;
        public final int helmet;
        public final int chestplate;
        public final int leggings;
        public final int boots;

        VanillaToughness(int fullSet, int helmet, int chestplate, int leggings, int boots) {
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
