package dev.satyrn.lepidoptera.fabric;

import dev.satyrn.lepidoptera.LepidopteraAPI;
import net.fabricmc.api.ModInitializer;

import static dev.satyrn.lepidoptera.LepidopteraAPI.info;

/**
 * ModMeta initializer for Fabric loader.
 */
public final class MainEntrypoint implements ModInitializer {
    @Override
    public void onInitialize() {
        info("Initializing Lepidoptera API for Fabric MC.");

        LepidopteraAPI.INSTANCE.preInit();
        LepidopteraAPI.INSTANCE.init();
        LepidopteraAPI.INSTANCE.postInit();

        info("Leptidoptera API for Fabric MC loaded.");
    }
}
