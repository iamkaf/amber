package com.iamkaf.amber.api.permission.v1;

import net.minecraft.resources.Identifier;
//? if >=1.21.11
import net.minecraft.server.permissions.PermissionLevel;

/**
 * A registered permission. Create one with {@link AmberPermissions#register}.
 */
public final class PermissionNode {
    private final Identifier id;
    //? if >=1.21.11 {
    private final PermissionLevel defaultLevel;

    PermissionNode(Identifier id, PermissionLevel defaultLevel) {
        this.id = id;
        this.defaultLevel = defaultLevel;
    }
    //?} else {
    /*private static final String[] LEVEL_NAMES = {"all", "moderators", "gamemasters", "admins", "owners"};
    private final int defaultLevel;

    PermissionNode(Identifier id, int defaultLevel) {
        this.id = id;
        this.defaultLevel = defaultLevel;
    }
    *///?}

    public Identifier id() {
        return id;
    }

    //? if >=1.21.11 {
    /** The vanilla permission level that grants this node when no permission system decides it. */
    public PermissionLevel defaultLevel() {
        return defaultLevel;
    }

    @Override
    public String toString() {
        return "PermissionNode[" + id + ", default " + defaultLevel.getSerializedName() + "]";
    }
    //?} else {
    /*/^* The vanilla permission level, 0 to 4, that grants this node when no permission system decides it. ^/
    public int defaultLevel() {
        return defaultLevel;
    }

    @Override
    public String toString() {
        return "PermissionNode[" + id + ", default " + LEVEL_NAMES[defaultLevel] + "]";
    }
    *///?}
}
