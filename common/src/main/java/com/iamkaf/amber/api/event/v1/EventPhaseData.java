package com.iamkaf.amber.api.event.v1;


import java.lang.reflect.Array;
import java.util.Arrays;

import com.iamkaf.amber.event.toposort.SortableNode;
//? if <1.21.11 {
/*import net.minecraft.resources.ResourceLocation;*/
//?} else {
import net.minecraft.resources.Identifier;
//?}

/**
 * Data of an {@link ArrayBackedEvent} phase.
 */
class EventPhaseData<T> extends SortableNode<EventPhaseData<T>> {
    //? if <1.21.11 {
    /*final ResourceLocation id;*/
    //?} else {
    final Identifier id;
    //?}
    T[] listeners;

    @SuppressWarnings("unchecked")
    //? if <1.21.11 {
    /*EventPhaseData(ResourceLocation id, Class<?> listenerClass) {*/
    //?} else {
    EventPhaseData(Identifier id, Class<?> listenerClass) {
    //?}
        this.id = id;
        this.listeners = (T[]) Array.newInstance(listenerClass, 0);
    }

    void addListener(T listener) {
        int oldLength = listeners.length;
        listeners = Arrays.copyOf(listeners, oldLength + 1);
        listeners[oldLength] = listener;
    }

    @Override
    protected String getDescription() {
        return id.toString();
    }
}
