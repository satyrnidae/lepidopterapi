package dev.satyrn.lepidoptera.api;

import dev.satyrn.lepidoptera.api.annotations.Api;
import org.jetbrains.annotations.Contract;

/**
 * Base utility for classes that must never be instantiated.
 *
 * <p>Call one of the static helpers from a private constructor (or from a mixin's
 * constructor) to throw an {@link AssertionError} at runtime if instantiation is
 * attempted, with a descriptive message identifying the offending class.</p>
 *
 * @since 0.4.0+1.19.2
 */
@Api("0.4.0+1.19.2")
public final class NotInitializable {
    @Contract("-> fail")
    private NotInitializable() {
        staticClass(this);
    }

    /**
     * Throws an {@link AssertionError} indicating that the class of {@code instance}
     * is a static utility class and must not be instantiated.
     *
     * @param instance the illegally-constructed instance (used only to obtain its class)
     * @param <T>      the type of the instance
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @Api("1.0.0-SNAPSHOT.1+1.21.1")
    @Contract("_ -> fail")
    public static <T> void staticClass(final T instance) {
        staticClass(instance.getClass());
    }

    /**
     * Throws an {@link AssertionError} indicating that the class of {@code instance}
     * is a mixin and must not be instantiated directly.
     *
     * @param instance the illegally-constructed instance (used only to obtain its class)
     * @param <T>      the type of the instance
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @Api("1.0.0-SNAPSHOT.1+1.21.1")
    @Contract("_ -> fail")
    public static <T> void mixinClass(final T instance) {
        mixinClass(instance.getClass());
    }

    /**
     * Throws an {@link AssertionError} indicating that {@code clazz} is a static utility
     * class and must not be instantiated.
     *
     * @param clazz the class that was illegally instantiated
     * @param <T>   the type
     *
     * @since 0.4.0+1.19.2
     * @deprecated Prefer the instance overload {@link #staticClass(Object)} to avoid
     * passing the class explicitly.
     */
    @Api(value = "0.4.0+1.19.2", deprecated = "1.0.0-SNAPSHOT.1+1.21.1")
    @Contract("_ -> fail")
    @Deprecated(since = "1.0.0-SNAPSHOT.1+1.21.1", forRemoval = true)
    public static <T> void staticClass(final Class<T> clazz) {
        throw new AssertionError(clazz.getName() + " is a static class and should not be initialized.");
    }

    /**
     * Throws an {@link AssertionError} indicating that {@code clazz} is a mixin and
     * must not be instantiated directly.
     *
     * @param clazz the class that was illegally instantiated
     * @param <T>   the type
     *
     * @since 0.4.0+1.19.2
     * @deprecated Prefer the instance overload {@link #mixinClass(Object)} to avoid
     * passing the class explicitly.
     */
    @Api(value = "0.4.0+1.19.2", deprecated = "1.0.0-SNAPSHOT.1+1.21.1")
    @Contract("_ -> fail")
    @Deprecated(since = "1.0.0-SNAPSHOT.1+1.21.1", forRemoval = true)
    public static <T> void mixinClass(final Class<T> clazz) {
        throw new AssertionError(clazz.getName() + " is a mixin and should not be initialized.");
    }
}
