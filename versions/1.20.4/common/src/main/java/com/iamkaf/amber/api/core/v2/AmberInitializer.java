package com.iamkaf.amber.api.core.v2;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.platform.v1.Platform;
import org.jetbrains.annotations.Nullable;

public class AmberInitializer {
    public static AmberModInfo initialize(String id, String name, String version, AmberModInfo.AmberModSide side,
            @Nullable Object eventBus) {
        if (eventBus == null && (Platform.getPlatformName().equals("Forge") || Platform.getPlatformName()
                .equals("NeoForge"))) {
            throw new IllegalArgumentException("Event bus cannot be null for Forge or NeoForge platforms.");
        }
        AmberModInfo modInfo = new AmberModInfo(id, name, version, side, eventBus);
        AmberMod.AMBER_MODS.add(modInfo);
        return modInfo;
    }
}
