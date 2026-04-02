package dev.satyrn.lepidoptera.compat.accessories;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.satyrn.lepidoptera.LepidopteraAPI;
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
                final PoseStack matrices,
                final EntityModel<M> model,
                final MultiBufferSource multiBufferSource,
                final int light,
                final float limbSwing,
                final float limbSwingAmount,
                final float partialTicks,
                final float ageInTicks,
                final float netHeadYaw,
                final float headPitch) {

            // This doesn't update?
            final @Nullable AccessoriesConfig cfg = Objects.requireNonNull(LepidopteraAPI.SYNCED_CONFIG).get().accessories;
            if (!cfg.enableAlembicHatRenderer) {
                return;
            }

            matrices.pushPose();

            // Navigate to the entity's head bone
            if (model instanceof HumanoidModel<?> humanoidModel) {
                AccessoryRenderer.transformToModelPart(matrices, humanoidModel.head);
            }
            // Translate up one pixel
            matrices.translate(0.0f, 0.125f, 0.0f);

            matrices.translate(cfg.alembicHatTransform.getOffset().getX(),
                    cfg.alembicHatTransform.getOffset().getY(),
                    cfg.alembicHatTransform.getOffset().getZ());

            // Apply configurable scale and center the item (1-block items need -0.5 offset)
            final float s = cfg.alembicHatTransform.getScale();
            matrices.scale(s, s, s);

            // Rotate (5/8)π (112.5°) on the Y axis
            matrices.mulPose(Axis.YP.rotation(Mth.PI));

            // Apply configurable rotation
            if(Math.abs(cfg.alembicHatTransform.getRotation().getX()) > 1e-4f) {
                matrices.rotateAround(Axis.XP.rotation(cfg.alembicHatTransform.getRotation().toRadiansX()),
                        0.0f, 1.0f, 0.0f);
            }
            if (Math.abs(cfg.alembicHatTransform.getRotation().getY()) > 1e-4f) {
                matrices.rotateAround(Axis.YP.rotation(cfg.alembicHatTransform.getRotation().toRadiansY()),
                        0.0f, 1.0f, 0.0f);
            }
            if (Math.abs(cfg.alembicHatTransform.getRotation().getZ()) > 1e-4f) {
                matrices.rotateAround(Axis.ZP.rotation(cfg.alembicHatTransform.getRotation().toRadiansZ()),
                        0.0f, 1.0f, 0.0f);
            }

            Minecraft.getInstance().getItemRenderer().renderStatic(
                    stack, ItemDisplayContext.HEAD, light,
                    OverlayTexture.NO_OVERLAY, matrices, multiBufferSource, null, 0);

            matrices.popPose();
        }
    }
}
