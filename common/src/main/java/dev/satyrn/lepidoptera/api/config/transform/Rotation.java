package dev.satyrn.lepidoptera.api.config.transform;

import dev.satyrn.lepidoptera.api.config.NestingConfigData;
import dev.satyrn.lepidoptera.api.config.serializers.YamlComment;
import dev.satyrn.lepidoptera.api.config.sync.ConfigCodec;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Contract;

import java.beans.BeanProperty;

@Transformation(rotation = true)
public final class Rotation implements NestingConfigData<Rotation> {
    @YamlComment("The X rotation of the transformation in degrees.")
    private float x;
    @YamlComment("The Y rotation of the transformation in degrees.")
    private float y;
    @YamlComment("The Z rotation of the transformation in degrees.")
    private float z;

    @Contract(pure = true)
    public Rotation() {}

    @Contract(pure = true)
    public Rotation(final float x, final float y, final float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Contract(mutates = "param1")
    private Rotation(final FriendlyByteBuf buf) {
        this(buf.readFloat(), buf.readFloat(), buf.readFloat());
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
    @Contract(mutates = "this")
    @Override
    public void copyFrom(Rotation other) {
        this.setX(other.getX());
        this.setY(other.getY());
        this.setZ(other.getZ());
    }

    @BeanProperty
    @Contract(pure = true)
    public float getX() {
        return this.x;
    }

    @Contract(mutates = "this")
    public void setX(float x) {
        this.x = x;
    }

    @BeanProperty
    @Contract(pure = true)
    public float getY() {
        return this.y;
    }

    @Contract(mutates = "this")
    public void setY(float y) {
        this.y = y;
    }

    @BeanProperty
    @Contract(pure = true)
    public float getZ() {
        return this.z;
    }

    @Contract(mutates = "this")
    public void setZ(float z) {
        this.z = z;
    }

    @Contract(pure = true)
    public float toRadiansX() {
        return (float)Math.toRadians(this.x);
    }

    @Contract(pure = true)
    public float toRadiansY() {
        return (float)Math.toRadians(this.y);
    }

    @Contract(pure = true)
    public float toRadiansZ() {
        return (float)Math.toRadians(this.z);
    }

    public enum Codec implements ConfigCodec<Rotation> {
        INSTANCE;

        /**
         * Writes {@code value} to the buffer.
         *
         * @param value the value to encode
         * @param buf   the target buffer
         *
         * @since 1.0.0-SNAPSHOT+1.21.1
         */
        @Contract(mutates = "param1")
        @Override
        public void encode(Rotation value, FriendlyByteBuf buf) {
            buf.writeFloat(value.getX());
            buf.writeFloat(value.getY());
            buf.writeFloat(value.getZ());
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
        @Contract(value = "_ -> new", mutates = "param1")
        @Override
        public Rotation decode(FriendlyByteBuf buf) {
            return new Rotation(buf);
        }
    }
}
