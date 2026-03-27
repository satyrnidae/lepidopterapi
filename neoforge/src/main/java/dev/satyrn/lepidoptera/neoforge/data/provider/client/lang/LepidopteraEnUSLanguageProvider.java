package dev.satyrn.lepidoptera.neoforge.data.provider.client.lang;

import dev.satyrn.lepidoptera.LepidopteraAPI;
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

        this.add(T9n.gamerule("doAnimalStarvation"), "Animal Starvation");
        this.add(T9n.gameruleDesc("doAnimalStarvation"), "Lepidoptera API: Allows animals to die from starvation.");

        this.add(T9n.netMsg(this.metadata, "versionMismatch"),
                "Incompatible Lepidoptera API version. Server: %s, Client: %s");

        this.add(T9n.configTitle(LepidopteraConfig.class), "Lepidoptera API Configuration");
        this.add(T9n.configOption(LepidopteraConfig.class, "debug"), "Debug logging");
        this.add(T9n.configTooltip(LepidopteraConfig.class, "debug", 0),
                new FormattedStringBuilder("WARNING! ", ChatFormatting.RED).append(
                        "This will log every API interaction! "));
        this.add(T9n.configTooltip(LepidopteraConfig.class, "debug", 1),
                new FormattedStringBuilder("Only enable this if you know what you are doing.", ChatFormatting.YELLOW));
        this.add(T9n.configOption(LepidopteraConfig.class, "enableAlchemicalAlembicRecipes"),
                "Enable alembic crafting recipes");
        this.add(T9n.configTooltip(LepidopteraConfig.class, "enableAlchemicalAlembicRecipes", 0),
                "Changing this requires a server restart or /reload to take effect.");
        this.add(T9n.configTooltip(LepidopteraConfig.class, "enableAlchemicalAlembicRecipes", 1),
                "The server value is synced to the clients on join and on config reload.");
        this.add(T9n.configOption(LepidopteraConfig.class, "alchemicalAlembicCanShiftClick"),
                "Shift-click to equip alembic to head slot");
        this.add(T9n.configTooltip(LepidopteraConfig.class, "alchemicalAlembicCanShiftClick", 0),
                "Changes take effect when the config is reloaded.");
        this.add(T9n.configTooltip(LepidopteraConfig.class, "alchemicalAlembicCanShiftClick", 1),
                "The server value is pushed to clients on join and on config reload.");
        this.add(T9n.configOption(LepidopteraConfig.class, "showAlembicInCreativeTabs"),
                "Show alembic in the Creative inventory");
        this.add(T9n.configTooltip(LepidopteraConfig.class, "showAlembicInCreativeTabs", 0),
                "Toggles the alchemical alembic in the Tools and Utilities Creative tab.");
        this.add(T9n.configTooltip(LepidopteraConfig.class, "showAlembicInCreativeTabs", 1),
                new FormattedStringBuilder("Changing this value requires a full server restart.",
                        ChatFormatting.YELLOW));

        this.add(T9n.configOption(LepidopteraConfig.class, "demoInventorySize"), "Demo inventory size");
        this.add(T9n.configTooltip(LepidopteraConfig.class, "demoInventorySize", 0),
                "[Demo] Example inventory size entry for visual testing of the InventorySizeEntry widget.");
        this.add(T9n.configTooltip(LepidopteraConfig.class, "demoInventorySize", 1),
                "Format: WxH (e.g. 9x3). Max width: 18, max height: 9.");
        this.add(T9n.gui(LepidopteraAPI.class, "inventory_size", "width"), "Width: %s");
        this.add(T9n.gui(LepidopteraAPI.class, "inventory_size", "width_short"), "W: %s");
        this.add(T9n.gui(LepidopteraAPI.class, "inventory_size", "height"), "Height: %s");
        this.add(T9n.gui(LepidopteraAPI.class, "inventory_size", "height_short"), "H: %s");
        this.add(T9n.gui(LepidopteraAPI.class, "inventory_size", "summary"), "%s × %s");
    }
}
