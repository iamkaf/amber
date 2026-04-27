package com.iamkaf.amber.api.keymapping;

import net.minecraft.client.KeyMapping;

import java.util.ArrayList;

/**
 * @deprecated Use {@link com.iamkaf.amber.api.registry.v1.KeybindHelper} instead.
 * This class will be removed in Amber 10.0
 */
@Deprecated
public class KeybindHelper {

    /**
     * @deprecated Use {@link com.iamkaf.amber.api.registry.v1.KeybindHelper#register(KeyMapping)} instead.
     * Will be removed in Amber 10.0
     */
    @Deprecated
    public static KeyMapping register(KeyMapping keybind) {
        return com.iamkaf.amber.api.registry.v1.KeybindHelper.register(keybind);
    }

    /**
     * @deprecated Use {@link com.iamkaf.amber.api.registry.v1.KeybindHelper#getKeybindings()} instead.
     * Will be removed in Amber 10.0
     */
    @Deprecated
    public static ArrayList<KeyMapping> getKeybindings() {
        return com.iamkaf.amber.api.registry.v1.KeybindHelper.getKeybindings();
    }

    /**
     * @deprecated This field is moved to {@link com.iamkaf.amber.api.registry.v1.KeybindHelper#forgeEventAlreadyFired}.
     * Will be removed in Amber 10.0
     */
    @Deprecated
    public static boolean forgeEventAlreadyFired = com.iamkaf.amber.api.registry.v1.KeybindHelper.forgeEventAlreadyFired;
}