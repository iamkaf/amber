package com.iamkaf.amber.quilt;

import com.iamkaf.amber.Amber;
import net.fabricmc.api.ModInitializer;

@SuppressWarnings("deprecation") // good one, quilt
public final class AmberQuilt implements ModInitializer {
    @Override
    public void onInitialize() {
        Amber.init();
    }
}
