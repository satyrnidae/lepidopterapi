package dev.satyrn.lepidoptera.neoforge.data.provider.client.lang;

import dev.satyrn.lepidoptera.LepidopteraAPI;
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

    protected @Override void addTranslations() {
        // I tried with this lol
        this.add(T9n.item(LepidopteraItems.ALCHEMICAL_ALEMBIC), "Alambic alchimiste");
        this.add(T9n.item(LepidopteraItems.DEPLETED_ALEMBIC), "Alambic alchimiste éteint");

        this.add(T9n.gamerule("doAnimalStarvation"), "Inanition des animaux");
        this.add(T9n.gameruleDesc("doAnimalStarvation"),
                "Lepidoptera API: Les animaux affamés peuvent subir des dégâts jusqu'à ce qu'ils meurent.");

        this.add(T9n.netMsg(this.metadata, "versionMismatch"),
                "Les versions de Lepidoptera API sont dépareillées entre le serveur et le client. Serveur : %s, Client : %s");

        this.add(T9n.configTitle(LepidopteraConfig.class), "Lepidoptera API configuration");
        this.add(T9n.configOption(LepidopteraConfig.class, "debug"), "Journalisation de débogage");
        this.add(T9n.configTooltip(LepidopteraConfig.class, "debug", 0),
                new FormattedStringBuilder("AVERTISSEMENT ! ", ChatFormatting.RED).append(
                        "Toutes les interactions d'API seront journalisées."));
        this.add(T9n.configTooltip(LepidopteraConfig.class, "debug", 1),
                new FormattedStringBuilder("À n'activer que si vous comprenez les conséquences.",
                        ChatFormatting.YELLOW));
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

        this.add(T9n.configOption(LepidopteraConfig.class, "demoInventorySize"), "Taille de l'inventaire (démo)");
        this.add(T9n.configTooltip(LepidopteraConfig.class, "demoInventorySize", 0),
                "[Démo] Entrée de la taille de l'inventaire pour tests visuels du widget InventorySizeEntry.");
        this.add(T9n.configTooltip(LepidopteraConfig.class, "demoInventorySize", 1),
                "Format : LxH (ex. : 9x3). Largeur maximale : 18, Hauteur maximale : 9.");
        this.add(T9n.gui(LepidopteraAPI.class, "inventory_size", "width"), "Largeur : %s");
        this.add(T9n.gui(LepidopteraAPI.class, "inventory_size", "width_short"), "L : %s");
        this.add(T9n.gui(LepidopteraAPI.class, "inventory_size", "height"), "Hauteur : %s");
        this.add(T9n.gui(LepidopteraAPI.class, "inventory_size", "height_short"), "H : %s");
        this.add(T9n.gui(LepidopteraAPI.class, "inventory_size", "summary"), "%s × %s");

        // region-specific overrides
        this.addAlchemicalAlembicCanShiftClick();
    }

    protected void addAlchemicalAlembicCanShiftClick() {
        this.add(T9n.configOption(LepidopteraConfig.class, "alchemicalAlembicCanShiftClick"),
                "Shift-clic droit pour équiper l'alambic dans le casque");
    }
}
