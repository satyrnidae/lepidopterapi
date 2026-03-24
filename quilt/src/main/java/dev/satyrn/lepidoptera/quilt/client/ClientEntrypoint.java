package dev.satyrn.lepidoptera.quilt.client;

import dev.satyrn.lepidoptera.client.LepidopteraAPIClient;
import dev.satyrn.lepidoptera.quilt.client.network.play.QuiltClientNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import static dev.satyrn.lepidoptera.LepidopteraAPI.info;

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public class ClientEntrypoint implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        info("Initializing client-sided code for Lepidoptera API for Quilt MC.");

        LepidopteraAPIClient.INSTANCE.preInit();
        LepidopteraAPIClient.INSTANCE.init();
        LepidopteraAPIClient.INSTANCE.postInit();

        // Register S2C receivers and client ready callback hook.
        QuiltClientNetworking.init();

        info("Initialized client-sided code for Lepidoptera API for Quilt MC.");
    }
}
