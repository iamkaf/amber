package com.iamkaf.mochila.registry;

import com.iamkaf.amber.api.registry.v1.DeferredRegister;
import com.iamkaf.amber.api.registry.v1.RegistrySupplier;
import com.iamkaf.mochila.Constants;
import com.iamkaf.mochila.recipe.BackpackColoring;
import com.iamkaf.mochila.recipe.BackpackUpgrading;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class RecipeSerializers {
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Constants.MOD_ID, Registries.RECIPE_SERIALIZER);

    public static final RegistrySupplier<RecipeSerializer<? extends CustomRecipe>> BACKPACK_COLORING_SERIALIZER =
            RECIPE_SERIALIZERS.register(
                    "crafting_special_backpackcoloring",
                    () -> new CustomRecipe.Serializer<>(BackpackColoring::new)
            );

    public static final RegistrySupplier<RecipeSerializer<? extends CustomRecipe>> BACKPACK_UPGRADING_SERIALIZER =
            RECIPE_SERIALIZERS.register(
                    "crafting_special_backpackupgrading",
                    () -> new CustomRecipe.Serializer<>(BackpackUpgrading::new)
            );

    public static void init() {
        RECIPE_SERIALIZERS.register();
    }
}
