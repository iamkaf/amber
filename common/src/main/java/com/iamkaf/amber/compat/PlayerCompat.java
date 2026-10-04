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
//? if >=26.3 {
import net.minecraft.world.level.block.AbstractBedBlock;
import net.minecraft.world.level.block.state.BlockState;
//?}
//? if <1.19 {
/*import com.iamkaf.amber.api.event.v1.events.common.EntityEvent;
import net.minecraft.core.GlobalPos;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
*///?}

public final class PlayerCompat {
    //? if <1.19 {
    /*// Minecraft records the last death location itself from 1.19. Keyed by UUID because respawning creates a new
    // player object; like the rest of this emulation, it does not survive a server restart.
    private static final Map<UUID, GlobalPos> LAST_DEATH_LOCATIONS = new ConcurrentHashMap<>();
    *///?}

    private PlayerCompat() {
    }

    //? if <1.19 {
    /*public static void trackLastDeathLocations() {
        EntityEvent.ENTITY_DEATH.register((entity, source) -> {
            if (entity instanceof Player player && !player.level.isClientSide) {
                setLastDeathLocation(player, GlobalPos.of(player.level.dimension(), player.blockPosition()));
            }
        });
    }

    public static Optional<GlobalPos> lastDeathLocation(Player player) {
        return Optional.ofNullable(LAST_DEATH_LOCATIONS.get(player.getUUID()));
    }

    public static void setLastDeathLocation(Player player, GlobalPos position) {
        LAST_DEATH_LOCATIONS.put(player.getUUID(), position);
    }
    *///?}

    public static void displayClientMessage(Player player, Component message, boolean actionBar) {
        //? if >=26.1 {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(message, actionBar);
        } else if (actionBar) {
            player.sendOverlayMessage(message);
        } else {
            player.sendSystemMessage(message);
        }
        //?} else {
        /*player.displayClientMessage(message, actionBar);
        *///?}
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
        //? if >=1.21.5
        return inventory.getSelectedSlot();
        //? if <1.21.5
        /*return inventory.selected;*/
    }

    public static void setSelectedSlot(Inventory inventory, int slot) {
        //? if >=1.21.5
        inventory.setSelectedSlot(slot);
        //? if <1.21.5 && >=1.21.2
        /*inventory.setSelectedHotbarSlot(slot);*/
        //? if <1.21.2
        /*inventory.selected = slot;*/
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
        //? if >=26.3 {
        BlockState state = player.level().getBlockState(pos);
        if (state.getBlock() instanceof AbstractBedBlock bed) {
            player.startSleepInBed(bed, state, bed.getBedRule(player.level(), pos), pos);
        }
        //?} else {
        /*player.startSleepInBed(pos);
        *///?}
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
