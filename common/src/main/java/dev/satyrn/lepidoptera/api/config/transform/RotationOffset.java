package dev.satyrn.lepidoptera.api.config.transform;

import dev.satyrn.lepidoptera.api.config.NestingConfigData;
import dev.satyrn.lepidoptera.api.config.serializers.YamlComment;
import dev.satyrn.lepidoptera.api.config.sync.ConfigCodec;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Contract;

import java.beans.BeanProperty;

@Transformation(rotation = true, offset = true)
public final class RotationOffset implements NestingConfigData<RotationOffset> {
    @ConfigEntry.Gui.TransitiveObject
    @YamlComment("The rotation of the transformation.")
    private Rotation rotation;
    @ConfigEntry.Gui.TransitiveObject
    @YamlComment("The translation of the transformation.")
    private Offset offset;

    public RotationOffset() {
        this(new Rotation(), new Offset());
    }

    public RotationOffset(final float rotX,
                          final float rotY,
                          final float rotZ) {
        this(new Rotation(rotX, rotY, rotZ), new Offset());
    }

    public RotationOffset(final float rotX,
                          final float rotY,
                          final float rotZ,
                          final int offsetX,
                          final int offsetY,
                          final int offsetZ) {
        this(new Rotation(rotX, rotY, rotZ), new Offset(offsetX, offsetY, offsetZ));
    }

    private RotationOffset(final Rotation rotation, final Offset offset) {
        this.rotation = rotation;
        this.offset = offset;
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
    public void copyFrom(RotationOffset other) {
        this.getRotation().copyFrom(other.getRotation());
        this.getOffset().copyFrom(other.getOffset());
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
    public Offset getOffset() {
        return this.offset;
    }

    public void setOffset(Offset offset) {
        this.offset = offset;
    }

    public enum Codec implements ConfigCodec<RotationOffset> {
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
        public void encode(RotationOffset value, FriendlyByteBuf buf) {
            Rotation.Codec.INSTANCE.encode(value.getRotation(), buf);
            Offset.Codec.INSTANCE.encode(value.getOffset(), buf);
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
        public RotationOffset decode(FriendlyByteBuf buf) {
            final var rotation = Rotation.Codec.INSTANCE.decode(buf);
            final var offset = Offset.Codec.INSTANCE.decode(buf);
            return new RotationOffset(rotation, offset);
        }
    }
}
