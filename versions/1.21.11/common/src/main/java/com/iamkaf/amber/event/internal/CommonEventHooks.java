package com.iamkaf.amber.event.internal;

import com.iamkaf.amber.api.event.v1.events.common.BlockEvents;
import com.iamkaf.amber.api.event.v1.events.common.EntityEvent;
import com.iamkaf.amber.api.event.v1.events.common.ItemEvents;
import com.iamkaf.amber.api.event.v1.events.common.PlayerEvents;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

public final class CommonEventHooks {
    private static final ThreadLocal<ArrayDeque<EntityIgnitionMetadata>> ENTITY_IGNITION_SOURCES =
            ThreadLocal.withInitial(ArrayDeque::new);
    private static final ThreadLocal<ArrayDeque<ShearMetadata>> SHEAR_SOURCES =
            ThreadLocal.withInitial(ArrayDeque::new);
    private static final ThreadLocal<ShearCapture> ACTIVE_SHEAR_CAPTURE = new ThreadLocal<>();

    private static final Set<Item> FISHING_TREASURE_ITEMS = Set.of(
            Items.NAME_TAG,
            Items.SADDLE,
            Items.NAUTILUS_SHELL
    );

    private static final Set<Item> FISHING_JUNK_ITEMS = Set.of(
            Blocks.LILY_PAD.asItem(),
            Items.LEATHER_BOOTS,
            Items.LEATHER,
            Items.BONE,
            Items.POTION,
            Items.STRING,
            Items.BOWL,
            Items.STICK,
            Items.INK_SAC,
            Blocks.TRIPWIRE_HOOK.asItem(),
            Items.ROTTEN_FLESH,
            Blocks.BAMBOO.asItem()
    );

    private CommonEventHooks() {
    }

    public static void fireBlockIgnite(
            Level level,
            BlockPos pos,
            BlockState state,
            @Nullable Player player,
            ItemStack ignitionItem,
            BlockEvents.BlockIgnitionSource source
    ) {
        if (level.isClientSide()) {
            return;
        }

        BlockEvents.IGNITE_BLOCK.invoker().ignite(
                new BlockEvents.SimpleBlockIgnitionContext(player, copySingle(ignitionItem), pos, state, level, source)
        );
    }

    public static void pushEntityIgnitionSource(
            @Nullable Player player,
            ItemStack ignitionItem,
            EntityEvent.EntityIgnitionSource source
    ) {
        pushEntityIgnitionSource(player, ignitionItem, source, true);
    }

    public static void pushEntityIgnitionSource(
            @Nullable Player player,
            ItemStack ignitionItem,
            EntityEvent.EntityIgnitionSource source,
            boolean fireOnIgnite
    ) {
        ENTITY_IGNITION_SOURCES.get().push(new EntityIgnitionMetadata(player, copySingle(ignitionItem), source, fireOnIgnite));
    }

    public static void popEntityIgnitionSource() {
        ArrayDeque<EntityIgnitionMetadata> stack = ENTITY_IGNITION_SOURCES.get();
        if (!stack.isEmpty()) {
            stack.pop();
        }
        if (stack.isEmpty()) {
            ENTITY_IGNITION_SOURCES.remove();
        }
    }

    public static boolean shouldFireEntityIgnitionOnIgnite() {
        EntityIgnitionMetadata metadata = ENTITY_IGNITION_SOURCES.get().peek();
        return metadata == null || metadata.fireOnIgnite();
    }

    public static void fireEntityIgnite(Entity entity) {
        if (entity.level().isClientSide()) {
            return;
        }

        EntityIgnitionMetadata metadata = ENTITY_IGNITION_SOURCES.get().peek();
        EntityEvent.EntityIgnitionSource source = metadata != null
                ? metadata.source()
                : EntityEvent.EntityIgnitionSource.ENVIRONMENTAL;
        Player player = metadata != null ? metadata.player() : null;
        ItemStack ignitionItem = metadata != null ? metadata.ignitionItem() : ItemStack.EMPTY;

        EntityEvent.IGNITE_ENTITY.invoker().ignite(
                new EntityEvent.SimpleEntityIgnitionContext(player, ignitionItem, entity, entity.level(), source)
        );
    }

    public static void pushShearSource(@Nullable Player player, ItemStack shears, EntityEvent.ShearSource source) {
        SHEAR_SOURCES.get().push(new ShearMetadata(toServerPlayer(player), copySingle(shears), source));
    }

    public static void popShearSource() {
        ArrayDeque<ShearMetadata> stack = SHEAR_SOURCES.get();
        if (!stack.isEmpty()) {
            stack.pop();
        }
        if (stack.isEmpty()) {
            SHEAR_SOURCES.remove();
        }
    }

    public static void beginShearCapture(Entity target) {
        ShearMetadata metadata = SHEAR_SOURCES.get().peek();
        if (metadata == null) {
            metadata = new ShearMetadata(null, new ItemStack(Items.SHEARS), EntityEvent.ShearSource.UNKNOWN);
        }
        ACTIVE_SHEAR_CAPTURE.set(new ShearCapture(target, metadata, new ArrayList<>()));
    }

    public static void captureShearDrop(Entity entity) {
        ShearCapture capture = ACTIVE_SHEAR_CAPTURE.get();
        if (capture == null || !(entity instanceof ItemEntity itemEntity)) {
            return;
        }
        capture.drops().add(itemEntity.getItem().copy());
    }

    public static void finishShearCapture(Entity target, Level level) {
        ShearCapture capture = ACTIVE_SHEAR_CAPTURE.get();
        ACTIVE_SHEAR_CAPTURE.remove();
        if (capture == null || capture.target() != target || level.isClientSide()) {
            return;
        }

        EntityEvent.SHEAR.invoker().shear(
                new EntityEvent.SimpleShearingContext(
                        capture.metadata().player(),
                        capture.metadata().shears(),
                        target,
                        level,
                        getShearTarget(target),
                        capture.drops(),
                        true,
                        capture.metadata().source()
                )
        );
    }

    public static ItemStack findFishingRod(Player player) {
        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.is(Items.FISHING_ROD)) {
            return copySingle(mainHand);
        }

        ItemStack offHand = player.getOffhandItem();
        if (offHand.is(Items.FISHING_ROD)) {
            return copySingle(offHand);
        }

        return ItemStack.EMPTY;
    }

    public static void fireFishingStart(ServerPlayer player, ItemStack rod, FishingHook hook) {
        PlayerEvents.FISHING_START.invoker().start(
                new PlayerEvents.SimpleFishingContext(player, copySingle(rod), hook, player.level())
        );
    }

    public static void fireFishingBite(ServerPlayer player, ItemStack rod, FishingHook hook) {
        PlayerEvents.FISHING_BITE.invoker().bite(
                new PlayerEvents.SimpleFishingContext(player, copySingle(rod), hook, player.level())
        );
    }

    public static void fireFishingSuccess(
            ServerPlayer player,
            ItemStack rod,
            FishingHook hook,
            ItemStack caughtItem,
            @Nullable Entity caughtEntity,
            int experienceValue
    ) {
        PlayerEvents.FishingResult result = classifyFishingResult(caughtItem, caughtEntity);
        PlayerEvents.FISHING_SUCCESS.invoker().success(
                new PlayerEvents.SimpleFishingSuccessContext(
                        player,
                        copySingle(rod),
                        hook,
                        player.level(),
                        result,
                        caughtItem,
                        caughtEntity,
                        result == PlayerEvents.FishingResult.TREASURE,
                        experienceValue
                )
        );
    }

    public static void fireFishingStop(
            ServerPlayer player,
            ItemStack rod,
            FishingHook hook,
            PlayerEvents.FishingStopReason reason,
            boolean wasSuccessful
    ) {
        PlayerEvents.FISHING_STOP.invoker().stop(
                new PlayerEvents.SimpleFishingStopContext(player, copySingle(rod), hook, player.level(), reason, wasSuccessful)
        );
    }

    public static PlayerEvents.FishingResult classifyFishingResult(ItemStack caughtItem, @Nullable Entity caughtEntity) {
        if (caughtEntity != null) {
            return PlayerEvents.FishingResult.ENTITY;
        }
        if (caughtItem.isEmpty()) {
            return PlayerEvents.FishingResult.NOTHING;
        }
        if (caughtItem.is(ItemTags.FISHES)) {
            return PlayerEvents.FishingResult.FISH;
        }
        if (isFishingTreasure(caughtItem)) {
            return PlayerEvents.FishingResult.TREASURE;
        }
        return PlayerEvents.FishingResult.JUNK;
    }

    public static boolean isFishingTreasure(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        if (FISHING_TREASURE_ITEMS.contains(stack.getItem())) {
            return true;
        }
        if (stack.is(Items.BOW) || stack.is(Items.FISHING_ROD) || stack.is(Items.ENCHANTED_BOOK)) {
            return stack.isEnchanted();
        }
        if (stack.is(Items.BOOK)) {
            return stack.isEnchanted();
        }
        return false;
    }

    public static boolean isFishingJunk(ItemStack stack) {
        return !stack.isEmpty() && FISHING_JUNK_ITEMS.contains(stack.getItem());
    }

    public static ItemEvents.SmithingType classifySmithingType(@Nullable Recipe<?> recipe, ItemStack template) {
        if (recipe instanceof SmithingTrimRecipe) {
            return ItemEvents.SmithingType.TRIMMING;
        }
        if (template.is(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE)) {
            return ItemEvents.SmithingType.NETHERITE_UPGRADE;
        }
        if (recipe instanceof SmithingTransformRecipe || recipe instanceof SmithingRecipe) {
            return ItemEvents.SmithingType.TRANSFORMATION;
        }
        return ItemEvents.SmithingType.CUSTOM;
    }

    public static List<ItemStack> getConsumedSmithingItems(ItemStack template, ItemStack base, ItemStack addition) {
        List<ItemStack> consumed = new ArrayList<>(3);
        if (!template.isEmpty()) {
            consumed.add(copySingle(template));
        }
        if (!base.isEmpty()) {
            consumed.add(copySingle(base));
        }
        if (!addition.isEmpty()) {
            consumed.add(copySingle(addition));
        }
        return List.copyOf(consumed);
    }

    public static int getSmithingSignature(RecipeHolder<?> recipeHolder, ItemStack template, ItemStack base, ItemStack addition, ItemStack result) {
        return Objects.hash(
                recipeHolder.id(),
                ItemStack.hashItemAndComponents(template),
                ItemStack.hashItemAndComponents(base),
                ItemStack.hashItemAndComponents(addition),
                ItemStack.hashItemAndComponents(result)
        );
    }

    private static EntityEvent.ShearTarget getShearTarget(Entity target) {
        String className = target.getClass().getName();
        return switch (className) {
            case "net.minecraft.world.entity.animal.sheep.Sheep" -> EntityEvent.ShearTarget.SHEEP;
            case "net.minecraft.world.entity.animal.cow.MushroomCow" -> EntityEvent.ShearTarget.MUSHROOM_COW;
            case "net.minecraft.world.entity.animal.golem.SnowGolem" -> EntityEvent.ShearTarget.SNOW_GOLEM;
            default -> EntityEvent.ShearTarget.OTHER;
        };
    }

    private static @Nullable ServerPlayer toServerPlayer(@Nullable Player player) {
        return player instanceof ServerPlayer serverPlayer ? serverPlayer : null;
    }

    private static ItemStack copySingle(ItemStack stack) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return stack.copyWithCount(1);
    }

    private record EntityIgnitionMetadata(
            @Nullable Player player,
            ItemStack ignitionItem,
            EntityEvent.EntityIgnitionSource source,
            boolean fireOnIgnite
    ) {
    }

    private record ShearMetadata(
            @Nullable ServerPlayer player,
            ItemStack shears,
            EntityEvent.ShearSource source
    ) {
    }

    private record ShearCapture(Entity target, ShearMetadata metadata, List<ItemStack> drops) {
    }
}
