package com.iamkaf.amber;

import com.iamkaf.amber.api.core.v2.AmberInitializer;
import com.iamkaf.amber.api.core.v2.AmberModInfo;
import com.iamkaf.amber.api.platform.v1.ModInfo;
import com.iamkaf.amber.api.platform.v1.Platform;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class AmberNeoForge {
    public AmberNeoForge(IEventBus eventBus) {
        ModInfo info = Platform.getModInfo(Constants.MOD_ID);
        assert info != null;
        AmberInitializer.initialize(info.id(), info.name(), info.version(), AmberModInfo.AmberModSide.COMMON, eventBus);
        AmberMod.init();
    }
}