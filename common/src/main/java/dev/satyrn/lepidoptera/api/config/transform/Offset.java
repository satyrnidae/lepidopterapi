package dev.satyrn.lepidoptera.api.config.transform;

import dev.satyrn.lepidoptera.api.config.NestingConfigData;
import dev.satyrn.lepidoptera.api.config.serializers.YamlComment;
import dev.satyrn.lepidoptera.api.config.sync.ConfigCodec;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Contract;

import java.beans.BeanProperty;

@Transformation(offset = true)
public final class Offset implements NestingConfigData<Offset> {
    @YamlComment("The X offset of the transformation in 1-pixel increments.")
    private float x;
    @YamlComment("The Y offset of the transformation in 1-pixel increments.")
    private float y;
    @YamlComment("The Z offset of the transformation in 1-pixel increments.")
    private float z;

    @Contract(pure = true)
    public Offset() {}

    @Contract(pure = true)
    public Offset(float x, float y, float offsetZ) {
        this.x = x;
        this.y = y;
        this.z = offsetZ;
    }

    @Contract(mutates = "param1")
    private Offset(FriendlyByteBuf buf) {
        this(buf.readFloat(), buf.readFloat(), buf.readFloat());
    }

    @Contract(mutates = "this")
    @Override
    public void copyFrom(Offset other) {
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

    public enum Codec implements ConfigCodec<Offset> {
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
        public void encode(Offset value, FriendlyByteBuf buf) {
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
        public Offset decode(FriendlyByteBuf buf) {
            return new Offset(buf);
        }
    }
}
