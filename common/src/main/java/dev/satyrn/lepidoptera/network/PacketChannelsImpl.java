package dev.satyrn.lepidoptera.network;

import dev.satyrn.lepidoptera.api.network.PacketChannels;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/**
 * Platform-specific implementation of channel registration timing.
 *
 * <p><b>Internal SPI - not for external use.</b> Implemented by each platform module and
 * injected into {@link PacketChannels} during mod
 * initialization via {@code PacketChannels.setImpl(impl)}.</p>
 *
 * <p>Implementations must handle the timing constraints of their platform:</p>
 * <ul>
 *   <li>Fabric/Quilt: can register payload types immediately during {@code onInitialize}</li>
 *   <li>NeoForge: must defer actual payload registration until {@code RegisterPayloadHandlersEvent}</li>
 * </ul>
 */
public interface PacketChannelsImpl {

    /**
     * Called when a C2S channel is registered via
     * {@link PacketChannels#registerServerChannel}.
     *
     * <p>The implementation is responsible for registering the channel's payload type with
     * the platform networking system and wiring the incoming packet handler. The handler should
     * dispatch to {@link PacketChannels#SERVER_RECEIVERS}
     * at call time (live lookup, not captured at registration time).</p>
     *
     * @param id the channel identifier
     */
    void onServerChannelRegistered(ResourceLocation id);

    /**
     * Called when an S2C channel is registered via
     * {@link PacketChannels#registerClientChannel}.
     *
     * <p>The implementation registers the channel's payload type with the platform. Client-side
     * receiver wiring is done separately in the client entrypoint.</p>
     *
     * @param id the channel identifier
     */
    void onClientChannelRegistered(ResourceLocation id);

    /**
     * Sends a packet to a specific player on the given S2C channel.
     *
     * <p>Used by {@link PacketChannels#sendToPlayer}
     * for targeted sends outside of an active receiver context (e.g. hot-reload broadcasts).</p>
     *
     * @param player the target player
     * @param id     the S2C channel identifier
     * @param buf    the payload buffer; all readable bytes are consumed
     */
    void sendToPlayer(ServerPlayer player, ResourceLocation id, FriendlyByteBuf buf);
}
