package dev.satyrn.lepidoptera.neoforge.data.provider.client.lang;

import dev.satyrn.lepidoptera.LepidopteraAPI;
import dev.satyrn.lepidoptera.api.lang.FormattedStringBuilder;
import dev.satyrn.lepidoptera.api.lang.T9n;
import dev.satyrn.lepidoptera.config.LepidopteraConfig;
import dev.satyrn.lepidoptera.neoforge.api.provider.client.lang.ModLanguageProvider;
import dev.satyrn.lepidoptera.item.LepidopteraItems;
import net.minecraft.ChatFormatting;
import net.minecraft.data.PackOutput;

public class LepidopteraLanguageProvider extends ModLanguageProvider {
    public LepidopteraLanguageProvider(PackOutput output) {
        super(LepidopteraAPI.class, output, "en_us");
    }

    @Override
    protected void addTranslations() {
        this.add(T9n.item(LepidopteraItems.ALCHEMICAL_ALEMBIC), "Alchemical Alembic");
        this.add(T9n.item(LepidopteraItems.DEPLETED_ALEMBIC), "Depleted Alembic");

        this.add(T9n.gamerule("doAnimalStarvation"), "Animal Starvation");
        this.add(T9n.gameruleDesc("doAnimalStarvation"), "Lepidopera API: Enables animal starvation, allowing animals to take damage to the point of death when their food meter reaches 0.");

        this.add(T9n.netMsg(this.metadata, "versionMismatch"),
                "Incompatible Lepidoptera API version. Server: %s, Client: %s");

        this.add(T9n.configTitle(LepidopteraConfig.class), "Lepidoptera API");
        this.add(T9n.configOption(LepidopteraConfig.class, "debug"), "Debug logging");
        this.add(T9n.configTooltip(LepidopteraConfig.class, "debug", 0), new FormattedStringBuilder("WARNING! ", ChatFormatting.RED).append("This will log every API interaction! "));
        this.add(T9n.configTooltip(LepidopteraConfig.class, "debug", 1), new FormattedStringBuilder("Only enable this if you know what you are doing.", ChatFormatting.YELLOW));
        this.add(T9n.configOption(LepidopteraConfig.class, "enableAlchemicalAlembicRecipes"),"Enable crafting recipes using the alembic");
        this.add(T9n.configTooltip(LepidopteraConfig.class, "enableAlchemicalAlembicRecipes", 0), "Enables breakable crafting recipes that use the alchemical alembic.");
        this.add(T9n.configTooltip(LepidopteraConfig.class, "enableAlchemicalAlembicRecipes", 1), "Changing this requires a server restart or /reload to take effect.");
        this.add(T9n.configTooltip(LepidopteraConfig.class, "enableAlchemicalAlembicRecipes", 2), "The server value is synced to the clients on join.");
        this.add(T9n.configOption(LepidopteraConfig.class, "alchemicalAlembicCanShiftClick"), "Shift-click to equip alembic to head slot");
        this.add(T9n.configTooltip(LepidopteraConfig.class, "alchemicalAlembicCanShiftClick", 0), "Allows the alchemical alembic to be shift-click equipped into the helmet slot.");
        this.add(T9n.configTooltip(LepidopteraConfig.class, "alchemicalAlembicCanShiftClick", 1), "Changing this requires a server restart to take effect.");
        this.add(T9n.configTooltip(LepidopteraConfig.class, "alchemicalAlembicCanShiftClick", 2), "The server value is pushed to clients on join.");
        this.add(T9n.configOption(LepidopteraConfig.class, "showAlembicInCreativeTabs"), "Show alchemical alembic in creative tab");
        this.add(T9n.configTooltip(LepidopteraConfig.class, "showAlembicInCreativeTabs", 0), "Allows the alchemical alembic to appear in the creative tabs.");
        this.add(T9n.configTooltip(LepidopteraConfig.class, "showAlembicInCreativeTabs", 1), new FormattedStringBuilder("Changing this value requires a full server restart.", ChatFormatting.YELLOW));
    }
}
