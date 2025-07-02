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
        ModInfo info = Platform.getModInfo(Constants.MOD_ID);
        assert info != null;
        AmberInitializer.initialize(info.id(), info.name(), info.version(), AmberModInfo.AmberModSide.COMMON, null);
        AmberMod.init();

        registerKeybinds();
    }

    private static void registerKeybinds() {
        EnvExecutor.runInEnv(
                Env.CLIENT, () -> () -> {
                    Constants.LOG.info("Registering Amber keybindings for Fabric...");
                    KeybindHelper.getKeybindings().forEach(key -> {
                        try {
                            KeyBindingHelper.registerKeyBinding(key);
                        } catch (IllegalArgumentException e) {
                            Constants.LOG.error("Failed to register keybind: {}", key.getName(), e);
                        }
                    });
                }
        );
    }
}
