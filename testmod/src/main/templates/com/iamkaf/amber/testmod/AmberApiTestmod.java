package com.iamkaf.amber.testmod;

import com.iamkaf.amber.api.commands.v1.SimpleCommands;
import com.iamkaf.amber.api.core.v2.AmberInitializer;
import com.iamkaf.amber.api.event.v1.events.common.BlockEvents;
import com.iamkaf.amber.api.event.v1.events.common.PlayerEvents;
import com.iamkaf.amber.api.event.v1.events.common.ServerTickEvents;
import com.iamkaf.amber.api.event.v1.events.common.client.ClientTickEvents;
import com.iamkaf.amber.api.functions.v1.MathFunctions;
import com.iamkaf.amber.api.functions.v1.PlayerFunctions;
import com.iamkaf.amber.api.functions.v1.WorldFunctions;
import com.iamkaf.amber.api.networking.v1.NetworkChannel;
import com.iamkaf.amber.api.networking.v1.Packet;
import com.iamkaf.amber.api.networking.v1.PacketContext;
import com.iamkaf.amber.api.platform.v1.Platform;
import com.iamkaf.amber.api.registry.v1.DeferredRegister;
import com.iamkaf.amber.api.registry.v1.RegistrySupplier;
import com.iamkaf.amber.api.registry.v1.creativetabs.CreativeModeTabRegistry;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.@ID_TYPE@;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;

import java.util.Objects;

/**
 * Compile-only consumer of Amber's public API surface.
 *
 * <p>This fixture intentionally avoids Amber internals. Later phases should
 * grow this into a runtime probe mod that TeaKit can drive in-game.</p>
 */
public final class AmberApiTestmod {
    private static final String MOD_ID = "amber_testmod";

    private static final ResourceKey<Registry<Item>> ITEM_REGISTRY = ResourceKey.createRegistryKey(id("item"));
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(MOD_ID, ITEM_REGISTRY);
    private static final RegistrySupplier<Item> TEST_ITEM = ITEMS.register(
            "test_item",
            () -> new Item(new Item.Properties())
    );
    private static final NetworkChannel CHANNEL = NetworkChannel.create(id("main"));

    private AmberApiTestmod() {
    }

    public static void compilePublicApiSurface() {
        AmberInitializer.initialize(MOD_ID);

        SimpleCommands.createBaseCommand(MOD_ID);
        Platform.isFabric();
        Platform.getPlatformName();

        registerEvents();
        registerNetworking();
        registerRegistries();
        callFunctions();
    }

    private static void registerEvents() {
        BlockEvents.BLOCK_BREAK_BEFORE.register((level, player, pos, state, blockEntity) -> InteractionResult.PASS);
        BlockEvents.BLOCK_BREAK_AFTER.register((level, player, pos, state, blockEntity) -> { });
        BlockEvents.BLOCK_PLACE.register((level, player, pos, state, stack) -> InteractionResult.PASS);
        BlockEvents.BLOCK_INTERACT.register((player, level, hand, hitResult) -> InteractionResult.PASS);
        BlockEvents.BLOCK_CLICK.register((player, level, hand, pos, direction) -> InteractionResult.PASS);

        PlayerEvents.PLAYER_JOIN.register(player -> { });
        PlayerEvents.PLAYER_LEAVE.register(player -> { });
        PlayerEvents.PLAYER_RESPAWN.register((oldPlayer, newPlayer, alive) -> { });
        PlayerEvents.CRAFT_ITEM.register((player, stacks) -> { });
        PlayerEvents.SHIELD_BLOCK.register((player, shield, blockedDamage, source) -> { });

        ServerTickEvents.START_SERVER_TICK.register(() -> { });
        ServerTickEvents.END_SERVER_TICK.register(() -> { });
        ClientTickEvents.START_CLIENT_TICK.register(() -> { });
        ClientTickEvents.END_CLIENT_TICK.register(() -> { });
    }

    private static void registerNetworking() {
        CHANNEL.register(TestmodPacket.class, TestmodPacket::encode, TestmodPacket::decode, TestmodPacket::handle);
    }

    private static void registerRegistries() {
        ITEMS.register();
        Objects.requireNonNull(TEST_ITEM.getId());
        CreativeModeTabRegistry.builder(id("tab"));
    }

    private static void callFunctions() {
        MathFunctions.chance(0.5f);
        MathFunctions.nextIntInclusive(1, 3);
        WorldFunctions.isOverworld(null);
        PlayerFunctions.getExperienceLevel(null);
    }

    private static @ID_TYPE@ id(String path) {
        return @ID_FACTORY@;
    }

    private record TestmodPacket(int value) implements Packet<TestmodPacket> {
        private void encode(FriendlyByteBuf buffer) {
            buffer.writeInt(value);
        }

        private static TestmodPacket decode(FriendlyByteBuf buffer) {
            return new TestmodPacket(buffer.readInt());
        }

        private void handle(PacketContext context) {
        }
    }
}
