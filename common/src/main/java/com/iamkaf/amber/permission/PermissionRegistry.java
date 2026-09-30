//? if >=1.21.11 {
package com.iamkaf.amber.permission;

import com.iamkaf.amber.api.permission.v1.PermissionNode;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permission;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Registered permission nodes and the loader permission system that resolves them. */
public final class PermissionRegistry {
    private static final Map<Identifier, PermissionNode> NODES = new ConcurrentHashMap<>();
    private static volatile Backend backend = PermissionRegistry::hasDefault;

    private PermissionRegistry() {
    }

    public static void add(PermissionNode node) {
        if (NODES.putIfAbsent(node.id(), node) != null) {
            throw new IllegalArgumentException("Permission node " + node.id() + " is already registered");
        }
    }

    public static List<PermissionNode> nodes() {
        return List.copyOf(NODES.values());
    }

    /** Called once by the loader during initialization when it has a permission system. */
    public static void install(Backend loaderBackend) {
        backend = loaderBackend;
    }

    public static boolean check(ServerPlayer player, PermissionNode node) {
        return backend.check(player, node);
    }

    /** The vanilla answer used when no permission system overrides the node. */
    public static boolean hasDefault(ServerPlayer player, PermissionNode node) {
        return player.permissions().hasPermission(new Permission.HasCommandLevel(node.defaultLevel()));
    }

    @FunctionalInterface
    public interface Backend {
        boolean check(ServerPlayer player, PermissionNode node);
    }
}
//?}
