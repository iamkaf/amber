package com.iamkaf.amber.api.registry.v1.creativetabs;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class TabBuilder {
    private final ResourceLocation id;
    private Component title = new TextComponent("");
    private Supplier<ItemStack> icon = () -> ItemStack.EMPTY;
    private final List<Supplier<ItemLike>> items = new ArrayList<>();
    private ResourceLocation backgroundTexture = new ResourceLocation("textures/gui/container/creative_inventory/tab_items.png");
    private boolean canScroll = true;
    private boolean showTitle = true;
    private boolean alignedRight = false;
    private int column = 0;

    TabBuilder(ResourceLocation id) {
        this.id = id;
    }

    public TabBuilder title(Component title) {
        this.title = title;
        return this;
    }

    public TabBuilder icon(Supplier<ItemStack> icon) {
        this.icon = icon;
        return this;
    }

    public TabBuilder icon(ItemLike icon) {
        return icon(() -> new ItemStack(icon));
    }

    public TabBuilder addItem(Supplier<ItemLike> item) {
        items.add(item);
        return this;
    }

    public TabBuilder addItem(ItemLike item) {
        return addItem(() -> item);
    }

    public TabBuilder addItems(ItemLike... items) {
        for (ItemLike item : items) {
            addItem(item);
        }
        return this;
    }

    public TabBuilder backgroundTexture(ResourceLocation backgroundTexture) {
        this.backgroundTexture = backgroundTexture;
        return this;
    }

    public TabBuilder canScroll(boolean canScroll) {
        this.canScroll = canScroll;
        return this;
    }

    public TabBuilder showTitle(boolean showTitle) {
        this.showTitle = showTitle;
        return this;
    }

    public TabBuilder alignedRight(boolean alignedRight) {
        this.alignedRight = alignedRight;
        return this;
    }

    public TabBuilder row(Object row) {
        return this;
    }

    public TabBuilder column(int column) {
        this.column = column;
        return this;
    }

    public TabBuilder type(Object type) {
        return this;
    }

    CreativeModeTab build() {
        return CreativeModeTab.TAB_MISC;
    }

    public ResourceLocation getId() {
        return id;
    }

}
