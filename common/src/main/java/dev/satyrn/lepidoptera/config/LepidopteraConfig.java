package dev.satyrn.lepidoptera.config;

import dev.satyrn.lepidoptera.api.config.InventorySize;
import dev.satyrn.lepidoptera.api.config.InventorySizeField;
import dev.satyrn.lepidoptera.api.config.NestingConfigData;
import dev.satyrn.lepidoptera.api.config.serializers.YamlComment;
import dev.satyrn.lepidoptera.api.config.sync.ConfigCodec;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Lepidoptera API configuration.
 *
 * <p><b>Internal configuration class, API not guaranteed. Not for External Use</b></p>
 */
@Config(name = "lepidoptera/config")
public class LepidopteraConfig implements NestingConfigData<LepidopteraConfig> {

    @ConfigEntry.Gui.Tooltip(count = 2)
    @YamlComment("Enables recipes for and using the alembic items. " +
            "Changing this requires a server restart or /reload to take effect. " +
            "The server value is pushed to clients on join and on config reload.")
    public boolean enableAlchemicalAlembicRecipes = false;

    @ConfigEntry.Gui.Tooltip(count = 2)
    @YamlComment("Allows the alembics to be shift-click equipped into the helmet slot. " +
            "Changes take effect when the config is reloaded; no server restart required. " +
            "The server value is pushed to clients on join and on config reload.")
    public boolean alchemicalAlembicCanShiftClick = true;

    @ConfigEntry.Gui.RequiresRestart
    @ConfigEntry.Gui.Tooltip(count = 2)
    @YamlComment("Allows the Alchemical Alembic to show in the Creative tabs. " +
            "Changing this value requires a full restart to take effect.")
    public boolean showAlembicInCreativeTabs = false;

    @InventorySizeField(minWidth = 3, maxWidth = 9, minHeight = 2, maxHeight = 4)
    @ConfigEntry.Gui.Tooltip(count = 2)
    @YamlComment("[Demo] Example inventory size entry for visual testing of the InventorySizeEntry widget.")
    @SuppressWarnings("unused") // Demo
    public String demoInventorySize = new InventorySize(5, 3).toString();

    @Override
    public void copyFrom(LepidopteraConfig other) {
        this.enableAlchemicalAlembicRecipes = other.enableAlchemicalAlembicRecipes;
        this.alchemicalAlembicCanShiftClick = other.alchemicalAlembicCanShiftClick;
    }

    /**
     * Encodes and decodes the server-synced fields of {@link LepidopteraConfig}.
     */
    public enum Codec implements ConfigCodec<LepidopteraConfig> {
        INSTANCE;

        @Override
        public void encode(LepidopteraConfig value, FriendlyByteBuf buf) {
            buf.writeBoolean(value.enableAlchemicalAlembicRecipes);
            buf.writeBoolean(value.alchemicalAlembicCanShiftClick);
        }

        @Override
        public LepidopteraConfig decode(FriendlyByteBuf buf) {
            LepidopteraConfig config = new LepidopteraConfig();
            config.enableAlchemicalAlembicRecipes = buf.readBoolean();
            config.alchemicalAlembicCanShiftClick = buf.readBoolean();
            return config;
        }
    }
}
