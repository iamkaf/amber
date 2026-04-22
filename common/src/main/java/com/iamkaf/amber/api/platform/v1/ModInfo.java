package com.iamkaf.amber.api.platform.v1;

/**
 * Creates a new ModInfo instance.
 *
 * @param id          the unique identifier for the mod
 * @param name        the human-readable name of the mod
 * @param version     the version of the mod
 * @param description a brief description of what the mod does
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
