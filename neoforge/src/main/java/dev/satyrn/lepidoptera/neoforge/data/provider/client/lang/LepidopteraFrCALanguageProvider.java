package dev.satyrn.lepidoptera.neoforge.data.provider.client.lang;

import dev.satyrn.lepidoptera.api.lang.T9n;
import dev.satyrn.lepidoptera.config.LepidopteraConfig;
import net.minecraft.data.PackOutput;

public class LepidopteraFrCALanguageProvider extends LepidopteraFrFRLanguageProvider {
    public LepidopteraFrCALanguageProvider(PackOutput output) {
        super(output, "fr_ca");
    }

    protected @Override void addAlchemicalAlembicCanShiftClick() {
        this.add(T9n.configOption(LepidopteraConfig.class, "alchemicalAlembicCanShiftClick"),
                "Maj-clic droit pour équiper l'alambic dans le casque");
    }
}
