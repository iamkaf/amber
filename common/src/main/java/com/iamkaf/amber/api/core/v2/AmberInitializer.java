package com.iamkaf.amber.api.core.v2;

import com.iamkaf.amber.AmberMod;

public class AmberInitializer {
    public static AmberModInfo initialize(String id, String name, String version, AmberModInfo.AmberModSide side) {
        AmberModInfo modInfo = new AmberModInfo(id, name, version, side);
        AmberMod.AMBER_MODS.add(modInfo);
        return modInfo;
    }
}
