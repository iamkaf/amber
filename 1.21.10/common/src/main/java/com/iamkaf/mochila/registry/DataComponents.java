package com.iamkaf.mochila.registry;

import com.iamkaf.amber.api.component.SimpleIntegerDataComponent;
import com.iamkaf.amber.api.registry.v1.DeferredRegister;
import com.iamkaf.amber.api.registry.v1.RegistrySupplier;
import com.iamkaf.mochila.Constants;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;

public class DataComponents {
    private static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
            DeferredRegister.create(Constants.MOD_ID, Registries.DATA_COMPONENT_TYPE);

    public static final RegistrySupplier<DataComponentType<SimpleIntegerDataComponent>> QUICKSTASH_MODE =
            DATA_COMPONENTS.register(
                    "quickstash_mode",
                    () -> DataComponentType.<SimpleIntegerDataComponent>builder()
                            .persistent(SimpleIntegerDataComponent.CODEC)
                            .build()
            );

    public static void init() {
        DATA_COMPONENTS.register();
    }
}
