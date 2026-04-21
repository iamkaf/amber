package com.iamkaf.amber.platform;

import com.iamkaf.amber.Constants;
import com.iamkaf.amber.platform.services.IKeybindRegister;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;

public class FabricKeybindRegister implements IKeybindRegister {
    @Override
    public void register(KeyMapping keybind) {
        try {
            KeyBindingHelper.registerKeyBinding(keybind);
        } catch (IllegalArgumentException e) {
            Constants.LOG.error("Failed to register keybind: {}", keybind.getName(), e);
        }
    }
}
