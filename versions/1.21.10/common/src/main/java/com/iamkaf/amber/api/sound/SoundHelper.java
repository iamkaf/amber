package com.iamkaf.amber.api.sound;

import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

/**
 * @deprecated Use {@link com.iamkaf.amber.api.functions.v1.WorldFunctions#playSoundAt} for position-based sounds or {@link com.iamkaf.amber.api.functions.v1.PlayerFunctions#playSound} for player-directed sounds.
 * This class will be removed in Amber 10.0
 */
@Deprecated
public class SoundHelper {
    public static void sendClientSound(Player player, SoundEvent sound) {
        sendClientSound(player, sound, SoundSource.PLAYERS, 1f, 1f);
    }

    public static void sendClientSound(Player player, SoundEvent sound, SoundSource source) {
        sendClientSound(player, sound, source, 1f, 1f);
    }

    public static void sendClientSound(Player player, SoundEvent sound, SoundSource source, float volume) {
        sendClientSound(player, sound, source, volume, 1f);
    }

    public static void sendClientSound(Player player, SoundEvent sound, SoundSource source, float volume, float pitch) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundSoundPacket(
                    Holder.direct(sound),
                    source,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    volume,
                    pitch,
                    player.level().getRandom().nextLong()
            ));
        }
    }
}
