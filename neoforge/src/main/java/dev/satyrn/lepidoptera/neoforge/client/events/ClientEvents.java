package dev.satyrn.lepidoptera.neoforge.client.events;

import dev.satyrn.lepidoptera.api.network.ClientPlayContext;
import dev.satyrn.lepidoptera.api.network.PacketChannels;
import dev.satyrn.lepidoptera.api.network.PacketReadyCallback;
import dev.satyrn.lepidoptera.api.network.PacketReceiver;
import dev.satyrn.lepidoptera.client.LepidopteraAPIClient;
import dev.satyrn.lepidoptera.config.LepidopteraConfig;
import dev.satyrn.lepidoptera.neoforge.network.NeoForgePacketChannelsImpl;
import dev.satyrn.lepidoptera.network.ChannelPayload;
import io.netty.buffer.Unpooled;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

import static dev.satyrn.lepidoptera.LepidopteraAPI.MOD_ID;
import static dev.satyrn.lepidoptera.LepidopteraAPI.info;

/**
 * NeoForge client-side initialization events.
 */
@EventBusSubscriber(value = Dist.CLIENT, modid = MOD_ID)
@OnlyIn(Dist.CLIENT)
public final class ClientEvents {
    private ClientEvents() {
        throw new AssertionError();
    }

    @SubscribeEvent
    static void onClientSetup(final FMLClientSetupEvent event) {
        info("Initializing client-side code for Lepidoptera API for NeoForge");

        ModLoadingContext.get()
                .registerExtensionPoint(IConfigScreenFactory.class,
                        () -> (mc, screen) -> AutoConfig.getConfigScreen(LepidopteraConfig.class, screen).get());

        LepidopteraAPIClient.INSTANCE.preInit();
        LepidopteraAPIClient.INSTANCE.init();
        LepidopteraAPIClient.INSTANCE.postInit();

        // Set the S2C client dispatcher so NeoForgePacketChannelsImpl can call client-only code.
        NeoForgePacketChannelsImpl.clientPayloadDispatcher = ClientEvents::dispatchClientPayload;

        // Client ready callbacks - ClientPlayerNetworkEvent.LoggingIn fires on the game bus.
        NeoForge.EVENT_BUS.addListener(ClientEvents::onClientLoggedIn);
        // Client disconnect callbacks
        NeoForge.EVENT_BUS.addListener(ClientEvents::onClientLoggedOut);

        info("Initialized client-side code for Lepidoptera API for NeoForge.");
    }

    @OnlyIn(Dist.CLIENT)
    private static void dispatchClientPayload(ChannelPayload payload, IPayloadContext ctx) {
        List<PacketReceiver<ClientPlayContext>> receivers = PacketChannels.CLIENT_RECEIVERS.get(payload.channelId());
        if (receivers == null || receivers.isEmpty()) {
            return;
        }
        Minecraft client = Minecraft.getInstance();
        NeoForgeClientPlayContext context = new NeoForgeClientPlayContext(client);
        // Read the buf immediately on the IO thread (payload.data() is a stable byte[]).
        // Dispatch receiver callbacks to the game thread so any downstream state mutations
        // (e.g. onApply callbacks from SyncedConfig) run safely.
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.wrappedBuffer(payload.data()));
        ctx.enqueueWork(() -> {
            for (PacketReceiver<ClientPlayContext> receiver : receivers) {
                receiver.receive(context, buf);
            }
        });
    }

    @OnlyIn(Dist.CLIENT)
    private static void onClientLoggedOut(net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.LoggingOut event) {
        for (Runnable cb : PacketChannels.CLIENT_DISCONNECT_CALLBACKS) {
            cb.run();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void onClientLoggedIn(net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.LoggingIn event) {
        if (PacketChannels.CLIENT_READY_CALLBACKS.isEmpty()) {
            return;
        }
        Minecraft client = Minecraft.getInstance();
        NeoForgeClientPlayContext context = new NeoForgeClientPlayContext(client);
        boolean hasAnyChannel = PacketChannels.SERVER_CHANNELS.stream().anyMatch(context::canSend);
        if (!hasAnyChannel) {
            return;
        }
        for (PacketReadyCallback<ClientPlayContext> callback : PacketChannels.CLIENT_READY_CALLBACKS) {
            callback.onReady(context);
        }
    }

    // -------------------------------------------------------------------------
    // Client play context implementation (client-only, defined here to keep
    // client-only imports out of NeoForgePacketChannelsImpl)
    // -------------------------------------------------------------------------

    @OnlyIn(Dist.CLIENT)
    static final class NeoForgeClientPlayContext implements ClientPlayContext {

        private final Minecraft client;

        NeoForgeClientPlayContext(Minecraft client) {
            this.client = client;
        }

        @Override
        public Minecraft client() {
            return client;
        }

        @Override
        public ClientPacketListener handler() {
            return client.getConnection();
        }

        @Override
        public void send(ResourceLocation id, FriendlyByteBuf buf) {
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            ClientPacketListener conn = client.getConnection();
            if (conn != null) {
                conn.send(new net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket(
                        new ChannelPayload(id, bytes)));
            }
        }

        @Override
        public boolean canSend(ResourceLocation id) {
            // On NeoForge, if we have a connection and the channel was registered, assume supported.
            return client.getConnection() != null && PacketChannels.SERVER_CHANNELS.contains(id);
        }
    }
}
