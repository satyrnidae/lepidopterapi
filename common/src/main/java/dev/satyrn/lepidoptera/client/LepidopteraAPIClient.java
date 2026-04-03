package dev.satyrn.lepidoptera.client;

import dev.satyrn.lepidoptera.LepidopteraAPI;
import dev.satyrn.lepidoptera.api.LepidopteraMod;
import dev.satyrn.lepidoptera.api.client.config.InventorySizeEntry;
import dev.satyrn.lepidoptera.api.client.config.TransformEntry;
import dev.satyrn.lepidoptera.api.compatibility.Compatibility;
import dev.satyrn.lepidoptera.api.config.InventorySizeField;
import dev.satyrn.lepidoptera.api.config.TransformField;
import dev.satyrn.lepidoptera.api.config.transform.Transformation;
import dev.satyrn.lepidoptera.config.LepidopteraConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Client-sided init
 *
 * <p><b>Internal - Not for External Use</b></p>
 */
@Environment(EnvType.CLIENT)
public class LepidopteraAPIClient implements LepidopteraMod {
    public static LepidopteraMod INSTANCE = new LepidopteraAPIClient();

    private LepidopteraAPIClient() {
    }

    @Override
    public void postInit() {
        // Register the InventorySize GUI entry type provider for the demo field in LepidopteraConfig.
        // Downstream mods should make their own equivalent call for their own config classes.
        AutoConfig.getGuiRegistry(LepidopteraConfig.class)
                .registerPredicateProvider(InventorySizeEntry.TYPE_PROVIDER, field -> field.getType() == String.class &&
                        field.isAnnotationPresent(InventorySizeField.class));

        // Register the 3D transform entry provider for @Transformation fields annotated with @TransformField.
        // Downstream mods should make their own equivalent call for their own config classes.
        AutoConfig.getGuiRegistry(LepidopteraConfig.class)
                .registerPredicateProvider(TransformEntry.TYPE_PROVIDER,
                        field -> field.getType().isAnnotationPresent(Transformation.class)
                              && field.isAnnotationPresent(TransformField.class));

        LepidopteraAPI.debug("POST-INIT: Begin client-sided init for compatibility layer");
        Compatibility.clientPreInit();
        Compatibility.clientInit();
        Compatibility.clientPostInit();
        LepidopteraAPI.debug("POST-INIT: Client-sided compatibility layer init complete");

    }
}
