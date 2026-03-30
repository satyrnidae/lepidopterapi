package dev.satyrn.lepidoptera.api.compatibility;

import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import dev.satyrn.lepidoptera.api.NotInitializable;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.StackLocatorUtil;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.UnmodifiableView;

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
 * @CompatibilityProviders(
 *     value = {"com.example.mymod.compat.CuriosProvider"},
 *     client = {"com.example.mymod.compat.JeiClientProvider"}
 * )
 * public class MyMod implements LepidopteraMod {
 *     public void preInit() {
 *         Compatibility.registerAll(MyMod.class);
 *         Compatibility.preInit();
 *     }
 * }
 * }</pre>
 *
 * <p>Alternatively, call {@link #register(String)} / {@link #registerClient(String)} once per
 * provider. Passing a class name string rather than a direct class reference defers class
 * loading — if the class cannot be found (because the target mod is absent), registration
 * fails silently with a log message rather than crashing.</p>
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
    private static final List<ClientCompatibilityProvider> CLIENT_PROVIDERS = new ArrayList<>();
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
     * silently skipped and a warning is logged — your mod will continue to load normally.</p>
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
     * Loads and registers a {@link ClientCompatibilityProvider} by fully-qualified class name.
     * Registration is silently skipped on a dedicated server.
     *
     * <p>Class loading is deferred to this call using
     * {@code Class.forName(className, true, Compatibility.class.getClassLoader())}.
     * If the class is not found (because the target mod is absent), registration is
     * silently skipped and a warning is logged — your mod will continue to load normally.</p>
     *
     * <p>Registration also fails silently (with a warning log) if:</p>
     * <ul>
     *   <li>the class does not extend {@link ClientCompatibilityProvider}</li>
     *   <li>the class cannot be instantiated via its no-argument constructor</li>
     * </ul>
     *
     * <p>If the class loads and instantiates correctly but the provider's
     * {@link CompatibilityProvider#getModId() target mod} is not loaded, the provider is
     * discarded with a debug log entry.</p>
     *
     * @param className the fully-qualified name of a concrete {@link ClientCompatibilityProvider}
     *                  subclass with a public no-argument constructor
     *
     * @since 1.0.1-SNAPSHOT.2+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.2+1.21.1")
    public static void registerClient(final String className) {
        if (Platform.getEnvironment() != Env.CLIENT) {
            return;
        }

        final Class<?> rawClass;
        try {
            rawClass = Class.forName(className, true, Compatibility.class.getClassLoader());
        } catch (final ClassNotFoundException e) {
            LOGGER.warn("Client compatibility provider class not found: {}  skipping ({})", className, e.getMessage());
            return;
        }

        if (!ClientCompatibilityProvider.class.isAssignableFrom(rawClass)) {
            LOGGER.warn("Class {} does not extend ClientCompatibilityProvider  skipping", className);
            return;
        }

        final ClientCompatibilityProvider provider;
        try {
            provider = (ClientCompatibilityProvider) rawClass.getDeclaredConstructor().newInstance();
        } catch (final ReflectiveOperationException e) {
            LOGGER.warn("Failed to instantiate client compatibility provider {}: {}", className, e.getMessage());
            return;
        }

        if (!provider.isModLoaded()) {
            LOGGER.debug("Target mod '{}' not loaded; skipping client provider {}", provider.getModId(), className);
            return;
        }

        CLIENT_PROVIDERS.add(provider);
        CLIENT_PROVIDERS.sort(COMPARATOR.reversed());
        LOGGER.debug("Registered client compatibility provider {} for mod '{}'", className, provider.getModId());
    }

    /**
     * Reads {@link CompatibilityProviders} from {@code modClass} and calls
     * {@link #register(String)} for each name in {@link CompatibilityProviders#value()} and
     * {@link #registerClient(String)} for each name in {@link CompatibilityProviders#client()}.
     *
     * <p>If {@code modClass} is not annotated with {@link CompatibilityProviders} this
     * method is a no-op. Each class name is resolved and registered exactly as if
     * {@link #register(String)} or {@link #registerClient(String)} had been called directly —
     * failures are logged and silently skipped.</p>
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
        for (final String className : annotation.client()) {
            registerClient(className);
        }
    }

    /**
     * Registers all compatibility providers for the calling class.
     * <p>See {@link #registerAll(Class)} for more information.
     *
     * @since 1.0.1-SNAPSHOT.2+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.2+1.21.1")
    @SuppressWarnings("unused") // API method
    public static void registerAll() {
        registerAll(StackLocatorUtil.getCallerClass(2));
    }

    /**
     * Returns an unmodifiable view of all registered common providers, sorted by descending
     * priority (highest first). Only providers whose target mods are loaded are included.
     *
     * @return the registered common providers, highest priority first
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @Contract(pure = true)
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public static @UnmodifiableView List<CompatibilityProvider> getProviders() {
        return Collections.unmodifiableList(PROVIDERS);
    }

    /**
     * Returns an unmodifiable view of all registered client providers, sorted by descending
     * priority (highest first). Only populated on a Minecraft client; always empty on a
     * dedicated server.
     *
     * @return the registered client providers, highest priority first
     *
     * @since 1.0.1-SNAPSHOT.2+1.21.1
     */
    @Contract(pure = true)
    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.2+1.21.1")
    public static @UnmodifiableView List<ClientCompatibilityProvider> getClientProviders() {
        return Collections.unmodifiableList(CLIENT_PROVIDERS);
    }

    /**
     * Calls {@link CompatibilityProvider#onPreInit()} on all registered common providers in
     * descending priority order.
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @ApiStatus.Internal
    public static void preInit() {
        for (final CompatibilityProvider provider : PROVIDERS) {
            provider.onPreInit();
        }
    }

    /**
     * Calls {@link CompatibilityProvider#onInit()} on all registered common providers in
     * descending priority order.
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @ApiStatus.Internal
    public static void init() {
        for (final CompatibilityProvider provider : PROVIDERS) {
            provider.onInit();
        }
    }

    /**
     * Calls {@link CompatibilityProvider#onPostInit()} on all registered common providers in
     * descending priority order.
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @ApiStatus.Internal
    public static void postInit() {
        for (final CompatibilityProvider provider : PROVIDERS) {
            provider.onPostInit();
        }
    }

    /**
     * Calls {@link CompatibilityProvider#onServerStarted(MinecraftServer)} on all registered
     * common providers in descending priority order.
     *
     * @since 1.0.1-SNAPSHOT.2+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.2+1.21.1")
    @ApiStatus.Internal
    public static void serverStarted(MinecraftServer server) {
        for (final CompatibilityProvider provider : PROVIDERS) {
            provider.onServerStarted(server);
        }
    }

    /**
     * Calls {@link CompatibilityProvider#onServerStopped()} on all registered common providers
     * in descending priority order.
     *
     * @since 1.0.1-SNAPSHOT.2+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.2+1.21.1")
    @ApiStatus.Internal
    public static void serverStopped() {
        for (final CompatibilityProvider provider : PROVIDERS) {
            provider.onServerStopped();
        }
    }

    /**
     * Calls {@link CompatibilityProvider#onTagsLoaded()} on all registered common providers
     * in descending priority order.
     *
     * @since 1.0.1-SNAPSHOT.2+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.2+1.21.1")
    @ApiStatus.Internal
    public static void tagsLoaded() {
        for (final CompatibilityProvider provider : PROVIDERS) {
            provider.onTagsLoaded();
        }
    }

    /**
     * Calls {@link ClientCompatibilityProvider#onClientPreInit()} on all registered client
     * providers in descending priority order.
     *
     * @since 1.0.1-SNAPSHOT.2+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.2+1.21.1")
    @ApiStatus.Internal
    public static void clientPreInit() {
        for (final ClientCompatibilityProvider provider : CLIENT_PROVIDERS) {
            provider.onClientPreInit();
        }
    }

    /**
     * Calls {@link ClientCompatibilityProvider#onClientInit()} on all registered client
     * providers in descending priority order.
     *
     * @since 1.0.1-SNAPSHOT.2+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.2+1.21.1")
    @ApiStatus.Internal
    public static void clientInit() {
        for (final ClientCompatibilityProvider provider : CLIENT_PROVIDERS) {
            provider.onClientInit();
        }
    }

    /**
     * Calls {@link ClientCompatibilityProvider#onClientPostInit()} on all registered client
     * providers in descending priority order.
     *
     * @since 1.0.1-SNAPSHOT.2+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.2+1.21.1")
    @ApiStatus.Internal
    public static void clientPostInit() {
        for (final ClientCompatibilityProvider provider : CLIENT_PROVIDERS) {
            provider.onClientPostInit();
        }
    }
}
