package dev.satyrn.lepidoptera.compat.accessories;

import dev.satyrn.lepidoptera.api.config.NestingConfigData;
import dev.satyrn.lepidoptera.api.config.serializers.YamlComment;
import dev.satyrn.lepidoptera.api.config.sync.ConfigCodec;
import dev.satyrn.lepidoptera.api.config.transform.RotationOffsetScale;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Partition for optional Accessories mod integration settings.
 *
 * <p>Lives as a {@code @ConfigEntry.Category("accessories") @ConfigEntry.Gui.TransitiveObject}
 * field in {@link dev.satyrn.lepidoptera.config.LepidopteraConfig} so it appears as a
 * dedicated tab in the config screen. Synced from server to client via
 * {@link dev.satyrn.lepidoptera.LepidopteraAPI#SYNCED_CONFIG}.</p>
 *
 * @since 1.0.1-SNAPSHOT.3+1.21.1
 */
public class AccessoriesConfig implements NestingConfigData<AccessoriesConfig> {

    @YamlComment("Enable the custom accessory renderer for the Alchemical Alembic in the Accessories hat slot.")
    public boolean enableAlembicHatRenderer = true;

    @YamlComment("Transform of the Alchemical Alembic when rendered as an Accessories hat.")
    @ConfigEntry.Gui.TransitiveObject
    public RotationOffsetScale alembicHatTransform = new RotationOffsetScale();

    @Override
    public void copyFrom(final AccessoriesConfig other) {
        this.enableAlembicHatRenderer = other.enableAlembicHatRenderer;
        this.alembicHatTransform.copyFrom(other.alembicHatTransform);
    }

    /**
     * {@link ConfigCodec} for network-syncing {@link AccessoriesConfig} from server to client.
     */
    public enum Codec implements ConfigCodec<AccessoriesConfig> {
        INSTANCE;

        @Override
        public void encode(final AccessoriesConfig value, final FriendlyByteBuf buf) {
            buf.writeBoolean(value.enableAlembicHatRenderer);
            RotationOffsetScale.Codec.INSTANCE.encode(value.alembicHatTransform, buf);
        }

        @Override
        public AccessoriesConfig decode(final FriendlyByteBuf buf) {
            final AccessoriesConfig cfg = new AccessoriesConfig();
            cfg.enableAlembicHatRenderer = buf.readBoolean();
            var hatTransform = RotationOffsetScale.Codec.INSTANCE.decode(buf);
            cfg.alembicHatTransform.copyFrom(hatTransform);
            return cfg;
        }
    }
}
