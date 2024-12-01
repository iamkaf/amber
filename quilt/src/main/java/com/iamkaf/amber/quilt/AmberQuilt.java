package com.iamkaf.amber.quilt;

import com.iamkaf.amber.Amber;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

public final class AmberQuilt implements ModInitializer {
    @Override
    public void onInitialize(ModContainer mod) {
        Amber.init();
    }
}
