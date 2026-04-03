package dev.satyrn.lepidoptera.api.config;

import dev.satyrn.lepidoptera.api.config.transform.Rotation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Quaternionf;

/**
 * Supplies the 3D viewport preview data for a {@link TransformField}-annotated config field.
 *
 * <p>Implementations mirror the baked-in (non-configurable) transforms that the real layer
 * renderer applies before the user-configured {@code RotationOffsetScale} values, so that the
 * preview widget shows the item in the same orientation it would appear in-game at the
 * neutral (all-zero) config values.</p>
 *
 * <p>Implementations must have a zero-argument constructor so that {@code TransformEntry}
 * can instantiate them via reflection.</p>
 *
 * <p>No {@code @Environment} is needed here — {@link ItemStack}, {@link Vec3}, and
 * {@link Quaternionf} (JOML, bundled with Minecraft) are accessible on both sides.</p>
 *
 * @since 1.0.1-SNAPSHOT.3+1.21.1
 */
@ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
public interface TransformDisplayObject {

    /**
     * Returns the item stack to render in the 3D transform viewport.
     *
     * @since 1.0.1-SNAPSHOT.3+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    ItemStack getDisplayStack();

    /**
     * Returns the fixed translation applied before the configurable offset, in 1/16-block
     * (pixel) units.
     *
     * <p>Mirrors whatever baked-in {@code translate()} call the real layer renderer applies
     * before reading the configurable offset from the config. Return {@link Vec3#ZERO} when
     * there is no such baked-in translation.</p>
     *
     * @since 1.0.1-SNAPSHOT.3+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    default Vec3 getInitialDisplayOffset() {
        return Vec3.ZERO;
    }

    /**
     * Returns the fixed rotation applied after the configurable scale and before the
     * configurable rotation axes.
     *
     * <p>Mirrors whatever baked-in {@code mulPose()} call the real layer renderer applies
     * before the configurable rotations. Return {@code new Quaternionf()} (identity) when
     * there is no such baked-in rotation.</p>
     *
     * @since 1.0.1-SNAPSHOT.3+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    default Quaternionf getInitialRotation() {
        return new Quaternionf();
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    default Quaternionf calcConfiguredPose(Rotation rotation) {
        return calcConfiguredPose(new float[] {rotation.getX(), rotation.getY(), rotation.getZ()});
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    default Quaternionf calcConfiguredPose(float[] rotation) {
        float[] hangle = {
                (float)Math.toRadians(rotation[0]/2f),
                (float)Math.toRadians(rotation[1]/2f),
                (float)Math.toRadians(rotation[2]/2f)
        };
        return new Quaternionf(
                Mth.sin(hangle[0])*Mth.cos(hangle[1])*Mth.cos(hangle[2])-Mth.cos(hangle[0])*Mth.sin(hangle[1])*Mth.sin(hangle[2]),
                Mth.cos(hangle[0])*Mth.sin(hangle[1])*Mth.cos(hangle[2])+Mth.sin(hangle[0])*Mth.cos(hangle[1])*Mth.sin(hangle[2]),
                Mth.cos(hangle[0])*Mth.cos(hangle[1])*Mth.sin(hangle[2])-Mth.sin(hangle[0])*Mth.sin(hangle[1])*Mth.cos(hangle[2]),
                Mth.cos(hangle[0])*Mth.cos(hangle[1])*Mth.cos(hangle[2])+Mth.sin(hangle[0])*Mth.sin(hangle[1])*Mth.sin(hangle[2])
        );
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    default ItemDisplayContext getDisplayContext() { return ItemDisplayContext.FIXED; }
}
