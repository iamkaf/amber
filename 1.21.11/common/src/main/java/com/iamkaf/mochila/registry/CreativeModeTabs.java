package com.iamkaf.mochila.registry;

import com.iamkaf.amber.api.registry.v1.DeferredRegister;
import com.iamkaf.amber.api.registry.v1.RegistrySupplier;
import com.iamkaf.mochila.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class CreativeModeTabs {
    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Constants.MOD_ID, Registries.CREATIVE_MODE_TAB);

    private static final List<Supplier<Item>> ITEMS = new ArrayList<>();

    public static final RegistrySupplier<CreativeModeTab> MOCHILA = TABS.register(Constants.MOD_ID, () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .icon(() -> new ItemStack(Items.LIGHT_BLUE_IRON_BACKPACK.get()))
            .title(Component.translatable("creativetab." + Constants.MOD_ID + "." + Constants.MOD_ID))
            .displayItems((itemDisplayParameters, output) -> {
                ITEMS.forEach(item -> output.accept(item.get()));
            }).build());

    public static void addItem(Supplier<Item> item) {
        ITEMS.add(item);
    }

    public static void init() {
        TABS.register();
    }
}