package dev.satyrn.lepidoptera.fabric.network.play;

import dev.satyrn.lepidoptera.api.network.PacketChannels;
import dev.satyrn.lepidoptera.api.network.PacketReceiver;
import dev.satyrn.lepidoptera.api.network.ServerPlayContext;
import dev.satyrn.lepidoptera.network.ChannelPayload;
import dev.satyrn.lepidoptera.network.PacketChannelsImpl;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import java.util.List;

/**
 * Fabric implementation of {@link PacketChannelsImpl}.
 *
 * <p>C2S channels are registered immediately via {@link PayloadTypeRegistry} and
 * {@link ServerPlayNetworking}. S2C channels are registered with {@link PayloadTypeRegistry}
 * here; client-side receiver wiring and ready callbacks are done in
 * {@link dev.satyrn.lepidoptera.fabric.client.ClientEntrypoint}.</p>
 */
public final class FabricPacketChannelsImpl implements PacketChannelsImpl {

    @Override
    public void onServerChannelRegistered(ResourceLocation id) {
        // Register payload type for C2S
        PayloadTypeRegistry.playC2S().register(ChannelPayload.typeFor(id), ChannelPayload.codecFor(id));
        // Register server-side receiver - handler does a live lookup so receivers registered
        // after this call are still picked up
        ServerPlayNetworking.registerGlobalReceiver(ChannelPayload.typeFor(id), (payload, ctx) -> {
            List<PacketReceiver<ServerPlayContext>> receivers =
                    PacketChannels.SERVER_RECEIVERS.get(payload.channelId());
            if (receivers == null || receivers.isEmpty()) return;
            FabricServerPlayContext context = new FabricServerPlayContext(ctx.player(), ctx.server());
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.wrappedBuffer(payload.data()));
            for (PacketReceiver<ServerPlayContext> receiver : receivers) {
                receiver.receive(context, buf);
            }
        });
    }

    @Override
    public void onClientChannelRegistered(ResourceLocation id) {
        // Register payload type for S2C; client receiver wiring done in ClientEntrypoint
        PayloadTypeRegistry.playS2C().register(ChannelPayload.typeFor(id), ChannelPayload.codecFor(id));
    }

    @Override
    public void sendToPlayer(ServerPlayer player, ResourceLocation id, FriendlyByteBuf buf) {
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        ServerPlayNetworking.send(player, new ChannelPayload(id, bytes));
    }

    // -------------------------------------------------------------------------
    // Server play context implementation
    // -------------------------------------------------------------------------

    /**
     * {@link ServerPlayContext} backed by Fabric's networking context.
     */
    public record FabricServerPlayContext(ServerPlayer player, MinecraftServer server) implements ServerPlayContext {

        @Override
        public MinecraftServer server() {
            return server;
        }

        @Override
        public ServerPlayer player() {
            return player;
        }

        @Override
        public ServerGamePacketListenerImpl handler() {
            return player.connection;
        }

        @Override
        public void send(ResourceLocation id, FriendlyByteBuf buf) {
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            ServerPlayNetworking.send(player, new ChannelPayload(id, bytes));
        }

        @Override
        public boolean canSend(ResourceLocation id) {
            CustomPacketPayload.Type<ChannelPayload> type = ChannelPayload.typeFor(id);
            return ServerPlayNetworking.canSend(player, type);
        }
    }
}
