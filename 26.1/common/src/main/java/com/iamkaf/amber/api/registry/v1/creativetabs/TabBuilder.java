package com.iamkaf.amber.api.registry.v1.creativetabs;

import com.iamkaf.amber.api.event.v1.events.common.CreativeModeTabOutput;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Builder for creating custom creative mode tabs.
 * <p>
 * This provides a unified way to configure and create creative mode tabs
 * that works across all mod loaders.
 * <p>
 * Example usage:
 * <pre>{@code
 * RegistrySupplier<CreativeModeTab> myTab = CreativeModeTabRegistry.register(
 *     CreativeModeTabRegistry.builder("example")
 *         .title(Component.translatable("itemGroup.mymod.example"))
 *         .icon(MyItems.EXAMPLE_ITEM)
 *         .addItem(MyItems.EXAMPLE_ITEM)
 *         .addItem(MyBlocks.EXAMPLE_BLOCK)
 * );
 * }</pre>
 */
public class TabBuilder {
    private final Identifier id;
    private Component title = Component.empty();
    private Supplier<ItemStack> icon = () -> ItemStack.EMPTY;
    private final List<Supplier<ItemLike>> items = new ArrayList<>();
    private Identifier backgroundTexture = Identifier.withDefaultNamespace("textures/gui/container/creative_inventory/tab_items.png");
    private boolean canScroll = true;
    private boolean showTitle = true;
    private boolean alignedRight = false;
    private CreativeModeTab.Row row = CreativeModeTab.Row.TOP;
    private int column = 0;
    private CreativeModeTab.Type type = CreativeModeTab.Type.CATEGORY;

    TabBuilder(Identifier id) {
        this.id = id;
    }

    /**
     * Sets the title of the tab.
     * <p>
     * This should be a translatable component that will be displayed
     * as the tab's name in the creative inventory.
     * 
     * @param title The title component
     * @return This builder for chaining
     */
    public TabBuilder title(Component title) {
        this.title = title;
        return this;
    }

    /**
     * Sets the icon of the tab.
     * <p>
     * The icon will be displayed in the creative inventory tab selector.
     * 
     * @param icon A supplier that returns the icon item stack
     * @return This builder for chaining
     */
    public TabBuilder icon(Supplier<ItemStack> icon) {
        this.icon = icon;
        return this;
    }

    /**
     * Sets the icon of the tab.
     * <p>
     * The icon will be displayed in the creative inventory tab selector.
     * 
     * @param icon The item to use as the icon
     * @return This builder for chaining
     */
    public TabBuilder icon(ItemLike icon) {
        return icon(() -> new ItemStack(icon));
    }

    /**
     * Adds an item to this tab.
     * <p>
     * The item will be displayed in the tab when it's opened.
     * 
     * @param item A supplier that returns the item to add
     * @return This builder for chaining
     */
    public TabBuilder addItem(Supplier<ItemLike> item) {
        items.add(item);
        return this;
    }

    /**
     * Adds an item to this tab.
     * <p>
     * The item will be displayed in the tab when it's opened.
     * 
     * @param item The item to add
     * @return This builder for chaining
     */
    public TabBuilder addItem(ItemLike item) {
        return addItem(() -> item);
    }

    /**
     * Adds multiple items to this tab.
     * <p>
     * The items will be displayed in the tab when it's opened.
     * 
     * @param items The items to add
     * @return This builder for chaining
     */
    public TabBuilder addItems(ItemLike... items) {
        for (ItemLike item : items) {
            addItem(item);
        }
        return this;
    }

    /**
     * Sets the background texture for the tab.
     * <p>
     * Defaults to the standard items background texture.
     * 
     * @param backgroundTexture The background texture location
     * @return This builder for chaining
     */
    public TabBuilder backgroundTexture(Identifier backgroundTexture) {
        this.backgroundTexture = backgroundTexture;
        return this;
    }

    /**
     * Sets whether the tab can be scrolled.
     * <p>
     * Defaults to true.
     * 
     * @param canScroll Whether the tab should have a scrollbar
     * @return This builder for chaining
     */
    public TabBuilder canScroll(boolean canScroll) {
        this.canScroll = canScroll;
        return this;
    }

    /**
     * Sets whether the tab should show its title.
     * <p>
     * Defaults to true.
     * 
     * @param showTitle Whether the tab should show its title
     * @return This builder for chaining
     */
    public TabBuilder showTitle(boolean showTitle) {
        this.showTitle = showTitle;
        return this;
    }

    /**
     * Sets whether the tab should be aligned to the right.
     * <p>
     * Defaults to false.
     * 
     * @param alignedRight Whether the tab should be aligned right
     * @return This builder for chaining
     */
    public TabBuilder alignedRight(boolean alignedRight) {
        this.alignedRight = alignedRight;
        return this;
    }

    /**
     * Sets the row position of the tab.
     * <p>
     * Defaults to TOP.
     * 
     * @param row The row position
     * @return This builder for chaining
     */
    public TabBuilder row(CreativeModeTab.Row row) {
        this.row = row;
        return this;
    }

    /**
     * Sets the column position of the tab.
     * <p>
     * Defaults to 0.
     * 
     * @param column The column position
     * @return This builder for chaining
     */
    public TabBuilder column(int column) {
        this.column = column;
        return this;
    }

    /**
     * Sets the type of the tab.
     * <p>
     * Defaults to CATEGORY.
     * 
     * @param type The tab type
     * @return This builder for chaining
     */
    public TabBuilder type(CreativeModeTab.Type type) {
        this.type = type;
        return this;
    }

    /**
     * Builds the creative mode tab.
     * <p>
     * This is called internally during registration and should not be called directly.
     *
     * @return The built creative mode tab
     */
    CreativeModeTab build() {
        CreativeModeTab.Builder builder = CreativeModeTab.builder(row, column);

        builder.title(title);
        builder.icon(icon);
        // Note: displayItems is not used here because CreativeModeTab.Output is protected in 26.1
        // Items are added via the MODIFY_ENTRIES event in platform-specific implementations

        if (alignedRight) {
            builder.alignedRight();
        }
        if (!showTitle) {
            builder.hideTitle();
        }
        if (!canScroll) {
            builder.noScrollBar();
        }
        // Note: backgroundTexture and type methods are protected in Minecraft,
        // but they can be accessed through reflection in platform-specific implementations if needed

        return builder.build();
    }

    /**
     * Gets the ID of this tab.
     *
     * @return The tab ID
     */
    public Identifier getId() {
        return id;
    }

    /**
     * Gets the items registered to this tab builder.
     *
     * @return The list of item suppliers
     */
    public List<Supplier<ItemLike>> getItems() {
        return items;
    }
}