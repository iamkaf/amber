package com.iamkaf.amber.networking.fabric;

import com.iamkaf.amber.api.networking.v1.PacketContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * Fabric implementation of PacketContext for the networking API.
 */
public class FabricPacketContext implements PacketContext {
    
    private final boolean isClientSide;
    private final Player player;
    
    public FabricPacketContext(boolean isClientSide, Player player) {
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
        // In Fabric, we need to ensure execution on the correct thread
        if (isClientSide) {
            // Client-side execution
            executeOnClient(task);
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
    
    @Environment(EnvType.CLIENT)
    private void executeOnClient(Runnable task) {
        net.minecraft.client.Minecraft.getInstance().execute(task);
    }
}