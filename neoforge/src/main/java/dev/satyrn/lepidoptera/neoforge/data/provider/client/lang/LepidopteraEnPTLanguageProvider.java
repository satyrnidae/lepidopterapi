package dev.satyrn.lepidoptera.neoforge.data.provider.client.lang;

import dev.satyrn.lepidoptera.LepidopteraAPI;
import dev.satyrn.lepidoptera.api.entity.ApiEntityTags;
import dev.satyrn.lepidoptera.api.item.ApiItemTags;
import dev.satyrn.lepidoptera.api.lang.FormattedStringBuilder;
import dev.satyrn.lepidoptera.api.lang.T9n;
import dev.satyrn.lepidoptera.config.LepidopteraConfig;
import dev.satyrn.lepidoptera.item.LepidopteraItems;
import dev.satyrn.lepidoptera.neoforge.api.provider.client.lang.ModLanguageProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.data.PackOutput;

public class LepidopteraEnPTLanguageProvider extends ModLanguageProvider {
    public LepidopteraEnPTLanguageProvider(PackOutput output) {
        super(LepidopteraAPI.class, output, "en_pt");
    }

    @Override
    protected void addTranslations() {
        this.add(T9n.item(LepidopteraItems.ALCHEMICAL_ALEMBIC), "Bottle o' the Chemist");
        this.add(T9n.item(LepidopteraItems.DEPLETED_ALEMBIC), "Empty Bottle o' the Chemist");

        this.add(T9n.gamerule("doAnimalStarvation"), "Critters Need t'Eat");
        this.add(T9n.gameruleDesc("doAnimalStarvation"), "Lepidoptera API: Yer critters be perishin' if they don't be eatin'!");

        this.add(T9n.netMsg(this.metadata, "versionMismatch"),
                "Yer Lepidoptera API ship don't match these seas. Sea: %s, Ship: %s");

        this.add(T9n.configTitle(LepidopteraConfig.class), "Lepidoptera API Tweaks");
        this.add(T9n.configOption(LepidopteraConfig.class, "enableAlchemicalAlembicRecipes"),
                "Let ye be craftin' the Bottle o' the Chemist");
        this.add(T9n.configTooltip(LepidopteraConfig.class, "enableAlchemicalAlembicRecipes", 0),
                "If ye be changing this, ye be havin' to drop anchor before ye set sail again, or ye can use /reload.");
        this.add(T9n.configTooltip(LepidopteraConfig.class, "enableAlchemicalAlembicRecipes", 1),
                "Yer ship receives this value when ye set sail on the seas.");
        this.add(T9n.configOption(LepidopteraConfig.class, "alchemicalAlembicCanShiftClick"),
                "Put the Bottle o' the Chemist on yer head right quick with Shift-Click");
        this.add(T9n.configTooltip(LepidopteraConfig.class, "alchemicalAlembicCanShiftClick", 0),
                "If this be changin', the seas will know.");
        this.add(T9n.configTooltip(LepidopteraConfig.class, "alchemicalAlembicCanShiftClick", 1),
                "Yer ship receives this value when ye set sail on the seas.");
        this.add(T9n.configOption(LepidopteraConfig.class, "showAlembicInCreativeTabs"),
                "Bottle o' the Chemist can be summoned by pirate captains");
        this.add(T9n.configTooltip(LepidopteraConfig.class, "showAlembicInCreativeTabs", 0),
                "Whether the Bottle o' the Chemist will show in the Pirate Captain's inventory.");
        this.add(T9n.configTooltip(LepidopteraConfig.class, "showAlembicInCreativeTabs", 1),
                new FormattedStringBuilder("If ye be changin' this, ye need to leave the seas entirely before settin' sail again.",
                        ChatFormatting.YELLOW));

        this.add(T9n.configOption(LepidopteraConfig.class, "demoInventorySize"), "[Demo] Size o' yer stores");
        this.add(T9n.configTooltip(LepidopteraConfig.class, "demoInventorySize", 0),
                "[Demo] This only be here t'show off the abilities o' this here widget.");
        this.add(T9n.configTooltip(LepidopteraConfig.class, "demoInventorySize", 1),
                "Format: WxH (e.g. 9x3). Max width: 9, max height: 4.");
        this.add(T9n.gui(LepidopteraAPI.class, "inventory_size", "width"), "Width: %s");
        this.add(T9n.gui(LepidopteraAPI.class, "inventory_size", "width_short"), "W: %s");
        this.add(T9n.gui(LepidopteraAPI.class, "inventory_size", "height"), "Height: %s");
        this.add(T9n.gui(LepidopteraAPI.class, "inventory_size", "height_short"), "H: %s");
        this.add(T9n.gui(LepidopteraAPI.class, "inventory_size", "summary"), "%s × %s");

        this.add(T9n.itemTag(ApiItemTags.BODY_EQUIPMENT), "Cuirass o' the animals");
        this.add(T9n.itemTag(ApiItemTags.CHEST_EQUIPMENT), "Cuirasses");
        this.add(T9n.itemTag(ApiItemTags.CHEST_EQUIPMENT_SHIFTABLE), "Cuirasses ye be puttin' on in a hurry");
        this.add(T9n.itemTag(ApiItemTags.FEET_EQUIPMENT), "Boots");
        this.add(T9n.itemTag(ApiItemTags.FEET_EQUIPMENT_SHIFTABLE), "Boots ye be puttin' on in a hurry");
        this.add(T9n.itemTag(ApiItemTags.HEAD_EQUIPMENT), "Hats");
        this.add(T9n.itemTag(ApiItemTags.HEAD_EQUIPMENT_SHIFTABLE), "Hats ye be puttin' on in a hurry");
        this.add(T9n.itemTag(ApiItemTags.LEGS_EQUIPMENT), "Skivvies");
        this.add(T9n.itemTag(ApiItemTags.LEGS_EQUIPMENT_SHIFTABLE), "Skivvies ye be puttin' on in a hurry");

        this.add(T9n.entityTypeTag(ApiEntityTags.TICKS_FOOD), "Animals which need be eatin'");
    }
}
