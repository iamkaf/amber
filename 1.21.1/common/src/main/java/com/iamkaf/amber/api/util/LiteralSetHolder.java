package com.iamkaf.amber.api.util;

import java.util.HashSet;

public class LiteralSetHolder<T> {
    private final HashSet<T> set = new HashSet<>();

    public T add(T item) {
        set.add(item);
        return item;
    }

    @Deprecated(forRemoval = true)
    public HashSet<T> get() {
        return getSet();
    }

    public HashSet<T> getSet() {
        return set;
    }

    public void clear() {
        set.clear();
    }
}
