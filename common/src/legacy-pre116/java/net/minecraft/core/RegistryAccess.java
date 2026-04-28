package net.minecraft.core;

public final class RegistryAccess {
    private static final RegistryAccess BUILTIN = new RegistryAccess();

    private RegistryAccess() {
    }

    public static RegistryAccess builtin() {
        return BUILTIN;
    }
}
