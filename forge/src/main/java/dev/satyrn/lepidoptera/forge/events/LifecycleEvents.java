package dev.satyrn.lepidoptera.forge.events;

import dev.satyrn.lepidoptera.LepidopteraAPI;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import static dev.satyrn.lepidoptera.LepidopteraAPI.info;
import static dev.satyrn.lepidoptera.LepidopteraAPI.MOD_ID;

@EventBusSubscriber(modid = MOD_ID, bus = Bus.MOD)
public class LifecycleEvents {
    private LifecycleEvents() {
        throw new AssertionError();
    }

    @SubscribeEvent
    static void onCommonSetup(final FMLCommonSetupEvent event) {
        info("Lepidoptera API for Forge entered the post-initialization state.");

        LepidopteraAPI.INSTANCE.postInit();

        info("Lepidopetera API for Forge post-initialization complete.");
    }
}
