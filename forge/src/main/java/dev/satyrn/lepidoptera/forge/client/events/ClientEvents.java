package dev.satyrn.lepidoptera.forge.client.events;

import dev.satyrn.lepidoptera.client.LepidopteraAPIClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import static net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import static dev.satyrn.lepidoptera.LepidopteraAPI.info;
import static dev.satyrn.lepidoptera.LepidopteraAPI.MOD_ID;

/**
 * Forge client-side initialization events.
 */
@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT, modid = MOD_ID, bus = Bus.MOD)
public final class ClientEvents {
    private ClientEvents() {
        throw new AssertionError();
    }

    @SubscribeEvent
    static void onClientSetup(final FMLClientSetupEvent event) {
        // Do client init
        info("Initializing client-side code for Lepidoptera API for Forge");

        LepidopteraAPIClient.INSTANCE.preInit();
        LepidopteraAPIClient.INSTANCE.init();
        LepidopteraAPIClient.INSTANCE.postInit();

        info("Initialized client-side code for Lepidoptera API for Forge.");
    }
}
