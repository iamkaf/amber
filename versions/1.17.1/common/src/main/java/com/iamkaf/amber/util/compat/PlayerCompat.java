package com.iamkaf.amber.util.compat;

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

    public static void sendPacket(ServerPlayer player, Object packet) {
        player.connection.send((Packet<?>) packet);
    }

    public static ItemStack playerItemBySlot(Player player, EquipmentSlot slot) {
        return player.getItemBySlot(slot);
    }

    public static ItemStack inventoryItem(Inventory inventory, int slot) {
        return inventory.getItem(slot);
    }

    public static ItemStack playerItemStack(Player player, String method) {
        return switch (method) {
            case "getMainHandItem" -> player.getMainHandItem();
            case "getOffhandItem" -> player.getOffhandItem();
            default -> throw new IllegalArgumentException("Unsupported player item method " + method);
        };
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

    public static int intField(Object target, String name) {
        if (target instanceof Player player) {
            return switch (name) {
                case "totalExperience" -> player.totalExperience;
                case "experienceLevel" -> player.experienceLevel;
                default -> throw new IllegalArgumentException("Unsupported player int field " + name);
            };
        }
        throw new IllegalArgumentException("Unsupported int field target " + target.getClass().getName());
    }

    public static void setIntField(Object target, String name, int value) {
        if (target instanceof Player player && name.equals("experienceLevel")) {
            player.experienceLevel = value;
            return;
        }
        throw new IllegalArgumentException("Unsupported int field " + name + " on " + target.getClass().getName());
    }

    public static float floatField(Object target, String name) {
        if (target instanceof Player player && name.equals("experienceProgress")) {
            return player.experienceProgress;
        }
        throw new IllegalArgumentException("Unsupported float field " + name + " on " + target.getClass().getName());
    }

    public static boolean booleanField(Object target, String name) {
        if (target instanceof Abilities abilities) {
            return switch (name) {
                case "flying" -> abilities.flying;
                case "mayfly" -> abilities.mayfly;
                case "invulnerable" -> abilities.invulnerable;
                case "instabuild" -> abilities.instabuild;
                case "mayBuild" -> abilities.mayBuild;
                default -> throw new IllegalArgumentException("Unsupported abilities boolean field " + name);
            };
        }
        throw new IllegalArgumentException("Unsupported boolean field target " + target.getClass().getName());
    }

    public static void setBooleanField(Object target, String name, boolean value) {
        if (target instanceof Abilities abilities) {
            switch (name) {
                case "flying" -> abilities.flying = value;
                case "mayfly" -> abilities.mayfly = value;
                case "invulnerable" -> abilities.invulnerable = value;
                case "instabuild" -> abilities.instabuild = value;
                case "mayBuild" -> abilities.mayBuild = value;
                default -> throw new IllegalArgumentException("Unsupported abilities boolean field " + name);
            }
            return;
        }
        throw new IllegalArgumentException("Unsupported boolean field " + name + " on " + target.getClass().getName());
    }

    public static void invokePlayerInt(Player player, String method, int value) {
        switch (method) {
            case "giveExperiencePoints" -> player.giveExperiencePoints(value);
            case "giveExperienceLevels" -> player.giveExperienceLevels(value);
            default -> throw new IllegalArgumentException("Unsupported player int method " + method);
        }
    }

    public static void invokePlayer(Player player, String method) {
        switch (method) {
            case "resetAttackStrengthTicker" -> player.resetAttackStrengthTicker();
            case "stopSleeping" -> player.stopSleeping();
            default -> throw new IllegalArgumentException("Unsupported player method " + method);
        }
    }

    public static float playerFloat(Player player, String method, float value) {
        if (method.equals("getAttackStrengthScale")) {
            return player.getAttackStrengthScale(value);
        }
        throw new IllegalArgumentException("Unsupported player float method " + method);
    }

    public static boolean playerBoolean(Player player, String method) {
        if (method.equals("isSleeping")) {
            return player.isSleeping();
        }
        throw new IllegalArgumentException("Unsupported player boolean method " + method);
    }

    public static void invokePlayerBlockPos(Player player, String method, BlockPos pos) {
        if (method.equals("startSleepInBed")) {
            player.startSleepInBed(pos);
            return;
        }
        throw new IllegalArgumentException("Unsupported player block position method " + method);
    }

    public static int foodInt(FoodData foodData, String method) {
        if (method.equals("getFoodLevel")) {
            return foodData.getFoodLevel();
        }
        throw new IllegalArgumentException("Unsupported food data int method " + method);
    }

    public static float foodFloat(FoodData foodData, String method) {
        if (method.equals("getSaturationLevel")) {
            return foodData.getSaturationLevel();
        }
        throw new IllegalArgumentException("Unsupported food data float method " + method);
    }

    public static void invokeFoodInt(FoodData foodData, String method, int value) {
        if (method.equals("setFoodLevel")) {
            foodData.setFoodLevel(value);
            return;
        }
        throw new IllegalArgumentException("Unsupported food data int method " + method);
    }

    public static void invokeFoodFloat(FoodData foodData, String method, float value) {
        if (method.equals("addExhaustion")) {
            foodData.addExhaustion(value);
            return;
        }
        throw new IllegalArgumentException("Unsupported food data float method " + method);
    }
}
