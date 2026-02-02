package com.iamkaf.mochila.registry;

import com.iamkaf.amber.api.registry.v1.DeferredRegister;
import com.iamkaf.mochila.Constants;
import com.iamkaf.mochila.item.BackpackItem;
import com.iamkaf.mochila.item.EnderBackpackItem;
import com.iamkaf.mochila.item.backpack.BackpackContainer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class Items {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Constants.MOD_ID, Registries.ITEM);
    public static final BackpackContainer.BackpackSize LEATHER_BACKPACK_SIZE = BackpackContainer.BackpackSize.TWO_ROWS;
    public static final BackpackContainer.BackpackSize IRON_BACKPACK_SIZE = BackpackContainer.BackpackSize.THREE_ROWS;
    public static final BackpackContainer.BackpackSize GOLD_BACKPACK_SIZE = BackpackContainer.BackpackSize.FOUR_ROWS;
    public static final BackpackContainer.BackpackSize DIAMOND_BACKPACK_SIZE = BackpackContainer.BackpackSize.FIVE_ROWS;
    public static final BackpackContainer.BackpackSize NETHERITE_BACKPACK_SIZE = BackpackContainer.BackpackSize.SIX_ROWS;
    public static Supplier<Item> LEATHER_BACKPACK = e("leather_backpack", LEATHER_BACKPACK_SIZE);
    public static Supplier<Item> IRON_BACKPACK = e("iron_backpack", IRON_BACKPACK_SIZE);
    public static Supplier<Item> GOLD_BACKPACK = e("gold_backpack", GOLD_BACKPACK_SIZE);
    public static Supplier<Item> DIAMOND_BACKPACK = e("diamond_backpack", DIAMOND_BACKPACK_SIZE);
    public static Supplier<Item> NETHERITE_BACKPACK = e("netherite_backpack", NETHERITE_BACKPACK_SIZE);
    public static Supplier<Item> WHITE_LEATHER_BACKPACK = e("white_leather_backpack", LEATHER_BACKPACK_SIZE);
    public static Supplier<Item> WHITE_IRON_BACKPACK = e("white_iron_backpack", IRON_BACKPACK_SIZE);
    public static Supplier<Item> WHITE_GOLD_BACKPACK = e("white_gold_backpack", GOLD_BACKPACK_SIZE);
    public static Supplier<Item> WHITE_DIAMOND_BACKPACK = e("white_diamond_backpack", DIAMOND_BACKPACK_SIZE);
    public static Supplier<Item> WHITE_NETHERITE_BACKPACK = e("white_netherite_backpack", NETHERITE_BACKPACK_SIZE);
    public static Supplier<Item> LIGHT_GRAY_LEATHER_BACKPACK = e("light_gray_leather_backpack", LEATHER_BACKPACK_SIZE);
    public static Supplier<Item> LIGHT_GRAY_IRON_BACKPACK = e("light_gray_iron_backpack", IRON_BACKPACK_SIZE);
    public static Supplier<Item> LIGHT_GRAY_GOLD_BACKPACK = e("light_gray_gold_backpack", GOLD_BACKPACK_SIZE);
    public static Supplier<Item> LIGHT_GRAY_DIAMOND_BACKPACK = e("light_gray_diamond_backpack", DIAMOND_BACKPACK_SIZE);
    public static Supplier<Item> LIGHT_GRAY_NETHERITE_BACKPACK = e("light_gray_netherite_backpack", NETHERITE_BACKPACK_SIZE);
    public static Supplier<Item> GRAY_LEATHER_BACKPACK = e("gray_leather_backpack", LEATHER_BACKPACK_SIZE);
    public static Supplier<Item> GRAY_IRON_BACKPACK = e("gray_iron_backpack", IRON_BACKPACK_SIZE);
    public static Supplier<Item> GRAY_GOLD_BACKPACK = e("gray_gold_backpack", GOLD_BACKPACK_SIZE);
    public static Supplier<Item> GRAY_DIAMOND_BACKPACK = e("gray_diamond_backpack", DIAMOND_BACKPACK_SIZE);
    public static Supplier<Item> GRAY_NETHERITE_BACKPACK = e("gray_netherite_backpack", NETHERITE_BACKPACK_SIZE);
    public static Supplier<Item> BLACK_LEATHER_BACKPACK = e("black_leather_backpack", LEATHER_BACKPACK_SIZE);
    public static Supplier<Item> BLACK_IRON_BACKPACK = e("black_iron_backpack", IRON_BACKPACK_SIZE);
    public static Supplier<Item> BLACK_GOLD_BACKPACK = e("black_gold_backpack", GOLD_BACKPACK_SIZE);
    public static Supplier<Item> BLACK_DIAMOND_BACKPACK = e("black_diamond_backpack", DIAMOND_BACKPACK_SIZE);
    public static Supplier<Item> BLACK_NETHERITE_BACKPACK = e("black_netherite_backpack", NETHERITE_BACKPACK_SIZE);
    public static Supplier<Item> BROWN_LEATHER_BACKPACK = e("brown_leather_backpack", LEATHER_BACKPACK_SIZE);
    public static Supplier<Item> BROWN_IRON_BACKPACK = e("brown_iron_backpack", IRON_BACKPACK_SIZE);
    public static Supplier<Item> BROWN_GOLD_BACKPACK = e("brown_gold_backpack", GOLD_BACKPACK_SIZE);
    public static Supplier<Item> BROWN_DIAMOND_BACKPACK = e("brown_diamond_backpack", DIAMOND_BACKPACK_SIZE);
    public static Supplier<Item> BROWN_NETHERITE_BACKPACK = e("brown_netherite_backpack", NETHERITE_BACKPACK_SIZE);
    public static Supplier<Item> RED_LEATHER_BACKPACK = e("red_leather_backpack", LEATHER_BACKPACK_SIZE);
    public static Supplier<Item> RED_IRON_BACKPACK = e("red_iron_backpack", IRON_BACKPACK_SIZE);
    public static Supplier<Item> RED_GOLD_BACKPACK = e("red_gold_backpack", GOLD_BACKPACK_SIZE);
    public static Supplier<Item> RED_DIAMOND_BACKPACK = e("red_diamond_backpack", DIAMOND_BACKPACK_SIZE);
    public static Supplier<Item> RED_NETHERITE_BACKPACK = e("red_netherite_backpack", NETHERITE_BACKPACK_SIZE);
    public static Supplier<Item> YELLOW_LEATHER_BACKPACK = e("yellow_leather_backpack", LEATHER_BACKPACK_SIZE);
    public static Supplier<Item> YELLOW_IRON_BACKPACK = e("yellow_iron_backpack", IRON_BACKPACK_SIZE);
    public static Supplier<Item> YELLOW_GOLD_BACKPACK = e("yellow_gold_backpack", GOLD_BACKPACK_SIZE);
    public static Supplier<Item> YELLOW_DIAMOND_BACKPACK = e("yellow_diamond_backpack", DIAMOND_BACKPACK_SIZE);
    public static Supplier<Item> YELLOW_NETHERITE_BACKPACK = e("yellow_netherite_backpack", NETHERITE_BACKPACK_SIZE);
    public static Supplier<Item> ORANGE_LEATHER_BACKPACK = e("orange_leather_backpack", LEATHER_BACKPACK_SIZE);
    public static Supplier<Item> ORANGE_IRON_BACKPACK = e("orange_iron_backpack", IRON_BACKPACK_SIZE);
    public static Supplier<Item> ORANGE_GOLD_BACKPACK = e("orange_gold_backpack", GOLD_BACKPACK_SIZE);
    public static Supplier<Item> ORANGE_DIAMOND_BACKPACK = e("orange_diamond_backpack", DIAMOND_BACKPACK_SIZE);
    public static Supplier<Item> ORANGE_NETHERITE_BACKPACK = e("orange_netherite_backpack", NETHERITE_BACKPACK_SIZE);
    public static Supplier<Item> LIME_LEATHER_BACKPACK = e("lime_leather_backpack", LEATHER_BACKPACK_SIZE);
    public static Supplier<Item> LIME_IRON_BACKPACK = e("lime_iron_backpack", IRON_BACKPACK_SIZE);
    public static Supplier<Item> LIME_GOLD_BACKPACK = e("lime_gold_backpack", GOLD_BACKPACK_SIZE);
    public static Supplier<Item> LIME_DIAMOND_BACKPACK = e("lime_diamond_backpack", DIAMOND_BACKPACK_SIZE);
    public static Supplier<Item> LIME_NETHERITE_BACKPACK = e("lime_netherite_backpack", NETHERITE_BACKPACK_SIZE);
    public static Supplier<Item> GREEN_LEATHER_BACKPACK = e("green_leather_backpack", LEATHER_BACKPACK_SIZE);
    public static Supplier<Item> GREEN_IRON_BACKPACK = e("green_iron_backpack", IRON_BACKPACK_SIZE);
    public static Supplier<Item> GREEN_GOLD_BACKPACK = e("green_gold_backpack", GOLD_BACKPACK_SIZE);
    public static Supplier<Item> GREEN_DIAMOND_BACKPACK = e("green_diamond_backpack", DIAMOND_BACKPACK_SIZE);
    public static Supplier<Item> GREEN_NETHERITE_BACKPACK = e("green_netherite_backpack", NETHERITE_BACKPACK_SIZE);
    public static Supplier<Item> CYAN_LEATHER_BACKPACK = e("cyan_leather_backpack", LEATHER_BACKPACK_SIZE);
    public static Supplier<Item> CYAN_IRON_BACKPACK = e("cyan_iron_backpack", IRON_BACKPACK_SIZE);
    public static Supplier<Item> CYAN_GOLD_BACKPACK = e("cyan_gold_backpack", GOLD_BACKPACK_SIZE);
    public static Supplier<Item> CYAN_DIAMOND_BACKPACK = e("cyan_diamond_backpack", DIAMOND_BACKPACK_SIZE);
    public static Supplier<Item> CYAN_NETHERITE_BACKPACK = e("cyan_netherite_backpack", NETHERITE_BACKPACK_SIZE);
    public static Supplier<Item> LIGHT_BLUE_LEATHER_BACKPACK = e("light_blue_leather_backpack", LEATHER_BACKPACK_SIZE);
    public static Supplier<Item> LIGHT_BLUE_IRON_BACKPACK = e("light_blue_iron_backpack", IRON_BACKPACK_SIZE);
    public static Supplier<Item> LIGHT_BLUE_GOLD_BACKPACK = e("light_blue_gold_backpack", GOLD_BACKPACK_SIZE);
    public static Supplier<Item> LIGHT_BLUE_DIAMOND_BACKPACK = e("light_blue_diamond_backpack", DIAMOND_BACKPACK_SIZE);
    public static Supplier<Item> LIGHT_BLUE_NETHERITE_BACKPACK = e("light_blue_netherite_backpack", NETHERITE_BACKPACK_SIZE);
    public static Supplier<Item> BLUE_LEATHER_BACKPACK = e("blue_leather_backpack", LEATHER_BACKPACK_SIZE);
    public static Supplier<Item> BLUE_IRON_BACKPACK = e("blue_iron_backpack", IRON_BACKPACK_SIZE);
    public static Supplier<Item> BLUE_GOLD_BACKPACK = e("blue_gold_backpack", GOLD_BACKPACK_SIZE);
    public static Supplier<Item> BLUE_DIAMOND_BACKPACK = e("blue_diamond_backpack", DIAMOND_BACKPACK_SIZE);
    public static Supplier<Item> BLUE_NETHERITE_BACKPACK = e("blue_netherite_backpack", NETHERITE_BACKPACK_SIZE);
    public static Supplier<Item> PURPLE_LEATHER_BACKPACK = e("purple_leather_backpack", LEATHER_BACKPACK_SIZE);
    public static Supplier<Item> PURPLE_IRON_BACKPACK = e("purple_iron_backpack", IRON_BACKPACK_SIZE);
    public static Supplier<Item> PURPLE_GOLD_BACKPACK = e("purple_gold_backpack", GOLD_BACKPACK_SIZE);
    public static Supplier<Item> PURPLE_DIAMOND_BACKPACK = e("purple_diamond_backpack", DIAMOND_BACKPACK_SIZE);
    public static Supplier<Item> PURPLE_NETHERITE_BACKPACK = e("purple_netherite_backpack", NETHERITE_BACKPACK_SIZE);
    public static Supplier<Item> MAGENTA_LEATHER_BACKPACK = e("magenta_leather_backpack", LEATHER_BACKPACK_SIZE);
    public static Supplier<Item> MAGENTA_IRON_BACKPACK = e("magenta_iron_backpack", IRON_BACKPACK_SIZE);
    public static Supplier<Item> MAGENTA_GOLD_BACKPACK = e("magenta_gold_backpack", GOLD_BACKPACK_SIZE);
    public static Supplier<Item> MAGENTA_DIAMOND_BACKPACK = e("magenta_diamond_backpack", DIAMOND_BACKPACK_SIZE);
    public static Supplier<Item> MAGENTA_NETHERITE_BACKPACK = e("magenta_netherite_backpack", NETHERITE_BACKPACK_SIZE);
    public static Supplier<Item> PINK_LEATHER_BACKPACK = e("pink_leather_backpack", LEATHER_BACKPACK_SIZE);
    public static Supplier<Item> PINK_IRON_BACKPACK = e("pink_iron_backpack", IRON_BACKPACK_SIZE);
    public static Supplier<Item> PINK_GOLD_BACKPACK = e("pink_gold_backpack", GOLD_BACKPACK_SIZE);
    public static Supplier<Item> PINK_DIAMOND_BACKPACK = e("pink_diamond_backpack", DIAMOND_BACKPACK_SIZE);
    public static Supplier<Item> PINK_NETHERITE_BACKPACK = e("pink_netherite_backpack", NETHERITE_BACKPACK_SIZE);
    public static Supplier<Item> ENDER_BACKPACK = ITEMS.register("ender_backpack", () -> new EnderBackpackItem(new Item.Properties().stacksTo(1).fireResistant().setId(ResourceKey.create(Registries.ITEM, Constants.resource("ender_backpack")))));

    private static Supplier<Item> e(String id, BackpackContainer.BackpackSize size) {
        Supplier<Item> item = ITEMS.register(id, () -> new BackpackItem(size, new Item.Properties().stacksTo(1).setId(ResourceKey.create(Registries.ITEM, Constants.resource(id)))));

        CreativeModeTabs.addItem(item);

        return item;
    }

    public static void init() {
        CreativeModeTabs.addItem(ENDER_BACKPACK);
        ITEMS.register();
    }
}
