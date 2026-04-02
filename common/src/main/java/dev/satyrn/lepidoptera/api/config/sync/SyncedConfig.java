package dev.satyrn.lepidoptera.api.config.sync;

import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import dev.satyrn.lepidoptera.LepidopteraAPI;
import dev.satyrn.lepidoptera.api.ModHelper;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import io.netty.buffer.Unpooled;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static dev.satyrn.lepidoptera.api.network.PacketChannels.*;

/**
 * Wraps a {@link ConfigHolder} and manages server-to-client config synchronization.
 *
 * <p>On the server side {@link #get()} always returns the holder's current local value.
 * On the client side {@link #get()} returns the server-pushed value while a sync is active,
 * falling back to the holder's local value when disconnected.</p>
 *
 * <p>Instances are constructed through the fluent builder:</p>
 * <pre>{@code
 * SyncedConfig<MyConfig> synced = SyncedConfig.<MyConfig>builder(MOD_ID, MyConfigCodec.INSTANCE, holder)
 *     .networkVersion(1, "mymod.network.versionMismatch")
 *     .watchFile(Platform.getConfigFolder().resolve("mymod/config.yaml"))
 *     .register()
 *     .onApply(cfg -> applyServerConfig(cfg))
 *     .onClear(() -> resetToLocalConfig());
 *
 * // Read the active value anywhere:
 * MyConfig active = synced.get();
 * }</pre>
 *
 * <h2>Channels registered</h2>
 * Three channels are registered under the {@code lepidoptera} namespace:
 * <ul>
 *   <li>{@code lepidoptera:<modId>/version} — bidirectional version handshake</li>
 *   <li>{@code lepidoptera:<modId>/config} — S2C config push</li>
 *   <li>{@code lepidoptera:<modId>/config_req} — C2S client request for a config refresh</li>
 * </ul>
 *
 * <h2>Packet sequence</h2>
 * <ol>
 *   <li>Server ready callback fires → S2C version, S2C config</li>
 *   <li>Client receives version → C2S version echo</li>
 *   <li>Server receives client version → disconnect if mismatch</li>
 *   <li>Client receives config → decode into server value, fire {@link #onApply} callbacks</li>
 *   <li>Client disconnect → clear server value, fire {@link #onClear} callbacks</li>
 *   <li>Client sends {@code config_req} → server re-sends config</li>
 * </ol>
 *
 * @param <T> the config type
 *
 * @since 1.1.0+1.21.1
 */
@ApiStatus.AvailableSince("1.1.0+1.21.1")
public final class SyncedConfig<T extends ConfigData> implements Supplier<T> {

    // Server-pushed overlay value. null means no active sync (server side, or client after disconnect).
    private volatile @Nullable T serverValue = null;

    // Local config source — always delegated to holder.getConfig(), never cached.
    private final ConfigHolder<T> holder;
    private final ConfigCodec<T> codec;

    // Packet channels
    private final ResourceLocation versionChannel;
    private final ResourceLocation configChannel;
    private final ResourceLocation configReqChannel;
    private final int networkVersion;
    private final String mismatchKey;

    // Callbacks
    private final List<Consumer<T>> applyCallbacks = new ArrayList<>();
    private final List<Runnable> clearCallbacks = new ArrayList<>();

    // File watching — null means watchFile() was not called
    private final @Nullable Path watchPath;
    private volatile @Nullable MinecraftServer watchingServer = null;
    private volatile @Nullable WatchService watchService = null;

    // False on unregistered() sentinels — gates all network operations
    private final boolean registered;

    private SyncedConfig(final Builder<T> b,
                         final ResourceLocation versionChannel,
                         final ResourceLocation configChannel,
                         final ResourceLocation configReqChannel) {
        this.holder = b.holder;
        this.codec = b.codec;
        this.networkVersion = b.networkVersion;
        this.mismatchKey = b.mismatchKey;
        this.watchPath = b.watchPath;
        this.versionChannel = versionChannel;
        this.configChannel = configChannel;
        this.configReqChannel = configReqChannel;
        this.registered = true;
    }

    // Constructor for unregistered() sentinels — no channels, no network ops
    private SyncedConfig(final ConfigHolder<T> holder, final ConfigCodec<T> codec) {
        this.holder = holder;
        this.codec = codec;
        this.networkVersion = 0;
        this.mismatchKey = "";
        this.watchPath = null;
        this.versionChannel = ModHelper.resource(LepidopteraAPI.class, "unregistered/version");
        this.configChannel = ModHelper.resource(LepidopteraAPI.class, "unregistered/config");
        this.configReqChannel = ModHelper.resource(LepidopteraAPI.class, "unregistered/config_req");
        this.registered = false;
    }

    // --- Static factories ---

    /**
     * Creates a new builder for a given mod ID.
     *
     * @param modId  the mod's ID; used to namespace the packet channels
     * @param codec  encodes and decodes the config value for network transport
     * @param holder the Cloth Config holder; used as the local config source and to
     *               register save/load listeners that trigger broadcasts
     * @param <T>    the config type
     *
     * @return a new builder
     *
     * @since 1.1.0+1.21.1
     */
    @ApiStatus.AvailableSince("1.1.0+1.21.1")
    @Contract("_, _, _ -> new")
    public static <T extends ConfigData> Builder<T> builder(final String modId, final ConfigCodec<T> codec, final ConfigHolder<T> holder) {
        return new Builder<>(modId, codec, holder);
    }

    /**
     * Creates a new builder scoped to the mod ID declared in the class's {@code @ModMeta} annotation.
     *
     * @param modClass the mod class annotated with {@code @ModMeta}
     * @param codec    encodes and decodes the config value for network transport
     * @param holder   the Cloth Config holder; used as the local config source and to
     *                 register save/load listeners that trigger broadcasts
     * @param <T>      the config type
     *
     * @return a new builder
     *
     * @see #builder(String, ConfigCodec, ConfigHolder)
     * @since 1.1.0+1.21.1
     */
    @ApiStatus.AvailableSince("1.1.0+1.21.1")
    @Contract("_, _, _ -> new")
    public static <T extends ConfigData> Builder<T> builder(final Class<?> modClass, final ConfigCodec<T> codec, final ConfigHolder<T> holder) {
        return new Builder<>(ModHelper.modId(modClass), codec, holder);
    }

    /**
     * Creates an unregistered sentinel that wraps a holder without registering any packet channels.
     * Use this as a safe default before {@link Builder#register()} is called during mod init.
     *
     * <p>{@link #get()} and {@link #local()} both delegate to {@code holder.getConfig()}.
     * All network lifecycle methods ({@link #serverStarted}, {@link #serverStopped},
     * {@link #broadcastToAll}, {@link #sendTo}) are no-ops on this instance.</p>
     *
     * @param holder the holder to wrap
     * @param codec  the codec (retained but not used until registration)
     * @param <T>    the config type
     *
     * @return an unregistered sentinel instance
     *
     * @since 1.1.0+1.21.1
     */
    @ApiStatus.AvailableSince("1.1.0+1.21.1")
    @ApiStatus.Internal
    @Contract("_, _ -> new")
    public static <T extends ConfigData> SyncedConfig<T> unregistered(final ConfigHolder<T> holder, final ConfigCodec<T> codec) {
        return new SyncedConfig<>(holder, codec);
    }

    // --- Supplier<T> / public API ---

    /**
     * Returns the server-pushed config value if a sync is active, otherwise the current local
     * value from the underlying {@link ConfigHolder}.
     *
     * <p>Never returns a stale snapshot — delegates to {@code holder.getConfig()} for the
     * local fallback, so it always reflects the most recently loaded value.</p>
     *
     * @return the currently active config value
     *
     * @since 1.1.0+1.21.1
     */
    @ApiStatus.AvailableSince("1.1.0+1.21.1")
    @Contract(pure = true)
    @Override
    public T get() {
        final @Nullable T sv = serverValue;
        return sv != null ? sv : holder.getConfig();
    }

    /**
     * Returns the local config value from the underlying {@link ConfigHolder}, regardless of
     * any active server overlay. Equivalent to {@code holder::getConfig} as a {@link Supplier}.
     *
     * <p>Use this on the server side where the local config is always authoritative, or pass
     * {@code syncedConfig::local} wherever a {@link Supplier Supplier&lt;T&gt;} of the local
     * value is needed.</p>
     *
     * @return the current local config value
     *
     * @since 1.1.0+1.21.1
     */
    @ApiStatus.AvailableSince("1.1.0+1.21.1")
    @Contract(pure = true)
    public T local() {
        return holder.getConfig();
    }

    /**
     * Registers a callback to invoke when a server-pushed config overlay is applied.
     *
     * <p>The callback receives the newly active config value (i.e. the result of {@link #get()}
     * after the overlay is set). Callbacks run on the packet-receive thread; dispatch to the
     * game thread yourself if the callback touches game state.</p>
     *
     * <p>Only meaningful on the client — overlays are only ever set from S2C packets.</p>
     *
     * @param callback the callback to invoke after the overlay is applied
     *
     * @return {@code this}, for chaining
     *
     * @since 1.1.0+1.21.1
     */
    @ApiStatus.AvailableSince("1.1.0+1.21.1")
    @Contract("_ -> this")
    public SyncedConfig<T> onApply(final Consumer<T> callback) {
        applyCallbacks.add(callback);
        return this;
    }

    /**
     * Registers a callback to invoke when the server-pushed overlay is cleared (e.g. on disconnect).
     *
     * <p>Only meaningful on the client — overlays are only ever cleared from client-side events.</p>
     *
     * @param callback the callback to invoke after the overlay is cleared
     *
     * @return {@code this}, for chaining
     *
     * @since 1.1.0+1.21.1
     */
    @ApiStatus.AvailableSince("1.1.0+1.21.1")
    @Contract("_ -> this")
    public SyncedConfig<T> onClear(final Runnable callback) {
        clearCallbacks.add(callback);
        return this;
    }

    // --- Internal lifecycle ---

    /**
     * Marks the server as started and begins watching the config file for changes (if
     * {@link Builder#watchFile} was configured). Called by the platform entrypoint from
     * {@code serverStarted}.
     *
     * @param server the running server
     *
     * @since 1.1.0+1.21.1
     */
    @ApiStatus.AvailableSince("1.1.0+1.21.1")
    @ApiStatus.Internal
    public void serverStarted(final MinecraftServer server) {
        if (!registered) {
            return;
        }
        this.watchingServer = server;
        if (watchPath != null) {
            startWatching(server);
        }
    }

    /**
     * Marks the server as stopped and closes the config file watcher. Called by the platform
     * entrypoint from {@code serverStopped}.
     *
     * @since 1.1.0+1.21.1
     */
    @ApiStatus.AvailableSince("1.1.0+1.21.1")
    @ApiStatus.Internal
    public void serverStopped() {
        if (!registered) {
            return;
        }
        // Clear watchingServer before closing the service so the load listener sees null and
        // skips any broadcast that may race with shutdown.
        this.watchingServer = null;
        stopWatching();
    }

    /**
     * Re-sends the current config to all connected players. Use this after a server-side
     * config reload.
     *
     * @param server the running server
     *
     * @since 1.1.0+1.21.1
     */
    @ApiStatus.AvailableSince("1.1.0+1.21.1")
    @ApiStatus.Internal
    public void broadcastToAll(final MinecraftServer server) {
        if (!registered) {
            return;
        }
        for (final ServerPlayer player : server.getPlayerList().getPlayers()) {
            sendTo(player);
        }
    }

    /**
     * Sends the current config to a single player. Use this for targeted re-sync.
     *
     * @param player the target player
     *
     * @since 1.1.0+1.21.1
     */
    @ApiStatus.AvailableSince("1.1.0+1.21.1")
    @ApiStatus.Internal
    public void sendTo(final ServerPlayer player) {
        if (!registered) {
            return;
        }
        sendToPlayer(player, configChannel, buildConfigPacket());
    }

    // --- Private implementation ---

    void applyOverlay(final T value) {
        this.serverValue = value;
        final T resolved = get();
        for (final Consumer<T> cb : applyCallbacks) {
            cb.accept(resolved);
        }
    }

    void clearOverlay() {
        this.serverValue = null;
        for (final Runnable cb : clearCallbacks) {
            cb.run();
        }
    }

    private FriendlyByteBuf buildConfigPacket() {
        final FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        codec.encode(holder.getConfig(), buf);
        return buf;
    }

    private void registerChannels() {
        // Version is bidirectional; config is S2C only; config_req is C2S only.
        registerServerChannel(versionChannel);
        registerClientChannel(versionChannel);
        registerClientChannel(configChannel);
        registerServerChannel(configReqChannel);

        // Server ready: send version + config to the joining player
        registerServerReadyCallback(ctx -> {
            if (!ctx.canSend(versionChannel)) {
                LepidopteraAPI.debug("Player cannot receive lepidoptera channel {}, skipping config sync", versionChannel);
                return;
            }
            final FriendlyByteBuf verBuf = new FriendlyByteBuf(Unpooled.buffer());
            verBuf.writeVarInt(networkVersion);
            ctx.send(versionChannel, verBuf);
            ctx.send(configChannel, buildConfigPacket());
        });

        // Server receives version echo → disconnect on mismatch
        registerServerReceiver(versionChannel, (ctx, buf) -> {
            final int clientVersion = buf.readVarInt();
            if (clientVersion != networkVersion) {
                LepidopteraAPI.debug("Disconnecting client: version mismatch (server={}, client={})",
                        networkVersion, clientVersion);
                //noinspection resource - We don't want to dispose the server instance!
                ctx.server().execute(() -> ctx.player().connection.disconnect(
                        Component.translatable(mismatchKey, networkVersion, clientVersion)));
            }
        });

        // Server receives config_req → re-send config to that player
        registerServerReceiver(configReqChannel, (ctx, buf) -> sendTo(ctx.player()));
    }

    @Environment(EnvType.CLIENT)
    private void registerClientHandlers() {
        // Client receives server version → echo back own version
        registerClientReceiver(versionChannel, (ctx, buf) -> {
            buf.readVarInt(); // server version — not needed client-side
            final FriendlyByteBuf reply = new FriendlyByteBuf(Unpooled.buffer());
            reply.writeVarInt(networkVersion);
            ctx.send(versionChannel, reply);
        });

        // Client receives config → decode and apply overlay
        registerClientReceiver(configChannel, (ctx, buf) -> applyOverlay(codec.decode(buf)));

        // Client disconnect → clear overlay
        registerClientDisconnectCallback(this::clearOverlay);
    }

    private void startWatching(final MinecraftServer server) {
        try {
            final WatchService ws = FileSystems.getDefault().newWatchService();
            this.watchService = ws;
            final @Nullable Path dir = watchPath != null ? watchPath.getParent() : null;
            if (dir != null) {
                dir.register(ws, StandardWatchEventKinds.ENTRY_MODIFY);
            }
            final Thread t = new Thread(() -> watchLoop(server, ws), "lepidoptera-config-watcher");
            t.setDaemon(true);
            t.start();
        } catch (final IOException e) {
            LepidopteraAPI.error("Failed to start config file watcher.", e);
        }
    }

    private void stopWatching() {
        final @Nullable WatchService ws = this.watchService;
        if (ws != null) {
            this.watchService = null;
            try {
                ws.close();
            } catch (final IOException e) {
                LepidopteraAPI.debug("Config file watcher failed to close", e);
            }
        }
    }

    private void watchLoop(final MinecraftServer server, final WatchService ws) {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                final WatchKey key = ws.take();
                for (final WatchEvent<?> event : key.pollEvents()) {
                    if (event.kind() == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    }
                    @SuppressWarnings("unchecked") final WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
                    final Path changed = pathEvent.context();
                    if (watchPath != null && watchPath.getFileName().equals(changed)) {
                        // Notify Cloth Config to reload from disk; the registered load listener
                        // will then broadcast the updated config to all connected clients.
                        server.execute(holder::load);
                        break;
                    }
                }
                if (!key.reset()) {
                    break;
                }
            }
        } catch (final InterruptedException | ClosedWatchServiceException e) {
            LepidopteraAPI.debug("Config file watcher was closed unexpectedly", e);
            Thread.currentThread().interrupt();
        }
    }

    // --- Builder ---

    /**
     * Fluent builder for {@link SyncedConfig}.
     *
     * @param <T> the config type
     *
     * @since 1.1.0+1.21.1
     */
    @ApiStatus.AvailableSince("1.1.0+1.21.1")
    public static final class Builder<T extends ConfigData> {

        private final String modId;
        private final ConfigCodec<T> codec;
        private final ConfigHolder<T> holder;
        private int networkVersion = 1;
        private String mismatchKey = "";
        private @Nullable Path watchPath = null;

        private Builder(final String modId, final ConfigCodec<T> codec, final ConfigHolder<T> holder) {
            this.modId = modId;
            this.codec = codec;
            this.holder = holder;
        }

        /**
         * Sets the network protocol version. Clients whose version differs from the server's
         * are disconnected with a translatable message.
         *
         * @param version     integer version; increment on any breaking config wire-format change
         * @param mismatchKey i18n key for the disconnect message;
         *                    receives {@code (serverVersion, clientVersion)} as format arguments
         *
         * @return this builder
         *
         * @since 1.1.0+1.21.1
         */
        @ApiStatus.AvailableSince("1.1.0+1.21.1")
        @Contract("_, _ -> this")
        public Builder<T> networkVersion(final int version, final String mismatchKey) {
            this.networkVersion = version;
            this.mismatchKey = mismatchKey;
            return this;
        }

        /**
         * Registers a config file to watch for changes. When the file is modified, Cloth Config's
         * {@code holder.load()} is called on the server thread, which reloads the in-memory config
         * and fires all registered {@link ConfigHolder#registerLoadListener load listeners} —
         * including the one registered by this builder that broadcasts to clients.
         *
         * <p>The path must be the exact on-disk file path including extension (e.g.
         * {@code Platform.getConfigFolder().resolve("mymod/config.yaml")}).</p>
         *
         * @param configPath the absolute path to the config file on disk
         *
         * @return this builder
         *
         * @since 1.1.0+1.21.1
         */
        @ApiStatus.AvailableSince("1.1.0+1.21.1")
        @Contract("_ -> this")
        public Builder<T> watchFile(final Path configPath) {
            this.watchPath = configPath;
            return this;
        }

        /**
         * Finalizes registration. Registers all packet channels and wires all handlers via
         * {@link dev.satyrn.lepidoptera.api.network.PacketChannels PacketChannels}. Also
         * hooks {@link ConfigHolder#registerSaveListener} and
         * {@link ConfigHolder#registerLoadListener} on the holder to broadcast config to
         * clients on every save or reload. Must be called once during mod initialization.
         *
         * <p>On a client-environment JVM, client-side packet receivers are registered
         * automatically — no separate call is needed from a client entrypoint.</p>
         *
         * @return the registered {@link SyncedConfig} instance
         *
         * @since 1.1.0+1.21.1
         */
        @ApiStatus.AvailableSince("1.1.0+1.21.1")
        @Contract("-> new")
        public SyncedConfig<T> register() {
            final ResourceLocation versionChannel = ModHelper.resource(LepidopteraAPI.class, modId + "/version");
            final ResourceLocation configChannel = ModHelper.resource(LepidopteraAPI.class, modId + "/config");
            final ResourceLocation configReqChannel = ModHelper.resource(LepidopteraAPI.class, modId + "/config_req");

            final SyncedConfig<T> sync = new SyncedConfig<>(this, versionChannel, configChannel, configReqChannel);
            sync.registerChannels();

            if (Platform.getEnvironment() == Env.CLIENT) {
                sync.registerClientHandlers();
            }

            // Broadcast when config is saved via the Cloth Config GUI (fires before disk write,
            // with the GUI's in-memory values already applied).
            holder.registerSaveListener((h, cfg) -> {
                final @Nullable MinecraftServer server = sync.watchingServer;
                if (server != null) {
                    server.execute(() -> sync.broadcastToAll(server));
                }
                return InteractionResult.SUCCESS;
            });

            // Broadcast when config is (re)loaded from disk — covers external file edits
            // (triggered by the WatchService via holder.load()) and GUI saves (Cloth Config
            // calls load internally after save).
            holder.registerLoadListener((h, cfg) -> {
                final @Nullable MinecraftServer server = sync.watchingServer;
                if (server != null) {
                    server.execute(() -> sync.broadcastToAll(server));
                }
                return InteractionResult.SUCCESS;
            });

            return sync;
        }
    }
}
