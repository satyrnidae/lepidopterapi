package dev.satyrn.lepidoptera.api.config.sync;

import dev.satyrn.lepidoptera.LepidopteraAPI;
import dev.satyrn.lepidoptera.api.ModHelper;
import dev.satyrn.lepidoptera.api.ModMeta;
import dev.satyrn.lepidoptera.api.network.PacketChannels;
import io.netty.buffer.Unpooled;
import me.shedaniel.autoconfig.ConfigHolder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * Registers and manages server-to-client config synchronization for a mod.
 *
 * <h2>Usage</h2>
 * <pre>{@code
 * public static final ServerConfigSync CONFIG_SYNC =
 *     ServerConfigSync.builder(MY_MOD_ID)
 *         .networkVersion(1, MY_MOD_ID + ".network.versionMismatch")
 *         .commonConfig(CommonConfigCodec.INSTANCE,
 *                       () -> getServerConfig().getCommon(),
 *                       COMMON_OVERLAY)
 *         .clientOverride(() -> getServerConfig().shouldOverrideClient(),
 *                         ClientConfigCodec.INSTANCE,
 *                         () -> getServerConfig().getClientOverrides(),
 *                         CLIENT_OVERLAY)
 *         .register();
 * }</pre>
 *
 * <h2>Channels registered</h2>
 * Three channels are registered under the {@code lepidoptera} namespace:
 * <ul>
 *   <li>{@code lepidoptera:<modId>/version} - bidirectional version handshake</li>
 *   <li>{@code lepidoptera:<modId>/config} - S2C config push</li>
 *   <li>{@code lepidoptera:<modId>/config_req} - C2S client request for a config refresh</li>
 * </ul>
 *
 * <h2>Packet sequence</h2>
 * <ol>
 *   <li>Server ready callback fires → S2C version, S2C config</li>
 *   <li>Client receives version → C2S version echo</li>
 *   <li>Server receives client version → disconnect if mismatch</li>
 *   <li>Client receives config → decode into overlays</li>
 *   <li>Client disconnect → clear all overlays</li>
 *   <li>Client sends {@code config_req} → server re-sends config</li>
 * </ol>
 *
 * @since 1.0.0-SNAPSHOT+1.21.1
 */
@ApiStatus.AvailableSince("1.0.0-SNAPSHOT+1.21.1")
public final class ServerConfigSync {

    private final int networkVersion;
    private final String mismatchKey;
    private final List<CommonConfigEntry<?>> commonConfigs;
    private final List<ClientOverrideEntry<?>> clientOverrides;
    private final List<WatchEntry> watchEntries;
    private final ResourceLocation versionChannel;
    private final ResourceLocation configChannel;
    private final ResourceLocation configReqChannel;

    private volatile @Nullable MinecraftServer watchingServer = null;
    private volatile @Nullable WatchService watchService = null;

    private ServerConfigSync(final Builder b) {
        this.networkVersion = b.networkVersion;
        this.mismatchKey = b.mismatchKey;
        this.commonConfigs = List.copyOf(b.commonConfigs);
        this.clientOverrides = List.copyOf(b.clientOverrides);
        this.watchEntries = List.copyOf(b.watchEntries);
        this.versionChannel = ResourceLocation.fromNamespaceAndPath("lepidoptera", b.modId + "/version");
        this.configChannel = ResourceLocation.fromNamespaceAndPath("lepidoptera", b.modId + "/config");
        this.configReqChannel = ResourceLocation.fromNamespaceAndPath("lepidoptera", b.modId + "/config_req");
    }

    /**
     * Creates a new builder for the given mod ID.
     *
     * @param modId the mod's ID; used to namespace the packet channels
     *
     * @return a new builder
     *
     * @since 1.0.0-SNAPSHOT+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT+1.21.1")
    @Contract("_ -> new")
    public static Builder builder(final String modId) {
        return new Builder(modId);
    }

    /**
     * Creates a new builder scoped to the mod ID declared in a {@link ModMeta} annotation.
     *
     * @param metadata the {@code @ModMeta} annotation from the mod's entry class
     *
     * @return a new builder
     *
     * @see #builder(String)
     * @since 1.0.0-SNAPSHOT+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT+1.21.1")
    @Contract("_ -> new")
    public static Builder builder(final ModMeta metadata) {
        return new Builder(metadata.name());
    }

    /**
     * Creates a new builder scoped to the mod ID declared in the class's {@link ModMeta} annotation.
     *
     * @param modClass the mod class annotated with {@code @ModMeta}
     *
     * @return a new builder
     *
     * @see #builder(String)
     * @since 1.0.0-SNAPSHOT+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT+1.21.1")
    @Contract("_ -> new")
    public static Builder builder(final Class<?> modClass) {
        return new Builder(ModHelper.name(modClass));
    }

    // Generic helpers to avoid unchecked-cast warnings on the raw entry lists
    private static <T> void encodeCommon(final CommonConfigEntry<T> entry, final FriendlyByteBuf buf) {
        entry.codec().encode(entry.source().get(), buf);
    }

    private static <T> void decodeCommon(final CommonConfigEntry<T> entry, final FriendlyByteBuf buf) {
        T value = entry.codec().decode(buf);
        if (entry.synced() != null) {
            entry.synced().applyOverlay(value);
        } else {
            entry.overlay().set(value);
        }
    }

    private static <T> void encodeOverride(final ClientOverrideEntry<T> entry, final FriendlyByteBuf buf) {
        entry.codec().encode(entry.source().get(), buf);
    }

    private static <T> void decodeOverride(final ClientOverrideEntry<T> entry, final FriendlyByteBuf buf) {
        T value = entry.codec().decode(buf);
        if (entry.synced() != null) {
            entry.synced().applyOverlay(value);
        } else {
            entry.overlay().set(value);
        }
    }

    private static <T> void clearEntry(final CommonConfigEntry<T> entry) {
        if (entry.synced() != null) {
            entry.synced().clearOverlay();
        } else {
            entry.overlay().clear();
        }
    }

    private static <T> void clearEntry(final ClientOverrideEntry<T> entry) {
        if (entry.synced() != null) {
            entry.synced().clearOverlay();
        } else {
            entry.overlay().clear();
        }
    }

    /**
     * Re-sends the current config to all connected players.
     * Use this after a server-side config reload.
     *
     * @param server the running server
     *
     * @since 1.0.0-SNAPSHOT+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT+1.21.1")
    @ApiStatus.Internal
    public void broadcastToAll(final MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            sendTo(player);
        }
    }

    /**
     * Sends the current config to a single player.
     * Use this for targeted re-sync (e.g. after {@code /reload}).
     *
     * @param player the target player
     *
     * @since 1.0.0-SNAPSHOT+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT+1.21.1")
    @ApiStatus.Internal
    public void sendTo(final ServerPlayer player) {
        PacketChannels.sendToPlayer(player, configChannel, buildConfigPacket());
    }

    /**
     * Starts watching registered config files for changes. Called by the platform entrypoint
     * when the server has fully started. Each file change triggers {@code holder.load()},
     * which in turn fires the load listeners registered by {@link Builder#watchConfig}.
     *
     * @param server the running server; used to dispatch file-change work onto the server thread
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @ApiStatus.Internal
    public void startWatching(final MinecraftServer server) {
        if (watchEntries.isEmpty()) {
            return;
        }
        this.watchingServer = server;
        try {
            WatchService ws = FileSystems.getDefault().newWatchService();
            this.watchService = ws;
            for (WatchEntry entry : watchEntries) {
                Path dir = entry.configPath().getParent();
                if (dir != null) {
                    dir.register(ws, StandardWatchEventKinds.ENTRY_MODIFY);
                }
            }
            Thread t = new Thread(() -> watchLoop(server, ws), "lepidoptera-config-watcher");
            t.setDaemon(true);
            t.start();
        } catch (IOException e) {
            LepidopteraAPI.error("Failed to start config file watcher.", e);
        }
    }

    private void watchLoop(final MinecraftServer server, final WatchService ws) {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                WatchKey key = ws.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.kind() == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    }
                    @SuppressWarnings("unchecked") WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
                    Path changed = pathEvent.context();
                    for (WatchEntry entry : watchEntries) {
                        if (entry.configPath().getFileName().equals(changed)) {
                            server.execute(entry.holder()::load);
                            break;
                        }
                    }
                }
                if (!key.reset()) {
                    break;
                }
            }
        } catch (InterruptedException | ClosedWatchServiceException e) {
            LepidopteraAPI.debug("Config file watcher was closed unexpectedly", e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Stops the config file watcher. Called by the platform entrypoint when the server stops.
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @ApiStatus.Internal
    public void stopWatching() {
        this.watchingServer = null;
        final @Nullable WatchService ws = this.watchService;
        if (ws != null) {
            this.watchService = null;
            try {
                ws.close();
            } catch (IOException e) {
                LepidopteraAPI.debug("Config file watcher failed to close", e);
            }
        }
    }

    private void registerChannels() {
        // Register version channel as both C2S and S2C
        PacketChannels.registerServerChannel(versionChannel);
        PacketChannels.registerClientChannel(versionChannel);
        // Config is S2C only; config_req is C2S only
        PacketChannels.registerClientChannel(configChannel);
        PacketChannels.registerServerChannel(configReqChannel);

        // Server ready: send version then config to the joining player
        PacketChannels.registerServerReadyCallback(ctx -> {
            if (!ctx.canSend(versionChannel)) {
                LepidopteraAPI.debug("Failed to register client/server channel {}", versionChannel);
                return;
            }
            FriendlyByteBuf verBuf = new FriendlyByteBuf(Unpooled.buffer());
            verBuf.writeVarInt(networkVersion);
            ctx.send(versionChannel, verBuf);
            ctx.send(configChannel, buildConfigPacket());
        });

        // Server receives client's echoed version - disconnect on mismatch
        PacketChannels.registerServerReceiver(versionChannel, (ctx, buf) -> {
            int clientVersion = buf.readVarInt();
            if (clientVersion != networkVersion) {
                LepidopteraAPI.debug("Disconnecting client: server version mismatch!");
                //noinspection resource - We don't want to dispose the server instance!
                ctx.server()
                        .execute(() -> ctx.player().connection.disconnect(
                                Component.translatable(mismatchKey, networkVersion, clientVersion)));
            }
        });

        // Client receives server's version → echo back own version
        PacketChannels.registerClientReceiver(versionChannel, (ctx, buf) -> {
            buf.readVarInt(); // server version - not needed client-side
            FriendlyByteBuf reply = new FriendlyByteBuf(Unpooled.buffer());
            reply.writeVarInt(networkVersion);
            ctx.send(versionChannel, reply);
        });

        // Client receives config → decode into overlays
        PacketChannels.registerClientReceiver(configChannel, (ctx, buf) -> {
            decodeConfigPacket(buf);
        });

        // Server receives config_req → re-send config to that player
        PacketChannels.registerServerReceiver(configReqChannel, (ctx, buf) -> {
            ctx.send(configChannel, buildConfigPacket());
        });

        // Client disconnect → clear all overlays
        PacketChannels.registerClientDisconnectCallback(this::clearAllOverlays);
    }

    private FriendlyByteBuf buildConfigPacket() {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        // Snapshot enabled flags first to avoid a race between the flags pass and data pass
        boolean[] hasOverride = new boolean[clientOverrides.size()];
        for (int i = 0; i < clientOverrides.size(); i++) {
            hasOverride[i] = clientOverrides.get(i).enabled().getAsBoolean();
        }
        for (boolean flag : hasOverride) {
            buf.writeBoolean(flag);
        }
        for (CommonConfigEntry<?> entry : commonConfigs) {
            encodeCommon(entry, buf);
        }
        for (int i = 0; i < clientOverrides.size(); i++) {
            if (hasOverride[i]) {
                encodeOverride(clientOverrides.get(i), buf);
            }
        }
        return buf;
    }

    private void decodeConfigPacket(final FriendlyByteBuf buf) {
        boolean[] hasOverride = new boolean[clientOverrides.size()];
        for (int i = 0; i < clientOverrides.size(); i++) {
            hasOverride[i] = buf.readBoolean();
        }
        for (CommonConfigEntry<?> entry : commonConfigs) {
            decodeCommon(entry, buf);
        }
        for (int i = 0; i < clientOverrides.size(); i++) {
            if (hasOverride[i]) {
                decodeOverride(clientOverrides.get(i), buf);
            } else {
                clientOverrides.get(i).overlay().clear();
            }
        }
    }

    private void clearAllOverlays() {
        for (CommonConfigEntry<?> entry : commonConfigs) {
            clearEntry(entry);
        }
        for (ClientOverrideEntry<?> entry : clientOverrides) {
            clearEntry(entry);
        }
    }

    private record CommonConfigEntry<T>(ConfigCodec<T> codec,
                                        Supplier<T> source,
                                        ConfigOverlay<T> overlay,
                                        @Nullable SyncedConfig<T> synced) {
    }

    private record ClientOverrideEntry<T>(BooleanSupplier enabled,
                                          ConfigCodec<T> codec,
                                          Supplier<T> source,
                                          ConfigOverlay<T> overlay,
                                          @Nullable SyncedConfig<T> synced) {
    }

    private record WatchEntry(ConfigHolder<?> holder, Path configPath) {
    }

    /**
     * Fluent builder for {@link ServerConfigSync}.
     *
     * @since 1.0.0-SNAPSHOT+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT+1.21.1")
    public static final class Builder {

        private final String modId;
        private final List<CommonConfigEntry<?>> commonConfigs = new ArrayList<>();
        private final List<ClientOverrideEntry<?>> clientOverrides = new ArrayList<>();
        private final List<WatchEntry> watchEntries = new ArrayList<>();
        private int networkVersion = 1;
        private String mismatchKey = "";

        private Builder(final String modId) {
            this.modId = modId;
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
         * @since 1.0.0-SNAPSHOT+1.21.1
         */
        @ApiStatus.AvailableSince("1.0.0-SNAPSHOT+1.21.1")
        @Contract(value = "_, _ -> this", mutates = "this")
        public Builder networkVersion(final int version, final String mismatchKey) {
            this.networkVersion = version;
            this.mismatchKey = mismatchKey;
            return this;
        }

        /**
         * Registers a common config category to push to all clients on join.
         * May be called multiple times for separate config partitions.
         *
         * @param codec   encodes and decodes the config value
         * @param source  supplies the current server-side value at send time
         * @param overlay the client-side holder that receives the pushed value
         * @param <T>     the config type
         *
         * @return this builder
         *
         * @since 1.0.0-SNAPSHOT+1.21.1
         */
        @ApiStatus.AvailableSince("1.0.0-SNAPSHOT+1.21.1")
        @Contract(value = "_, _, _ -> this", mutates = "this")
        public <T> Builder commonConfig(final ConfigCodec<T> codec,
                                        final Supplier<T> source,
                                        final ConfigOverlay<T> overlay) {
            commonConfigs.add(new CommonConfigEntry<>(codec, source, overlay, null));
            return this;
        }

        /**
         * Registers an optional client-side config override.
         * Only included in the config packet when {@code enabled} returns {@code true}.
         *
         * @param enabled server-side flag: should this override be sent?
         * @param codec   encodes and decodes the override value
         * @param source  supplies the current override value at send time
         * @param overlay the client-side holder that receives the pushed value
         * @param <T>     the config type
         *
         * @return this builder
         *
         * @since 1.0.0-SNAPSHOT+1.21.1
         */
        @ApiStatus.AvailableSince("1.0.0-SNAPSHOT+1.21.1")
        @Contract(value = "_, _, _, _ -> this", mutates = "this")
        public <T> Builder clientOverride(final BooleanSupplier enabled,
                                          final ConfigCodec<T> codec,
                                          final Supplier<T> source,
                                          final ConfigOverlay<T> overlay) {
            clientOverrides.add(new ClientOverrideEntry<>(enabled, codec, source, overlay, null));
            return this;
        }

        /**
         * Registers a common config category using a {@link SyncedConfig}.
         * The overlay is created internally and wired into the sync pipeline.
         * Returns the {@link SyncedConfig} for runtime access via {@link SyncedConfig#get()}.
         *
         * @param codec       encodes and decodes the config value
         * @param localConfig the local config instance; used as the server-side source and
         *                    client-side fallback
         * @param <T>         the config type
         *
         * @return the {@link SyncedConfig} pairing the local config with the new overlay
         *
         * @since 1.0.0-SNAPSHOT+1.21.1
         */
        @ApiStatus.AvailableSince("1.0.0-SNAPSHOT+1.21.1")
        @Contract(value = "_, _ -> new", mutates = "this")
        public <T> SyncedConfig<T> commonConfig(final ConfigCodec<T> codec, final T localConfig) {
            SyncedConfig<T> synced = new SyncedConfig<>(localConfig, new ConfigOverlay<>());
            commonConfigs.add(new CommonConfigEntry<>(codec, synced::local, synced.overlay(), synced));
            return synced;
        }

        /**
         * Registers an optional client override using a {@link SyncedConfig}.
         * The overlay is created internally and wired into the sync pipeline.
         * Returns the {@link SyncedConfig} for runtime access via {@link SyncedConfig#get()}.
         *
         * @param enabled     server-side flag: should this override be sent?
         * @param codec       encodes and decodes the override value
         * @param localConfig the local config instance; used as the server-side source and
         *                    client-side fallback
         * @param <T>         the config type
         *
         * @return the {@link SyncedConfig} pairing the local config with the new overlay
         *
         * @since 1.0.0-SNAPSHOT+1.21.1
         */
        @ApiStatus.AvailableSince("1.0.0-SNAPSHOT+1.21.1")
        @Contract("_, _, _ -> new")
        public <T> SyncedConfig<T> clientOverride(final BooleanSupplier enabled,
                                                  final ConfigCodec<T> codec,
                                                  final T localConfig) {
            SyncedConfig<T> synced = new SyncedConfig<>(localConfig, new ConfigOverlay<>());
            clientOverrides.add(new ClientOverrideEntry<>(enabled, codec, synced::local, synced.overlay(), synced));
            return synced;
        }

        /**
         * Registers a config holder and its on-disk path for automatic server-to-client
         * broadcast on change.
         *
         * <p>Two events trigger a broadcast to all connected players:
         * <ul>
         *   <li><b>File change on disk</b> - detected by a daemon {@link WatchService} thread
         *       started in {@link ServerConfigSync#startWatching(MinecraftServer)}. When the file
         *       is modified, {@code holder.load()} is invoked on the server thread, which fires
         *       Cloth Config's load event and then broadcasts.</li>
         *   <li><b>In-game Cloth Config GUI save</b> - Cloth Config's GUI save calls
         *       {@code holder.save()}, which in turn calls {@code holder.load()} internally;
         *       the load listener then broadcasts.</li>
         * </ul>
         *
         * <p>The path must be the exact on-disk file path, including extension (e.g.
         * {@code Platform.getConfigFolder().resolve("mymod/config.yaml")}). It cannot be
         * auto-derived because the extension varies by serializer type (yaml, json5, toml…).</p>
         *
         * @param holder     the Cloth Config holder whose file should be watched
         * @param configPath the absolute path to the config file on disk
         *
         * @return this builder
         *
         * @since 1.0.0-SNAPSHOT+1.21.1
         */
        @ApiStatus.AvailableSince("1.0.0-SNAPSHOT+1.21.1")
        @Contract("_, _ -> this")
        public Builder watchConfig(final ConfigHolder<?> holder, final Path configPath) {
            watchEntries.add(new WatchEntry(holder, configPath));
            return this;
        }

        /**
         * Finalizes registration. Registers all packet channels and wires all handlers
         * via {@link PacketChannels}. Must be called once during mod initialization.
         *
         * @return the registered {@link ServerConfigSync} instance, which can be held
         * for later calls to {@link #broadcastToAll} or {@link #sendTo}
         *
         * @since 1.0.0-SNAPSHOT+1.21.1
         */
        @ApiStatus.AvailableSince("1.0.0-SNAPSHOT+1.21.1")
        @Contract("-> new")
        public ServerConfigSync register() {
            ServerConfigSync sync = new ServerConfigSync(this);
            sync.registerChannels();
            // Wire broadcast-on-load for each watched config holder.
            for (WatchEntry entry : sync.watchEntries) {
                entry.holder().registerLoadListener((h, cfg) -> {
                    @Nullable MinecraftServer server = sync.watchingServer;
                    if (server != null) {
                        sync.broadcastToAll(server);
                    }
                    return InteractionResult.SUCCESS;
                });
            }
            return sync;
        }
    }
}
