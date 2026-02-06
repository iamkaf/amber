package com.iamkaf.amber;

import com.iamkaf.amber.api.core.v2.AmberInitializer;
import com.iamkaf.amber.api.core.v2.AmberModInfo;
import com.iamkaf.amber.api.platform.v1.ModInfo;
import com.iamkaf.amber.api.platform.v1.Platform;
import com.iamkaf.amber.platform.Services;
import com.iamkaf.amber.platform.services.INetworkingService;
import com.iamkaf.amber.networking.neoforge.NeoForgeNetworkChannelImpl;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@Mod(Constants.MOD_ID)
public class AmberNeoForge {
    public AmberNeoForge(IEventBus eventBus) {
        AmberInitializer.initialize(Constants.MOD_ID);
        // Store the event bus internally for Amber's use
        AmberInitializer.setEventBus(Constants.MOD_ID, eventBus);
        
        // Register event listener for payload registration
        eventBus.addListener(this::onRegisterPayloadHandlers);
        
        // Initialize Amber (networking will be deferred until RegisterPayloadHandlersEvent)
        AmberMod.init();
    }
    
    @SubscribeEvent
    public void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        // Set the payload registrar for all NeoForge network channels
        INetworkingService networkingService = Services.NETWORKING;
        if (networkingService instanceof com.iamkaf.amber.platform.NeoForgeNetworkingService neoForgeService) {
            // Get all created channels and set their registrar
            neoForgeService.setPayloadRegistrar(event.registrar(Constants.MOD_ID));
        }
    }
}