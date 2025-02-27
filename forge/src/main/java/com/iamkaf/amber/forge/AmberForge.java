package com.iamkaf.amber.forge;

import com.iamkaf.amber.Amber;
import net.minecraftforge.fml.common.Mod;

@Mod(Amber.MOD_ID)
public final class AmberForge {
    public AmberForge() {
        Amber.init();
    }
}
