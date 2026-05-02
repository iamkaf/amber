package com.iamkaf.amber.platform;

import com.iamkaf.amber.platform.services.IKeybindRegister;
import net.minecraft.client.KeyMapping;
//? if >=1.18 && <1.19
import net.minecraftforge.client.ClientRegistry;
//? if <1.18
/*import net.minecraftforge.fmlclient.registry.ClientRegistry;*/

public class ForgeKeybindRegister implements IKeybindRegister {
    @Override
    public void register(KeyMapping keybind) {
        //? if <1.19
        /*ClientRegistry.registerKeyBinding(keybind);*/
    }
}
