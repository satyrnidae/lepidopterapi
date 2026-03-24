package dev.satyrn.lepidoptera.api.config.sync;

import dev.satyrn.lepidoptera.api.annotations.Api;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Pairs a local config instance with a {@link ConfigOverlay} and resolves to whichever
 * value is currently active.
 *
 * <p>On the server the overlay is never populated, so {@link #get()} always returns the local
 * config. On the client it returns the server-pushed value whenever a sync is in effect,
 * falling back to the local config when disconnected.</p>
 *
 * <p>Instances are typically obtained from
 * {@link ServerConfigSync.Builder#commonConfig(ConfigCodec, Object)} or
 * {@link ServerConfigSync.Builder#clientOverride(java.util.function.BooleanSupplier, ConfigCodec, Object)},
 * which create the overlay internally and wire it into the sync pipeline automatically.</p>
 *
 * <h2>Usage</h2>
 * <pre>{@code
 * SyncedConfig<MyConfig> synced = ServerConfigSync.builder(MOD_ID)
 *     .clientOverride(() -> true, MyConfigCodec.INSTANCE, new MyConfig())
 *     .register();
 *     // ...
 * // At any call site:
 * boolean flag = synced.get().someFlag;
 * }</pre>
 *
 * @param <T> the config type
 */
@Api
public final class SyncedConfig<T> {

    private final T config;
    private final ConfigOverlay<T> overlay;
    private final List<Consumer<T>> applyCallbacks = new ArrayList<>();
    private final List<Runnable> clearCallbacks = new ArrayList<>();

    /**
     * Creates a new synced config pairing.
     *
     * @param config  the local config instance; used as the server-side source and as the
     *                client-side fallback when no overlay is active
     * @param overlay the overlay that receives server-pushed values
     */
    @Api public SyncedConfig(final T config, final ConfigOverlay<T> overlay) {
        this.config = config;
        this.overlay = overlay;
    }

    /**
     * Returns the server-pushed value if a sync is active, otherwise the local config.
     *
     * @return the currently active config value
     */
    @Contract(pure = true)
    @Api public T get() {
        return overlay.get().orElse(config);
    }

    /**
     * Returns the local config instance regardless of any active overlay.
     * Used as the supplier of the current server-side value when building sync packets.
     *
     * @return the local config instance
     */
    @Contract(pure = true)
    @Api public T local() {
        return config;
    }

    /**
     * Returns the underlying {@link ConfigOverlay}.
     * Use this when registering with {@link ServerConfigSync.Builder} directly.
     *
     * @return the overlay
     */
    @Contract(pure = true)
    @Api public ConfigOverlay<T> overlay() {
        return overlay;
    }

    /**
     * Registers a callback to invoke when a server-pushed config overlay is applied.
     *
     * <p>The callback receives the resolved config value (i.e. {@link #get()}, which is the
     * newly applied server value). Callbacks run on the packet-receive thread; dispatch to
     * the game thread yourself if your callback touches game state.</p>
     *
     * <p>Only meaningful on the client — overlays are only ever set from S2C packets.</p>
     *
     * @param callback the callback to invoke after the overlay is set
     * @return {@code this}, for chaining
     */
    @Contract("_ -> this")
    @Api public SyncedConfig<T> onApply(final Consumer<T> callback) {
        applyCallbacks.add(callback);
        return this;
    }

    /**
     * Registers a callback to invoke when the server-pushed overlay is cleared (e.g. on disconnect).
     *
     * <p>Only meaningful on the client — overlays are only ever cleared from client-side events.</p>
     *
     * @param callback the callback to invoke after the overlay is cleared
     * @return {@code this}, for chaining
     */
    @Contract("_ -> this")
    @Api public SyncedConfig<T> onClear(final Runnable callback) {
        clearCallbacks.add(callback);
        return this;
    }

    /**
     * Sets the overlay value and fires all registered {@link #onApply} callbacks.
     * Package-private — called by {@link ServerConfigSync}.
     *
     * @param value the server-pushed config value
     */
    void applyOverlay(final T value) {
        this.overlay.set(value);
        T resolved = get();
        for (Consumer<T> cb : applyCallbacks) cb.accept(resolved);
    }

    /**
     * Clears the overlay and fires all registered {@link #onClear} callbacks.
     * Package-private — called by {@link ServerConfigSync}.
     */
    void clearOverlay() {
        this.overlay.clear();
        for (Runnable cb : clearCallbacks) cb.run();
    }
}
