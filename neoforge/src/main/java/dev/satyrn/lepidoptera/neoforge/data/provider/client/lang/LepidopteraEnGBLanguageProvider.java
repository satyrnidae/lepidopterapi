package dev.satyrn.lepidoptera.neoforge.data.provider.client.lang;

import dev.satyrn.lepidoptera.api.item.ApiItemTags;
import dev.satyrn.lepidoptera.api.lang.T9n;
import net.minecraft.data.PackOutput;

public class LepidopteraEnGBLanguageProvider extends LepidopteraEnUSLanguageProvider {
    public LepidopteraEnGBLanguageProvider(PackOutput output) {
        this(output, "en_gb");
    }

    public LepidopteraEnGBLanguageProvider(PackOutput output, String locale) {
        super(output, locale);
    }

    @Override
    protected void addBodyEquipmentTag() {
        this.add(T9n.itemTag(ApiItemTags.BODY_EQUIPMENT), "Animal Armour");
    }
}
