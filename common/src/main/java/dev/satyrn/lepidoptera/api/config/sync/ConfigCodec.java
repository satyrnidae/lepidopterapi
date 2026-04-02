package dev.satyrn.lepidoptera.api.config.sync;

import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.ApiStatus;

/**
 * Encodes and decodes a config value to/from a network buffer.
 *
 * <p>Implement one instance per config category and pass it to
 * {@link SyncedConfig#builder(String, ConfigCodec, me.shedaniel.autoconfig.ConfigHolder)}.</p>
 *
 * <p>Implementations must be stateless - the same instance is reused for every
 * encode/decode call.</p>
 *
 * @param <T> the config type
 *
 * @since 1.0.0-SNAPSHOT+1.21.1
 */
@ApiStatus.AvailableSince("1.0.0-SNAPSHOT+1.21.1")
public interface ConfigCodec<T> {

    /**
     * Writes {@code value} to the buffer.
     *
     * @param value the value to encode
     * @param buf   the target buffer
     *
     * @since 1.0.0-SNAPSHOT+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT+1.21.1")
    void encode(final T value, final FriendlyByteBuf buf);

    /**
     * Reads and returns a value from the buffer.
     *
     * @param buf the source buffer
     *
     * @return the decoded value
     *
     * @since 1.0.0-SNAPSHOT+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT+1.21.1")
    T decode(final FriendlyByteBuf buf);
}
