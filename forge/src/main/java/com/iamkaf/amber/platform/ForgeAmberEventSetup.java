package com.iamkaf.amber.platform;

import com.iamkaf.amber.platform.services.IAmberEventSetup;

public class ForgeAmberEventSetup implements IAmberEventSetup {
    @Override
    public void registerCommon() {
        ForgeAmberEventHandlers.registerModifyLootEvents();
        ForgeAmberEventHandlers.registerEntityInteractEvents();
        ForgeAmberEventHandlers.registerCommandEvents();
        ForgeAmberEventHandlers.registerEntitySpawnEvents();
        ForgeAmberEventHandlers.registerEntityDeathEvents();
        ForgeAmberEventHandlers.registerEntityDamageEvents();
        ForgeAmberEventHandlers.registerWorldLifecycleEvents();
        ForgeAmberEventHandlers.registerLightningStrikeEvents();
        ForgeAmberEventHandlers.registerBlockEvents();
        ForgeAmberEventHandlers.registerAnimalEvents();
        ForgeAmberEventHandlers.registerFishingEvents();
        ForgeAmberEventHandlers.registerShieldBlockEvents();
        ForgeAmberEventHandlers.registerCreativeTabEvents();
        ForgeAmberEventHandlers.registerDefaultItemComponentEvents();
    }

    @Override
    public void registerClient() {
        ForgeAmberEventHandlers.registerClientCommandEvents();
        ForgeAmberEventHandlers.registerKeybindEvents();
        ForgeAmberEventHandlers.registerClientTickEvents();
        ForgeAmberEventHandlers.registerRenderGuiEvents();
        ForgeAmberEventHandlers.registerMouseScrollEvents();
        ForgeAmberEventHandlers.registerBlockOutlineRenderEvents();
    }

    // FIXME: registerServer() called from common init due to EnvExecutor inconsistency
    // TODO: Move all server events to registerCommon() and sunset registerServer() methods
    @Override
    public void registerServer() {
        ForgeAmberEventHandlers.registerServerTickEvents();
        ForgeAmberEventHandlers.registerPlayerLifecycleEvents();
        ForgeAmberEventHandlers.registerItemEvents();
        ForgeAmberEventHandlers.registerCraftItemEvents();
    }
}
