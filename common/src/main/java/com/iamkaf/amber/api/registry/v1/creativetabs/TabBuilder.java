package com.iamkaf.amber.api.registry.v1.creativetabs;

import net.minecraft.network.chat.Component;
//? if <1.19
/*import net.minecraft.network.chat.TextComponent;*/
//? if <1.19.3
/*import net.minecraft.core.NonNullList;*/
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

//? if <1.19.3
/*import java.lang.reflect.Field;*/
import java.util.ArrayList;
//? if <1.19.3
/*import java.util.Arrays;*/
import java.util.List;
import java.util.function.Supplier;
//? if <1.19.3
/*import sun.misc.Unsafe;*/

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
     * <p>
     * Defaults to CATEGORY.
     * 
     * @param type The tab type
     * @return This builder for chaining
     */
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
     * This is called internally during registration and should not be called directly.
     *
     * @return The built creative mode tab
     */
    public CreativeModeTab build() {
        //? if >=1.19.3 {
        CreativeModeTab.Builder builder = CreativeModeTab.builder(row, column);

        builder.title(title);
        builder.icon(icon);
        //? if <26.1
        /*// Items are added via MODIFY_ENTRIES in platform-specific implementations.*/

        if (alignedRight) {
            builder.alignedRight();
        }
        if (!showTitle) {
            builder.hideTitle();
        }
        if (!canScroll) {
            builder.noScrollBar();
        }
        return builder.build();
        //?} else {
        /*class LegacyCreativeModeTab extends CreativeModeTab {
            private final Identifier tabId;
            private final Supplier<ItemStack> tabIcon;
            private final List<Supplier<ItemLike>> tabItems;

            private LegacyCreativeModeTab(int index, String name, Identifier tabId, Supplier<ItemStack> tabIcon, List<Supplier<ItemLike>> tabItems) {
                super(index, name);
                this.tabId = tabId;
                this.tabIcon = tabIcon;
                this.tabItems = tabItems;
            }

            @Override
            public ItemStack makeIcon() {
                return tabIcon.get();
            }

            @Override
            public void fillItemList(NonNullList<ItemStack> stacks) {
                for (Supplier<ItemLike> item : tabItems) {
                    stacks.add(new ItemStack(item.get()));
                }
                com.iamkaf.amber.api.event.v1.events.common.CreativeModeTabEvents.MODIFY_ENTRIES.invoker()
                        .modifyEntries(
                                CreativeTabHelper.creativeModeTabKey(tabId),
                                new com.iamkaf.amber.api.event.v1.events.common.CreativeModeTabOutput() {
                                    @Override
                                    public void accept(ItemStack stack, com.iamkaf.amber.api.event.v1.events.common.CreativeModeTabOutput.TabVisibility visibility) {
                                        stacks.add(stack);
                                    }
                                }
                        );
            }
        }
        return new LegacyCreativeModeTab(nextLegacyTabIndex(), legacyTabName(id), id, icon, items);*/
        //?}
    }

    //? if <1.19.3 {
    /*@SuppressWarnings("deprecation")*/
    private static int nextLegacyTabIndex() {
        //? if <1.19.3 {
        /*CreativeModeTab[] tabs = legacyTabs();
        int index = tabs.length;
        setLegacyTabs(Arrays.copyOf(tabs, index + 1));
        return index;*/
        //?}
        //? if >=1.19.3
        return 0;
    }

    //? if <1.19.3 {
    /*private static CreativeModeTab[] legacyTabs() {
        return legacyTabsReflectively();
    }

    private static void setLegacyTabs(CreativeModeTab[] tabs) {
        setLegacyTabsReflectively(tabs);
    }

    private static CreativeModeTab[] legacyTabsReflectively() {
        try {
            Field field = CreativeModeTab.class.getDeclaredField("TABS");
            field.setAccessible(true);
            return (CreativeModeTab[]) field.get(null);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to read legacy creative tabs", exception);
        }
    }

    private static void setLegacyTabsReflectively(CreativeModeTab[] tabs) {
        try {
            Field field = CreativeModeTab.class.getDeclaredField("TABS");
            field.setAccessible(true);
            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            Unsafe unsafe = (Unsafe) unsafeField.get(null);
            unsafe.putObject(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field), tabs);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to resize legacy creative tabs", exception);
        }
    }*/
    //?}

    private static String legacyTabName(Identifier id) {
        return id.toString().replace(':', '.');
    }
    //?}

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
