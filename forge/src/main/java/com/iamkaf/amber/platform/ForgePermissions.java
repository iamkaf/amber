//? if >=1.21.11 {
package com.iamkaf.amber.platform;

import com.iamkaf.amber.api.permission.v1.PermissionNode;
import com.iamkaf.amber.permission.PermissionRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;
import net.minecraftforge.server.permission.nodes.PermissionTypes;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Resolves Amber permission nodes through Forge's {@link PermissionAPI}. */
final class ForgePermissions implements PermissionRegistry.Backend {
    private static final Map<PermissionNode, net.minecraftforge.server.permission.nodes.PermissionNode<Boolean>> NATIVE_NODES =
            new ConcurrentHashMap<>();

    private ForgePermissions() {
    }

    static void register() {
        PermissionGatherEvent.Nodes.BUS.addListener(ForgePermissions::gatherNodes);
        PermissionRegistry.install(new ForgePermissions());
    }

    private static void gatherNodes(PermissionGatherEvent.Nodes event) {
        for (PermissionNode node : PermissionRegistry.nodes()) {
            event.addNodes(NATIVE_NODES.computeIfAbsent(node, ForgePermissions::nativeNode));
        }
    }

    private static net.minecraftforge.server.permission.nodes.PermissionNode<Boolean> nativeNode(PermissionNode node) {
        return new net.minecraftforge.server.permission.nodes.PermissionNode<>(node.id(), PermissionTypes.BOOLEAN,
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
//?}
