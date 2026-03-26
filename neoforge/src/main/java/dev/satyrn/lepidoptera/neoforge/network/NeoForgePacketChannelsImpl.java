package dev.satyrn.lepidoptera.neoforge.network;

import dev.satyrn.lepidoptera.api.network.PacketChannels;
import dev.satyrn.lepidoptera.api.network.PacketReceiver;
import dev.satyrn.lepidoptera.api.network.ServerPlayContext;
import dev.satyrn.lepidoptera.network.ChannelPayload;
import dev.satyrn.lepidoptera.network.PacketChannelsImpl;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * NeoForge implementation of {@link PacketChannelsImpl}.
 *
 * <p>Channel IDs collected via {@link #onServerChannelRegistered} and
 * {@link #onClientChannelRegistered} are queued locally. They are flushed to NeoForge during
 * {@link RegisterPayloadHandlersEvent}, which fires on the mod event bus after all {@code @Mod}
 * constructors have run. This guarantees that dependent mods can register channels during their
 * own {@code @Mod} constructor before the flush occurs.</p>
 *
 * <p>S2C client receiver wiring is done by a client-only subscriber in
 * {@link dev.satyrn.lepidoptera.neoforge.client.events.ClientEvents}, which sets
 * {@link #clientPayloadDispatcher} before any packets are received.</p>
 */
public final class NeoForgePacketChannelsImpl implements PacketChannelsImpl {

    private final List<ResourceLocation> pendingC2S = new ArrayList<>();
    private final List<ResourceLocation> pendingS2C = new ArrayList<>();

    /**
     * Client-side dispatcher for S2C payloads. Set from a client-only class so that
     * client-specific imports ({@code Minecraft}, {@code ClientPlayNetworking}) are never
     * loaded on the dedicated server.
     *
     * <p>The handler receives any S2C {@link ChannelPayload} and is responsible for dispatching
     * to the appropriate {@link PacketChannels#CLIENT_RECEIVERS}. The field is {@code volatile}
     * so that the write from {@code FMLClientSetupEvent} is visible to the Netty IO thread.</p>
     */
    public static volatile @Nullable ClientPayloadDispatcher clientPayloadDispatcher = null;

    /**
     * Dispatcher interface - does not reference any client-only classes, so it is safe to
     * declare in this server-visible class.
     */
    @FunctionalInterface
    public interface ClientPayloadDispatcher {
        /**
         * Dispatches an incoming S2C payload to registered client receivers.
         *
         * @param payload the received payload
         * @param context the NeoForge payload context
         */
        void dispatch(ChannelPayload payload, IPayloadContext context);
    }

    /**
     * Subscribes this instance to {@link RegisterPayloadHandlersEvent} on the given mod event bus.
     *
     * @param modEventBus the mod event bus passed to the {@code @Mod} constructor
     */
    public void register(IEventBus modEventBus) {
        modEventBus.addListener(this::onRegisterPayloads);
    }

    public @Override void onServerChannelRegistered(ResourceLocation id) {
        pendingC2S.add(id);
    }

    public @Override void onClientChannelRegistered(ResourceLocation id) {
        pendingS2C.add(id);
    }

    public @Override void sendToPlayer(ServerPlayer player, ResourceLocation id, FriendlyByteBuf buf) {
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        player.connection.send(new ClientboundCustomPayloadPacket(new ChannelPayload(id, bytes)));
    }

    @SubscribeEvent
    public void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        // Bidirectional channels appear in both lists (e.g. version handshake).
        // NeoForge does not allow registering the same payload type via both playToServer
        // and playToClient - use playBidirectional and detect direction via player type.
        for (ResourceLocation id : pendingC2S) {
            if (pendingS2C.contains(id)) {
                registrar.playBidirectional(ChannelPayload.typeFor(id), ChannelPayload.codecFor(id), (payload, ctx) -> {
                    if (ctx.player() instanceof ServerPlayer serverPlayer) {
                        // C2S path
                        List<PacketReceiver<ServerPlayContext>> receivers = PacketChannels.SERVER_RECEIVERS.get(
                                payload.channelId());
                        if (receivers == null || receivers.isEmpty()) {
                            return;
                        }
                        NeoForgeServerPlayContext context = new NeoForgeServerPlayContext(serverPlayer);
                        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.wrappedBuffer(payload.data()));
                        for (PacketReceiver<ServerPlayContext> receiver : receivers) {
                            receiver.receive(context, buf);
                        }
                    } else {
                        // S2C path - delegate to client-only dispatcher
                        @Nullable ClientPayloadDispatcher dispatcher = clientPayloadDispatcher;
                        if (dispatcher != null) {
                            dispatcher.dispatch(payload, ctx);
                        }
                    }
                });
            } else {
                registrar.playToServer(ChannelPayload.typeFor(id), ChannelPayload.codecFor(id), (payload, ctx) -> {
                    List<PacketReceiver<ServerPlayContext>> receivers = PacketChannels.SERVER_RECEIVERS.get(
                            payload.channelId());
                    if (receivers == null || receivers.isEmpty()) {
                        return;
                    }
                    if (!(ctx.player() instanceof ServerPlayer serverPlayer)) {
                        return;
                    }
                    NeoForgeServerPlayContext context = new NeoForgeServerPlayContext(serverPlayer);
                    FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.wrappedBuffer(payload.data()));
                    for (PacketReceiver<ServerPlayContext> receiver : receivers) {
                        receiver.receive(context, buf);
                    }
                });
            }
        }

        // Register S2C-only channels (not already handled as bidirectional).
        for (ResourceLocation id : pendingS2C) {
            if (!pendingC2S.contains(id)) {
                registrar.playToClient(ChannelPayload.typeFor(id), ChannelPayload.codecFor(id), (payload, ctx) -> {
                    @Nullable ClientPayloadDispatcher dispatcher = clientPayloadDispatcher;
                    if (dispatcher != null) {
                        dispatcher.dispatch(payload, ctx);
                    }
                });
            }
        }
    }

    // -------------------------------------------------------------------------
    // Server play context implementation
    // -------------------------------------------------------------------------

    /**
     * {@link ServerPlayContext} backed by NeoForge's networking infrastructure.
     */
    public static final class NeoForgeServerPlayContext implements ServerPlayContext {

        private final ServerPlayer player;

        public NeoForgeServerPlayContext(ServerPlayer player) {
            this.player = player;
        }

        public @Override MinecraftServer server() {
            return player.server;
        }

        public @Override ServerPlayer player() {
            return player;
        }

        public @Override ServerGamePacketListenerImpl handler() {
            return player.connection;
        }

        public @Override void send(ResourceLocation id, FriendlyByteBuf buf) {
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            player.connection.send(new ClientboundCustomPayloadPacket(new ChannelPayload(id, bytes)));
        }

        public @Override boolean canSend(ResourceLocation id) {
            // On NeoForge, all clients are mod-aware; channel availability tracks registration.
            // A more precise check (payload setup negotiation) can be added later.
            return player.connection.isAcceptingMessages() && PacketChannels.CLIENT_CHANNELS.contains(id);
        }
    }
}
