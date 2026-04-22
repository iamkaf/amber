package com.iamkaf.amber.api.sound;

//? if >1.18.2 {
import net.minecraft.core.Holder;
//?}
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

/**
 * @deprecated This helper will be replaced by a versioned alternative in a future release.
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
        if (!(player instanceof ServerPlayer)) {
            return;
        }

        sendPacket((ServerPlayer) player, player, sound, source, volume, pitch);
    }

    //? if <=1.14.4 {
    /*private static void sendPacket(ServerPlayer serverPlayer, Player player, SoundEvent sound, SoundSource source, float volume, float pitch) {
        serverPlayer.connection.send(new ClientboundSoundPacket(
                sound,
                source,
                player.x,
                player.y,
                player.z,
                volume,
                pitch
        ));
    }
    *///?}

    //? if >1.14.4 && <=1.18.2 {
    /*private static void sendPacket(ServerPlayer serverPlayer, Player player, SoundEvent sound, SoundSource source, float volume, float pitch) {
        serverPlayer.connection.send(new ClientboundSoundPacket(
                sound,
                source,
                player.getX(),
                player.getY(),
                player.getZ(),
                volume,
                pitch
        ));
    }
    *///?}

    //? if >1.18.2 && <=1.19.4 {
    /*private static void sendPacket(ServerPlayer serverPlayer, Player player, SoundEvent sound, SoundSource source, float volume, float pitch) {
        serverPlayer.connection.send(new ClientboundSoundPacket(
                Holder.direct(sound),
                source,
                player.getX(),
                player.getY(),
                player.getZ(),
                volume,
                pitch,
                player.getLevel().getRandom().nextLong()
        ));
    }
    *///?}

    //? if >1.19.4 {
    private static void sendPacket(ServerPlayer serverPlayer, Player player, SoundEvent sound, SoundSource source, float volume, float pitch) {
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
    //?}
}
