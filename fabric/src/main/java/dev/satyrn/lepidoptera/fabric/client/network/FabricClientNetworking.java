package dev.satyrn.lepidoptera.fabric.client.network;

import dev.satyrn.lepidoptera.api.network.ClientPlayContext;
import dev.satyrn.lepidoptera.api.network.PacketChannels;
import dev.satyrn.lepidoptera.api.network.PacketReadyCallback;
import dev.satyrn.lepidoptera.api.network.PacketReceiver;
import dev.satyrn.lepidoptera.fabric.network.FabricPacketChannelsImpl;
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
 * Client-only Fabric networking setup: registers S2C receivers and client ready callbacks.
 *
 * <p>Called from {@link dev.satyrn.lepidoptera.fabric.client.ClientEntrypoint#onInitializeClient}.
 * Kept in a client-only class so that no client-specific imports pollute the server-safe
 * {@link FabricPacketChannelsImpl}.</p>
 */
@Environment(EnvType.CLIENT)
public final class FabricClientNetworking {

    private FabricClientNetworking() {
    }

    /**
     * Registers all S2C receivers and the client ready callback hook.
     * Must be called during {@code ClientModInitializer.onInitializeClient}.
     */
    public static void init() {
        // Register a global receiver for each S2C channel
        for (ResourceLocation id : PacketChannels.CLIENT_CHANNELS) {
            ClientPlayNetworking.registerGlobalReceiver(ChannelPayload.typeFor(id), (payload, ctx) -> {
                List<PacketReceiver<ClientPlayContext>> receivers = PacketChannels.CLIENT_RECEIVERS.get(
                        payload.channelId());
                if (receivers == null || receivers.isEmpty()) {
                    return;
                }
                FabricClientPlayContext context = new FabricClientPlayContext(ctx.client(),
                        ctx.client().getConnection());
                FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.wrappedBuffer(payload.data()));
                for (PacketReceiver<ClientPlayContext> receiver : receivers) {
                    receiver.receive(context, buf);
                }
            });
        }

        // Fire client ready callbacks when joining a Lepidoptera-aware server
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (PacketChannels.CLIENT_READY_CALLBACKS.isEmpty()) {
                return;
            }
            FabricClientPlayContext context = new FabricClientPlayContext(client, handler);
            // Only fire if the server has at least one Lepidoptera channel registered
            boolean hasAnyChannel = PacketChannels.SERVER_CHANNELS.stream().anyMatch(context::canSend);
            if (!hasAnyChannel) {
                return;
            }
            for (PacketReadyCallback<ClientPlayContext> callback : PacketChannels.CLIENT_READY_CALLBACKS) {
                callback.onReady(context);
            }
        });

        // Fire client disconnect callbacks when leaving a server
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            for (Runnable cb : PacketChannels.CLIENT_DISCONNECT_CALLBACKS) {
                cb.run();
            }
        });
    }

    // -------------------------------------------------------------------------
    // Client play context implementation
    // -------------------------------------------------------------------------

    /**
     * {@link ClientPlayContext} backed by Fabric's client networking.
     */
    @Environment(EnvType.CLIENT)
    public static final class FabricClientPlayContext implements ClientPlayContext {

        private final Minecraft client;
        private final ClientPacketListener handler;

        public FabricClientPlayContext(Minecraft client, ClientPacketListener handler) {
            this.client = client;
            this.handler = handler;
        }

        public @Override Minecraft client() {
            return client;
        }

        public @Override ClientPacketListener handler() {
            return handler;
        }

        public @Override void send(ResourceLocation id, FriendlyByteBuf buf) {
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            ClientPlayNetworking.send(new ChannelPayload(id, bytes));
        }

        public @Override boolean canSend(ResourceLocation id) {
            CustomPacketPayload.Type<ChannelPayload> type = ChannelPayload.typeFor(id);
            return ClientPlayNetworking.canSend(type);
        }
    }
}
