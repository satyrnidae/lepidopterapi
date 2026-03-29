package dev.satyrn.lepidoptera.api.compatibility;

import dev.satyrn.lepidoptera.api.NotInitializable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Static registry and lifecycle delegate for {@link CompatibilityProvider} instances.
 *
 * <p>Annotate your mod's main class with {@link CompatibilityProviders @CompatibilityProviders}
 * and call {@link #registerAll(Class)} during
 * {@link dev.satyrn.lepidoptera.api.LepidopteraMod#preInit() preInit} to register all
 * providers in a single declarative step:</p>
 *
 * <pre>{@code
 * @ModMeta(value = "mymod", ...)
 * @CompatibilityProviders({
 *     "com.example.mymod.compat.JeiProvider",
 *     "com.example.mymod.compat.CuriosProvider"
 * })
 * public class MyMod implements LepidopteraMod {
 *     public void preInit() {
 *         Compatibility.registerAll(MyMod.class);
 *         Compatibility.preInit();
 *     }
 * }
 * }</pre>
 *
 * <p>Alternatively, call {@link #register(String)} once per provider. Passing a class name
 * string rather than a direct class reference defers class loading  if the class cannot be
 * found (because the target mod is absent), registration fails silently with a log message
 * rather than crashing.</p>
 *
 * <p>Providers are invoked in descending priority order (highest first). See
 * {@link Provider.Priority} for named priority levels.</p>
 *
 * @since 1.0.0-SNAPSHOT.1+1.21.1
 */
@ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
public final class Compatibility {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final List<CompatibilityProvider> PROVIDERS = new ArrayList<>();
    private static final Provider.ProviderComparator COMPARATOR = new Provider.ProviderComparator();

    @Contract("-> fail")
    private Compatibility() {
        NotInitializable.staticClass(this);
    }

    /**
     * Loads and registers a {@link CompatibilityProvider} by fully-qualified class name.
     *
     * <p>Class loading is deferred to this call using
     * {@code Class.forName(className, true, Compatibility.class.getClassLoader())}.
     * If the class is not found (because the target mod is absent), registration is
     * silently skipped and a warning is logged  your mod will continue to load normally.</p>
     *
     * <p>Registration also fails silently (with a warning log) if:</p>
     * <ul>
     *   <li>the class does not extend {@link CompatibilityProvider}</li>
     *   <li>the class cannot be instantiated via its no-argument constructor</li>
     * </ul>
     *
     * <p>If the class loads and instantiates correctly but the provider's
     * {@link CompatibilityProvider#getModId() target mod} is not loaded, the provider is
     * discarded with a debug log entry.</p>
     *
     * @param className the fully-qualified name of a concrete {@link CompatibilityProvider}
     *                  subclass with a public no-argument constructor
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public static void register(final String className) {
        final Class<?> rawClass;
        try {
            rawClass = Class.forName(className, true, Compatibility.class.getClassLoader());
        } catch (final ClassNotFoundException e) {
            LOGGER.warn("Compatibility provider class not found: {}  skipping ({})", className, e.getMessage());
            return;
        }

        if (!CompatibilityProvider.class.isAssignableFrom(rawClass)) {
            LOGGER.warn("Class {} does not extend CompatibilityProvider  skipping", className);
            return;
        }

        final CompatibilityProvider provider;
        try {
            provider = (CompatibilityProvider) rawClass.getDeclaredConstructor().newInstance();
        } catch (final ReflectiveOperationException e) {
            LOGGER.warn("Failed to instantiate compatibility provider {}: {}", className, e.getMessage());
            return;
        }

        if (!provider.isModLoaded()) {
            LOGGER.debug("Target mod '{}' not loaded; skipping provider {}", provider.getModId(), className);
            return;
        }

        PROVIDERS.add(provider);
        PROVIDERS.sort(COMPARATOR.reversed());
        LOGGER.debug("Registered compatibility provider {} for mod '{}'", className, provider.getModId());
    }

    /**
     * Reads {@link CompatibilityProviders} from {@code modClass} and calls
     * {@link #register(String)} for each listed class name.
     *
     * <p>If {@code modClass} is not annotated with {@link CompatibilityProviders} this
     * method is a no-op. Each class name is resolved and registered exactly as if
     * {@link #register(String)} had been called directly  failures are logged and
     * silently skipped.</p>
     *
     * @param modClass the mod's main class, typically annotated with
     *                 {@link CompatibilityProviders} and
     *                 {@link dev.satyrn.lepidoptera.api.ModMeta @ModMeta}
     *
     * @since 1.0.1-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.1+1.21.1")
    public static void registerAll(final Class<?> modClass) {
        final CompatibilityProviders annotation = modClass.getAnnotation(CompatibilityProviders.class);
        if (annotation == null) {
            return;
        }
        for (final String className : annotation.value()) {
            register(className);
        }
    }

    /**
     * Returns an unmodifiable view of all registered providers, sorted by descending
     * priority (highest first). Only providers whose target mods are loaded are included.
     *
     * @return the registered providers, highest priority first
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public static List<CompatibilityProvider> getProviders() {
        return Collections.unmodifiableList(PROVIDERS);
    }

    /**
     * Calls {@link CompatibilityProvider#onPreInit()} on all registered providers in
     * descending priority order.
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public static void preInit() {
        for (final CompatibilityProvider provider : PROVIDERS) {
            provider.onPreInit();
        }
    }

    /**
     * Calls {@link CompatibilityProvider#onInit()} on all registered providers in
     * descending priority order.
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public static void init() {
        for (final CompatibilityProvider provider : PROVIDERS) {
            provider.onInit();
        }
    }

    /**
     * Calls {@link CompatibilityProvider#onPostInit()} on all registered providers in
     * descending priority order.
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public static void postInit() {
        for (final CompatibilityProvider provider : PROVIDERS) {
            provider.onPostInit();
        }
    }
}
