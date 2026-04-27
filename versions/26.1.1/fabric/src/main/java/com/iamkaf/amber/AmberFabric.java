package com.iamkaf.amber;

import com.iamkaf.amber.api.core.v2.AmberInitializer;
import com.iamkaf.amber.api.core.v2.AmberModInfo;
import com.iamkaf.amber.api.registry.v1.KeybindHelper;
import com.iamkaf.amber.api.platform.v1.ModInfo;
import com.iamkaf.amber.api.platform.v1.Platform;
import com.iamkaf.amber.util.Env;
import com.iamkaf.amber.util.EnvExecutor;
import net.fabricmc.api.ModInitializer;

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
