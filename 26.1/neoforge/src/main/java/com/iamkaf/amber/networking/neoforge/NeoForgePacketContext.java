package com.iamkaf.amber.networking.neoforge;

import com.iamkaf.amber.api.networking.v1.PacketContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * NeoForge implementation of PacketContext for the networking API.
 */
public class NeoForgePacketContext implements PacketContext {
    
    private final boolean isClientSide;
    private final Player player;
    
    public NeoForgePacketContext(boolean isClientSide, Player player) {
        this.isClientSide = isClientSide;
        this.player = player;
    }
    
    @Override
    public boolean isClientSide() {
        return isClientSide;
    }
    
    @Override
    public Player getPlayer() {
        return player;
    }
    
    @Override
    public void execute(Runnable task) {
        // In NeoForge, we need to ensure execution on the correct thread
        if (isClientSide) {
            // Client-side execution
            if (net.neoforged.fml.loading.FMLEnvironment.getDist().isClient()) {
                net.minecraft.client.Minecraft.getInstance().execute(task);
            } else {
                // Fallback
                task.run();
            }
        } else {
            // Server-side execution
            if (player instanceof ServerPlayer serverPlayer && serverPlayer.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                serverLevel.getServer().execute(task);
            } else {
                // Fallback to immediate execution
                task.run();
            }
        }
    }
}