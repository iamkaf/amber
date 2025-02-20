package com.iamkaf.amber.util;

import com.iamkaf.amber.api.platform.v1.Platform;
import net.fabricmc.api.EnvType;

import java.util.Optional;
import java.util.function.Supplier;

public final class EnvExecutor {
    private EnvExecutor() {
    }

    public static void runInEnv(EnvType type, Supplier<Runnable> runnableSupplier) {
        runInEnv(Env.fromPlatform(type), runnableSupplier);
    }

    public static void runInEnv(Env type, Supplier<Runnable> runnableSupplier) {
        if (Platform.getEnvironment() == type) {
            runnableSupplier.get().run();
        }
    }

    public static <T> Optional<T> getInEnv(EnvType type, Supplier<Supplier<T>> runnableSupplier) {
        return getInEnv(Env.fromPlatform(type), runnableSupplier);
    }

    public static <T> Optional<T> getInEnv(Env type, Supplier<Supplier<T>> runnableSupplier) {
        if (Platform.getEnvironment() == type) {
            return Optional.ofNullable(runnableSupplier.get().get());
        }

        return Optional.empty();
    }

    public static <T> T getEnvSpecific(Supplier<Supplier<T>> client, Supplier<Supplier<T>> server) {
        if (Platform.getEnvironment() == Env.CLIENT) {
            return client.get().get();
        } else {
            return server.get().get();
        }
    }
}