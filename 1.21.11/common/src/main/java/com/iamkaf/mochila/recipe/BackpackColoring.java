package com.iamkaf.mochila.recipe;

import com.iamkaf.mochila.item.BackpackItem;
import com.iamkaf.mochila.item.backpack.BackpackUtils;
import com.iamkaf.mochila.registry.RecipeSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * Colors a backpack copying its contents and tier.
 */
public class BackpackColoring extends CustomRecipe {
    public BackpackColoring(CraftingBookCategory category) {
        super(category);
    }

    public boolean matches(CraftingInput input, Level level) {
        int backpackCount = 0;
        int dyeCount = 0;

        for (int k = 0; k < input.size(); k++) {
            ItemStack itemStack = input.getItem(k);
            if (!itemStack.isEmpty()) {
                if (itemStack.getItem() instanceof BackpackItem) {
                    backpackCount++;
                } else {
                    if (!(itemStack.getItem() instanceof DyeItem)) {
                        return false;
                    }

                    dyeCount++;
                }

                if (dyeCount > 1 || backpackCount > 1) {
                    return false;
                }
            }
        }

        return backpackCount == 1 && dyeCount == 1;
    }

    // TODO: add support for coloring higher tiers backpacks
    public @NotNull ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack backpack = ItemStack.EMPTY;
        DyeItem dyeItem = (DyeItem) Items.WHITE_DYE;

        for (int i = 0; i < input.size(); i++) {
            ItemStack inputStack = input.getItem(i);
            if (!inputStack.isEmpty()) {
                Item item = inputStack.getItem();
                if (item instanceof BackpackItem) {
                    backpack = inputStack;
                } else if (item instanceof DyeItem) {
                    dyeItem = (DyeItem) item;
                }
            }
        }

        Item item = BackpackUtils.getBackpackByColor(backpack, dyeItem.getDyeColor());
        return backpack.transmuteCopy(item, 1);
    }

    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public @NotNull RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return RecipeSerializers.BACKPACK_COLORING_SERIALIZER.get();
    }
}
