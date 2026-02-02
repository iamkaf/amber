package com.iamkaf.mochila.fabric.datagen;

import com.iamkaf.mochila.Constants;
import com.iamkaf.mochila.tags.MochilaTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ModItemTagProvider(FabricDataOutput output,
            CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        var backpacks = valueLookupBuilder(MochilaTags.Items.BACKPACKS);
        var leather_backpacks = valueLookupBuilder(MochilaTags.Items.LEATHER_BACKPACKS);
        var iron_backpacks = valueLookupBuilder(MochilaTags.Items.IRON_BACKPACKS);
        var gold_backpacks = valueLookupBuilder(MochilaTags.Items.GOLD_BACKPACKS);
        var diamond_backpacks = valueLookupBuilder(MochilaTags.Items.DIAMOND_BACKPACKS);
        var netherite_backpacks = valueLookupBuilder(MochilaTags.Items.NETHERITE_BACKPACKS);

        for (var item : BuiltInRegistries.ITEM) {
            Identifier id = BuiltInRegistries.ITEM.getKey(item);
            if (!id.getNamespace().equals("mochila")) {
                continue;
            }
            Constants.LOG.info("Mochila: {}", item);
            backpacks.add(item);

            if (id.getPath().contains("leather")) {
                leather_backpacks.add(item);
            }
            if (id.getPath().contains("iron")) {
                iron_backpacks.add(item);
            }
            if (id.getPath().contains("gold")) {
                gold_backpacks.add(item);
            }
            if (id.getPath().contains("diamond")) {
                diamond_backpacks.add(item);
            }
            if (id.getPath().contains("netherite")) {
                netherite_backpacks.add(item);
            }
        }
    }
}
