package dev.satyrn.lepidoptera.compat.accessories;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.satyrn.lepidoptera.LepidopteraAPI;
import dev.satyrn.lepidoptera.api.config.transform.Rotation;
import org.joml.Quaternionf;
import dev.satyrn.lepidoptera.api.compatibility.ClientCompatibilityProvider;
import dev.satyrn.lepidoptera.item.LepidopteraItems;
import io.wispforest.accessories.api.client.AccessoriesRendererRegistry;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Client-side compatibility provider for the Accessories mod.
 *
 * <p>Registers a custom {@link AccessoryRenderer} for the Alchemical Alembic that corrects
 * its orientation when worn in the Accessories hat slot. Rendering parameters are read from
 * {@link dev.satyrn.lepidoptera.LepidopteraAPI#SYNCED_CONFIG} so server admins can override them.</p>
 *
 * @since 1.0.1-SNAPSHOT.3+1.21.1
 */
@Environment(EnvType.CLIENT)
@SuppressWarnings("unused")
public final class AccessoriesClientCompatProvider extends ClientCompatibilityProvider {
    @Override
    public String getModId() {
        return "accessories";
    }

    @Override
    public void onClientInit() {
        AccessoriesRendererRegistry.registerRenderer(
                LepidopteraItems.ALCHEMICAL_ALEMBIC.get(),
                AlchemicalAlembicRenderer::new);
    }

    @Environment(EnvType.CLIENT)
    private static final class AlchemicalAlembicRenderer implements AccessoryRenderer {

        @Override
        public <M extends LivingEntity> void render(
                final ItemStack stack,
                final SlotReference reference,
                final PoseStack pose,
                final EntityModel<M> model,
                final MultiBufferSource multiBufferSource,
                final int light,
                final float limbSwing,
                final float limbSwingAmount,
                final float partialTicks,
                final float ageInTicks,
                final float netHeadYaw,
                final float headPitch) {

            final AccessoriesTransformDisplay displayHelper = new AccessoriesTransformDisplay();

            final @Nullable AccessoriesConfig cfg = Objects.requireNonNull(LepidopteraAPI.SYNCED_CONFIG).get().accessories;
            if (!cfg.enableAlembicHatRenderer) {
                return;
            }

            pose.pushPose();

            // Navigate to the entity's head bone
            if (model instanceof HumanoidModel<?> humanoidModel) {
                AccessoryRenderer.transformToModelPart(pose, humanoidModel.head);
            }
            // Attach the item to the top of the head first...
            pose.translate(0f, 0.5f, 0f);
            // ...then move it around by the configured amount
            pose.translate(cfg.alembicHatTransform.getOffset().getX(),
                    cfg.alembicHatTransform.getOffset().getY(),
                    cfg.alembicHatTransform.getOffset().getZ());

            // Scale item down first (~2/3 to match original HEAD display)...
            pose.scale(0.67f, 0.67f, 0.67f);
            // ...then up by config amt
            final float s = cfg.alembicHatTransform.getScale();
            pose.scale(s, s, s);

            // Apply configurable rotation via quaternion.
            final Rotation r = cfg.alembicHatTransform.getRotation();

            pose.mulPose(displayHelper.calcConfiguredPose(r));

            // Rotate frame item up 90°
            pose.mulPose(displayHelper.getInitialRotation());
            // Rotate π (180°) on the Y axis
            pose.mulPose(Axis.YP.rotation(Mth.PI));

            // We're rendering in Fixed mode so the rotation origin is correct.
            Minecraft.getInstance().getItemRenderer().renderStatic(
                    stack, displayHelper.getDisplayContext(), light,
                    OverlayTexture.NO_OVERLAY, pose, multiBufferSource, null, 0);

            pose.popPose();
        }
    }
}
