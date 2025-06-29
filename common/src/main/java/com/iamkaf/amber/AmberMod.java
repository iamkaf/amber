package com.iamkaf.amber;

import com.iamkaf.amber.api.core.v2.AmberModInfo;
import com.iamkaf.amber.api.registry.v1.DeferredRegister;
import com.iamkaf.amber.api.registry.v1.RegistrySupplier;
import com.iamkaf.amber.platform.Services;
import com.iamkaf.amber.util.Env;
import com.iamkaf.amber.util.EnvExecutor;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

import java.util.ArrayList;

/**
 * Common entry point for the Amber mod.
 * Replace the contents with your own implementation.
 */
public class AmberMod {
    public static final ArrayList<AmberModInfo> AMBER_MODS = new ArrayList<>();


    public static DeferredRegister<Item> ITEMS = DeferredRegister.create(Constants.MOD_ID, Registries.ITEM);
    public static RegistrySupplier<Item> TEST_ITEM =
            ITEMS.register("test_item", (key) -> new Item(new Item.Properties().setId(key)));

    /**
     * Called during mod initialization for all loaders.
     */
    public static void init() {
        Constants.LOG.info("Initializing Everlasting Amber Dreams on {}...", Services.PLATFORM.getPlatformName());
        Services.AMBER_EVENT_SETUP.registerCommon();
        EnvExecutor.runInEnv(Env.CLIENT, () -> Services.AMBER_EVENT_SETUP::registerClient);
        EnvExecutor.runInEnv(Env.SERVER, () -> Services.AMBER_EVENT_SETUP::registerServer);

        ITEMS.register();
    }
}
