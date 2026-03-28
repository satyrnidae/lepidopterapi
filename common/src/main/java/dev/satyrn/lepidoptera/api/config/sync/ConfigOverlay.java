package dev.satyrn.lepidoptera.api.config.sync;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Holds a server-provided config value that takes priority over local config.
 *
 * <p>Create one instance per config category and register it with
 * {@link ServerConfigSync.Builder#commonConfig} or
 * {@link ServerConfigSync.Builder#clientOverride}. Lepidoptera will call
 * {@link #set} when a sync packet is received and {@link #clear} when the client
 * disconnects.</p>
 *
 * <h2>Threading</h2>
 * {@link #set} and {@link #clear} are called from the Netty IO thread (packet handlers).
 * {@link #get} is typically called from the game logic thread. The backing field is
 * {@code volatile} to guarantee visibility across threads without blocking.
 *
 * <h2>Usage</h2>
 * <pre>{@code
 * public static CommonConfig<?> getCommonConfig() {
 *     return COMMON_OVERLAY.get().orElseGet(() -> localConfig().getCommon());
 * }
 * }</pre>
 *
 * @param <T> the config type
 *
 * @since 1.0.0-SNAPSHOT+1.21.1
 */
@ApiStatus.AvailableSince("1.0.0-SNAPSHOT+1.21.1")
public final class ConfigOverlay<T> {

    private volatile @Nullable T value;

    /**
     * Returns the server-pushed value, or empty if no sync has occurred or the
     * client has disconnected.
     *
     * @return the active server value, or {@link Optional#empty()}
     *
     * @since 1.0.0-SNAPSHOT+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT+1.21.1")
    @Contract(pure = true)
    public Optional<T> get() {
        return Optional.ofNullable(value);
    }

    /**
     * Stores the server-provided value. Called by Lepidoptera when a sync packet arrives.
     *
     * @param value the received value
     *
     * @since 1.0.0-SNAPSHOT+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT+1.21.1")
    @Contract(mutates = "this")
    public void set(final T value) {
        this.value = value;
    }

    /**
     * Clears the stored value. Called by Lepidoptera on client disconnect.
     *
     * @since 1.0.0-SNAPSHOT+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT+1.21.1")
    @Contract(mutates = "this")
    public void clear() {
        this.value = null;
    }
}
