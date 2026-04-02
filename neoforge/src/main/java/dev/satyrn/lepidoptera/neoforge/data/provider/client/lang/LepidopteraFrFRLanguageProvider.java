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

public class LepidopteraFrFRLanguageProvider extends ModLanguageProvider {
    public LepidopteraFrFRLanguageProvider(PackOutput output) {
        this(output, "fr_fr");
    }

    public LepidopteraFrFRLanguageProvider(PackOutput output, String locale) {
        super(LepidopteraAPI.class, output, locale);
    }

    @Override
    protected void addTranslations() {
        // I tried with this lol
        this.add(T9n.item(LepidopteraItems.ALCHEMICAL_ALEMBIC), "Alambic alchimiste");
        this.add(T9n.item(LepidopteraItems.DEPLETED_ALEMBIC), "Alambic alchimiste éteint");

        this.add(T9n.gamerule("doAnimalStarvation"), "Inanition des animaux");
        this.add(T9n.gameruleDesc("doAnimalStarvation"),
                "Lepidoptera API: Les animaux affamés peuvent subir des dégâts jusqu'à ce qu'ils meurent.");

        this.add(T9n.netMsg(this.metadata, "versionMismatch"),
                "Les versions de Lepidoptera API sont dépareillées entre le serveur et le client. Serveur : %s, Client : %s");

        this.add(T9n.configTitle(LepidopteraConfig.class), "Lepidoptera API configuration");
        this.add(T9n.configOption(LepidopteraConfig.class, "enableAlchemicalAlembicRecipes"),
                "Activer les recettes de l'alambic alchimiste");
        this.add(T9n.configTooltip(LepidopteraConfig.class, "enableAlchemicalAlembicRecipes", 0),
                "Modifier ce paramètre nécessite un redémarrage du serveur, ou la commande /reload.");
        this.add(T9n.configTooltip(LepidopteraConfig.class, "enableAlchemicalAlembicRecipes", 1),
                "C'est synchronisé vers les clients lorsqu'ils se connectent, ou lors d'un rechargement de la configuration.");
        this.add(T9n.configTooltip(LepidopteraConfig.class, "alchemicalAlembicCanShiftClick", 0),
                "Les changements prennent effet lors du rechargement de la configuration.");
        this.add(T9n.configTooltip(LepidopteraConfig.class, "alchemicalAlembicCanShiftClick", 1),
                "C'est synchronisé vers les clients lorsqu'ils se connectent, ou lors d'un rechargement de la configuration.");
        this.add(T9n.configOption(LepidopteraConfig.class, "showAlembicInCreativeTabs"),
                "Afficher l'alambic alchimiste dans l'inventaire Créatif");
        this.add(T9n.configTooltip(LepidopteraConfig.class, "showAlembicInCreativeTabs", 0),
                "Afficher les alambics alchimistes dans l'onglet Outils et utilitaires du mode Créatif.");
        this.add(T9n.configTooltip(LepidopteraConfig.class, "showAlembicInCreativeTabs", 1),
                new FormattedStringBuilder("Modifier ce paramètre nécessite un redémarrage complet du serveur !",
                        ChatFormatting.YELLOW));
        this.add(T9n.configOption(LepidopteraConfig.class, "demoToolEffectiveness"), "[Démo] Exemple d'efficacité des outils");

        this.add(T9n.configOption(LepidopteraConfig.class, "demoInventorySize"), "[Démo] Taille de l'inventaire");
        this.add(T9n.configTooltip(LepidopteraConfig.class, "demoInventorySize", 0),
                "[Démo] Entrée de la taille de l'inventaire pour tests visuels du widget InventorySizeEntry.");
        this.add(T9n.configTooltip(LepidopteraConfig.class, "demoInventorySize", 1),
                "Format : LxH (ex. : 9x3). Largeur maximale : 9, Hauteur maximale : 4.");
        this.add(T9n.gui(LepidopteraAPI.class, "inventory_size", "width"), "Largeur : %s");
        this.add(T9n.gui(LepidopteraAPI.class, "inventory_size", "width_short"), "L : %s");
        this.add(T9n.gui(LepidopteraAPI.class, "inventory_size", "height"), "Hauteur : %s");
        this.add(T9n.gui(LepidopteraAPI.class, "inventory_size", "height_short"), "H : %s");
        this.add(T9n.gui(LepidopteraAPI.class, "inventory_size", "summary"), "%s × %s");

        this.add(T9n.configCategory(LepidopteraConfig.class, "default"), "Default");
        this.add(T9n.configCategory(LepidopteraConfig.class, "accessories"), "Accessories");

        this.add(T9n.configOption(LepidopteraConfig.class, "accessories", "enableAlembicHatRenderer"), "Render alembic in hat slot");
        this.add(T9n.configOption(LepidopteraConfig.class, "accessories", "alembicHatTransform", "rotation", "x"), "X rotation in degrees");
        this.add(T9n.configOption(LepidopteraConfig.class, "accessories", "alembicHatTransform", "rotation", "y"), "Y rotation in degrees");
        this.add(T9n.configOption(LepidopteraConfig.class, "accessories", "alembicHatTransform", "rotation", "z"), "Z rotation in degrees");
        this.add(T9n.configOption(LepidopteraConfig.class, "accessories", "alembicHatTransform", "offset", "x"), "X offset");
        this.add(T9n.configOption(LepidopteraConfig.class, "accessories", "alembicHatTransform", "offset", "y"), "Y offset");
        this.add(T9n.configOption(LepidopteraConfig.class, "accessories", "alembicHatTransform", "offset", "z"), "Z offset");
        this.add(T9n.configOption(LepidopteraConfig.class, "accessories", "alembicHatTransform", "scale"), "Scale multiplier");

        this.add(T9n.itemTag(ApiItemTags.BODY_EQUIPMENT), "Armure de l'animaux");
        this.add(T9n.itemTag(ApiItemTags.CHEST_EQUIPMENT), "Plastrons");
        this.add(T9n.itemTag(ApiItemTags.CHEST_EQUIPMENT_SHIFTABLE), "Plastrons à équipement rapide");
        this.add(T9n.itemTag(ApiItemTags.FEET_EQUIPMENT), "Bottes");
        this.add(T9n.itemTag(ApiItemTags.FEET_EQUIPMENT_SHIFTABLE), "Bottes à équipement rapide");
        this.add(T9n.itemTag(ApiItemTags.HEAD_EQUIPMENT), "Casques");
        this.add(T9n.itemTag(ApiItemTags.HEAD_EQUIPMENT_SHIFTABLE), "Casques à équipement rapide");
        this.add(T9n.itemTag(ApiItemTags.LEGS_EQUIPMENT), "Jambières");
        this.add(T9n.itemTag(ApiItemTags.LEGS_EQUIPMENT_SHIFTABLE), "Jambières à équipement rapide");

        this.add(T9n.entityTypeTag(ApiEntityTags.TICKS_FOOD), "Animaux affamés");

        this.add(T9n.enumKey(ToolEffectivenessTier.lepidoptera_api$tool_effectiveness$wooden), "En bois");
        this.add(T9n.enumKey(ToolEffectivenessTier.lepidoptera_api$tool_effectiveness$stone), "En pierre");
        this.add(T9n.enumKey(ToolEffectivenessTier.lepidoptera_api$tool_effectiveness$copper), "En cuivre");
        this.add(T9n.enumKey(ToolEffectivenessTier.lepidoptera_api$tool_effectiveness$iron), "En fer");
        this.add(T9n.enumKey(ToolEffectivenessTier.lepidoptera_api$tool_effectiveness$golden), "En or");
        this.add(T9n.enumKey(ToolEffectivenessTier.lepidoptera_api$tool_effectiveness$diamond), "En diamant");
        this.add(T9n.enumKey(ToolEffectivenessTier.lepidoptera_api$tool_effectiveness$netherite), "En Netherite");

        // region-specific overrides
        this.addAlchemicalAlembicCanShiftClick();
    }

    protected void addAlchemicalAlembicCanShiftClick() {
        this.add(T9n.configOption(LepidopteraConfig.class, "alchemicalAlembicCanShiftClick"),
                "Shift-clic droit pour équiper l'alambic dans le casque");
    }
}
