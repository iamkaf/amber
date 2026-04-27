package com.iamkaf.amber.api.platform.v1;

/**
 * Creates a new ModInfo instance.
 */
public final class ModInfo {
    private final String id;
    private final String name;
    private final String version;
    private final String description;

    public ModInfo(String id, String name, String version, String description) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.description = description;
    }

    public String id() {
        return this.id;
    }

    public String name() {
        return this.name;
    }

    public String version() {
        return this.version;
    }

    public String description() {
        return this.description;
    }
}
