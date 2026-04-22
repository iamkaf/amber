package com.iamkaf.amber.platform.services;

import com.iamkaf.amber.api.registry.v1.Registrar;
import net.minecraft.core.Registry;
//? if <=1.15.2 {
/*import net.minecraft.resources.ResourceLocation;*/
//?} else {
import net.minecraft.resources.ResourceKey;
//?}

/**
 * Service providing loader specific registrar implementations.
 */
public interface IRegistrarManager {
    /**
     * Creates a registrar for the given registry and mod id.
     */
    //? if <=1.15.2 {
    /*<T> Registrar<T> create(String modId, ResourceLocation key);*/
    //?} else {
    <T> Registrar<T> create(String modId, ResourceKey<Registry<T>> key);
    //?}
}
