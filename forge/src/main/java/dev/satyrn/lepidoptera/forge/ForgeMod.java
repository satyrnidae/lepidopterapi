package dev.satyrn.lepidoptera.forge;

import dev.architectury.platform.forge.EventBuses;
import dev.satyrn.lepidoptera.LepidopteraAPI;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static dev.satyrn.lepidoptera.LepidopteraAPI.info;
import static dev.satyrn.lepidoptera.LepidopteraAPI.MOD_ID;

@Mod(MOD_ID)
public class ForgeMod {
    public ForgeMod(final FMLJavaModLoadingContext context) {
        EventBuses.registerModEventBus(MOD_ID, context.getModEventBus());

        info("Initializing Lepidoptera API for Forge");

        LepidopteraAPI.INSTANCE.preInit();
        LepidopteraAPI.INSTANCE.init();

        info("Lepidoptera API for Forge initialized.");
    }
}
