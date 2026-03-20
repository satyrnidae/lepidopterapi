package dev.satyrn.lepidoptera.neoforge;

import dev.satyrn.lepidoptera.LepidopteraAPI;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

import static dev.satyrn.lepidoptera.LepidopteraAPI.info;
import static dev.satyrn.lepidoptera.LepidopteraAPI.MOD_ID;

@Mod(MOD_ID)
public class NeoForgeMod {
    public NeoForgeMod(final IEventBus modEventBus) {
        info("Initializing Lepidoptera API for NeoForge");

        LepidopteraAPI.INSTANCE.preInit();
        LepidopteraAPI.INSTANCE.init();

        info("Lepidoptera API for NeoForge initialized.");
    }
}
