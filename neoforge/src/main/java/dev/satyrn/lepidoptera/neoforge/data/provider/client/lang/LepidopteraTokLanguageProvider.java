package dev.satyrn.lepidoptera.neoforge.data.provider.client.lang;

import dev.satyrn.lepidoptera.LepidopteraAPI;
import dev.satyrn.lepidoptera.api.lang.FormattedStringBuilder;
import dev.satyrn.lepidoptera.api.lang.T9n;
import dev.satyrn.lepidoptera.config.LepidopteraConfig;
import dev.satyrn.lepidoptera.item.LepidopteraItems;
import dev.satyrn.lepidoptera.neoforge.api.provider.client.lang.ModLanguageProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.data.PackOutput;

public class LepidopteraTokLanguageProvider extends ModLanguageProvider {
    public LepidopteraTokLanguageProvider(PackOutput output) {
        super(LepidopteraAPI.class, output, "tok");
    }

    @Override
    protected void addTranslations() {
        // ilo telo nasa = tool-liquid-strange
        this.add(T9n.item(LepidopteraItems.ALCHEMICAL_ALEMBIC), "ilo telo nasa");
        // ilo telo nasa weka = tool-liquid-strange-gone
        this.add(T9n.item(LepidopteraItems.DEPLETED_ALEMBIC), "ilo telo nasa weka");

        // moli soweli wile moku = death of food-wanting animal
        this.add(T9n.gamerule("doAnimalStarvation"), "moli soweli wile moku");
        // soweli li ken moli tan moku ala = animal can die because-of eating-not
        this.add(T9n.gameruleDesc("doAnimalStarvation"), "Lepidoptera API: soweli li ken moli tan moku ala.");

        // nanpa ... li ante = number ... is-different; ma tomo = place-home (server); ilo = tool (client)
        this.add(T9n.netMsg(this.metadata, "versionMismatch"), "nanpa Lepidoptera API li ante. ma tomo: %s, ilo: %s");

        // nasin pi Lepidoptera API = rules/settings of Lepidoptera API
        this.add(T9n.configTitle(LepidopteraConfig.class), "nasin pi Lepidoptera API");
        // sitelen pi pakala = writing of errors (debug logging)
        this.add(T9n.configOption(LepidopteraConfig.class, "debug"), "sitelen pi pakala");
        // o sona e ni! = know this! (WARNING!); ni li sitelen e pali ale = this writes all interactions
        this.add(T9n.configTooltip(LepidopteraConfig.class, "debug", 0),
                new FormattedStringBuilder("o sona e ni! ", ChatFormatting.RED).append("ni li sitelen e pali ale!"));
        // o open e ni taso la sina sona e pali sina = open this only if you know your work
        this.add(T9n.configTooltip(LepidopteraConfig.class, "debug", 1),
                new FormattedStringBuilder("o open e ni taso la sina sona e pali sina.", ChatFormatting.YELLOW));
        // o open e nasin pali pi ilo telo nasa = enable the crafting-ways of the alembic
        this.add(T9n.configOption(LepidopteraConfig.class, "enableAlchemicalAlembicRecipes"),
                "o open e nasin pali pi ilo telo nasa");
        // ni li ante la o sin e ma tomo = if this changes, restart the server; anu o pali e /reload = or do /reload
        this.add(T9n.configTooltip(LepidopteraConfig.class, "enableAlchemicalAlembicRecipes", 0),
                "ni li ante la o sin e ma tomo, anu o pali e /reload.");
        // ma tomo li tawa e ni tawa ilo jan lon tenpo kama en tenpo sin nasin = the server sends this on join and on settings-renewal
        this.add(T9n.configTooltip(LepidopteraConfig.class, "enableAlchemicalAlembicRecipes", 1),
                "ma tomo li tawa e ni tawa ilo jan lon tenpo kama en tenpo sin nasin.");
        // luka sin = additional/modified hand-action (shift-click); tawa pana e ilo telo nasa tawa lawa = to give the alembic to the head
        this.add(T9n.configOption(LepidopteraConfig.class, "alchemicalAlembicCanShiftClick"),
                "luka sin tawa pana e ilo telo nasa tawa lawa");
        // ante li lon la o sin e nasin = for changes to apply, renew the settings
        this.add(T9n.configTooltip(LepidopteraConfig.class, "alchemicalAlembicCanShiftClick", 0),
                "ante li lon la o sin e nasin.");
        // ma tomo li tawa e ni tawa ilo jan lon tenpo kama en tenpo sin nasin = the server sends this on join and on settings-renewal
        this.add(T9n.configTooltip(LepidopteraConfig.class, "alchemicalAlembicCanShiftClick", 1),
                "ma tomo li tawa e ni tawa ilo jan lon tenpo kama en tenpo sin nasin.");
        // o lukin e ilo telo nasa lon poki pali = show the alembic in the creative-mode inventory
        this.add(T9n.configOption(LepidopteraConfig.class, "showAlembicInCreativeTabs"),
                "o lukin e ilo telo nasa lon poki pali");
        // ni li ante e lukin = this changes visibility; poki pali pi ilo en pali = creative tab of tools-and-utilities
        this.add(T9n.configTooltip(LepidopteraConfig.class, "showAlembicInCreativeTabs", 0),
                "ni li ante e lukin pi ilo telo nasa lon poki pali pi ilo en pali.");
        // ni li ante la o sin ale e ma tomo = if this changes, fully restart the server
        this.add(T9n.configTooltip(LepidopteraConfig.class, "showAlembicInCreativeTabs", 1),
                new FormattedStringBuilder("ni li ante la o sin ale e ma tomo!", ChatFormatting.YELLOW));

        // suli poki (lukin) = inventory-size (demonstration)
        this.add(T9n.configOption(LepidopteraConfig.class, "demoInventorySize"), "suli poki (lukin)");
        // sitelen pi suli poki = text of inventory-size; tawa lukin pi ilo = for visual testing of the widget
        this.add(T9n.configTooltip(LepidopteraConfig.class, "demoInventorySize", 0),
                "[lukin] sitelen pi suli poki tawa lukin pi ilo InventorySizeEntry.");
        // nasin = format; poka = side (width); sewi = up/sky (height); suli = max
        this.add(T9n.configTooltip(LepidopteraConfig.class, "demoInventorySize", 1),
                "nasin: pxs (sama: 9x3). poka suli: 18, sewi suli: 9.");
        // poka = side (width)
        this.add(T9n.gui(LepidopteraAPI.class, "inventory_size", "width"), "poka: %s");
        // p = poka (side/width)
        this.add(T9n.gui(LepidopteraAPI.class, "inventory_size", "width_short"), "p: %s");
        // sewi = up/sky (height)
        this.add(T9n.gui(LepidopteraAPI.class, "inventory_size", "height"), "sewi: %s");
        // s = sewi (up/height)
        this.add(T9n.gui(LepidopteraAPI.class, "inventory_size", "height_short"), "s: %s");
        this.add(T9n.gui(LepidopteraAPI.class, "inventory_size", "summary"), "%s × %s");
    }
}
