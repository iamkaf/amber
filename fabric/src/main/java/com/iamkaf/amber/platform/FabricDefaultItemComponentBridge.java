package com.iamkaf.amber.platform;

import com.iamkaf.amber.api.event.v1.events.common.ItemEvents;
//? if >=1.20.5
import com.iamkaf.amber.mixin.ItemAccessor;
//? if >=1.20.5
import net.minecraft.core.component.DataComponentMap;
//? if >=1.20.5
import net.minecraft.core.registries.BuiltInRegistries;

public final class FabricDefaultItemComponentBridge {
    private FabricDefaultItemComponentBridge() {
    }

    public static void modifyItemComponents() {
        //? if >=1.20.5 && <1.20.6 {
        ItemEvents.MODIFY_DEFAULT_COMPONENTS.invoker().modify((item, builderConsumer) -> {
            for (var registryItem : BuiltInRegistries.ITEM) {
                if (registryItem == item) {
                    DataComponentMap.Builder builder = DataComponentMap.builder().addAll(registryItem.components());
                    builderConsumer.accept(builder);
                    ((ItemAccessor) registryItem).amber$setComponents(builder.build());
                }
            }
        });
        //?}
    }
}
