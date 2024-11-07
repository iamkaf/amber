package com.iamkaf.amber.api.util;

import java.util.HashSet;

public class LiteralSetHolder<T> {
    private final HashSet<T> theSet = new HashSet<>();

    public T add(T theThing) {
        theSet.add(theThing);
        return theThing;
    }

    public HashSet<T> get() {
        return theSet;
    }
}
