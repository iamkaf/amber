package com.iamkaf.mochila.item.backpack;

import com.iamkaf.amber.api.component.SimpleIntegerDataComponent;
import com.iamkaf.amber.api.event.v1.events.common.PlayerEvents;
import com.iamkaf.amber.api.sound.SoundHelper;
import com.iamkaf.mochila.item.BackpackItem;
import com.iamkaf.mochila.registry.DataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class QuickStash {
    public static final List<Block> CONTAINER_WHITELIST = List.of(
            Blocks.CHEST,
            Blocks.TRAPPED_CHEST,
            Blocks.BARREL,
            Blocks.COPPER_CHEST,
            Blocks.EXPOSED_COPPER_CHEST,
            Blocks.WEATHERED_COPPER_CHEST,
            Blocks.OXIDIZED_COPPER_CHEST,
            Blocks.WAXED_COPPER_CHEST,
            Blocks.WAXED_EXPOSED_COPPER_CHEST,
            Blocks.WAXED_WEATHERED_COPPER_CHEST,
            Blocks.WAXED_OXIDIZED_COPPER_CHEST
    );

    static {
        PlayerEvents.ENTITY_INTERACT.register((player, level, hand, entity) -> {
            if (!level.isClientSide() && player.getItemInHand(hand)
                    .getItem() instanceof BackpackItem backpackItem && player.isShiftKeyDown() && entity instanceof
                    ItemFrame frame) {
                Direction direction = frame.getDirection().getOpposite();
                BlockPos blockPosBehindFrame = frame.getPos().relative(direction);
                BlockState blockState = level.getBlockState(blockPosBehindFrame);

                boolean inserted = quickStash(blockState,
                        player,
                        level,
                        blockPosBehindFrame,
                        hand,
                        direction.getOpposite(),
                        backpackItem.size,
                        getMode(player.getItemInHand(hand))
                );

                if (inserted) {
                    return InteractionResult.CONSUME;
                }
            }

            return InteractionResult.PASS;
        });
    }

    public static Mode getMode(ItemStack stack) {
        return stack.getOrDefault(DataComponents.QUICKSTASH_MODE.get(), SimpleIntegerDataComponent.empty())
                .value() == 0 ? Mode.DUMP : Mode.STORE;
    }

    public static void sendParticles(Direction direction, Level level, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel) {
            BlockPos particlesPosition = pos.relative(direction);
            serverLevel.sendParticles(
                    ParticleTypes.CLOUD,
                    particlesPosition.getX() + 0.5d,
                    particlesPosition.getY() + 0.5d,
                    particlesPosition.getZ() + 0.5d,
                    8,
                    0.01d,
                    -0.02d,
                    0.01d,
                    0.01d
            );
        }
    }

    public static boolean quickStash(BlockState state, Player player, Level level, BlockPos pos, InteractionHand hand,
            Direction direction, BackpackContainer.BackpackSize size, Mode mode) {
        if (CONTAINER_WHITELIST.stream().noneMatch(state::is)) {
            return false;
        }

        BackpackContainer backpack = new BackpackContainer(player.getItemInHand(hand), size);
        Container container = HopperBlockEntity.getContainerAt(level, pos);

        if (container != null) {
            boolean inserted = false;
            for (var i = 0; i < backpack.getContainerSize(); i++) {
                if (mode.equals(Mode.STORE) && !contains(backpack.getItem(i), container)) {
                    continue;
                }
                ItemStack toInsert = ContainerHelper.takeItem(backpack.getItems(), i);
                int inCount = toInsert.getCount();
                boolean insertingEmpty = toInsert.isEmpty();
                ItemStack remainder = HopperBlockEntity.addItem(backpack, container, toInsert, null);
                backpack.setItem(i, remainder);
                if (!insertingEmpty && (remainder.isEmpty() || inCount > remainder.getCount())) {
                    inserted = true;
                }
            }
            if (inserted) {
                if (player instanceof ServerPlayer serverPlayer) {
                    SoundHelper.sendClientSound(
                            player,
                            SoundEvents.ARMOR_EQUIP_NETHERITE.value(),
                            SoundSource.PLAYERS,
                            0.5f,
                            1f
                    );
                }
                sendParticles(direction, level, pos);
            }
            backpack.setChanged();
            return true;
        }
        return false;
    }

    public static boolean contains(ItemStack stack, Container container) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            if (ItemStack.isSameItem(stack, container.getItem(i))) {
                return true;
            }
        }
        return false;
    }

    public enum Mode {
        DUMP,
        STORE
    }
}
