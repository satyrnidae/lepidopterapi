package dev.satyrn.lepidoptera.api;

import dev.architectury.platform.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Static utilities for safely integrating with optional third-party mods at runtime.
 *
 * <p>These helpers complement {@link dev.satyrn.lepidoptera.api.compatibility.Compatibility}
 * for one-off checks and ad-hoc integrations that do not need a full
 * {@link dev.satyrn.lepidoptera.api.compatibility.CompatibilityProvider} class.</p>
 *
 * @since 1.0.0-SNAPSHOT.1+1.21.1
 */
@ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
public final class ModIntegration {

    private static final Logger LOGGER = LogManager.getLogger();

    @Contract("-> fail")
    private ModIntegration() {
        NotInitializable.staticClass(this);
    }

    /**
     * Returns {@code true} if the given mod is currently loaded.
     *
     * <p>Delegates to {@link Platform#isModLoaded(String)}. The set of loaded mods is
     * fixed after the mod loader has finished its startup phase; this method does not
     * cache the result.</p>
     *
     * @param modId the mod ID to check
     *
     * @return {@code true} if the mod is loaded
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public static boolean isModLoaded(final String modId) {
        return Platform.isModLoaded(modId);
    }

    /**
     * Runs {@code action} only if the given mod is loaded.
     *
     * <p>This is a convenience wrapper around {@link #isModLoaded(String)} for
     * conditional setup blocks that should be skipped when an optional dependency is
     * absent.</p>
     *
     * @param modId  the mod ID to check
     * @param action the action to run if the mod is loaded
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public static void ifModLoaded(final String modId, final Runnable action) {
        if (Platform.isModLoaded(modId)) {
            action.run();
        }
    }

    /**
     * Returns a value from the given mod if it is loaded, or {@code fallback} otherwise.
     *
     * <p>{@code supplier} is only called when the mod is loaded. Its return value may
     * itself be {@code null}.</p>
     *
     * @param modId    the mod ID to check
     * @param supplier produces the value when the mod is loaded
     * @param fallback the value to return when the mod is not loaded (may be {@code null})
     * @param <T>      the value type
     *
     * @return the supplied value, or {@code fallback}
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public static @Nullable <T> T fromModOrDefault(final String modId,
                                                   final Supplier<T> supplier,
                                                   final @Nullable T fallback) {
        return Platform.isModLoaded(modId) ? supplier.get() : fallback;
    }

    /**
     * Safely loads a class by name, returning an empty {@link Optional} if the class
     * cannot be found or does not extend {@code superType}.
     *
     * <p>Uses {@code Class.forName(className, true, ModIntegration.class.getClassLoader())}
     * to resolve the class through the mod class loader, which can see classes from all
     * loaded mods.</p>
     *
     * @param className the fully-qualified class name to load
     * @param superType the expected supertype; the loaded class must be assignable to this
     * @param <T>       the expected type
     *
     * @return an {@link Optional} containing the class, or empty if loading fails or the
     * class does not extend {@code superType}
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public static <T> Optional<Class<? extends T>> loadClass(final String className, final Class<T> superType) {
        final Class<?> rawClass;
        try {
            rawClass = Class.forName(className, true, ModIntegration.class.getClassLoader());
        } catch (final ClassNotFoundException e) {
            return Optional.empty();
        }
        if (!superType.isAssignableFrom(rawClass)) {
            return Optional.empty();
        }
        // Safe: isAssignableFrom guarantees rawClass extends T
        return Optional.of(rawClass.asSubclass(superType));
    }

    /**
     * Safely loads and instantiates a class by name via its public no-argument constructor.
     *
     * <p>Returns an empty {@link Optional}  logging a warning  if the class cannot be
     * found, does not extend {@code superType}, or cannot be instantiated (e.g. it is
     * abstract or lacks a public no-argument constructor).</p>
     *
     * @param className the fully-qualified class name to load and instantiate
     * @param superType the expected supertype; the loaded class must be assignable to this
     * @param <T>       the expected type
     *
     * @return an {@link Optional} containing the new instance, or empty on any failure
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public static <T> Optional<T> instantiate(final String className, final Class<T> superType) {
        final Optional<Class<? extends T>> loaded = loadClass(className, superType);
        if (loaded.isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(superType.cast(loaded.get().getDeclaredConstructor().newInstance()));
        } catch (final ReflectiveOperationException e) {
            LOGGER.warn("Failed to instantiate {}: {}", className, e.getMessage());
            return Optional.empty();
        }
    }
}
