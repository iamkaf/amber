package com.iamkaf.amber.networking.neoforge;

import com.iamkaf.amber.api.networking.v1.PacketContext;
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
        if (isClientSide) {
            executeClient(task);
        } else {
            executeServer(task);
        }
    }

    //? if <=1.21.8 {
    /*private void executeClient(Runnable task) {
        if (net.neoforged.fml.loading.FMLEnvironment.dist.isClient()) {
            net.minecraft.client.Minecraft.getInstance().execute(task);
        } else {
            task.run();
        }
    }

    private void executeServer(Runnable task) {
        if (player != null && player.getServer() != null) {
            player.getServer().execute(task);
        } else {
            task.run();
        }
    }
    *///?}

    //? if >1.21.8 {
    private void executeClient(Runnable task) {
        if (net.neoforged.fml.loading.FMLEnvironment.getDist().isClient()) {
            net.minecraft.client.Minecraft.getInstance().execute(task);
        } else {
            task.run();
        }
    }

    private void executeServer(Runnable task) {
        if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer
                && serverPlayer.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            serverLevel.getServer().execute(task);
        } else {
            task.run();
        }
    }
    //?}
}
