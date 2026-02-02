package com.iamkaf.mochila.fabric.datagen;

import com.iamkaf.mochila.Constants;
import com.iamkaf.mochila.registry.Items;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.Optional;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) {

    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerators) {
        List<String> materials = List.of("leather", "iron", "gold", "diamond", "netherite");
        List<String> colors = List.of(
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

        int i = 1;
        for (var color : colors) {
            for (var material : materials) {
                String itemName;
                if (!color.isEmpty()) {
                    itemName = String.join("_", color, material, "backpack");
                } else {
                    itemName = String.join("_", material, "backpack");
                }

                Item theBackpack = BuiltInRegistries.ITEM.getValue(Constants.resource(itemName));

                // this whole part here is a bit of a mess, it's here because the way the textures are
                // named does not match the item ids
                ModelTemplate model = new ModelTemplate(
                        Optional.of(Identifier.withDefaultNamespace("item/generated")),
                        Optional.empty(),
                        TextureSlot.LAYER0
                );
                itemModelGenerators.itemModelOutput.accept(
                        theBackpack, ItemModelUtils.plainModel(model.create(
                                theBackpack,
                                TextureMapping.layer0(Constants.resource("item/backpack" + i)),
                                itemModelGenerators.modelOutput
                        ))
                );

                i++;
            }
        }

        itemModelGenerators.generateFlatItem(Items.ENDER_BACKPACK.get(), ModelTemplates.FLAT_ITEM);
    }
}
