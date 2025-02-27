package com.iamkaf.amber;

import com.iamkaf.amber.event.AmberEventSetup;
import com.iamkaf.amber.util.Env;
import com.iamkaf.amber.util.EnvExecutor;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public final class Amber {
    public static final String MOD_ID = "amber";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        LOGGER.info("Everlasting Amber Dreams.");

        AmberEventSetup.registerCommon();
        EnvExecutor.runInEnv(Env.CLIENT, () -> AmberEventSetup::registerClient);
    }
}
