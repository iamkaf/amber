package com.iamkaf.amber.api.registry.v1.creativetabs;

//? if <1.21
/*import com.iamkaf.amber.Constants;*/
import net.minecraft.network.chat.Component;
//? if <1.19
/*import net.minecraft.network.chat.TextComponent;*/
//? if <1.19.3 {
/*import com.iamkaf.amber.api.event.v1.events.common.CreativeModeTabEvents;
import com.iamkaf.amber.api.event.v1.events.common.CreativeModeTabOutput;
import com.iamkaf.amber.platform.Services;
import net.minecraft.core.NonNullList;
*///?}
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
    private Component title = emptyTitle();
    private Supplier<ItemStack> icon = () -> ItemStack.EMPTY;
    private final List<Supplier<ItemLike>> items = new ArrayList<>();
    private Identifier backgroundTexture = defaultId("textures/gui/container/creative_inventory/tab_items.png");
    private boolean canScroll = true;
    private boolean showTitle = true;
    private boolean alignedRight = false;
    //? if >=1.19.3
    private CreativeModeTab.Row row = CreativeModeTab.Row.TOP;
    private int column = 0;
    //? if >=1.19.3
    private CreativeModeTab.Type type = CreativeModeTab.Type.CATEGORY;

    TabBuilder(Identifier id) {
        this.id = id;
    }

    private static Component emptyTitle() {
        //? if >=1.19
        return Component.literal("");
        //? if <1.19
        /*return new TextComponent("");*/
    }

    private static Identifier defaultId(String path) {
        //? if >=1.21
        return Identifier.withDefaultNamespace(path);
        //? if <1.21
        /*return new Identifier("minecraft", path);*/
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
     * Defaults to the standard items background texture. Before Minecraft 1.21 only textures named
     * {@code minecraft:textures/gui/container/creative_inventory/tab_<name>} can be used; others keep the default.
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
    //? if >=1.19.3
    public TabBuilder row(CreativeModeTab.Row row) {
    //? if <1.19.3
    /*public TabBuilder row(Object row) {*/
        //? if >=1.19.3
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
     *
     * @param type The tab type
     * @return This builder for chaining
     * @deprecated Has no effect. Minecraft keeps every type other than {@code CATEGORY} for its own tabs.
     */
    @Deprecated
    //? if >=1.19.3
    public TabBuilder type(CreativeModeTab.Type type) {
    //? if <1.19.3
    /*public TabBuilder type(Object type) {*/
        //? if >=1.19.3
        this.type = type;
        return this;
    }

    /**
     * Builds the creative mode tab.
     * <p>
     * This is called internally during registration and should not be called directly. Before Minecraft 1.19.3
     * the tab joins the game's tab list as soon as it is built.
     *
     * @return The built creative mode tab
     */
    public CreativeModeTab build() {
        //? if >=1.19.3 {
        return applyTo(CreativeModeTab.builder(row, column)).build();
        //?} else {
        /*LegacyCreativeModeTab tab = new LegacyCreativeModeTab(Services.CREATIVE_MODE_TABS.legacyTabIndex(), this);
        if (!showTitle) {
            tab.hideTitle();
        }
        if (!canScroll) {
            tab.hideScroll();
        }
        tab.setBackgroundSuffix(backgroundSuffix());
        return tab;*/
        //?}
    }

    //? if >=1.19.3 {
    /**
     * Applies this builder's title, icon, and layout settings to a Minecraft tab builder.
     * <p>
     * Used by loader implementations that create the Minecraft builder themselves. Items are not applied here.
     *
     * @param builder The Minecraft tab builder to configure
     * @return The same Minecraft tab builder
     */
    public CreativeModeTab.Builder applyTo(CreativeModeTab.Builder builder) {
        builder.title(title);
        builder.icon(icon);
        if (alignedRight) {
            builder.alignedRight();
        }
        if (!showTitle) {
            builder.hideTitle();
        }
        if (!canScroll) {
            builder.noScrollBar();
        }
        //? if >=1.21
        builder.backgroundTexture(backgroundTexture);
        //? if <1.21
        /*builder.backgroundSuffix(backgroundSuffix());*/
        return builder;
    }
    //?}

    //? if <1.21 {
    /*private static final String LEGACY_BACKGROUND_PREFIX = "textures/gui/container/creative_inventory/tab_";

    // Older Minecraft only resolves "minecraft:" + LEGACY_BACKGROUND_PREFIX + suffix.
    private String backgroundSuffix() {
        String path = backgroundTexture.getPath();
        if (backgroundTexture.getNamespace().equals("minecraft") && path.startsWith(LEGACY_BACKGROUND_PREFIX)) {
            return path.substring(LEGACY_BACKGROUND_PREFIX.length());
        }
        Constants.LOG.warn("Creative tab {} cannot use background {} before Minecraft 1.21; keeping the default", id, backgroundTexture);
        return "items.png";
    }
    *///?}

    //? if <1.19.3 {
    /*private static final class LegacyCreativeModeTab extends CreativeModeTab {
        private final TabBuilder builder;

        private LegacyCreativeModeTab(int index, TabBuilder builder) {
            super(index, builder.id.toString().replace(':', '.'));
            this.builder = builder;
        }

        @Override
        public ItemStack makeIcon() {
            return builder.icon.get();
        }

        @Override
        public Component getDisplayName() {
            return builder.title;
        }

        @Override
        public boolean isAlignedRight() {
            return builder.alignedRight;
        }

        @Override
        public void fillItemList(NonNullList<ItemStack> stacks) {
            for (Supplier<ItemLike> item : builder.items) {
                stacks.add(new ItemStack(item.get()));
            }
            CreativeModeTabOutput output = (stack, visibility) -> stacks.add(stack);
            CreativeModeTabEvents.MODIFY_ENTRIES.invoker().modifyEntries(CreativeTabHelper.creativeModeTabKey(builder.id), output);
        }
    }
    *///?}

    /**
     * Gets the ID of this tab.
     *
     * @return The tab ID
     */
    public Identifier getId() {
        return id;
    }

    public Component getTitle() {
        return title;
    }

    public Supplier<ItemStack> getIcon() {
        return icon;
    }

    public int getColumn() {
        return column;
    }

    //? if >=1.19.3 {
    public CreativeModeTab.Row getRow() {
        return row;
    }

    public CreativeModeTab.Type getType() {
        return type;
    }
    //?}

    public boolean canScroll() {
        return canScroll;
    }

    public boolean shouldShowTitle() {
        return showTitle;
    }

    public boolean isAlignedRight() {
        return alignedRight;
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
