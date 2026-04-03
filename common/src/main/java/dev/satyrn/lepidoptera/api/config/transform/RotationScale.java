package dev.satyrn.lepidoptera.api.config.transform;

import dev.satyrn.lepidoptera.api.config.NestingConfigData;
import dev.satyrn.lepidoptera.api.config.serializers.YamlComment;
import dev.satyrn.lepidoptera.api.config.sync.ConfigCodec;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Contract;

import java.beans.BeanProperty;

@SuppressWarnings("unused")
@Transformation(rotation = true, scale = true)
public final class RotationScale implements NestingConfigData<RotationScale> {
    @ConfigEntry.Gui.TransitiveObject
    @YamlComment("The rotation of the transformation.")
    private Rotation rotation;
    @ConfigEntry.Gui.TransitiveObject
    @YamlComment("The scale of the transformation.")
    private float scale;

    public RotationScale() {
        this(new Rotation(), 1.0F);
    }

    public RotationScale(float scale) {
        this(new Rotation(), scale);
    }

    public RotationScale(final float rotX, final float rotY, final float rotZ) {
        this(new Rotation(rotX, rotY, rotZ), 1.0F);
    }

    public RotationScale(final float rotX, final float rotY, final float rotZ, final float scale) {
        this(new Rotation(rotX, rotY, rotZ), scale);
    }

    private RotationScale(final Rotation rotation, final float scale) {
        this.rotation = rotation;
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
    public void copyFrom(RotationScale other) {
        this.getRotation().copyFrom(other.getRotation());
        this.setScale(other.getScale());
    }

    @BeanProperty
    @Contract(pure = true)
    public Rotation getRotation() {
        return this.rotation;
    }

    public void setRotation(final Rotation rotation) {
        this.rotation = rotation;
    }

    @BeanProperty
    @Contract(pure = true)
    public float getScale() {
        return this.scale;
    }

    public void setScale(final float scale) {
        this.scale = scale;
    }

    public enum Codec implements ConfigCodec<RotationScale> {
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
        public void encode(RotationScale value, FriendlyByteBuf buf) {
            Rotation.Codec.INSTANCE.encode(value.getRotation(), buf);
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
        public RotationScale decode(FriendlyByteBuf buf) {
            var rotation = Rotation.Codec.INSTANCE.decode(buf);
            return new  RotationScale(rotation, buf.readFloat());
        }
    }
}
