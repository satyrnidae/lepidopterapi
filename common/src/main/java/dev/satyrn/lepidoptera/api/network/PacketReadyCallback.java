package dev.satyrn.lepidoptera.api.network;

import org.jetbrains.annotations.ApiStatus;

/**
 * Invoked when a connection partner that has Lepidoptera's packet channels registered has joined.
 *
 * <p>This is the first safe point to send packets to/from that partner. Callbacks are only fired
 * when at least one registered Lepidoptera channel is reported as available by the remote end
 * (i.e. {@link PacketSender#canSend} returns {@code true} for at least one channel), preventing
 * spurious callbacks when a vanilla client or server is involved.</p>
 *
 * <p><b>Threading:</b> Callback timing is platform-dependent. Dispatch game-state mutations to the
 * appropriate executor before mutating world state.</p>
 *
 * @param <C> the context type - {@link ServerPlayContext} for server ready callbacks,
 *            {@link ClientPlayContext} for client ready callbacks
 */
@ApiStatus.AvailableSince("0.4.0+1.19.2")
@FunctionalInterface
public interface PacketReadyCallback<C> {

    /**
     * Called when the remote end is ready to receive packets on Lepidoptera channels.
     *
     * @param context the connection context, also usable as a {@link PacketSender}
     */
    void onReady(C context);
}
