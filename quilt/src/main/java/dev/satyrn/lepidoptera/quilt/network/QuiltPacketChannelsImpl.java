package dev.satyrn.lepidoptera.quilt.network;

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
 * Quilt implementation of {@link PacketChannelsImpl}.
 *
 * <p>Uses Fabric API networking ({@code net.fabricmc.fabric.api.networking.v1}) for all channel
 * registration and sends - QSL/QFAPI is discontinued.</p>
 */
public final class QuiltPacketChannelsImpl implements PacketChannelsImpl {

    public @Override void onServerChannelRegistered(ResourceLocation id) {
        PayloadTypeRegistry.playC2S().register(ChannelPayload.typeFor(id), ChannelPayload.codecFor(id));
        ServerPlayNetworking.registerGlobalReceiver(ChannelPayload.typeFor(id), (payload, ctx) -> {
            List<PacketReceiver<ServerPlayContext>> receivers = PacketChannels.SERVER_RECEIVERS.get(
                    payload.channelId());
            if (receivers == null || receivers.isEmpty()) {
                return;
            }
            QuiltServerPlayContext context = new QuiltServerPlayContext(ctx.player(), ctx.server());
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.wrappedBuffer(payload.data()));
            for (PacketReceiver<ServerPlayContext> receiver : receivers) {
                receiver.receive(context, buf);
            }
        });
    }

    public @Override void onClientChannelRegistered(ResourceLocation id) {
        PayloadTypeRegistry.playS2C().register(ChannelPayload.typeFor(id), ChannelPayload.codecFor(id));
    }

    public @Override void sendToPlayer(ServerPlayer player, ResourceLocation id, FriendlyByteBuf buf) {
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        ServerPlayNetworking.send(player, new ChannelPayload(id, bytes));
    }

    // -------------------------------------------------------------------------
    // Server play context implementation
    // -------------------------------------------------------------------------

    public record QuiltServerPlayContext(ServerPlayer player, MinecraftServer server) implements ServerPlayContext {

        public @Override ServerGamePacketListenerImpl handler() {
            return player.connection;
        }

        public @Override void send(ResourceLocation id, FriendlyByteBuf buf) {
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            ServerPlayNetworking.send(player, new ChannelPayload(id, bytes));
        }

        public @Override boolean canSend(ResourceLocation id) {
            CustomPacketPayload.Type<ChannelPayload> type = ChannelPayload.typeFor(id);
            return ServerPlayNetworking.canSend(player, type);
        }
    }
}
