package com.iamkaf.amber.neoforge;

import com.iamkaf.amber.Amber;
import net.neoforged.fml.common.Mod;

@Mod(Amber.MOD_ID)
public final class AmberNeoForge {
    public AmberNeoForge() {
        // Run our common setup.
        Amber.init();
    }
}
