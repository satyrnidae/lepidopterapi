package dev.satyrn.lepidoptera.api.network;

import dev.satyrn.lepidoptera.LepidopteraAPI;
import dev.satyrn.lepidoptera.api.NotInitializable;
import dev.satyrn.lepidoptera.network.PacketChannelsImpl;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Static entry point for Lepidoptera's cross-platform play-phase packet API.
 *
 * <h2>Usage</h2>
 * <ol>
 *   <li>Call {@link #registerServerChannel} / {@link #registerClientChannel} from
 *       <em>both</em> sides during mod initialization (before any world loads).</li>
 *   <li>Optionally call {@link #registerServerReceiver} / {@link #registerClientReceiver} to
 *       handle incoming packets.</li>
 *   <li>Optionally call {@link #registerServerReadyCallback} / {@link #registerClientReadyCallback}
 *       to be notified when a Lepidoptera-aware peer joins.</li>
 * </ol>
 *
 * <h2>Threading</h2>
 * All registration methods are safe to call from any thread during mod initialization.
 * Registrations must be complete before any player connects.
 *
 * @since 1.0.0-SNAPSHOT.1+1.21.1
 */
@ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
@ApiStatus.Internal
public final class PacketChannels {

    /**
     * Registered C2S channel IDs.
     */
    public static final List<ResourceLocation> SERVER_CHANNELS = new ArrayList<>();

    /**
     * Registered S2C channel IDs.
     */
    public static final List<ResourceLocation> CLIENT_CHANNELS = new ArrayList<>();

    /**
     * Registered C2S receivers, keyed by channel ID.
     */
    public static final Map<ResourceLocation, List<PacketReceiver<ServerPlayContext>>> SERVER_RECEIVERS = new HashMap<>();

    /**
     * Registered S2C receivers, keyed by channel ID.
     */
    public static final Map<ResourceLocation, List<PacketReceiver<ClientPlayContext>>> CLIENT_RECEIVERS = new HashMap<>();

    /**
     * Server-side ready callbacks, fired when a Lepidoptera-aware client joins.
     */
    public static final List<PacketReadyCallback<ServerPlayContext>> SERVER_READY_CALLBACKS = new ArrayList<>();

    /**
     * Client-side ready callbacks, fired when joining a Lepidoptera-aware server.
     */
    public static final List<PacketReadyCallback<ClientPlayContext>> CLIENT_READY_CALLBACKS = new ArrayList<>();

    /**
     * Client-side disconnect callbacks, fired when leaving a Lepidoptera-aware server.
     */
    public static final List<Runnable> CLIENT_DISCONNECT_CALLBACKS = new ArrayList<>();

    /**
     * Calls buffered before {@link #setImpl} is called.
     */
    private static final List<Runnable> pendingCalls = new ArrayList<>();
    private static @Nullable PacketChannelsImpl impl = null;

    private PacketChannels() {
        NotInitializable.staticClass(this);
    }

    /**
     * Injects the platform-specific implementation and replays all buffered registrations.
     *
     * <p><b>Internal - not @ApiStatus.AvailableSince("0.4.0+1.19.2").</b> Called by platform entrypoints during mod initialization.
     * Must be called exactly once per process lifetime before any packets are sent.</p>
     *
     * @param newImpl the platform implementation
     */
    @ApiStatus.Internal
    public static synchronized void setImpl(PacketChannelsImpl newImpl) {
        impl = newImpl;
        for (Runnable pending : pendingCalls) {
            pending.run();
        }
        pendingCalls.clear();
    }

    private static synchronized void dispatch(Runnable call) {
        if (impl != null) {
            call.run();
        } else {
            pendingCalls.add(call);
        }
    }

    /**
     * Registers a client-to-server channel. Must be called on <em>both</em> sides before
     * any packet is sent or received on this channel.
     *
     * @param id the channel identifier
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public static synchronized void registerServerChannel(ResourceLocation id) {
        SERVER_CHANNELS.add(id);
        dispatch(() -> Objects.requireNonNull(impl).onServerChannelRegistered(id));
    }

    /**
     * Registers a server-to-client channel. Must be called on <em>both</em> sides before
     * any packet is sent or received on this channel.
     *
     * @param id the channel identifier
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public static synchronized void registerClientChannel(ResourceLocation id) {
        CLIENT_CHANNELS.add(id);
        dispatch(() -> Objects.requireNonNull(impl).onClientChannelRegistered(id));
    }

    /**
     * Registers a receiver for a C2S channel. <b>Server side only.</b>
     *
     * <p>Receivers run on the Netty IO thread - read {@code buf} immediately and dispatch
     * game-state mutations via {@code context.server().execute(...)}.</p>
     *
     * @param id       the C2S channel identifier
     * @param receiver the handler to invoke when a packet arrives
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public static synchronized void registerServerReceiver(ResourceLocation id,
                                                           PacketReceiver<ServerPlayContext> receiver) {
        SERVER_RECEIVERS.computeIfAbsent(id, k -> new ArrayList<>()).add(receiver);
    }

    /**
     * Registers a receiver for an S2C channel. <b>Client side only.</b>
     *
     * <p>Receivers run on the Netty IO thread - read {@code buf} immediately and dispatch
     * game-state mutations via {@code context.client().execute(...)}.</p>
     *
     * @param id       the S2C channel identifier
     * @param receiver the handler to invoke when a packet arrives
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public static synchronized void registerClientReceiver(ResourceLocation id,
                                                           PacketReceiver<ClientPlayContext> receiver) {
        CLIENT_RECEIVERS.computeIfAbsent(id, k -> new ArrayList<>()).add(receiver);
    }

    /**
     * Registers a callback invoked when a Lepidoptera-aware client joins the server.
     * This is the first safe point to send S2C packets to that player.
     *
     * <p>The callback fires only if {@link PacketSender#canSend} returns {@code true} for at
     * least one registered Lepidoptera channel, preventing spurious calls for vanilla clients.</p>
     *
     * @param callback the callback to invoke
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public static synchronized void registerServerReadyCallback(PacketReadyCallback<ServerPlayContext> callback) {
        SERVER_READY_CALLBACKS.add(callback);
    }

    /**
     * Registers a callback invoked when the client joins a Lepidoptera-aware server.
     * This is the first safe point to send C2S packets.
     *
     * <p>The callback fires only if {@link PacketSender#canSend} returns {@code true} for at
     * least one registered Lepidoptera channel, preventing spurious calls for vanilla servers.</p>
     *
     * @param callback the callback to invoke
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @SuppressWarnings("unused")
    public static synchronized void registerClientReadyCallback(PacketReadyCallback<ClientPlayContext> callback) {
        CLIENT_READY_CALLBACKS.add(callback);
    }

    /**
     * Registers a callback invoked when the client disconnects from a server.
     * Use this to clear any server-pushed state (e.g. {@code ConfigOverlay} values).
     *
     * @param callback the callback to invoke on disconnect
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public static synchronized void registerClientDisconnectCallback(Runnable callback) {
        CLIENT_DISCONNECT_CALLBACKS.add(callback);
    }

    /**
     * Sends a packet directly to a specific player on the given S2C channel.
     *
     * <p>Use this for targeted sends outside of an active receiver context, such as
     * broadcasting a config change to a specific player on hot-reload.</p>
     *
     * @param player the target player
     * @param id     the S2C channel identifier
     * @param buf    the payload buffer
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public static void sendToPlayer(ServerPlayer player, ResourceLocation id, FriendlyByteBuf buf) {
        if (impl == null) {
            LepidopteraAPI.error("Failed to send packet {} to player: unregistered implementation!", id);
            return;
        }
        impl.sendToPlayer(player, id, buf);
    }
}
