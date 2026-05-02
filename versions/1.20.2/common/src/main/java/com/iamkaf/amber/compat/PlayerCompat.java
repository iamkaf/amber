package com.iamkaf.amber.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;

public final class PlayerCompat {
    private PlayerCompat() {
    }

    public static void displayClientMessage(Player player, Component message, boolean actionBar) {
        player.displayClientMessage(message, actionBar);
    }

    public static Inventory playerInventory(Player player) {
        return player.getInventory();
    }

    public static Abilities playerAbilities(Player player) {
        return player.getAbilities();
    }

    public static void updateAbilities(Player player) {
        player.onUpdateAbilities();
    }

    public static GameType serverPlayerGameMode(ServerPlayer player) {
        return player.gameMode.getGameModeForPlayer();
    }

    public static FoodData playerFoodData(Player player) {
        return player.getFoodData();
    }

    public static PlayerEnderChestContainer playerEnderChest(Player player) {
        return player.getEnderChestInventory();
    }

    public static void sendPacket(ServerPlayer player, Packet<?> packet) {
        player.connection.send(packet);
    }

    public static ItemStack playerItemBySlot(Player player, EquipmentSlot slot) {
        return player.getItemBySlot(slot);
    }

    public static ItemStack inventoryItem(Inventory inventory, int slot) {
        return inventory.getItem(slot);
    }

    public static ItemStack mainHandItem(Player player) {
        return player.getMainHandItem();
    }

    public static ItemStack offhandItem(Player player) {
        return player.getOffhandItem();
    }

    public static ItemStack emptyStack() {
        return ItemStack.EMPTY;
    }

    public static void inventorySetItem(Inventory inventory, int slot, ItemStack stack) {
        inventory.setItem(slot, stack);
    }

    public static ItemStack containerItem(Container container, int slot) {
        return container.getItem(slot);
    }

    public static void containerSetItem(Container container, int slot, ItemStack stack) {
        container.setItem(slot, stack);
    }

    public static int selectedSlot(Inventory inventory) {
        return inventory.selected;
    }

    public static void setSelectedSlot(Inventory inventory, int slot) {
        inventory.selected = slot;
    }

    public static int totalExperience(Player player) {
        return player.totalExperience;
    }

    public static int experienceLevel(Player player) {
        return player.experienceLevel;
    }

    public static void setExperienceLevel(Player player, int level) {
        player.experienceLevel = level;
    }

    public static float experienceProgress(Player player) {
        return player.experienceProgress;
    }

    public static boolean flying(Abilities abilities) {
        return abilities.flying;
    }

    public static void setFlying(Abilities abilities, boolean value) {
        abilities.flying = value;
    }

    public static boolean mayfly(Abilities abilities) {
        return abilities.mayfly;
    }

    public static void setMayfly(Abilities abilities, boolean value) {
        abilities.mayfly = value;
    }

    public static boolean invulnerable(Abilities abilities) {
        return abilities.invulnerable;
    }

    public static void setInvulnerable(Abilities abilities, boolean value) {
        abilities.invulnerable = value;
    }

    public static boolean instabuild(Abilities abilities) {
        return abilities.instabuild;
    }

    public static void setInstabuild(Abilities abilities, boolean value) {
        abilities.instabuild = value;
    }

    public static boolean mayBuild(Abilities abilities) {
        return abilities.mayBuild;
    }

    public static void setMayBuild(Abilities abilities, boolean value) {
        abilities.mayBuild = value;
    }

    public static void giveExperiencePoints(Player player, int amount) {
        player.giveExperiencePoints(amount);
    }

    public static void giveExperienceLevels(Player player, int levels) {
        player.giveExperienceLevels(levels);
    }

    public static float attackStrengthScale(Player player, float adjustTicks) {
        return player.getAttackStrengthScale(adjustTicks);
    }

    public static void resetAttackStrengthTicker(Player player) {
        player.resetAttackStrengthTicker();
    }

    public static boolean sleeping(Player player) {
        return player.isSleeping();
    }

    public static void startSleepInBed(Player player, BlockPos pos) {
        player.startSleepInBed(pos);
    }

    public static void stopSleeping(Player player) {
        player.stopSleeping();
    }

    public static int foodLevel(FoodData foodData) {
        return foodData.getFoodLevel();
    }

    public static void setFoodLevel(FoodData foodData, int level) {
        foodData.setFoodLevel(level);
    }

    public static float saturationLevel(FoodData foodData) {
        return foodData.getSaturationLevel();
    }

    public static void addExhaustion(FoodData foodData, float amount) {
        foodData.addExhaustion(amount);
    }
}
