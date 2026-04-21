package com.iamkaf.amber;

import com.iamkaf.amber.api.core.v2.AmberInitializer;
import com.iamkaf.amber.api.core.v2.AmberModInfo;
import com.iamkaf.amber.api.keymapping.KeybindHelper;
import com.iamkaf.amber.api.platform.v1.ModInfo;
import com.iamkaf.amber.api.platform.v1.Platform;
import com.iamkaf.amber.util.Env;
import com.iamkaf.amber.util.EnvExecutor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

/**
 * Fabric entry point.
 */
public class AmberFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        AmberInitializer.initialize(Constants.MOD_ID);
        AmberMod.init();
    }
}
