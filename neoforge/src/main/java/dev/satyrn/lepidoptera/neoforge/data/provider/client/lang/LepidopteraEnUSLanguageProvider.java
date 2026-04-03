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

public class LepidopteraEnUSLanguageProvider extends ModLanguageProvider {
    public LepidopteraEnUSLanguageProvider(PackOutput output) {
        this(output, "en_us");
    }

    public LepidopteraEnUSLanguageProvider(PackOutput output, String locale) {
        super(LepidopteraAPI.class, output, locale);
    }

    @Override
    protected void addTranslations() {
        this.add(T9n.item(LepidopteraItems.ALCHEMICAL_ALEMBIC), "Alchemical Alembic");
        this.add(T9n.item(LepidopteraItems.DEPLETED_ALEMBIC), "Depleted Alembic");

        // This tag is region-specific
        this.addBodyEquipmentTag();
        this.add(ApiItemTags.CHEST_EQUIPMENT, "Chest equipment");
        this.add(ApiItemTags.CHEST_EQUIPMENT_SHIFTABLE, "Quick-equip chest equipment");
        this.add(ApiItemTags.FEET_EQUIPMENT, "Footwear");
        this.add(ApiItemTags.FEET_EQUIPMENT_SHIFTABLE, "Quick-equip footwear");
        this.add(ApiItemTags.HEAD_EQUIPMENT, "Hats");
        this.add(ApiItemTags.HEAD_EQUIPMENT_SHIFTABLE, "Quick-equip hats");
        this.add(ApiItemTags.LEGS_EQUIPMENT, "Pants");
        this.add(ApiItemTags.LEGS_EQUIPMENT_SHIFTABLE, "Quick-equip pants");
        this.add(ApiEntityTags.TICKS_FOOD, "Hungry Animals");

        this.addGui("inventory_size.width",        "Width: %s");
        this.addGui("inventory_size.width_short",  "W: %s");
        this.addGui("inventory_size.height",       "Height: %s");
        this.addGui("inventory_size.height_short", "H: %s");
        this.addGui("inventory_size.summary",      "%s × %s");
        this.addGui("transform.mode.rotate",       "Rotate");
        this.addGui("transform.mode.translate",    "Translate");
        this.addGui("transform.mode.scale",        "Scale");

        this.addNetMsg("versionMismatch", "Incompatible Lepidoptera API version. Server: %s, Client: %s");

        this.addGamerule("doAnimalStarvation", "Animal Starvation");
        this.addGameruleDesc("doAnimalStarvation", "Allows animals to die from starvation if entity\nhunger for their type is enabled.");

        this.addConfigTitle(LepidopteraConfig.class, "Lepidoptera API Configuration");

        this.addConfigCategory(LepidopteraConfig.class, "default", "Default");

        this.addConfigOption(LepidopteraConfig.class, "enableAlchemicalAlembicRecipes", "Enable alembic crafting recipes");
        this.addConfigTooltip(LepidopteraConfig.class, "enableAlchemicalAlembicRecipes", new String[]{
                "Changing this requires a server restart or /reload\n"+
                "to take effect.",
                "The server value is synced to the clients on join\n"+
                "and on config reload."
        });

        this.addConfigOption(LepidopteraConfig.class, "alchemicalAlembicCanShiftClick", "Shift-click to equip alembic to head slot");
        this.addConfigTooltip(LepidopteraConfig.class, "alchemicalAlembicCanShiftClick", new String[] {
                "Changes take effect when the config is reloaded.",
                "The server value is synced to the clients on join\n"+
                "and on config reload."
        });

        this.addConfigOption(LepidopteraConfig.class, "showAlembicInCreativeTabs", "Show alembic in the Creative inventory");
        this.addConfigTooltip(LepidopteraConfig.class, "showAlembicInCreativeTabs", new String[]{
                "Toggles the alchemical alembic in the Tools and\n" +
                "Utilities Creative tab.",
                new FormattedStringBuilder(
                "Changing this value requires a full server", ChatFormatting.YELLOW).append("\n").append(
                "restart.", ChatFormatting.YELLOW).toString()
        });

        this.addConfigOption(LepidopteraConfig.class, "demoToolEffectiveness", "[Demo] Example tool effectiveness");
        this.add(ToolEffectivenessTier.lepidoptera_api$tool_effectiveness$wooden, "Wooden");
        this.add(ToolEffectivenessTier.lepidoptera_api$tool_effectiveness$stone, "Stone");
        this.add(ToolEffectivenessTier.lepidoptera_api$tool_effectiveness$copper, "Copper");
        this.add(ToolEffectivenessTier.lepidoptera_api$tool_effectiveness$iron, "Iron");
        this.add(ToolEffectivenessTier.lepidoptera_api$tool_effectiveness$golden, "Golden");
        this.add(ToolEffectivenessTier.lepidoptera_api$tool_effectiveness$diamond, "Diamond");
        this.add(ToolEffectivenessTier.lepidoptera_api$tool_effectiveness$netherite, "Netherite");

        this.addConfigOption(LepidopteraConfig.class, "demoInventorySize", "[Demo] Inventory size");
        this.addConfigTooltip(LepidopteraConfig.class, "demoInventorySize", new String[]{
                "[Demo] Example inventory size entry for visual\n"+
                "testing of the InventorySizeEntry widget.",
                "Config file format: WxH (e.g. 9x3).\n"+
                "Max width: 9, max height: 4."
        });

        this.addConfigCategory(LepidopteraConfig.class, "accessories", "Accessories");

        this.addConfigOption(LepidopteraConfig.class, "accessories", "enableAlembicHatRenderer", "Render alembic in hat slot");
        this.addConfigTooltip(LepidopteraConfig.class, "accessories", "enableAlembicHatRenderer", new String[]{
                "If disabled, the alembic will not render on the\n" +
                "user's head when placed in the hat slot.",
                "Does not affect the rendering of the alembic hat\n"+
                "in the vanilla helmet slot."
        });

        this.addConfigOption(LepidopteraConfig.class, "accessories", "alembicHatTransform", "Size, rotation, and scale of the Alembic in the hat slot");
        this.addConfigTooltip(LepidopteraConfig.class, "accessories", "alembicHatTransform", new String[]{
                "Approximate transformation of the alembic hat.",
                "Does not affect the rendering of the alembic hat\n"+
                "in the vanilla helmet slot."
        });
    }

    protected void addBodyEquipmentTag() {
        this.add(ApiItemTags.BODY_EQUIPMENT, "Animal armor");
    }
}
