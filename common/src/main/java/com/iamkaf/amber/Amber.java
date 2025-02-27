package com.iamkaf.amber;

import com.iamkaf.amber.api.event.v1.events.common.EntityEvent;
import com.iamkaf.amber.event.AmberEventSetup;
import com.iamkaf.amber.util.Env;
import com.iamkaf.amber.util.EnvExecutor;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public final class Amber {
    public static final String MOD_ID = "amber";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        LOGGER.info("Everlasting Amber Dreams!");

        AmberEventSetup.registerCommon();
        EnvExecutor.runInEnv(Env.CLIENT, () -> AmberEventSetup::registerClient);

        EntityEvent.AFTER_DAMAGE.register((livingEntity, damageSource, damageTaken, damageDealt, blocked) -> {
            LOGGER.info("EntityEvent.AFTER_DAMAGE: {} {} {} {} {}", livingEntity, damageSource, damageTaken, damageDealt, blocked);
        });
    }

    /**
     * Creates resource location in the mod namespace with the given path.
     */
    public static ResourceLocation resource(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
