package dev.satyrn.lepidoptera.compat.accessories;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import com.mojang.math.Axis;
import dev.satyrn.lepidoptera.api.config.TransformDisplayObject;
import dev.satyrn.lepidoptera.item.LepidopteraItems;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;

/**
 * {@link TransformDisplayObject} for the {@link AccessoriesConfig#alembicHatTransform} field.
 *
 * <p>The baked-in transforms mirror those applied by
 * {@code AccessoriesClientCompatProvider.AlchemicalAlembicRenderer} before the configurable
 * values are read:</p>
 * <ul>
 *   <li>{@code matrices.translate(0, 0.125, 0)} — 0.125 block = 2 pixels up</li>
 *   <li>{@code matrices.mulPose(Axis.YP.rotation(Mth.PI))} — 180° Y rotation</li>
 * </ul>
 *
 * @since 1.0.1-SNAPSHOT.3+1.21.1
 */
public final class AccessoriesTransformDisplay implements TransformDisplayObject {

    @Override
    public ItemStack getDisplayStack() {
        return new ItemStack(LepidopteraItems.ALCHEMICAL_ALEMBIC.get());
    }

    @Override
    public Quaternionf getInitialRotation() {
        return Axis.XP.rotation(Mth.PI / 2);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void applyPose(final com.mojang.blaze3d.vertex.PoseStack pose,
                          final float[] rotation) {
        if (Math.abs(rotation[0]) > 1e-4f)
            pose.mulPose(Axis.XP.rotation((float) Math.toRadians(rotation[0])));
        // Y and Z are swapped due to the baked-in XP 90° from ItemDisplayContext.FIXED
        if (Math.abs(rotation[1]) > 1e-4f)
            pose.mulPose(Axis.ZP.rotation((float) Math.toRadians(rotation[1])));
        if (Math.abs(rotation[2]) > 1e-4f)
            pose.mulPose(Axis.YP.rotation(-(float) Math.toRadians(rotation[2])));
    }

}
