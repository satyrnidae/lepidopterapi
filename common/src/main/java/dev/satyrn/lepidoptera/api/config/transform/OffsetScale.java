package dev.satyrn.lepidoptera.api.config.transform;

import dev.satyrn.lepidoptera.api.config.NestingConfigData;
import dev.satyrn.lepidoptera.api.config.serializers.YamlComment;
import dev.satyrn.lepidoptera.api.config.sync.ConfigCodec;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Contract;

import java.beans.BeanProperty;

@Transformation(offset = true, scale = true)
public final class OffsetScale implements NestingConfigData<OffsetScale> {
    @ConfigEntry.Gui.TransitiveObject
    @YamlComment("The translation of the transformation in 16-pixel increments.")
    private Offset offset;
    @ConfigEntry.Gui.TransitiveObject
    @YamlComment("The scale of the transformation.")
    private float scale;

    public OffsetScale() {
        this(new Offset(), 1.0F);
    }

    public OffsetScale(final float scale) {
        this(new Offset(), scale);
    }

    public OffsetScale(final int offsetX, final int offsetY, final int offsetZ, final float scale) {
        this(new Offset(offsetX, offsetY, offsetZ), scale);
    }

    private OffsetScale(final Offset offset, final float scale) {
        this.offset = offset;
        this.scale = scale;
    }

    /**
     * Copies all values from {@code other} into this config object.
     *
     * <p>Used by the config sync system to apply server-authoritative values
     * over the local config without replacing the object reference.</p>
     *
     * @param other the source config to copy from
     *
     * @since 0.4.0+1.19.2
     */
    @Override
    public void copyFrom(OffsetScale other) {
        this.getOffset().copyFrom(other.getOffset());
        this.setScale(other.getScale());
    }

    @BeanProperty
    @Contract(pure = true)
    public Offset getOffset()
    {
        return this.offset;
    }

    public void setOffset(final Offset offset)
    {
        this.offset = offset;
    }

    @BeanProperty
    @Contract(pure = true)
    public float getScale()
    {
        return this.scale;
    }

    public void setScale(final float scale)
    {
        this.scale = scale;
    }

    public enum Codec implements ConfigCodec<OffsetScale> {
        INSTANCE;

        /**
         * Writes {@code value} to the buffer.
         *
         * @param value the value to encode
         * @param buf   the target buffer
         *
         * @since 1.0.0-SNAPSHOT+1.21.1
         */
        @Override
        public void encode(OffsetScale value, FriendlyByteBuf buf) {
            Offset.Codec.INSTANCE.encode(value.getOffset(), buf);
            buf.writeFloat(value.getScale());
        }

        /**
         * Reads and returns a value from the buffer.
         *
         * @param buf the source buffer
         *
         * @return the decoded value
         *
         * @since 1.0.0-SNAPSHOT+1.21.1
         */
        @Override
        public OffsetScale decode(FriendlyByteBuf buf) {
            var offset = Offset.Codec.INSTANCE.decode(buf);
            return new OffsetScale(offset, buf.readFloat());
        }
    }
}
