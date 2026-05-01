package com.iamkaf.amber.api.event.v1;

/**
 * A compound event result that can stop listener evaluation, carry an optional boolean outcome,
 * and carry an optional extra object result.
 *
 * @param <T> the extra object result type
 */
public final class CompoundEventResult<T> {
    private static final CompoundEventResult<?> PASS = new CompoundEventResult<>(false, null, null);

    private final boolean interruptsFurtherEvaluation;
    private final Boolean value;
    private final T object;

    private CompoundEventResult(boolean interruptsFurtherEvaluation, Boolean value, T object) {
        this.interruptsFurtherEvaluation = interruptsFurtherEvaluation;
        this.value = value;
        this.object = object;
    }

    @SuppressWarnings("unchecked")
    public static <T> CompoundEventResult<T> pass() {
        return (CompoundEventResult<T>) PASS;
    }

    public static <T> CompoundEventResult<T> interrupt(Boolean value, T object) {
        return new CompoundEventResult<>(true, value, object);
    }

    public static <T> CompoundEventResult<T> interruptTrue(T object) {
        return interrupt(true, object);
    }

    public static <T> CompoundEventResult<T> interruptDefault(T object) {
        return interrupt(null, object);
    }

    public static <T> CompoundEventResult<T> interruptFalse(T object) {
        return interrupt(false, object);
    }

    public boolean interruptsFurtherEvaluation() {
        return interruptsFurtherEvaluation;
    }

    public Boolean value() {
        return value;
    }

    public boolean isEmpty() {
        return value == null;
    }

    public boolean isPresent() {
        return value != null;
    }

    public boolean isTrue() {
        return Boolean.TRUE.equals(value);
    }

    public boolean isFalse() {
        return Boolean.FALSE.equals(value);
    }

    public T object() {
        return object;
    }
}
