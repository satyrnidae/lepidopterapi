package dev.satyrn.lepidoptera.neoforge.data.provider.client.lang;

import dev.satyrn.lepidoptera.LepidopteraAPI;
import dev.satyrn.lepidoptera.api.config.ToolEffectivenessTier;
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
        this.add(LepidopteraItems.ALCHEMICAL_ALEMBIC, "Bottle o' the Chemist");
        this.add(LepidopteraItems.DEPLETED_ALEMBIC, "Empty Bottle o' the Chemist");

        this.add(ApiItemTags.BODY_EQUIPMENT, "Cuirass o' the animals");
        this.add(ApiItemTags.CHEST_EQUIPMENT, "Cuirasses");
        this.add(ApiItemTags.CHEST_EQUIPMENT_SHIFTABLE, "Cuirasses ye be puttin' on in a hurry");
        this.add(ApiItemTags.FEET_EQUIPMENT, "Boots");
        this.add(ApiItemTags.FEET_EQUIPMENT_SHIFTABLE, "Boots ye be puttin' on in a hurry");
        this.add(ApiItemTags.HEAD_EQUIPMENT, "Hats");
        this.add(ApiItemTags.HEAD_EQUIPMENT_SHIFTABLE, "Hats ye be puttin' on in a hurry");
        this.add(ApiItemTags.LEGS_EQUIPMENT, "Skivvies");
        this.add(ApiItemTags.LEGS_EQUIPMENT_SHIFTABLE, "Skivvies ye be puttin' on in a hurry");
        this.add(ApiEntityTags.TICKS_FOOD, "Animals which need be eatin'");

        this.addGui("inventory_size.width",        "Width: %s");
        this.addGui("inventory_size.width_short",  "W: %s");
        this.addGui("inventory_size.height",       "Height: %s");
        this.addGui("inventory_size.height_short", "H: %s");
        this.addGui("inventory_size.summary",      "%s × %s");
        this.addGui("transform.mode.rotate",       "Rotate");
        this.addGui("transform.mode.translate",    "Translate");
        this.addGui("transform.mode.scale",        "Scale");

        this.addNetMsg("versionMismatch", new FormattedStringBuilder().append(
                "Yer ship don't match these seas.", ChatFormatting.RED).append("\n").append(
                "Lepidoptera API Version: Server: %s, Client: %s"));

        this.addGamerule("doAnimalStarvation", "Critters Need t'Eat");
        this.addGameruleDesc("doAnimalStarvation", "Yer critters be perishin' if they don't be eatin'!");

        this.addConfigCategory(LepidopteraConfig.class, "default", "Mainsails");

        this.addConfigTitle(LepidopteraConfig.class, "Lepidoptera API Tweaks");
        this.addConfigOption(LepidopteraConfig.class, "enableAlchemicalAlembicRecipes", "Let ye be craftin' the Bottle o' the Chemist");
        this.addConfigTooltip(LepidopteraConfig.class, "enableAlchemicalAlembicRecipes", new String[]{
                "If ye be changing this, ye be havin' to drop\nanchor before ye set sail again, or ye can use\n/reload.",
                "Yer ship receives this value when ye set sail on\nthe seas."
        });

        this.addConfigOption(LepidopteraConfig.class, "alchemicalAlembicCanShiftClick", "Put the Bottle o' the Chemist on yer head right quick with Shift-Click");
        this.addConfigTooltip(LepidopteraConfig.class, "alchemicalAlembicCanShiftClick", new String[] {
                "If this be changin' in yer config file, the seas\nupdate it.",
                "Yer ship receives this value when ye set sail on the seas."
        });

        this.addConfigOption(LepidopteraConfig.class, "showAlembicInCreativeTabs", "Bottle o' the Chemist can be summoned by pirate captains");
        this.addConfigTooltip(LepidopteraConfig.class, "showAlembicInCreativeTabs", new String[]{
                "Whether the Bottle o' the Chemist will show in\nthe Pirate Captain's inventory.", new FormattedStringBuilder(
                "If ye be changin' this, ye need to leave the seas", ChatFormatting.YELLOW).append("\n").append(
                "entirely before settin' sail again.", ChatFormatting.YELLOW).toString()
        });

        this.addConfigOption(LepidopteraConfig.class, "demoToolEffectiveness", "[Demo] Bite o' yer tools");
        this.add(ToolEffectivenessTier.lepidoptera_api$tool_effectiveness$wooden, "Timber");
        this.add(ToolEffectivenessTier.lepidoptera_api$tool_effectiveness$stone, "Rock");
        this.add(ToolEffectivenessTier.lepidoptera_api$tool_effectiveness$copper, "Copper");
        this.add(ToolEffectivenessTier.lepidoptera_api$tool_effectiveness$iron, "Steel");
        this.add(ToolEffectivenessTier.lepidoptera_api$tool_effectiveness$golden, "Gold");
        this.add(ToolEffectivenessTier.lepidoptera_api$tool_effectiveness$diamond, "Diamond");
        this.add(ToolEffectivenessTier.lepidoptera_api$tool_effectiveness$netherite, "Blackbeard's");

        this.addConfigOption(LepidopteraConfig.class, "demoInventorySize", "[Demo] Size o' yer stores");
        this.addConfigTooltip(LepidopteraConfig.class, "demoInventorySize", new String[]{
                "[Demo] This only be here t'show off the abilities\no' this here widget.",
                "Config file format: WxH (e.g. 9x3).",
                "Max width: 9, max height: 4."
        });

        this.addConfigCategory(LepidopteraConfig.class, "accessories", "Booty");

        this.addConfigOption(LepidopteraConfig.class, "accessories", "enableAlembicHatRenderer", "Show th' Bottle o' th' Chemist on yer noggin");
        this.addConfigTooltip(LepidopteraConfig.class, "accessories", "enableAlembicHatRenderer", new String[]{
                "If ye turn this off, th' Bottle o' th' Chemist\nwon't show if it's in yer booty hat slot.",
                "Ye can't affect whether th' Bottle shows in yer\nmain hat slot with this tweak."
        });

        this.addConfigOption(LepidopteraConfig.class, "accessories", "alembicHatTransform", "Size, yaw, n' location o' th' Bottle o' th' Chemist on yer noggin");
        this.addConfigTooltip(LepidopteraConfig.class, "accessories", "alembicHatTransform", new String[]{
                "Changes where ye stash yer Bottle o' th' Chemist\non yer head.",
                "Ye can't affect how ye stash yer Bottle o' th'\nChemist if its in yer main hat slot with this\ntweak."
        });
    }
}
