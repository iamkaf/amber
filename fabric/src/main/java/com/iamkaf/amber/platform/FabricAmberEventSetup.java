package com.iamkaf.amber.platform;

import com.iamkaf.amber.platform.services.IAmberEventSetup;

public class FabricAmberEventSetup implements IAmberEventSetup {
    @Override
    public void registerCommon() {
        FabricAmberEventHandlers.registerModifyLootEvents();
        FabricAmberEventHandlers.registerEntityInteractEvents();
        FabricAmberEventHandlers.registerCommandEvents();
        FabricAmberEventHandlers.registerEntityDamageEvents();
        FabricAmberEventHandlers.registerBlockBreakEvents();
        FabricAmberEventHandlers.registerBlockInteractionEvents();
        FabricAmberEventHandlers.registerDefaultItemComponentEvents();
        FabricAmberEventHandlers.registerCreativeTabEvents();
    }

    @Override
    public void registerClient() {
        FabricAmberEventHandlers.registerClientCommandEvents();
        FabricAmberEventHandlers.registerRenderHudEvents();
        FabricAmberEventHandlers.registerStartClientTickEvents();
        FabricAmberEventHandlers.registerEndClientTickEvents();
        //? if >=1.21.11
        FabricClientConnectionEvents.register();
    }

    @Override
    public void registerServer() {
        FabricAmberEventHandlers.registerStartServerTickEvents();
        FabricAmberEventHandlers.registerEndServerTickEvents();
        FabricAmberEventHandlers.registerWorldLifecycleEvents();
        FabricAmberEventHandlers.registerPlayerLifecycleEvents();
    }
}
