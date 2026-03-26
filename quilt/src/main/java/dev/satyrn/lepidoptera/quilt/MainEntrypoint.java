package dev.satyrn.lepidoptera.quilt;

import dev.satyrn.lepidoptera.LepidopteraAPI;
import dev.satyrn.lepidoptera.api.network.PacketChannels;
import dev.satyrn.lepidoptera.api.network.PacketReadyCallback;
import dev.satyrn.lepidoptera.api.network.ServerPlayContext;
import dev.satyrn.lepidoptera.item.LepidopteraItems;
import dev.satyrn.lepidoptera.quilt.condition.AlchemicalAlembicRecipesCondition;
import dev.satyrn.lepidoptera.quilt.network.QuiltPacketChannelsImpl;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;

import static dev.satyrn.lepidoptera.LepidopteraAPI.info;

@SuppressWarnings("unused")
public class MainEntrypoint implements ModInitializer {

    public @Override void onInitialize() {
        info("Initializing Lepidoptera API for Quilt MC.");

        PacketChannels.setImpl(new QuiltPacketChannelsImpl());

        ResourceConditions.register(AlchemicalAlembicRecipesCondition.TYPE);

        LepidopteraAPI.INSTANCE.preInit();
        LepidopteraAPI.INSTANCE.init();
        LepidopteraAPI.INSTANCE.postInit();

        FuelRegistry.INSTANCE.add(LepidopteraItems.ALCHEMICAL_ALEMBIC.get(), 200);

        ServerLifecycleEvents.SERVER_STARTED.register(server -> LepidopteraAPI.INSTANCE.serverStarted(server));
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> LepidopteraAPI.INSTANCE.serverStopped());
        CommonLifecycleEvents.TAGS_LOADED.register((registryAccess, client) -> {
            if (!client) {
                LepidopteraAPI.INSTANCE.onTagsLoaded(registryAccess);
            }
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if (PacketChannels.SERVER_READY_CALLBACKS.isEmpty()) {
                return;
            }
            QuiltPacketChannelsImpl.QuiltServerPlayContext ctx = new QuiltPacketChannelsImpl.QuiltServerPlayContext(
                    handler.getPlayer(), server);
            boolean hasAnyChannel = PacketChannels.CLIENT_CHANNELS.stream().anyMatch(ctx::canSend);
            if (!hasAnyChannel) {
                return;
            }
            for (PacketReadyCallback<ServerPlayContext> callback : PacketChannels.SERVER_READY_CALLBACKS) {
                callback.onReady(ctx);
            }
        });

        info("Leptidoptera API for Quilt MC loaded.");
    }
}
