package dev.satyrn.lepidoptera.quilt;

import dev.satyrn.lepidoptera.LepidopteraAPI;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

import static dev.satyrn.lepidoptera.LepidopteraAPI.info;

@SuppressWarnings("unused")
public class InitEntrypoint implements ModInitializer {

    @Override
    public void onInitialize(ModContainer modContainer) {
        info("Initializing Lepidoptera API for Quilt MC.");

        LepidopteraAPI.INSTANCE.preInit();
        LepidopteraAPI.INSTANCE.init();
        LepidopteraAPI.INSTANCE.postInit();

        info("Leptidoptera API for Quilt MC loaded.");
    }
}
