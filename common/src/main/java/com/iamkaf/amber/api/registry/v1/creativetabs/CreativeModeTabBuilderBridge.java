package com.iamkaf.amber.api.registry.v1.creativetabs;

import com.iamkaf.amber.api.event.v1.events.common.CreativeModeTabEvents;
import com.iamkaf.amber.api.event.v1.events.common.CreativeModeTabOutputBridge;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
//? if >=1.19.4 {
import net.minecraft.resources.ResourceKey;
//?}

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.function.Supplier;

public final class CreativeModeTabBuilderBridge {
    private CreativeModeTabBuilderBridge() {
    }

    //? if >=1.19.4 {
    public static void attach(CreativeModeTab.Builder builder, Iterable<Supplier<ItemLike>> items, ResourceKey<CreativeModeTab> tabKey) {
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

                CreativeModeTabEvents.MODIFY_ENTRIES.invoker()
                        .modifyEntries(tabKey, CreativeModeTabOutputBridge.wrap(output));
                return null;
            });

            Method displayItems = builder.getClass().getMethod("displayItems", generatorClass);
            displayItems.invoke(builder, generator);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Failed to configure creative tab display items for " + tabKey, e);
        }
    }
    //?} else {
    public static void attach(Object builder, Iterable<Supplier<ItemLike>> items, Object tabKey) {
        throw new UnsupportedOperationException("Creative tab builder bridge is unavailable before 1.19.4");
    }
    //?}
}
