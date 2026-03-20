package dev.satyrn.lepidoptera.quilt.client;

import dev.satyrn.lepidoptera.client.LepidopteraAPIClient;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

import static dev.satyrn.lepidoptera.LepidopteraAPI.info;

@SuppressWarnings("unused")
@ClientOnly
public class ClientInitEntrypoint implements ClientModInitializer {

    @Override
    public void onInitializeClient(ModContainer modContainer) {
        info("Initializing client-sided code for Lepidoptera API for Fabric MC.");

        LepidopteraAPIClient.INSTANCE.preInit();
        LepidopteraAPIClient.INSTANCE.init();
        LepidopteraAPIClient.INSTANCE.postInit();

        info("Initialized client-sided code for Lepidoptera API for Fabric MC.");
    }
}
