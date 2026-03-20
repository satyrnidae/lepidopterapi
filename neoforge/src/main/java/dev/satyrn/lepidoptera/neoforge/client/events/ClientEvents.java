package dev.satyrn.lepidoptera.neoforge.client.events;

import dev.satyrn.lepidoptera.client.LepidopteraAPIClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

import static dev.satyrn.lepidoptera.LepidopteraAPI.info;
import static dev.satyrn.lepidoptera.LepidopteraAPI.MOD_ID;

/**
 * NeoForge client-side initialization events.
 */
@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT, modid = MOD_ID)
public final class ClientEvents {
    private ClientEvents() {
        throw new AssertionError();
    }

    @SubscribeEvent
    static void onClientSetup(final FMLClientSetupEvent event) {
        info("Initializing client-side code for Lepidoptera API for NeoForge");

        LepidopteraAPIClient.INSTANCE.preInit();
        LepidopteraAPIClient.INSTANCE.init();
        LepidopteraAPIClient.INSTANCE.postInit();

        info("Initialized client-side code for Lepidoptera API for NeoForge.");
    }
}
