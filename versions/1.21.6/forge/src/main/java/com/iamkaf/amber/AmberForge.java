package com.iamkaf.amber;

import com.iamkaf.amber.api.core.v2.AmberInitializer;
import com.iamkaf.amber.api.core.v2.AmberModInfo;
import com.iamkaf.amber.api.platform.v1.ModInfo;
import com.iamkaf.amber.api.platform.v1.Platform;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Constants.MOD_ID)
public class AmberForge {

    public AmberForge(FMLJavaModLoadingContext ctx) {
        ModInfo info = Platform.getModInfo(Constants.MOD_ID);
        assert info != null;
        AmberInitializer.initialize(
                info.id(),
                info.name(),
                info.version(),
                AmberModInfo.AmberModSide.COMMON,
                ctx.getModBusGroup()
        );
        AmberMod.init();
    }
}
