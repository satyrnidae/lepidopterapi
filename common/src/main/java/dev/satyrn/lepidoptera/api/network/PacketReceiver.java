package dev.satyrn.lepidoptera.api.network;

import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.ApiStatus;

/**
 * Receives an incoming packet on a registered channel.
 *
 * <p><b>Threading:</b> Receivers are invoked on the <em>Netty IO thread</em>. Read the buffer
 * immediately. Dispatch any mutations to game state via
 * {@code context.server().execute()} (server side) or {@code context.client().execute()} (client
 * side) to avoid race conditions.</p>
 *
 * @param <C> the context type - {@link ServerPlayContext} for C2S channels,
 *            {@link ClientPlayContext} for S2C channels
 */
@ApiStatus.AvailableSince("0.4.0+1.19.2")
@FunctionalInterface
public interface PacketReceiver<C> {

    /**
     * Called when a packet arrives on the registered channel.
     *
     * @param context the connection context, also usable as a {@link PacketSender} for replies
     * @param buf     the incoming payload; must be read before this method returns
     */
    void receive(C context, FriendlyByteBuf buf);
}
