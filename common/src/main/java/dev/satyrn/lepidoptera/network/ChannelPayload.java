package dev.satyrn.lepidoptera.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Internal {@link CustomPacketPayload} wrapper that carries a raw byte array for any registered
 * Lepidoptera channel.
 *
 * <p>A separate {@link CustomPacketPayload.Type} and {@link StreamCodec} are created per
 * channel ID at registration time. This satisfies the MC 1.21.1 requirement that each payload
 * type have a unique identifier while keeping the public API surface as plain
 * {@link net.minecraft.network.FriendlyByteBuf}.</p>
 *
 * <p><b>Internal - not for external use.</b></p>
 */
public record ChannelPayload(ResourceLocation channelId, byte[] data) implements CustomPacketPayload {

    /**
     * Per-channel Type instances, created lazily at registration time.
     */
    private static final ConcurrentHashMap<ResourceLocation, Type<ChannelPayload>> TYPES = new ConcurrentHashMap<>();

    /**
     * Returns (or creates) the {@link CustomPacketPayload.Type} for the given channel ID.
     * Thread-safe; safe to call from any thread.
     *
     * @param id the channel identifier
     *
     * @return the unique type token for this channel
     */
    public static Type<ChannelPayload> typeFor(ResourceLocation id) {
        return TYPES.computeIfAbsent(id, Type::new);
    }

    /**
     * Returns a {@link StreamCodec} for the given channel ID.
     *
     * <p>The codec captures {@code id} in its closure so the decoder can reconstruct the correct
     * {@link ChannelPayload} instance. Each call returns a new codec instance; callers should
     * cache the result if registering multiple times would be wasteful.</p>
     *
     * @param id the channel identifier
     *
     * @return a codec that reads/writes the raw payload bytes
     */
    public static StreamCodec<RegistryFriendlyByteBuf, ChannelPayload> codecFor(final ResourceLocation id) {
        return new StreamCodec<>() {
            public @Override ChannelPayload decode(RegistryFriendlyByteBuf buf) {
                byte[] bytes = new byte[buf.readableBytes()];
                buf.readBytes(bytes);
                return new ChannelPayload(id, bytes);
            }

            public @Override void encode(RegistryFriendlyByteBuf buf, ChannelPayload value) {
                buf.writeBytes(value.data());
            }
        };
    }

    public @Override Type<? extends CustomPacketPayload> type() {
        return typeFor(channelId);
    }
}
