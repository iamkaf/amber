package com.iamkaf.amber.api.core.v2;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.api.platform.v1.ModInfo;
import com.iamkaf.amber.api.platform.v1.Platform;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class AmberInitializer {
    private static final Map<String, Object> EVENT_BUSES = new HashMap<>();
    
    public static AmberModInfo initialize(String id) {
        // Get name and version from platform
        ModInfo platformInfo = Platform.getModInfo(id);
        String name = platformInfo != null ? platformInfo.name() : id;
        String version = platformInfo != null ? platformInfo.version() : "unknown";
        
        AmberModInfo modInfo = new AmberModInfo(id, name, version);
        AmberMod.AMBER_MODS.add(modInfo);
        
        return modInfo;
    }
    
    // Internal method for Amber to store event buses
    public static void setEventBus(String modId, Object eventBus) {
        EVENT_BUSES.put(modId, eventBus);
    }
    
    // Internal method for Amber to retrieve event buses
    public static @Nullable Object getEventBus(String modId) {
        return EVENT_BUSES.get(modId);
    }
}
