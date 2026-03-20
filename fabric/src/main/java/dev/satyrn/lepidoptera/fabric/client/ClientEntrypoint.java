package dev.satyrn.lepidoptera.fabric.client;

import dev.satyrn.lepidoptera.client.LepidopteraAPIClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import static dev.satyrn.lepidoptera.LepidopteraAPI.info;

/**
 * Client side mod initializer for Fabric MC
 */
@Environment(EnvType.CLIENT)
public final class ClientEntrypoint implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        info("Initializing client-sided code for Lepidoptera API for Fabric MC.");

        LepidopteraAPIClient.INSTANCE.preInit();
        LepidopteraAPIClient.INSTANCE.init();
        LepidopteraAPIClient.INSTANCE.postInit();

        info("Initialized client-sided code for Lepidoptera API for Fabric MC.");
    }
}
