//? if >=26.1.2 {
package com.iamkaf.amber.platform;

import com.iamkaf.amber.api.permission.v1.PermissionNode;
import com.iamkaf.amber.permission.PermissionRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerPlayer;

/** Resolves Amber permission nodes through Fabric API's permission API. */
final class FabricPermissions implements PermissionRegistry.Backend {
    private FabricPermissions() {
    }

    static void register() {
        // Older Fabric API builds for these Minecraft versions do not ship the permission module.
        if (FabricLoader.getInstance().isModLoaded("fabric-permission-api-v1")) {
            PermissionRegistry.install(new FabricPermissions());
        }
    }

    @Override
    public boolean check(ServerPlayer player, PermissionNode node) {
        return player.checkPermission(net.fabricmc.fabric.api.permission.v1.PermissionNode.of(node.id()),
                PermissionRegistry.hasDefault(player, node));
    }
}
//?}
