package com.iamkaf.amber.api.event.v1.events.common;

import net.minecraft.world.item.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class CreativeModeTabOutputBridge {
    private CreativeModeTabOutputBridge() {
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static CreativeModeTabOutput wrap(Object output) {
        ClassLoader classLoader = output.getClass().getClassLoader();
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
}
