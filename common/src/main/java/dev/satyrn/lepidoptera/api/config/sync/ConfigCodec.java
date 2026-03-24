package dev.satyrn.lepidoptera.api.config.sync;

import dev.satyrn.lepidoptera.api.annotations.Api;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Encodes and decodes a config value to/from a network buffer.
 *
 * <p>Implement one instance per config category and pass it to
 * {@link ServerConfigSync.Builder#commonConfig} or
 * {@link ServerConfigSync.Builder#clientOverride}.</p>
 *
 * <p>Implementations must be stateless - the same instance is reused for every
 * encode/decode call.</p>
 *
 * @param <T> the config type
 */
@Api
public interface ConfigCodec<T> {

    /**
     * Writes {@code value} to the buffer.
     *
     * @param value the value to encode
     * @param buf   the target buffer
     */
    @Api void encode(final T value, final FriendlyByteBuf buf);

    /**
     * Reads and returns a value from the buffer.
     *
     * @param buf the source buffer
     * @return the decoded value
     */
    @Api T decode(final FriendlyByteBuf buf);
}
