package com.iamkaf.amber.api.registry.v1;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Lightweight optional supplier used for deferred registry objects.
 *
 * @param <T> the type of value supplied
 */
public interface OptionalSupplier<T> extends Supplier<T> {
    /**
     * Checks if the value is present.
     *
     * @return whether the value is present.
     */
    boolean isPresent();

    /**
     * Returns the value if present or {@code null} otherwise.
     *
     * @return the value if present, or {@code null} if absent
     */
    default T getOrNull() {
        return isPresent() ? get() : null;
    }

    /**
     * Wraps the value in an {@link Optional}.
     *
     * @return an {@link Optional} containing the value if present
     */
    default Optional<T> toOptional() {
        return Optional.ofNullable(getOrNull());
    }

    /**
     * Runs the given consumer if a value is present.
     *
     * @param action the consumer to run if a value is present
     */
    default void ifPresent(Consumer<? super T> action) {
        if (isPresent()) {
            action.accept(get());
        }
    }

    /**
     * Runs one of the given runnables depending on presence of a value.
     *
     * @param action      the consumer to run if a value is present
     * @param emptyAction the runnable to run if no value is present
     */
    default void ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction) {
        if (isPresent()) {
            action.accept(get());
        } else {
            emptyAction.run();
        }
    }

    /**
     * Returns a stream containing the value if present.
     */
    default Stream<T> stream() {
        return isPresent() ? Stream.of(get()) : Stream.empty();
    }

    /**
     * Returns this value or the provided default.
     */
    default T orElse(T other) {
        return isPresent() ? get() : other;
    }

    /**
     * Returns this value or lazily calls the provided supplier if absent.
     */
    default T orElseGet(Supplier<? extends T> supplier) {
        return isPresent() ? get() : supplier.get();
    }
}
