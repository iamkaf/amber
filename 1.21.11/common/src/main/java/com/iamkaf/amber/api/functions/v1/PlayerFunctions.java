package com.iamkaf.amber.api.functions.v1;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Consolidated utility class for player-specific operations and mechanics.
 * This class contains functionality that is unique to players, not generic entities.
 *
 * @since 8.3.0
 */
public final class PlayerFunctions {

    private PlayerFunctions() {
        // Utility class - prevent instantiation
    }

    // ==================== EXPERIENCE & PROGRESSION UTILITIES ====================

    /**
     * Adds experience points to the player.
     *
     * @param player The player to give experience to.
     * @param amount The amount of experience points to add.
     */
    public static void addExperience(Player player, int amount) {
        player.giveExperiencePoints(amount);
    }

    /**
     * Adds experience levels to the player.
     *
     * @param player The player to give levels to.
     * @param levels The number of experience levels to add.
     */
    public static void addLevels(Player player, int levels) {
        player.giveExperienceLevels(levels);
    }

    /**
     * Sets the player's experience level directly.
     *
     * @param player The player to set the level for.
     * @param level The experience level to set.
     */
    public static void setExperienceLevel(Player player, int level) {
        player.experienceLevel = level;
    }

    /**
     * Gets the player's total experience points.
     *
     * @param player The player to get experience from.
     * @return The total experience points the player has.
     */
    public static int getExperiencePoints(Player player) {
        return player.totalExperience;
    }

    /**
     * Gets the player's current experience level.
     *
     * @param player The player to get the level from.
     * @return The current experience level.
     */
    public static int getExperienceLevel(Player player) {
        return player.experienceLevel;
    }

    /**
     * Gets the player's experience progress towards the next level.
     *
     * @param player The player to get progress from.
     * @return The experience progress (0.0 to 1.0).
     */
    public static float getExperienceProgress(Player player) {
        return player.experienceProgress;
    }

    /**
     * Checks if the player has enough experience points.
     *
     * @param player The player to check.
     * @param amount The amount of experience required.
     * @return true if the player has enough experience, false otherwise.
     */
    public static boolean hasEnoughExperience(Player player, int amount) {
        return player.totalExperience >= amount;
    }

    // ==================== ABILITIES & GAME MODE UTILITIES ====================

    /**
     * Gets the player's abilities object.
     *
     * @param player The player to get abilities from.
     * @return The player's abilities.
     */
    public static Abilities getAbilities(Player player) {
        return player.getAbilities();
    }

    /**
     * Sets whether the player is currently flying.
     *
     * @param player The player to set flying state for.
     * @param flying true if the player should be flying, false otherwise.
     */
    public static void setFlying(Player player, boolean flying) {
        Abilities abilities = player.getAbilities();
        abilities.flying = flying;
        player.onUpdateAbilities();
    }

    /**
     * Checks if the player is currently flying.
     *
     * @param player The player to check.
     * @return true if the player is flying, false otherwise.
     */
    public static boolean isFlying(Player player) {
        return player.getAbilities().flying;
    }

    /**
     * Sets whether the player is allowed to fly.
     *
     * @param player The player to set flight permission for.
     * @param allowFlight true if the player should be allowed to fly, false otherwise.
     */
    public static void setAllowFlight(Player player, boolean allowFlight) {
        Abilities abilities = player.getAbilities();
        abilities.mayfly = allowFlight;
        player.onUpdateAbilities();
    }

    /**
     * Checks if the player can fly.
     *
     * @param player The player to check.
     * @return true if the player can fly, false otherwise.
     */
    public static boolean canFly(Player player) {
        return player.getAbilities().mayfly;
    }

    /**
     * Sets whether the player is invulnerable.
     *
     * @param player The player to set invulnerability for.
     * @param invulnerable true if the player should be invulnerable, false otherwise.
     */
    public static void setInvulnerable(Player player, boolean invulnerable) {
        Abilities abilities = player.getAbilities();
        abilities.invulnerable = invulnerable;
        player.onUpdateAbilities();
    }

    /**
     * Checks if the player is invulnerable.
     *
     * @param player The player to check.
     * @return true if the player is invulnerable, false otherwise.
     */
    public static boolean isInvulnerable(Player player) {
        return player.getAbilities().invulnerable;
    }

    /**
     * Sets whether the player has creative mode instant building.
     *
     * @param player The player to set insta-build for.
     * @param instaBuild true if the player should have insta-build, false otherwise.
     */
    public static void setInstaBuild(Player player, boolean instaBuild) {
        Abilities abilities = player.getAbilities();
        abilities.instabuild = instaBuild;
        player.onUpdateAbilities();
    }

    /**
     * Checks if the player has creative mode instant building.
     *
     * @param player The player to check.
     * @return true if the player has insta-build, false otherwise.
     */
    public static boolean hasInstaBuild(Player player) {
        return player.getAbilities().instabuild;
    }

    /**
     * Sets whether the player may build/modify the world.
     *
     * @param player The player to set build permission for.
     * @param mayBuild true if the player may build, false otherwise.
     */
    public static void setMayBuild(Player player, boolean mayBuild) {
        Abilities abilities = player.getAbilities();
        abilities.mayBuild = mayBuild;
        player.onUpdateAbilities();
    }

    /**
     * Checks if the player may build/modify the world.
     *
     * @param player The player to check.
     * @return true if the player may build, false otherwise.
     */
    public static boolean mayBuild(Player player) {
        return player.getAbilities().mayBuild;
    }

    /**
     * Gets the player's current game mode.
     *
     * @param player The player to get game mode from.
     * @return The player's current game mode.
     */
    public static GameType getGameMode(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            return serverPlayer.gameMode.getGameModeForPlayer();
        }
        return GameType.SURVIVAL; // Default fallback
    }

    /**
     * Checks if the player is in creative mode.
     *
     * @param player The player to check.
     * @return true if the player is in creative mode, false otherwise.
     */
    public static boolean isCreativeMode(Player player) {
        return getGameMode(player) == GameType.CREATIVE;
    }

    /**
     * Checks if the player is in survival mode.
     *
     * @param player The player to check.
     * @return true if the player is in survival mode, false otherwise.
     */
    public static boolean isSurvivalMode(Player player) {
        return getGameMode(player) == GameType.SURVIVAL;
    }

    /**
     * Checks if the player is in adventure mode.
     *
     * @param player The player to check.
     * @return true if the player is in adventure mode, false otherwise.
     */
    public static boolean isAdventureMode(Player player) {
        return getGameMode(player) == GameType.ADVENTURE;
    }

    /**
     * Checks if the player is in spectator mode.
     *
     * @param player The player to check.
     * @return true if the player is in spectator mode, false otherwise.
     */
    public static boolean isSpectatorMode(Player player) {
        return getGameMode(player) == GameType.SPECTATOR;
    }

    // ==================== INVENTORY & SLOT UTILITIES ====================

    /**
     * Gets the item in the player's main hand.
     *
     * @param player The player to get the item from.
     * @return The item stack in the main hand.
     */
    public static ItemStack getMainHandItem(Player player) {
        return player.getMainHandItem();
    }

    /**
     * Gets the item in the player's off hand.
     *
     * @param player The player to get the item from.
     * @return The item stack in the off hand.
     */
    public static ItemStack getOffhandItem(Player player) {
        return player.getOffhandItem();
    }

    /**
     * Gets the item in the player's helmet slot.
     *
     * @param player The player to get the item from.
     * @return The item stack in the helmet slot.
     */
    public static ItemStack getHelmet(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD);
    }

    /**
     * Gets the item in the player's chestplate slot.
     *
     * @param player The player to get the item from.
     * @return The item stack in the chestplate slot.
     */
    public static ItemStack getChestplate(Player player) {
        return player.getItemBySlot(EquipmentSlot.CHEST);
    }

    /**
     * Gets the item in the player's leggings slot.
     *
     * @param player The player to get the item from.
     * @return The item stack in the leggings slot.
     */
    public static ItemStack getLeggings(Player player) {
        return player.getItemBySlot(EquipmentSlot.LEGS);
    }

    /**
     * Gets the item in the player's boots slot.
     *
     * @param player The player to get the item from.
     * @return The item stack in the boots slot.
     */
    public static ItemStack getBoots(Player player) {
        return player.getItemBySlot(EquipmentSlot.FEET);
    }

    /**
     * Sets the item in the player's main hand.
     *
     * @param player The player to set the item for.
     * @param stack The item stack to set.
     */
    public static void setMainHandItem(Player player, ItemStack stack) {
        player.setItemInHand(player.getUsedItemHand(), stack);
    }

    /**
     * Sets the item in the player's off hand.
     *
     * @param player The player to set the item for.
     * @param stack The item stack to set.
     */
    public static void setOffhandItem(Player player, ItemStack stack) {
        player.getInventory().setItem(Inventory.SLOT_OFFHAND, stack);
    }

    /**
     * Gets the player's selected hotbar slot.
     *
     * @param player The player to get the selected slot from.
     * @return The selected hotbar slot (0-8).
     */
    public static int getSelectedSlot(Player player) {
        return player.getInventory().getSelectedSlot();
    }

    /**
     * Sets the player's selected hotbar slot.
     *
     * @param player The player to set the selected slot for.
     * @param slot The hotbar slot to select (0-8).
     */
    public static void setSelectedSlot(Player player, int slot) {
        if (slot >= 0 && slot <= 8) {
            player.getInventory().setSelectedSlot(slot);
        }
    }

    /**
     * Gets the item in a specific hotbar slot.
     *
     * @param player The player to get the item from.
     * @param slot The hotbar slot (0-8).
     * @return The item stack in the specified hotbar slot.
     */
    public static ItemStack getHotbarItem(Player player, int slot) {
        if (slot >= 0 && slot <= 8) {
            return player.getInventory().getItem(slot);
        }
        return ItemStack.EMPTY;
    }

    /**
     * Sets the item in a specific hotbar slot.
     *
     * @param player The player to set the item for.
     * @param slot The hotbar slot (0-8).
     * @param stack The item stack to set.
     */
    public static void setHotbarItem(Player player, int slot, ItemStack stack) {
        if (slot >= 0 && slot <= 8) {
            player.getInventory().setItem(slot, stack);
        }
    }

    /**
     * Gets the player's attack strength (0.0 to 1.0).
     *
     * @param player The player to get attack strength from.
     * @return The current attack strength.
     */
    public static float getAttackStrength(Player player) {
        return player.getAttackStrengthScale(0.5f);
    }

    /**
     * Resets the player's attack strength cooldown.
     *
     * @param player The player to reset cooldown for.
     */
    public static void resetAttackStrength(Player player) {
        player.resetAttackStrengthTicker();
    }

    /**
     * Checks if the player has an attack cooldown.
     *
     * @param player The player to check.
     * @return true if the player has an attack cooldown, false otherwise.
     */
    public static boolean hasAttackCooldown(Player player) {
        return player.getAttackStrengthScale(0.5f) < 1.0f;
    }

    /**
     * Gets the player's attack cooldown progress (0.0 to 1.0).
     *
     * @param player The player to get cooldown progress from.
     * @return The attack cooldown progress.
     */
    public static float getAttackCooldownProgress(Player player) {
        return player.getAttackStrengthScale(0.5f);
    }

    // ==================== COMMUNICATION & FEEDBACK UTILITIES ====================

    /**
     * Sends a message to the player.
     *
     * @param player The player to send the message to.
     * @param message The message to send.
     */
    public static void sendMessage(Player player, Component message) {
        player.displayClientMessage(message, false);
    }

    /**
     * Sends an action bar message to the player.
     *
     * @param player The player to send the message to.
     * @param message The message to send.
     */
    public static void sendActionBarMessage(Player player, Component message) {
        player.displayClientMessage(message, true);
    }

    /**
     * Sends an action bar message to the player.
     *
     * @param player The player to send the message to.
     * @param message The action bar message to send.
     */
    public static void sendActionBar(Player player, Component message) {
        player.displayClientMessage(message, true);
    }

    /**
     * Sends a title to the player.
     *
     * @param player The player to send the title to.
     * @param title The title text (can be null for no title).
     * @param subtitle The subtitle text (can be null for no subtitle).
     * @param fadeIn The fade-in time in ticks.
     * @param stay The display time in ticks.
     * @param fadeOut The fade-out time in ticks.
     */
    public static void sendTitle(Player player, @Nullable Component title, @Nullable Component subtitle,
                                 int fadeIn, int stay, int fadeOut) {
        if (player instanceof ServerPlayer serverPlayer) {
            ClientboundSetTitlesAnimationPacket timesPacket = new ClientboundSetTitlesAnimationPacket(fadeIn, stay, fadeOut);
            serverPlayer.connection.send(timesPacket);

            if (title != null) {
                ClientboundSetTitleTextPacket titlePacket = new ClientboundSetTitleTextPacket(title);
                serverPlayer.connection.send(titlePacket);
            }

            if (subtitle != null) {
                ClientboundSetSubtitleTextPacket subtitlePacket = new ClientboundSetSubtitleTextPacket(subtitle);
                serverPlayer.connection.send(subtitlePacket);
            }
        }
    }

    /**
     * Sends a simple title to the player with default timing.
     *
     * @param player The player to send the title to.
     * @param title The title text.
     * @param subtitle The subtitle text.
     */
    public static void sendTitle(Player player, Component title, Component subtitle) {
        sendTitle(player, title, subtitle, 20, 60, 20);
    }

    /**
     * Clears the player's current title.
     *
     * @param player The player to clear the title for.
     */
    public static void clearTitle(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            // Send empty title packets to clear the title
            serverPlayer.connection.send(new ClientboundSetTitleTextPacket(Component.empty()));
            serverPlayer.connection.send(new ClientboundSetSubtitleTextPacket(Component.empty()));
            serverPlayer.connection.send(new ClientboundSetTitlesAnimationPacket(0, 0, 0));
        }
    }

    
    /**
     * Plays a sound for the player with default settings.
     *
     * @param player The player to play the sound for.
     * @param sound The sound event to play.
     */
    public static void playSound(Player player, SoundEvent sound) {
        playSound(player, sound, SoundSource.PLAYERS, 1.0f, 1.0f);
    }

    /**
     * Plays a sound for the player with specified source.
     *
     * @param player The player to play the sound for.
     * @param sound The sound event to play.
     * @param source The sound source category.
     */
    public static void playSound(Player player, SoundEvent sound, SoundSource source) {
        playSound(player, sound, source, 1.0f, 1.0f);
    }

    /**
     * Plays a sound for the player with specified volume.
     *
     * @param player The player to play the sound for.
     * @param sound The sound event to play.
     * @param source The sound source category.
     * @param volume The volume level (1.0 = normal volume).
     */
    public static void playSound(Player player, SoundEvent sound, SoundSource source, float volume) {
        playSound(player, sound, source, volume, 1.0f);
    }

    /**
     * Plays a sound for the player with full customization.
     *
     * @param player The player to play the sound for.
     * @param sound The sound event to play.
     * @param source The sound source category.
     * @param volume The volume level (1.0 = normal volume).
     * @param pitch The pitch multiplier (1.0 = normal pitch).
     */
    public static void playSound(Player player, SoundEvent sound, SoundSource source, float volume, float pitch) {
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

    // ==================== WORLD INTERACTION UTILITIES ====================

    /**
     * Gets the player's last death location.
     *
     * @param player The player to get the death location for.
     * @return The player's last death location, or empty if none set.
     */
    public static Optional<GlobalPos> getLastDeathLocation(Player player) {
        return player.getLastDeathLocation();
    }

    /**
     * Sets the player's last death location.
     *
     * @param player The player to set the death location for.
     * @param position The death location to set.
     */
    public static void setLastDeathLocation(Player player, GlobalPos position) {
        player.setLastDeathLocation(Optional.of(position));
    }

    /**
     * Triggers player respawn logic.
     * <p>
     * <strong>CLIENT ONLY:</strong> This function only works on client-side LocalPlayer instances.
     * </p>
     *
     * @param player The player to respawn. Must be a LocalPlayer instance.
     */
    public static void respawn(Player player) {
        if (player instanceof LocalPlayer) {
            ((LocalPlayer) player).respawn();
        }
    }

    /**
     * Checks if the player is currently sleeping.
     *
     * @param player The player to check.
     * @return true if the player is sleeping, false otherwise.
     */
    public static boolean isSleeping(Player player) {
        return player.isSleeping();
    }

    /**
     * Makes the player start sleeping at the specified position.
     *
     * @param player The player to make sleep.
     * @param pos The position to sleep at.
     */
    public static void startSleeping(Player player, BlockPos pos) {
        player.startSleepInBed(pos);
    }

    /**
     * Makes the player stop sleeping.
     *
     * @param player The player to wake up.
     */
    public static void stopSleeping(Player player) {
        player.stopSleeping();
    }

    /**
     * Gets the player's current food level.
     *
     * @param player The player to get food level from.
     * @return The current food level (0-20).
     */
    public static int getFoodLevel(Player player) {
        return player.getFoodData().getFoodLevel();
    }

    /**
     * Sets the player's food level.
     *
     * @param player The player to set food level for.
     * @param level The food level to set (0-20).
     */
    public static void setFoodLevel(Player player, int level) {
        FoodData foodData = player.getFoodData();
        foodData.setFoodLevel(level);
    }

    /**
     * Gets the player's current saturation level.
     *
     * @param player The player to get saturation from.
     * @return The current saturation level.
     */
    public static float getSaturationLevel(Player player) {
        return player.getFoodData().getSaturationLevel();
    }

    /**
     * Adds exhaustion to the player.
     *
     * @param player The player to add exhaustion to.
     * @param amount The amount of exhaustion to add.
     */
    public static void addExhaustion(Player player, float amount) {
        player.getFoodData().addExhaustion(amount);
    }

    /**
     * Feeds the player the specified amount.
     *
     * @param player The player to feed.
     * @param amount The amount of food to restore.
     */
    public static void feed(Player player, int amount) {
        FoodData foodData = player.getFoodData();
        foodData.setFoodLevel(Math.min(20, foodData.getFoodLevel() + amount));
    }

    /**
     * Gets the player's ender chest inventory.
     *
     * @param player The player to get the ender chest from.
     * @return The player's ender chest container, or null if not found.
     */
    @Nullable
    public static PlayerEnderChestContainer getEnderChest(Player player) {
        return player.getEnderChestInventory();
    }

    /**
     * Gets an item from the player's ender chest by slot.
     *
     * @param player The player to get the item from.
     * @param slot The ender chest slot (0-26).
     * @return The item stack in the specified ender chest slot.
     */
    public static ItemStack getEnderChestItem(Player player, int slot) {
        Container enderChest = getEnderChest(player);
        return enderChest != null && slot >= 0 && slot < 27 ? enderChest.getItem(slot) : ItemStack.EMPTY;
    }

    /**
     * Sets an item in the player's ender chest by slot.
     *
     * @param player The player to set the item for.
     * @param slot The ender chest slot (0-26).
     * @param stack The item stack to set.
     */
    public static void setEnderChestItem(Player player, int slot, ItemStack stack) {
        Container enderChest = getEnderChest(player);
        if (enderChest != null && slot >= 0 && slot < 27) {
            enderChest.setItem(slot, stack);
        }
    }

    // ==================== PERSISTENCE & DATA UTILITIES ====================

    /**
     * Sets persistent data for the player.
     *
     * @param player The player to set data for.
     * @param key The data key.
     * @param value The data value.
     */
    public static void setPersistentData(Player player, String key, String value) {
        // This would need to be implemented using the player's persistent data container
        // Note: Minecraft's persistent data system typically uses CompoundTag
        // This is a placeholder implementation
    }

    /**
     * Gets persistent data for the player.
     *
     * @param player The player to get data from.
     * @param key The data key.
     * @return The data value, or null if not found.
     */
    @Nullable
    public static String getPersistentData(Player player, String key) {
        // This would need to be implemented using the player's persistent data container
        // Note: Minecraft's persistent data system typically uses CompoundTag
        // This is a placeholder implementation
        return null;
    }

    /**
     * Checks if the player has persistent data for a key.
     *
     * @param player The player to check.
     * @param key The data key.
     * @return true if the player has data for the key, false otherwise.
     */
    public static boolean hasPersistentData(Player player, String key) {
        return getPersistentData(player, key) != null;
    }

    /**
     * Removes persistent data for the player.
     *
     * @param player The player to remove data from.
     * @param key The data key to remove.
     */
    public static void removePersistentData(Player player, String key) {
        // This would need to be implemented using the player's persistent data container
        // Note: Minecraft's persistent data system typically uses CompoundTag
        // This is a placeholder implementation
    }
}