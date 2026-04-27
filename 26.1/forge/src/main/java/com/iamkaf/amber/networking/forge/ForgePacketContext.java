package com.iamkaf.amber.networking.forge;

import com.iamkaf.amber.api.networking.v1.PacketContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * Forge implementation of PacketContext for the networking API.
 */
public class ForgePacketContext implements PacketContext {
    
    private final boolean isClientSide;
    private final Player player;
    
    public ForgePacketContext(boolean isClientSide, Player player) {
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
        // In Forge, we need to ensure execution on the correct thread
        if (isClientSide) {
            // Client-side execution
            if (net.minecraftforge.fml.loading.FMLLoader.getDist().isClient()) {
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