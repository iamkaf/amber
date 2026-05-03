package com.iamkaf.amber.platform;

import com.iamkaf.amber.platform.services.IAmberEventSetup;

public class NeoForgeAmberEventSetup implements IAmberEventSetup {
    @Override
    public void registerCommon() {
        NeoForgeAmberEventHandlers.registerModifyLootEvents();
        NeoForgeAmberEventHandlers.registerEntityInteractEvents();
        NeoForgeAmberEventHandlers.registerCommandEvents();
        NeoForgeAmberEventHandlers.registerEntitySpawnEvents();
        NeoForgeAmberEventHandlers.registerEntityDeathEvents();
        NeoForgeAmberEventHandlers.registerEntityDamageEvents();
        NeoForgeAmberEventHandlers.registerWorldLifecycleEvents();
        NeoForgeAmberEventHandlers.registerLightningStrikeEvents();
        NeoForgeAmberEventHandlers.registerBlockEvents();
        NeoForgeAmberEventHandlers.registerAnimalEvents();
        NeoForgeAmberEventHandlers.registerShieldBlockEvents();
        NeoForgeAmberEventHandlers.registerCreativeTabEvents();
        NeoForgeAmberEventHandlers.registerDefaultItemComponentEvents();
    }

    @Override
    public void registerClient() {
        NeoForgeAmberEventHandlers.registerClientCommandEvents();
        NeoForgeAmberEventHandlers.registerRenderGuiEvents();
        NeoForgeAmberEventHandlers.registerClientTickEvents();
        NeoForgeAmberEventHandlers.registerKeybindEvents();
    }

    // FIXME: registerServer() called from common init due to EnvExecutor inconsistency
    // TODO: Move all server events to registerCommon() and sunset registerServer() methods
    @Override
    public void registerServer() {
        NeoForgeAmberEventHandlers.registerServerTickEvents();
        NeoForgeAmberEventHandlers.registerPlayerLifecycleEvents();
        NeoForgeAmberEventHandlers.registerItemEvents();
    }
}
