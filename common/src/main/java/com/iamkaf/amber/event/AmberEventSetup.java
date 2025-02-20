package com.iamkaf.amber.event;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class AmberEventSetup {
    @ExpectPlatform
    public static void registerCommon() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerClient() {
        throw new AssertionError();
    }
}
