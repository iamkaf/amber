package com.iamkaf.amber.api.functions.v1;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
//? if >=1.18.2
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
//? if <1.19
/*import net.minecraft.network.chat.TextComponent;*/
//? if >=1.17 {
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
//?} else
/*import net.minecraft.network.protocol.game.ClientboundSetTitlesPacket;*/
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import com.iamkaf.amber.util.compat.PlayerCompat;
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

//? if <1.19
/*import java.util.Map;*/
import java.util.Optional;
//? if <1.19
/*import java.util.WeakHashMap;*/

/**
 * Consolidated utility class for player-specific operations and mechanics.
 * This class contains functionality that is unique to players, not generic entities.
 *
 * @since 8.3.0
 */
public final class PlayerFunctions {
    //? if <1.19
    /*private static final Map<Player, GlobalPos> LAST_DEATH_LOCATIONS = new WeakHashMap<>();*/

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
        giveExperiencePoints(player, amount);
    }

    /**
     * Adds experience levels to the player.
     *
     * @param player The player to give levels to.
     * @param levels The number of experience levels to add.
     */
    public static void addLevels(Player player, int levels) {
        giveExperienceLevels(player, levels);
    }

    /**
     * Sets the player's experience level directly.
     *
     * @param player The player to set the level for.
     * @param level The experience level to set.
     */
    public static void setExperienceLevel(Player player, int level) {
        PlayerCompat.setExperienceLevel(player, level);
    }

    /**
     * Gets the player's total experience points.
     *
     * @param player The player to get experience from.
     * @return The total experience points the player has.
     */
    public static int getExperiencePoints(Player player) {
        return totalExperience(player);
    }

    /**
     * Gets the player's current experience level.
     *
     * @param player The player to get the level from.
     * @return The current experience level.
     */
    public static int getExperienceLevel(Player player) {
        return experienceLevel(player);
    }

    /**
     * Gets the player's experience progress towards the next level.
     *
     * @param player The player to get progress from.
     * @return The experience progress (0.0 to 1.0).
     */
    public static float getExperienceProgress(Player player) {
        return experienceProgress(player);
    }

    /**
     * Checks if the player has enough experience points.
     *
     * @param player The player to check.
     * @param amount The amount of experience required.
     * @return true if the player has enough experience, false otherwise.
     */
    public static boolean hasEnoughExperience(Player player, int amount) {
        return getExperiencePoints(player) >= amount;
    }

    // ==================== ABILITIES & GAME MODE UTILITIES ====================

    /**
     * Gets the player's abilities object.
     *
     * @param player The player to get abilities from.
     * @return The player's abilities.
     */
    public static Abilities getAbilities(Player player) {
        return playerAbilities(player);
    }

    /**
     * Sets whether the player is currently flying.
     *
     * @param player The player to set flying state for.
     * @param flying true if the player should be flying, false otherwise.
     */
    public static void setFlying(Player player, boolean flying) {
        Abilities abilities = getAbilities(player);
        PlayerCompat.setFlying(abilities, flying);
        updateAbilities(player);
    }

    /**
     * Checks if the player is currently flying.
     *
     * @param player The player to check.
     * @return true if the player is flying, false otherwise.
     */
    public static boolean isFlying(Player player) {
        return flying(getAbilities(player));
    }

    /**
     * Sets whether the player is allowed to fly.
     *
     * @param player The player to set flight permission for.
     * @param allowFlight true if the player should be allowed to fly, false otherwise.
     */
    public static void setAllowFlight(Player player, boolean allowFlight) {
        Abilities abilities = getAbilities(player);
        setMayfly(abilities, allowFlight);
        updateAbilities(player);
    }

    /**
     * Checks if the player can fly.
     *
     * @param player The player to check.
     * @return true if the player can fly, false otherwise.
     */
    public static boolean canFly(Player player) {
        return mayfly(getAbilities(player));
    }

    /**
     * Sets whether the player is invulnerable.
     *
     * @param player The player to set invulnerability for.
     * @param invulnerable true if the player should be invulnerable, false otherwise.
     */
    public static void setInvulnerable(Player player, boolean invulnerable) {
        Abilities abilities = getAbilities(player);
        PlayerCompat.setInvulnerable(abilities, invulnerable);
        updateAbilities(player);
    }

    /**
     * Checks if the player is invulnerable.
     *
     * @param player The player to check.
     * @return true if the player is invulnerable, false otherwise.
     */
    public static boolean isInvulnerable(Player player) {
        return invulnerable(getAbilities(player));
    }

    /**
     * Sets whether the player has creative mode instant building.
     *
     * @param player The player to set insta-build for.
     * @param instaBuild true if the player should have insta-build, false otherwise.
     */
    public static void setInstaBuild(Player player, boolean instaBuild) {
        Abilities abilities = getAbilities(player);
        setInstabuild(abilities, instaBuild);
        updateAbilities(player);
    }

    /**
     * Checks if the player has creative mode instant building.
     *
     * @param player The player to check.
     * @return true if the player has insta-build, false otherwise.
     */
    public static boolean hasInstaBuild(Player player) {
        return instabuild(getAbilities(player));
    }

    /**
     * Sets whether the player may build/modify the world.
     *
     * @param player The player to set build permission for.
     * @param mayBuild true if the player may build, false otherwise.
     */
    public static void setMayBuild(Player player, boolean mayBuild) {
        Abilities abilities = getAbilities(player);
        PlayerCompat.setMayBuild(abilities, mayBuild);
        updateAbilities(player);
    }

    /**
     * Checks if the player may build/modify the world.
     *
     * @param player The player to check.
     * @return true if the player may build, false otherwise.
     */
    public static boolean mayBuild(Player player) {
        return PlayerFunctions.mayBuild(getAbilities(player));
    }

    /**
     * Gets the player's current game mode.
     *
     * @param player The player to get game mode from.
     * @return The player's current game mode.
     */
    public static GameType getGameMode(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            return serverPlayerGameMode(serverPlayer);
        }
        return gameType("SURVIVAL"); // Default fallback
    }

    /**
     * Checks if the player is in creative mode.
     *
     * @param player The player to check.
     * @return true if the player is in creative mode, false otherwise.
     */
    public static boolean isCreativeMode(Player player) {
        return isGameType(player, "CREATIVE");
    }

    /**
     * Checks if the player is in survival mode.
     *
     * @param player The player to check.
     * @return true if the player is in survival mode, false otherwise.
     */
    public static boolean isSurvivalMode(Player player) {
        return isGameType(player, "SURVIVAL");
    }

    /**
     * Checks if the player is in adventure mode.
     *
     * @param player The player to check.
     * @return true if the player is in adventure mode, false otherwise.
     */
    public static boolean isAdventureMode(Player player) {
        return isGameType(player, "ADVENTURE");
    }

    /**
     * Checks if the player is in spectator mode.
     *
     * @param player The player to check.
     * @return true if the player is in spectator mode, false otherwise.
     */
    public static boolean isSpectatorMode(Player player) {
        return isGameType(player, "SPECTATOR");
    }

    // ==================== INVENTORY & SLOT UTILITIES ====================

    /**
     * Gets the item in the player's main hand.
     *
     * @param player The player to get the item from.
     * @return The item stack in the main hand.
     */
    public static ItemStack getMainHandItem(Player player) {
        return mainHandItem(player);
    }

    /**
     * Gets the item in the player's off hand.
     *
     * @param player The player to get the item from.
     * @return The item stack in the off hand.
     */
    public static ItemStack getOffhandItem(Player player) {
        return offhandItem(player);
    }

    /**
     * Gets the item in the player's helmet slot.
     *
     * @param player The player to get the item from.
     * @return The item stack in the helmet slot.
     */
    public static ItemStack getHelmet(Player player) {
        return playerItemBySlot(player, EquipmentSlot.HEAD);
    }

    /**
     * Gets the item in the player's chestplate slot.
     *
     * @param player The player to get the item from.
     * @return The item stack in the chestplate slot.
     */
    public static ItemStack getChestplate(Player player) {
        return playerItemBySlot(player, EquipmentSlot.CHEST);
    }

    /**
     * Gets the item in the player's leggings slot.
     *
     * @param player The player to get the item from.
     * @return The item stack in the leggings slot.
     */
    public static ItemStack getLeggings(Player player) {
        return playerItemBySlot(player, EquipmentSlot.LEGS);
    }

    /**
     * Gets the item in the player's boots slot.
     *
     * @param player The player to get the item from.
     * @return The item stack in the boots slot.
     */
    public static ItemStack getBoots(Player player) {
        return playerItemBySlot(player, EquipmentSlot.FEET);
    }

    /**
     * Sets the item in the player's main hand.
     *
     * @param player The player to set the item for.
     * @param stack The item stack to set.
     */
    public static void setMainHandItem(Player player, ItemStack stack) {
        inventorySetItem(playerInventory(player), getSelectedSlot(player), stack);
    }

    /**
     * Sets the item in the player's off hand.
     *
     * @param player The player to set the item for.
     * @param stack The item stack to set.
     */
    public static void setOffhandItem(Player player, ItemStack stack) {
        //? if >=1.17
        inventorySetItem(playerInventory(player), offhandSlot(), stack);
        //? if <1.17
        /*player.inventory.offhand.set(0, stack);*/
    }

    /**
     * Gets the player's selected hotbar slot.
     *
     * @param player The player to get the selected slot from.
     * @return The selected hotbar slot (0-8).
     */
    public static int getSelectedSlot(Player player) {
        //? if >=1.21.5
        return player.getInventory().getSelectedSlot();
        //? if <1.21.5 && >=1.17
        /*return selectedSlot(playerInventory(player));*/
        //? if <1.17
        /*return player.inventory.selected;*/
    }

    /**
     * Sets the player's selected hotbar slot.
     *
     * @param player The player to set the selected slot for.
     * @param slot The hotbar slot to select (0-8).
     */
    public static void setSelectedSlot(Player player, int slot) {
        if (slot >= 0 && slot <= 8) {
            //? if >=1.21.5
            player.getInventory().setSelectedSlot(slot);
            //? if <1.21.5 && >=1.21.2
            /*player.getInventory().setSelectedHotbarSlot(slot);*/
            //? if <1.21.2 && >=1.17
            /*setSelectedSlot(playerInventory(player), slot);*/
            //? if <1.17
            /*player.inventory.selected = slot;*/
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
            //? if >=1.17
            return inventoryItem(playerInventory(player), slot);
            //? if <1.17
            /*return player.inventory.getItem(slot);*/
        }
        return emptyStack();
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
            //? if >=1.17
            inventorySetItem(playerInventory(player), slot, stack);
            //? if <1.17
            /*player.inventory.setItem(slot, stack);*/
        }
    }

    /**
     * Gets the player's attack strength (0.0 to 1.0).
     *
     * @param player The player to get attack strength from.
     * @return The current attack strength.
     */
    public static float getAttackStrength(Player player) {
        return attackStrengthScale(player, 0.5f);
    }

    /**
     * Resets the player's attack strength cooldown.
     *
     * @param player The player to reset cooldown for.
     */
    public static void resetAttackStrength(Player player) {
        resetAttackStrengthTicker(player);
    }

    /**
     * Checks if the player has an attack cooldown.
     *
     * @param player The player to check.
     * @return true if the player has an attack cooldown, false otherwise.
     */
    public static boolean hasAttackCooldown(Player player) {
        return getAttackStrength(player) < 1.0f;
    }

    /**
     * Gets the player's attack cooldown progress (0.0 to 1.0).
     *
     * @param player The player to get cooldown progress from.
     * @return The attack cooldown progress.
     */
    public static float getAttackCooldownProgress(Player player) {
        return getAttackStrength(player);
    }

    // ==================== COMMUNICATION & FEEDBACK UTILITIES ====================

    /**
     * Sends a message to the player.
     *
     * @param player The player to send the message to.
     * @param message The message to send.
     */
    public static void sendMessage(Player player, Component message) {
        displayClientMessage(player, message, false);
    }

    /**
     * Sends an action bar message to the player.
     *
     * @param player The player to send the message to.
     * @param message The message to send.
     */
    public static void sendActionBarMessage(Player player, Component message) {
        displayClientMessage(player, message, true);
    }

    /**
     * Sends an action bar message to the player.
     *
     * @param player The player to send the message to.
     * @param message The action bar message to send.
     */
    public static void sendActionBar(Player player, Component message) {
        displayClientMessage(player, message, true);
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
            //? if >=1.17 {
            ClientboundSetTitlesAnimationPacket timesPacket = new ClientboundSetTitlesAnimationPacket(fadeIn, stay, fadeOut);
            sendPacket(serverPlayer, timesPacket);

            if (title != null) {
                ClientboundSetTitleTextPacket titlePacket = new ClientboundSetTitleTextPacket(title);
                sendPacket(serverPlayer, titlePacket);
            }

            if (subtitle != null) {
                ClientboundSetSubtitleTextPacket subtitlePacket = new ClientboundSetSubtitleTextPacket(subtitle);
                sendPacket(serverPlayer, subtitlePacket);
            }
            //?} else {
            /*serverPlayer.connection.send(new ClientboundSetTitlesPacket(fadeIn, stay, fadeOut));

            if (title != null) {
                serverPlayer.connection.send(new ClientboundSetTitlesPacket(ClientboundSetTitlesPacket.Type.TITLE, title));
            }

            if (subtitle != null) {
                serverPlayer.connection.send(new ClientboundSetTitlesPacket(ClientboundSetTitlesPacket.Type.SUBTITLE, subtitle));
            }*/
            //?}
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
            //? if >=1.17 {
            sendPacket(serverPlayer, new ClientboundSetTitleTextPacket(emptyComponent()));
            sendPacket(serverPlayer, new ClientboundSetSubtitleTextPacket(emptyComponent()));
            sendPacket(serverPlayer, new ClientboundSetTitlesAnimationPacket(0, 0, 0));
            //?} else {
            /*serverPlayer.connection.send(new ClientboundSetTitlesPacket(ClientboundSetTitlesPacket.Type.TITLE, emptyComponent()));
            serverPlayer.connection.send(new ClientboundSetTitlesPacket(ClientboundSetTitlesPacket.Type.SUBTITLE, emptyComponent()));
            serverPlayer.connection.send(new ClientboundSetTitlesPacket(0, 0, 0));*/
            //?}
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
            //? if >=1.19 {
            sendPacket(serverPlayer, new ClientboundSoundPacket(
                    //? if >=1.19.3
                    Holder.direct(sound),
                    //? if <1.19.3
                    /*sound,*/
                    source,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    volume,
                    pitch,
                    //? if >=1.20
                    player.level().getRandom().nextLong()
                    //? if >=1.19 && <1.20
                    /*player.level.getRandom().nextLong()*/
            ));
            //?} else if >=1.15 {
            /*serverPlayer.connection.send(new ClientboundSoundPacket(
                    sound,
                    source,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    volume,
                    pitch
            ));*/
            //?} else {
            /*serverPlayer.connection.send(new ClientboundSoundPacket(
                    sound,
                    source,
                    player.x,
                    player.y,
                    player.z,
                    volume,
                    pitch
            ));*/
            //?}
        }
    }

    private static Component emptyComponent() {
        //? if >=1.19
        return Component.literal("");
        //? if <1.19
        /*return new TextComponent("");*/
    }

    // ==================== WORLD INTERACTION UTILITIES ====================

    /**
     * Gets the player's last death location.
     *
     * @param player The player to get the death location for.
     * @return The player's last death location, or empty if none set.
     */
    public static Optional<GlobalPos> getLastDeathLocation(Player player) {
        //? if >=1.19
        return player.getLastDeathLocation();
        //? if <1.19
        /*return Optional.ofNullable(LAST_DEATH_LOCATIONS.get(player));*/
    }

    /**
     * Sets the player's last death location.
     *
     * @param player The player to set the death location for.
     * @param position The death location to set.
     */
    public static void setLastDeathLocation(Player player, GlobalPos position) {
        //? if >=1.19
        player.setLastDeathLocation(Optional.of(position));
        //? if <1.19
        /*LAST_DEATH_LOCATIONS.put(player, position);*/
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
        return sleeping(player);
    }

    /**
     * Makes the player start sleeping at the specified position.
     *
     * @param player The player to make sleep.
     * @param pos The position to sleep at.
     */
    public static void startSleeping(Player player, BlockPos pos) {
        startSleepInBed(player, pos);
    }

    /**
     * Makes the player stop sleeping.
     *
     * @param player The player to wake up.
     */
    public static void stopSleeping(Player player) {
        PlayerCompat.stopSleeping(player);
    }

    /**
     * Gets the player's current food level.
     *
     * @param player The player to get food level from.
     * @return The current food level (0-20).
     */
    public static int getFoodLevel(Player player) {
        return foodLevel(playerFoodData(player));
    }

    /**
     * Sets the player's food level.
     *
     * @param player The player to set food level for.
     * @param level The food level to set (0-20).
     */
    public static void setFoodLevel(Player player, int level) {
        PlayerCompat.setFoodLevel(playerFoodData(player), level);
    }

    /**
     * Gets the player's current saturation level.
     *
     * @param player The player to get saturation from.
     * @return The current saturation level.
     */
    public static float getSaturationLevel(Player player) {
        return saturationLevel(playerFoodData(player));
    }

    /**
     * Adds exhaustion to the player.
     *
     * @param player The player to add exhaustion to.
     * @param amount The amount of exhaustion to add.
     */
    public static void addExhaustion(Player player, float amount) {
        PlayerCompat.addExhaustion(playerFoodData(player), amount);
    }

    /**
     * Feeds the player the specified amount.
     *
     * @param player The player to feed.
     * @param amount The amount of food to restore.
     */
    public static void feed(Player player, int amount) {
        FoodData foodData = playerFoodData(player);
        PlayerCompat.setFoodLevel(foodData, Math.min(20, foodLevel(foodData) + amount));
    }

    /**
     * Gets the player's ender chest inventory.
     *
     * @param player The player to get the ender chest from.
     * @return The player's ender chest container, or null if not found.
     */
    @Nullable
    public static PlayerEnderChestContainer getEnderChest(Player player) {
        return playerEnderChest(player);
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
        return enderChest != null && slot >= 0 && slot < 27 ? containerItem(enderChest, slot) : emptyStack();
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
            containerSetItem(enderChest, slot, stack);
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

    private static Inventory playerInventory(Player player) {
        return PlayerCompat.playerInventory(player);
    }

    private static Abilities playerAbilities(Player player) {
        return PlayerCompat.playerAbilities(player);
    }

    private static void updateAbilities(Player player) {
        PlayerCompat.updateAbilities(player);
    }

    private static GameType serverPlayerGameMode(ServerPlayer player) {
        return PlayerCompat.serverPlayerGameMode(player);
    }

    private static FoodData playerFoodData(Player player) {
        return PlayerCompat.playerFoodData(player);
    }

    private static PlayerEnderChestContainer playerEnderChest(Player player) {
        return PlayerCompat.playerEnderChest(player);
    }

    private static void displayClientMessage(Player player, Component message, boolean actionBar) {
        PlayerCompat.displayClientMessage(player, message, actionBar);
    }

    private static void sendPacket(ServerPlayer player, Packet<?> packet) {
        PlayerCompat.sendPacket(player, packet);
    }

    private static GameType gameType(String name) {
        return GameType.valueOf(name);
    }

    private static boolean isGameType(Player player, String name) {
        return getGameMode(player).name().equals(name);
    }

    private static int offhandSlot() {
        return 40;
    }

    private static ItemStack playerItemBySlot(Player player, EquipmentSlot slot) {
        return PlayerCompat.playerItemBySlot(player, slot);
    }

    private static ItemStack inventoryItem(Inventory inventory, int slot) {
        return PlayerCompat.inventoryItem(inventory, slot);
    }

    private static ItemStack mainHandItem(Player player) {
        return PlayerCompat.mainHandItem(player);
    }

    private static ItemStack offhandItem(Player player) {
        return PlayerCompat.offhandItem(player);
    }

    private static ItemStack emptyStack() {
        return PlayerCompat.emptyStack();
    }

    private static void inventorySetItem(Inventory inventory, int slot, ItemStack stack) {
        PlayerCompat.inventorySetItem(inventory, slot, stack);
    }

    private static ItemStack containerItem(Container container, int slot) {
        return PlayerCompat.containerItem(container, slot);
    }

    private static void containerSetItem(Container container, int slot, ItemStack stack) {
        PlayerCompat.containerSetItem(container, slot, stack);
    }

    private static int selectedSlot(Inventory inventory) {
        return PlayerCompat.selectedSlot(inventory);
    }

    private static void setSelectedSlot(Inventory inventory, int slot) {
        PlayerCompat.setSelectedSlot(inventory, slot);
    }

    private static int totalExperience(Player player) {
        return PlayerCompat.totalExperience(player);
    }

    private static int experienceLevel(Player player) {
        return PlayerCompat.experienceLevel(player);
    }

    private static float experienceProgress(Player player) {
        return PlayerCompat.experienceProgress(player);
    }

    private static boolean flying(Abilities abilities) {
        return PlayerCompat.flying(abilities);
    }

    private static boolean mayfly(Abilities abilities) {
        return PlayerCompat.mayfly(abilities);
    }

    private static void setMayfly(Abilities abilities, boolean value) {
        PlayerCompat.setMayfly(abilities, value);
    }

    private static boolean invulnerable(Abilities abilities) {
        return PlayerCompat.invulnerable(abilities);
    }

    private static boolean instabuild(Abilities abilities) {
        return PlayerCompat.instabuild(abilities);
    }

    private static void setInstabuild(Abilities abilities, boolean value) {
        PlayerCompat.setInstabuild(abilities, value);
    }

    private static boolean mayBuild(Abilities abilities) {
        return PlayerCompat.mayBuild(abilities);
    }

    private static void giveExperiencePoints(Player player, int amount) {
        PlayerCompat.giveExperiencePoints(player, amount);
    }

    private static void giveExperienceLevels(Player player, int levels) {
        PlayerCompat.giveExperienceLevels(player, levels);
    }

    private static float attackStrengthScale(Player player, float adjustTicks) {
        return PlayerCompat.attackStrengthScale(player, adjustTicks);
    }

    private static void resetAttackStrengthTicker(Player player) {
        PlayerCompat.resetAttackStrengthTicker(player);
    }

    private static boolean sleeping(Player player) {
        return PlayerCompat.sleeping(player);
    }

    private static void startSleepInBed(Player player, BlockPos pos) {
        PlayerCompat.startSleepInBed(player, pos);
    }

    private static int foodLevel(FoodData foodData) {
        return PlayerCompat.foodLevel(foodData);
    }

    private static float saturationLevel(FoodData foodData) {
        return PlayerCompat.saturationLevel(foodData);
    }
}
