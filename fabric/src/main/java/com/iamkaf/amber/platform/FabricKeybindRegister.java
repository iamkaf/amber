package com.iamkaf.amber.platform;

import com.iamkaf.amber.Constants;
import com.iamkaf.amber.platform.services.IKeybindRegister;
//? if >=26.1
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
//? if <26.1
/*import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;*/
import net.minecraft.client.KeyMapping;

public class FabricKeybindRegister implements IKeybindRegister {
    @Override
    public void register(KeyMapping keybind) {
        try {
            //? if >=26.1
            KeyMappingHelper.registerKeyMapping(keybind);
            //? if <26.1
            /*KeyBindingHelper.registerKeyBinding(keybind);*/
        } catch (IllegalArgumentException e) {
            Constants.LOG.error("Failed to register keybind: {}", keybind.getName(), e);
        }
    }
}
