package com.iamkaf.mochila;

import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {
    /**
     * Identifier of the mod. Update these fields when reusing the Mochila.
     */
    public static final String MOD_ID = "mochila";
    public static final String MOD_NAME = "Mochila";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    public static Identifier resource(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
