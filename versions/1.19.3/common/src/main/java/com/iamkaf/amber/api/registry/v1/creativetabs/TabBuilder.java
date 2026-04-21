package com.iamkaf.amber.api.registry.v1.creativetabs;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import com.iamkaf.amber.api.event.v1.events.common.CreativeModeTabOutput;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class TabBuilder {
    private static final ResourceKey<Registry<CreativeModeTab>> CREATIVE_MODE_TAB_REGISTRY =
            ResourceKey.createRegistryKey(new ResourceLocation("minecraft", "creative_mode_tab"));

    private final ResourceLocation id;
    private Component title = Component.empty();
    private Supplier<ItemStack> icon = () -> ItemStack.EMPTY;
    private final List<Supplier<ItemLike>> items = new ArrayList<>();
    private ResourceLocation backgroundTexture = new ResourceLocation("textures/gui/container/creative_inventory/tab_items.png");
    private boolean canScroll = true;
    private boolean showTitle = true;
    private boolean alignedRight = false;
    private CreativeModeTab.Row row = CreativeModeTab.Row.TOP;
    private int column = 0;
    private CreativeModeTab.Type type = CreativeModeTab.Type.CATEGORY;

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

    public TabBuilder row(CreativeModeTab.Row row) {
        this.row = row;
        return this;
    }

    public TabBuilder column(int column) {
        this.column = column;
        return this;
    }

    public TabBuilder type(CreativeModeTab.Type type) {
        this.type = type;
        return this;
    }

    CreativeModeTab build() {
        CreativeModeTab.Builder builder = CreativeModeTab.builder(row, column);

        builder.title(title);
        builder.icon(icon);
        attachDisplayItems(builder);

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
    }

    private void attachDisplayItems(CreativeModeTab.Builder builder) {
        try {
            ClassLoader classLoader = CreativeModeTab.class.getClassLoader();
            Class<?> generatorClass = Class.forName("net.minecraft.world.item.CreativeModeTab$DisplayItemsGenerator", false, classLoader);
            Object generator = Proxy.newProxyInstance(classLoader, new Class[]{generatorClass}, (proxy, method, args) -> {
                if (!"accept".equals(method.getName()) || args == null || args.length < 2) {
                    return null;
                }

                Object output = args[1];
                Method acceptStack = output.getClass().getMethod("accept", ItemStack.class);
                acceptStack.setAccessible(true);
                for (Supplier<ItemLike> item : items) {
                    acceptStack.invoke(output, new ItemStack(item.get()));
                }

                ResourceKey<CreativeModeTab> tabKey = ResourceKey.create(CREATIVE_MODE_TAB_REGISTRY, id);
                CreativeModeTabOutput amberOutput = createOutputBridge(output, classLoader);
                com.iamkaf.amber.api.event.v1.events.common.CreativeModeTabEvents.MODIFY_ENTRIES.invoker()
                        .modifyEntries(tabKey, amberOutput);
                return null;
            });

            Method displayItems = builder.getClass().getMethod("displayItems", generatorClass);
            displayItems.invoke(builder, generator);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Failed to configure creative tab display items for " + id, e);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static CreativeModeTabOutput createOutputBridge(Object output, ClassLoader classLoader) {
        return new CreativeModeTabOutput() {
            @Override
            public void accept(ItemStack stack, TabVisibility visibility) {
                try {
                    Class<? extends Enum> mcVisibilityClass =
                            (Class<? extends Enum>) Class.forName("net.minecraft.world.item.CreativeModeTab$TabVisibility", false, classLoader);
                    Enum<?> mcVisibility = Enum.valueOf((Class) mcVisibilityClass, visibility.name());
                    Method accept = output.getClass().getMethod("accept", ItemStack.class, mcVisibilityClass);
                    accept.setAccessible(true);
                    accept.invoke(output, stack, mcVisibility);
                } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    throw new IllegalStateException("Failed to forward creative tab entry", e);
                }
            }
        };
    }

    public ResourceLocation getId() {
        return id;
    }
}
