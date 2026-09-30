//? if >=1.21.11 {
package com.iamkaf.amber.api.permission.v1;

import com.iamkaf.amber.permission.PermissionRegistry;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.PermissionLevel;

import java.util.Objects;

/**
 * Per-feature permission checks for server players on every loader.
 *
 * <p>Register each node once during mod initialization, usually in a static field. A check asks
 * the loader's permission system first: Fabric API's permission API on Minecraft 26.1.2 and
 * newer, and {@code PermissionAPI} on NeoForge and Forge. When nothing overrides the node, the
 * player has it if their vanilla permission level is at least the node's default level.</p>
 *
 * <pre>{@code
 * public static final PermissionNode TELEPORT = AmberPermissions.register(
 *         Identifier.fromNamespaceAndPath("examplemod", "teleport"), PermissionLevel.GAMEMASTERS);
 *
 * if (AmberPermissions.check(player, TELEPORT)) {
 *     // ...
 * }
 * }</pre>
 */
public final class AmberPermissions {
    private AmberPermissions() {
    }

    /**
     * Registers a permission node.
     *
     * <p>NeoForge and Forge collect nodes when a server starts, so a node registered later keeps
     * its default until the next server start.</p>
     *
     * @param id           the node identifier, usually namespaced by the owning mod
     * @param defaultLevel the vanilla permission level that grants the node by default;
     *                     {@link PermissionLevel#ALL} grants it to every player
     * @throws IllegalArgumentException if a node with this identifier is already registered
     */
    public static PermissionNode register(Identifier id, PermissionLevel defaultLevel) {
        PermissionNode node = new PermissionNode(Objects.requireNonNull(id, "id"),
                Objects.requireNonNull(defaultLevel, "defaultLevel"));
        PermissionRegistry.add(node);
        return node;
    }

    /** Returns whether {@code player} has {@code node}. */
    public static boolean check(ServerPlayer player, PermissionNode node) {
        return PermissionRegistry.check(Objects.requireNonNull(player, "player"), Objects.requireNonNull(node, "node"));
    }
}
//?}
