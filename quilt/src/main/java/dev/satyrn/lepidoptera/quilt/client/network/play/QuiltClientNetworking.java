package dev.satyrn.lepidoptera.quilt.client.network.play;

import dev.satyrn.lepidoptera.api.network.ClientPlayContext;
import dev.satyrn.lepidoptera.api.network.PacketChannels;
import dev.satyrn.lepidoptera.api.network.PacketReadyCallback;
import dev.satyrn.lepidoptera.api.network.PacketReceiver;
import dev.satyrn.lepidoptera.network.ChannelPayload;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * Client-only Quilt networking setup, mirroring
 * {@link dev.satyrn.lepidoptera.fabric.client.network.play.FabricClientNetworking}.
 *
 * <p>Uses Fabric API networking ({@code net.fabricmc.fabric.api.client.networking.v1}) —
 * QSL/QFAPI is discontinued.</p>
 */
@Environment(EnvType.CLIENT)
public final class QuiltClientNetworking {

    private QuiltClientNetworking() {}

    /**
     * Registers all S2C receivers and the client ready callback hook.
     * Must be called during {@code ClientModInitializer.onInitializeClient}.
     */
    public static void init() {
        for (ResourceLocation id : PacketChannels.CLIENT_CHANNELS) {
            ClientPlayNetworking.registerGlobalReceiver(ChannelPayload.typeFor(id), (payload, ctx) -> {
                List<PacketReceiver<ClientPlayContext>> receivers =
                        PacketChannels.CLIENT_RECEIVERS.get(payload.channelId());
                if (receivers == null || receivers.isEmpty()) return;
                QuiltClientPlayContext context =
                        new QuiltClientPlayContext(ctx.client(), ctx.client().getConnection());
                FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.wrappedBuffer(payload.data()));
                for (PacketReceiver<ClientPlayContext> receiver : receivers) {
                    receiver.receive(context, buf);
                }
            });
        }

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (PacketChannels.CLIENT_READY_CALLBACKS.isEmpty()) return;
            QuiltClientPlayContext context = new QuiltClientPlayContext(client, handler);
            boolean hasAnyChannel = PacketChannels.SERVER_CHANNELS.stream()
                    .anyMatch(context::canSend);
            if (!hasAnyChannel) return;
            for (PacketReadyCallback<ClientPlayContext> callback : PacketChannels.CLIENT_READY_CALLBACKS) {
                callback.onReady(context);
            }
        });

        // Fire client disconnect callbacks when leaving a server
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            for (Runnable cb : PacketChannels.CLIENT_DISCONNECT_CALLBACKS) cb.run();
        });
    }

    @Environment(EnvType.CLIENT)
    public static final class QuiltClientPlayContext implements ClientPlayContext {

        private final Minecraft client;
        private final ClientPacketListener handler;

        public QuiltClientPlayContext(Minecraft client, ClientPacketListener handler) {
            this.client = client;
            this.handler = handler;
        }

        @Override
        public Minecraft client() {
            return client;
        }

        @Override
        public ClientPacketListener handler() {
            return handler;
        }

        @Override
        public void send(ResourceLocation id, FriendlyByteBuf buf) {
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            ClientPlayNetworking.send(new ChannelPayload(id, bytes));
        }

        @Override
        public boolean canSend(ResourceLocation id) {
            CustomPacketPayload.Type<ChannelPayload> type = ChannelPayload.typeFor(id);
            return ClientPlayNetworking.canSend(type);
        }
    }
}
