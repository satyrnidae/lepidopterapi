package dev.satyrn.lepidoptera.neoforge.events;

import dev.satyrn.lepidoptera.LepidopteraAPI;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import static dev.satyrn.lepidoptera.LepidopteraAPI.info;
import static dev.satyrn.lepidoptera.LepidopteraAPI.MOD_ID;

@EventBusSubscriber(modid = MOD_ID)
public class LifecycleEvents {
    private LifecycleEvents() {
        throw new AssertionError();
    }

    @SubscribeEvent
    static void onCommonSetup(final FMLCommonSetupEvent event) {
        info("Lepidoptera API for NeoForge entered the post-initialization state.");

        LepidopteraAPI.INSTANCE.postInit();

        info("Lepidoptera API for NeoForge post-initialization complete.");
    }
}
