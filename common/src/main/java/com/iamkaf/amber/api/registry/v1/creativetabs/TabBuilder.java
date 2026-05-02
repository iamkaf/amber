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
        //? if >=1.20 {
        if (com.iamkaf.amber.api.platform.v1.Platform.isFabric()) {
            configureFabricDisplayItems(builder);
        }
        //?}
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
        // Note: backgroundTexture and type methods are protected in Minecraft,
        // but they can be accessed through reflection in platform-specific implementations if needed

        return builder.build();
        //?} else {
        /*return new CreativeModeTab(nextLegacyTabIndex(), legacyTabName(id)) {
            @Override
            public ItemStack makeIcon() {
                return icon.get();
            }

            @Override
            public void fillItemList(NonNullList<ItemStack> stacks) {
                for (Supplier<ItemLike> item : items) {
                    stacks.add(new ItemStack(item.get()));
                }
                com.iamkaf.amber.api.event.v1.events.common.CreativeModeTabEvents.MODIFY_ENTRIES.invoker()
                        .modifyEntries(
                                CreativeTabHelper.creativeModeTabKey(id),
                                new com.iamkaf.amber.api.event.v1.events.common.CreativeModeTabOutput() {
                                    @Override
                                    public void accept(ItemStack stack, com.iamkaf.amber.api.event.v1.events.common.CreativeModeTabOutput.TabVisibility visibility) {
                                        stacks.add(stack);
                                    }
                                }
                        );
            }
        };*/
        //?}
    }

    //? if <1.19.3 {
    private static int nextLegacyTabIndex() {
        try {
            Object count = CreativeModeTab.class.getMethod("getGroupCountSafe").invoke(null);
            if (count instanceof Integer value) {
                return value;
            }
        } catch (ReflectiveOperationException ignored) {
            // Vanilla and Fabric do not expose Forge's dynamically-sized group counter.
        }

        try {
            Object tabs = CreativeModeTab.class.getField("TABS").get(null);
            return java.lang.reflect.Array.getLength(tabs);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to determine legacy creative tab index", exception);
        }
    }

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

    //? if >=1.20 {
    private void configureFabricDisplayItems(CreativeModeTab.Builder builder) {
        try {
            java.lang.reflect.Field displayItemsGenerator = CreativeModeTab.Builder.class.getDeclaredField("displayItemsGenerator");
            displayItemsGenerator.setAccessible(true);
            Class<?> generatorType = displayItemsGenerator.getType();
            Object generator = java.lang.reflect.Proxy.newProxyInstance(
                    generatorType.getClassLoader(),
                    new Class<?>[]{generatorType},
                    (proxy, method, args) -> {
                        if ("accept".equals(method.getName()) && args != null && args.length == 2) {
                            Object output = args[1];
                            for (Supplier<ItemLike> itemSupplier : items) {
                                acceptCreativeTabOutput(output, new ItemStack(itemSupplier.get()));
                            }

                            com.iamkaf.amber.api.event.v1.events.common.CreativeModeTabEvents.MODIFY_ENTRIES.invoker()
                                    .modifyEntries(
                                            net.minecraft.resources.ResourceKey.create(
                                                    net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB,
                                                    id
                                            ),
                                            new com.iamkaf.amber.api.event.v1.events.common.CreativeModeTabOutput() {
                                                @Override
                                                public void accept(ItemStack stack, com.iamkaf.amber.api.event.v1.events.common.CreativeModeTabOutput.TabVisibility visibility) {
                                                    acceptCreativeTabOutput(output, stack);
                                                }
                                            }
                                    );
                        }
                        return null;
                    }
            );
            displayItemsGenerator.set(builder, generator);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Failed to configure Fabric creative tab contents for " + id, exception);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void acceptCreativeTabOutput(Object output, ItemStack stack) {
        try {
            Class<?> visibilityType = Class.forName("net.minecraft.world.item.CreativeModeTab$TabVisibility");
            Object visibility = java.lang.Enum.valueOf((Class) visibilityType.asSubclass(Enum.class), "PARENT_AND_SEARCH_TABS");
            java.lang.reflect.Method accept = null;
            for (java.lang.reflect.Method method : output.getClass().getMethods()) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if ("accept".equals(method.getName())
                        && parameterTypes.length == 2
                        && parameterTypes[0].isAssignableFrom(ItemStack.class)
                        && parameterTypes[1].isAssignableFrom(visibilityType)) {
                    accept = method;
                    break;
                }
            }
            if (accept == null) {
                throw new NoSuchMethodException("Creative tab output accept(ItemStack, TabVisibility)");
            }
            accept.setAccessible(true);
            accept.invoke(output, stack, visibility);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Failed to add item to creative tab output", exception);
        }
    }
    //?}
}
