package com.iamkaf.amber.platform.services;

public interface IAmberEventSetup {
    /**
     * Registers common event handlers for the Amber mod.
     */
    void registerCommon();

    /**
     * Registers client-specific event handlers for the Amber mod.
     */
    void registerClient();

    /**
     * Registers server-specific event handlers for the Amber mod.
     */
    void registerServer();
}
