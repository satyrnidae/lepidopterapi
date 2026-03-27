package dev.satyrn.lepidoptera.api.network;

import org.jetbrains.annotations.ApiStatus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

/**
 * Common send interface available on both {@link ServerPlayContext} and {@link ClientPlayContext}.
 *
 * <p><b>Threading:</b> {@code send} serialises to the Netty write queue and is safe to call from any
 * thread. Callers do not need to dispatch to the game thread before sending.</p>
 */
@ApiStatus.AvailableSince("0.4.0+1.19.2")
public interface PacketSender {

    /**
     * Sends a raw buffer packet on the named channel.
     *
     * <p>The buffer is consumed immediately; callers may release it after this call returns.</p>
     *
     * @param id  the channel identifier - must have been registered with
     *            {@link PacketChannels#registerServerChannel} or
     *            {@link PacketChannels#registerClientChannel} before calling
     * @param buf the payload to send; read position should be at the start of the data
     */
    void send(ResourceLocation id, FriendlyByteBuf buf);

    /**
     * Returns {@code true} if the remote end has registered the given channel.
     *
     * <p>Consults the channel set exchanged during connection negotiation. Always returns
     * {@code false} for vanilla clients/servers that have not loaded Lepidoptera.</p>
     *
     * @param id the channel to test
     *
     * @return whether the remote side can receive on that channel
     */
    boolean canSend(ResourceLocation id);
}
