package com.iamkaf.amber.api.core.v2;

/**
 * Represents information about an Amber mod.
 */
public final class AmberModInfo {
    private final String id;
    private final String name;
    private final String version;

    public AmberModInfo(String id, String name, String version) {
        this.id = id;
        this.name = name;
        this.version = version;
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
}
