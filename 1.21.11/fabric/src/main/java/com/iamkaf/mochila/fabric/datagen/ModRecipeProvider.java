package com.iamkaf.mochila.fabric.datagen;

import com.iamkaf.mochila.Constants;
import com.iamkaf.mochila.recipe.BackpackColoring;
import com.iamkaf.mochila.recipe.BackpackUpgrading;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {
    protected ModRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
        super(provider, recipeOutput);
    }

    @Override
    public void buildRecipes() {
        ShapedRecipeBuilder.shaped(
                        registries.lookupOrThrow(BuiltInRegistries.ITEM.key()),
                        RecipeCategory.TOOLS,
                        com.iamkaf.mochila.registry.Items.LEATHER_BACKPACK.get()
                )
                .pattern("ABA")
                .pattern("ACD")
                .pattern("AEB")
                .define('A', Items.LEATHER)
                .define('B', Items.IRON_INGOT)
                .define('C', Items.CHEST)
                .define('D', Items.STRING)
                .define('E', ItemTags.WOOL)
                .unlockedBy("has_leather", has(Items.LEATHER))
                .save(output);

        ShapedRecipeBuilder.shaped(
                        registries.lookupOrThrow(BuiltInRegistries.ITEM.key()),
                        RecipeCategory.TOOLS,
                        com.iamkaf.mochila.registry.Items.ENDER_BACKPACK.get()
                )
                .pattern("ABA")
                .pattern("ACD")
                .pattern("AEB")
                .define('A', Items.LEATHER)
                .define('B', Items.IRON_INGOT)
                .define('C', Items.ENDER_CHEST)
                .define('D', Items.STRING)
                .define('E', ItemTags.WOOL)
                .unlockedBy("has_eye_of_ender", has(Items.ENDER_EYE))
                .save(output);

        SpecialRecipeBuilder.special(BackpackColoring::new).save(output, Constants.resource("backpack_coloring").toString());
        SpecialRecipeBuilder.special(BackpackUpgrading::new)
                .save(output, Constants.resource("backpack_upgrading").toString());

//        SmithingTransformRecipeBuilder.smithing(Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
//                        Ingredient.of(com.iamkaf.mochila.registry.Items.DIAMOND_BACKPACK.get()),
//                        Ingredient.of(Items.NETHERITE_INGOT),
//                        RecipeCategory.TOOLS,
//                        com.iamkaf.mochila.registry.Items.NETHERITE_BACKPACK.get()
//                )
//                .unlocks("backpackety", has(Items.NETHERITE_INGOT))
//                .save(output, Mochila.resource("netherite_backpack").toString());

        netheriteUpgrade(
                com.iamkaf.mochila.registry.Items.DIAMOND_BACKPACK.get(),
                com.iamkaf.mochila.registry.Items.NETHERITE_BACKPACK.get()
        );
        netheriteUpgrade(
                com.iamkaf.mochila.registry.Items.WHITE_DIAMOND_BACKPACK.get(),
                com.iamkaf.mochila.registry.Items.WHITE_NETHERITE_BACKPACK.get()
        );
        netheriteUpgrade(
                com.iamkaf.mochila.registry.Items.LIGHT_GRAY_DIAMOND_BACKPACK.get(),
                com.iamkaf.mochila.registry.Items.LIGHT_GRAY_NETHERITE_BACKPACK.get()
        );
        netheriteUpgrade(
                com.iamkaf.mochila.registry.Items.GRAY_DIAMOND_BACKPACK.get(),
                com.iamkaf.mochila.registry.Items.GRAY_NETHERITE_BACKPACK.get()
        );
        netheriteUpgrade(
                com.iamkaf.mochila.registry.Items.BLACK_DIAMOND_BACKPACK.get(),
                com.iamkaf.mochila.registry.Items.BLACK_NETHERITE_BACKPACK.get()
        );
        netheriteUpgrade(
                com.iamkaf.mochila.registry.Items.BROWN_DIAMOND_BACKPACK.get(),
                com.iamkaf.mochila.registry.Items.BROWN_NETHERITE_BACKPACK.get()
        );
        netheriteUpgrade(
                com.iamkaf.mochila.registry.Items.RED_DIAMOND_BACKPACK.get(),
                com.iamkaf.mochila.registry.Items.RED_NETHERITE_BACKPACK.get()
        );
        netheriteUpgrade(
                com.iamkaf.mochila.registry.Items.YELLOW_DIAMOND_BACKPACK.get(),
                com.iamkaf.mochila.registry.Items.YELLOW_NETHERITE_BACKPACK.get()
        );
        netheriteUpgrade(
                com.iamkaf.mochila.registry.Items.ORANGE_DIAMOND_BACKPACK.get(),
                com.iamkaf.mochila.registry.Items.ORANGE_NETHERITE_BACKPACK.get()
        );
        netheriteUpgrade(
                com.iamkaf.mochila.registry.Items.LIME_DIAMOND_BACKPACK.get(),
                com.iamkaf.mochila.registry.Items.LIME_NETHERITE_BACKPACK.get()
        );
        netheriteUpgrade(
                com.iamkaf.mochila.registry.Items.GREEN_DIAMOND_BACKPACK.get(),
                com.iamkaf.mochila.registry.Items.GREEN_NETHERITE_BACKPACK.get()
        );
        netheriteUpgrade(
                com.iamkaf.mochila.registry.Items.CYAN_DIAMOND_BACKPACK.get(),
                com.iamkaf.mochila.registry.Items.CYAN_NETHERITE_BACKPACK.get()
        );
        netheriteUpgrade(
                com.iamkaf.mochila.registry.Items.LIGHT_BLUE_DIAMOND_BACKPACK.get(),
                com.iamkaf.mochila.registry.Items.LIGHT_BLUE_NETHERITE_BACKPACK.get()
        );
        netheriteUpgrade(
                com.iamkaf.mochila.registry.Items.BLUE_DIAMOND_BACKPACK.get(),
                com.iamkaf.mochila.registry.Items.BLUE_NETHERITE_BACKPACK.get()
        );
        netheriteUpgrade(
                com.iamkaf.mochila.registry.Items.PURPLE_DIAMOND_BACKPACK.get(),
                com.iamkaf.mochila.registry.Items.PURPLE_NETHERITE_BACKPACK.get()
        );
        netheriteUpgrade(
                com.iamkaf.mochila.registry.Items.MAGENTA_DIAMOND_BACKPACK.get(),
                com.iamkaf.mochila.registry.Items.MAGENTA_NETHERITE_BACKPACK.get()
        );
        netheriteUpgrade(
                com.iamkaf.mochila.registry.Items.PINK_DIAMOND_BACKPACK.get(),
                com.iamkaf.mochila.registry.Items.PINK_NETHERITE_BACKPACK.get()
        );
    }

    private void netheriteUpgrade(Item from, Item to) {
        SmithingTransformRecipeBuilder.smithing(
                Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                Ingredient.of(from),
                Ingredient.of(Items.NETHERITE_INGOT),
                RecipeCategory.TOOLS,
                to
        ).unlocks("backpackety", has(Items.NETHERITE_INGOT)).save(output, BuiltInRegistries.ITEM.getKey(to).toString());
    }

    public static class Runner extends FabricRecipeProvider {
        public Runner(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected @NotNull RecipeProvider createRecipeProvider(HolderLookup.@NotNull Provider registries,
                @NotNull RecipeOutput output) {
            return new ModRecipeProvider(registries, output);
        }

        @Override
        public @NotNull String getName() {
            return "Mochila Recipes";
        }
    }
}
