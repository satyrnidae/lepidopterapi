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
        this.add(LepidopteraItems.ALCHEMICAL_ALEMBIC, "Alambic alchimiste");
        this.add(LepidopteraItems.DEPLETED_ALEMBIC, "Alambic alchimiste éteint");

        this.add(ApiItemTags.BODY_EQUIPMENT, "Armure de l'animaux");
        this.add(ApiItemTags.CHEST_EQUIPMENT, "Plastrons");
        addChestEquipmentShiftable();
        this.add(ApiItemTags.FEET_EQUIPMENT, "Bottes");
        addFeetEquipmentShiftable();
        this.add(ApiItemTags.HEAD_EQUIPMENT, "Casques");
        addHeadEquipmentShiftable();
        this.add(ApiItemTags.LEGS_EQUIPMENT, "Jambières");
        addLegsEquipmentShiftable();
        this.add(ApiEntityTags.TICKS_FOOD, "Animaux affamés");

        this.addGui("inventory_size.width",        "Largeur : %s");
        this.addGui("inventory_size.width_short",  "L : %s");
        this.addGui("inventory_size.height",       "Hauteur : %s");
        this.addGui("inventory_size.height_short", "H : %s");
        this.addGui("inventory_size.summary",      "%s × %s");
        this.addGui("transform.mode.rotate",       "Rotation");
        this.addGui("transform.mode.translate",    "Décalage");
        this.addGui("transform.mode.scale",        "Échelle");

        this.addGamerule("doAnimalStarvation", "Inanition des animaux");
        this.addGameruleDesc("doAnimalStarvation", "Les animaux affamés peuvent subir des dégâts\njusqu'à ce qu'ils meurent.");

        this.addNetMsg("versionMismatch",
                new FormattedStringBuilder("Les versions de Lepidoptera API sont dépareillées entre le serveur et le client.", ChatFormatting.RED)
                        .append("\n").append("Serveur : %s, Client : %s"));

        this.add(T9n.configCategory(LepidopteraConfig.class, "default"), "Défaut");

        this.addConfigTitle(LepidopteraConfig.class, "Lepidoptera API configuration");
        this.addConfigOption(LepidopteraConfig.class, "enableAlchemicalAlembicRecipes", "Activer les recettes de l'alambic alchimiste");
        this.addConfigTooltip(LepidopteraConfig.class, "enableAlchemicalAlembicRecipes", new String[] {
                "Modifier ce paramètre nécessite un redémarrage du\nserveur, ou la commande /reload.",
                "C'est synchronisé vers les clients lorsqu'ils se\nconnectent, ou lors d'un rechargement de\nla configuration."
        });

        // region-specific override
        this.addAlchemicalAlembicCanShiftClick();
        this.addConfigTooltip(LepidopteraConfig.class, "alchemicalAlembicCanShiftClick", new String[] {
                "Les changements prennent effet lors du\nrechargement de la configuration.",
                "C'est synchronisé vers les clients lorsqu'ils se\nconnectent, ou lors d'un rechargement de\nla configuration."
        });

        this.addConfigOption(LepidopteraConfig.class, "showAlembicInCreativeTabs", "Afficher l'alambic alchimiste dans l'inventaire Créatif");
        this.addConfigTooltip(LepidopteraConfig.class, "showAlembicInCreativeTabs", new String[] {
                "Afficher les alambics alchimistes dans l'onglet\nOutils et utilitaires du mode Créatif.",
                new FormattedStringBuilder("Modifier ce paramètre nécessite un redémarrage", ChatFormatting.YELLOW)
                        .append("\n").append("complet du serveur !", ChatFormatting.YELLOW).toString()
        });

        this.addConfigOption(LepidopteraConfig.class, "demoToolEffectiveness", "[Démo] Exemple d'efficacité des outils");
        this.add(ToolEffectivenessTier.lepidoptera_api$tool_effectiveness$wooden, "En bois");
        this.add(ToolEffectivenessTier.lepidoptera_api$tool_effectiveness$stone, "En pierre");
        this.add(ToolEffectivenessTier.lepidoptera_api$tool_effectiveness$copper, "En cuivre");
        this.add(ToolEffectivenessTier.lepidoptera_api$tool_effectiveness$iron, "En fer");
        this.add(ToolEffectivenessTier.lepidoptera_api$tool_effectiveness$golden, "En or");
        this.add(ToolEffectivenessTier.lepidoptera_api$tool_effectiveness$diamond, "En diamant");
        this.add(ToolEffectivenessTier.lepidoptera_api$tool_effectiveness$netherite, "En Netherite");

        this.addConfigOption(LepidopteraConfig.class, "demoInventorySize", "[Démo] Taille de l'inventaire");
        this.addConfigTooltip(LepidopteraConfig.class, "demoInventorySize", new String[] {
                "[Démo] Entrée de la taille de l'inventaire pour\ntests visuels du widget InventorySizeEntry.",
                "Format : LxH (ex. : 9x3).",
                "Largeur maximale : 9, Hauteur maximale : 4."
        });

        this.add(T9n.configCategory(LepidopteraConfig.class, "accessories"), "Accessoires");

        this.add(T9n.configOption(LepidopteraConfig.class, "accessories", "enableAlembicHatRenderer"), "Afficher l'alambic dans le casque");
        this.addConfigTooltip(LepidopteraConfig.class, "accessories", "enableAlembicHatRenderer", new String[]{
                "If disabled, the alembic will not render on the\nuser's head when placed in the hat slot.",
                "Does not affect the rendering of the alembic hat\nin the vanilla helmet slot."
        });

        this.add(T9n.configOption(LepidopteraConfig.class, "accessories", "alembicHatTransform"), "Décalage, rotation, et échelle de l'Alambic dans le casque");
        this.addConfigTooltip(LepidopteraConfig.class, "accessories", "alembicHatTransform", new String[]{
                "Approximate transformation of the alembic hat.",
                "Does not affect the rendering of the alembic hat\nin the vanilla helmet slot."
        });
    }

    protected void addLegsEquipmentShiftable() {
        this.add(ApiItemTags.LEGS_EQUIPMENT_SHIFTABLE, "Jambières à shift-clic");
    }

    protected void addHeadEquipmentShiftable() {
        this.add(ApiItemTags.HEAD_EQUIPMENT_SHIFTABLE, "Casques à shift-clic");
    }

    protected void addFeetEquipmentShiftable() {
        this.add(ApiItemTags.FEET_EQUIPMENT_SHIFTABLE, "Bottes à shift-clic");
    }

    protected void addChestEquipmentShiftable() {
        this.add(ApiItemTags.CHEST_EQUIPMENT_SHIFTABLE, "Plastrons à shift-clic");
    }

    protected void addAlchemicalAlembicCanShiftClick() {
        this.addConfigOption(LepidopteraConfig.class, "alchemicalAlembicCanShiftClick", "Shift-clic droit pour équiper l'alambic dans le casque");
    }
}
