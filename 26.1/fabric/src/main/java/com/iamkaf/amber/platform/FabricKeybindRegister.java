package com.iamkaf.amber.platform;

import com.iamkaf.amber.Constants;
import com.iamkaf.amber.platform.services.IKeybindRegister;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;

public class FabricKeybindRegister implements IKeybindRegister {
    @Override
    public void register(KeyMapping keybind) {
        try {
            KeyMappingHelper.registerKeyMapping(keybind);
        } catch (IllegalArgumentException e) {
            Constants.LOG.error("Failed to register keybind: {}", keybind.getName(), e);
        }
    }
}
