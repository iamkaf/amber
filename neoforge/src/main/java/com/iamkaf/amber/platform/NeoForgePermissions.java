package com.iamkaf.amber.platform;

import com.iamkaf.amber.api.permission.v1.PermissionNode;
import com.iamkaf.amber.permission.PermissionRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.server.permission.PermissionAPI;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;
import net.neoforged.neoforge.server.permission.nodes.PermissionTypes;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Resolves Amber permission nodes through NeoForge's {@link PermissionAPI}. */
final class NeoForgePermissions implements PermissionRegistry.Backend {
    private static final Map<PermissionNode, net.neoforged.neoforge.server.permission.nodes.PermissionNode<Boolean>> NATIVE_NODES =
            new ConcurrentHashMap<>();

    private NeoForgePermissions() {
    }

    static void register() {
        NeoForge.EVENT_BUS.addListener(PermissionGatherEvent.Nodes.class, NeoForgePermissions::gatherNodes);
        PermissionRegistry.install(new NeoForgePermissions());
    }

    private static void gatherNodes(PermissionGatherEvent.Nodes event) {
        for (PermissionNode node : PermissionRegistry.nodes()) {
            event.addNodes(NATIVE_NODES.computeIfAbsent(node, NeoForgePermissions::nativeNode));
        }
    }

    private static net.neoforged.neoforge.server.permission.nodes.PermissionNode<Boolean> nativeNode(PermissionNode node) {
        return new net.neoforged.neoforge.server.permission.nodes.PermissionNode<>(node.id(), PermissionTypes.BOOLEAN,
                (player, playerId, context) -> player != null && PermissionRegistry.hasDefault(player, node));
    }

    @Override
    public boolean check(ServerPlayer player, PermissionNode node) {
        var nativeNode = NATIVE_NODES.get(node);
        // The active handler rejects nodes that were registered after the server started.
        if (nativeNode == null || !PermissionAPI.getRegisteredNodes().contains(nativeNode)) {
            return PermissionRegistry.hasDefault(player, node);
        }
        return PermissionAPI.getPermission(player, nativeNode);
    }
}
