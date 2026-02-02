package com.iamkaf.mochila.item.backpack;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

// Based on the NeoForge docs: https://docs.neoforged.net/docs/blockentities/container#containers-on-itemstacks

public class BackpackContainer extends SimpleContainer {
    private final ItemStack stack;
    private final BackpackSize size;

    public BackpackContainer(ItemStack stack, BackpackSize size) {
        super(sizeToInt(size));
        this.stack = stack;
        this.size = size;

        ItemContainerContents contents =
                stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        contents.copyInto(this.getItems());
    }

    public static int sizeToInt(BackpackSize size) {
        return switch (size) {
            case TWO_ROWS -> 18;
            case THREE_ROWS -> 27;
            case FOUR_ROWS -> 36;
            case FIVE_ROWS -> 45;
            case SIX_ROWS -> 54;
        };
    }

    public MenuType<ChestMenu> getMenuType() {
        return sizeToChestMenu(size);
    }

    private static MenuType<ChestMenu> sizeToChestMenu(BackpackSize size) {
        return switch (size) {
            case TWO_ROWS -> MenuType.GENERIC_9x2;
            case THREE_ROWS -> MenuType.GENERIC_9x3;
            case FOUR_ROWS -> MenuType.GENERIC_9x4;
            case FIVE_ROWS -> MenuType.GENERIC_9x5;
            case SIX_ROWS -> MenuType.GENERIC_9x6;
        };
    }

    public int rows() {
        return switch (size) {
            case TWO_ROWS -> 2;
            case THREE_ROWS -> 3;
            case FOUR_ROWS -> 4;
            case FIVE_ROWS -> 5;
            case SIX_ROWS -> 6;
        };
    }

    @Override
    public void setChanged() {
        super.setChanged();
        this.stack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.getItems()));
    }

    public enum BackpackSize {
        TWO_ROWS,
        THREE_ROWS,
        FOUR_ROWS,
        FIVE_ROWS,
        SIX_ROWS
    }
}
