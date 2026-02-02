package com.iamkaf.mochila.fabric.datagen;

import com.iamkaf.mochila.Constants;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ModLanguageProvider extends FabricLanguageProvider {

    private static final List<String> MATERIALS = List.of("leather", "iron", "gold", "diamond", "netherite");
    private static final List<String> COLORS = List.of(
            "",
            "white",
            "light_gray",
            "gray",
            "black",
            "brown",
            "red",
            "yellow",
            "orange",
            "lime",
            "green",
            "cyan",
            "light_blue",
            "blue",
            "purple",
            "magenta",
            "pink"
    );

    public ModLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    protected void addTranslations(TranslationBuilder translationBuilder) {
        translationBuilder.add("creativetab.mochila.mochila", "Mochila");
        translationBuilder.add("key.category.mochila.mochila", "Mochila");
        translationBuilder.add("key.mochila.open_backpack", "Open Backpack");
        translationBuilder.add("key.mochila.open_ender_backpack", "Open Ender Backpack");
        translationBuilder.add("tag.item.mochila.backpacks", "Backpacks");
        translationBuilder.add("tag.item.mochila.leather_backpacks", "Leather Backpacks");
        translationBuilder.add("tag.item.mochila.iron_backpacks", "Iron Backpacks");
        translationBuilder.add("tag.item.mochila.gold_backpacks", "Gold Backpacks");
        translationBuilder.add("tag.item.mochila.diamond_backpacks", "Diamond Backpacks");
        translationBuilder.add("tag.item.mochila.netherite_backpacks", "Netherite Backpacks");
        addBackpackTranslations(translationBuilder);
        translationBuilder.add("item.mochila.ender_backpack", "Ender Backpack");

        translationBuilder.add("item.mochila.backpack.size", "Size: %s");
        translationBuilder.add("item.mochila.backpack.mode", "Mode: %s");
        translationBuilder.add("item.mochila.backpack.dump", "Sneak Right-Click on a chest to store all items.");
        translationBuilder.add(
                "item.mochila.backpack.store",
                "Sneak Right-Click on a chest to store items that are already on the chest."
        );
        translationBuilder.add("item.mochila.backpack.shift", "Use SHIFT+B to swap modes.");
        translationBuilder.add("mochila.keybind_mode_changed", "Backpack mode changed: %s");
    }

    private void addBackpackTranslations(TranslationBuilder translationBuilder) {
        for (String color : COLORS) {
            for (String material : MATERIALS) {
                String itemName = formatItemName(color, material);
                String key = formatTranslationKey(color, material);
                translationBuilder.add(String.format("item.%s.%s", Constants.MOD_ID, key), itemName);
            }
        }
    }

    private String formatItemName(String color, String material) {
        String formattedMaterial = StringUtils.capitalize(material) + " Backpack";

        if (color.isEmpty()) {
            return formattedMaterial;
        }

        String formattedColor =
                Arrays.stream(color.split("_")).map(StringUtils::capitalize).collect(Collectors.joining(" "));

        return formattedColor + " " + formattedMaterial;
    }

    private String formatTranslationKey(String color, String material) {
        return color.isEmpty() ? material + "_backpack" : color + "_" + material + "_backpack";
    }

    @Override
    public void generateTranslations(HolderLookup.Provider provider, TranslationBuilder translationBuilder) {
        addTranslations(translationBuilder);
    }
}
