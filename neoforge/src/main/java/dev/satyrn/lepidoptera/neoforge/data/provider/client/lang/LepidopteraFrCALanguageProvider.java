package dev.satyrn.lepidoptera.neoforge.data.provider.client.lang;

import dev.satyrn.lepidoptera.api.item.ApiItemTags;
import dev.satyrn.lepidoptera.api.lang.T9n;
import dev.satyrn.lepidoptera.config.LepidopteraConfig;
import net.minecraft.data.PackOutput;

public class LepidopteraFrCALanguageProvider extends LepidopteraFrFRLanguageProvider {
    public LepidopteraFrCALanguageProvider(PackOutput output) {
        super(output, "fr_ca");
    }

    @Override
    protected void addLegsEquipmentShiftable() {
        this.add(ApiItemTags.LEGS_EQUIPMENT_SHIFTABLE, "Jambières à maj-clic");
    }

    @Override
    protected void addHeadEquipmentShiftable() {
        this.add(ApiItemTags.HEAD_EQUIPMENT_SHIFTABLE, "Casques à maj-clic");
    }

    @Override
    protected void addFeetEquipmentShiftable() {
        this.add(ApiItemTags.FEET_EQUIPMENT_SHIFTABLE, "Bottes à maj-clic");
    }

    @Override
    protected void addChestEquipmentShiftable() {
        this.add(ApiItemTags.CHEST_EQUIPMENT_SHIFTABLE, "Plastrons à maj-clic");
    }

    @Override
    protected void addAlchemicalAlembicCanShiftClick() {
        this.addConfigOption(LepidopteraConfig.class, "alchemicalAlembicCanShiftClick", "Maj-clic droit pour équiper l'alambic dans le casque");
    }
}
