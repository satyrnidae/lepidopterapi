package dev.satyrn.lepidoptera.api.accessors.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

/**
 * Client-only mixin accessor exposing the render layer list of {@link LivingEntityRenderer}.
 *
 * <p>Allows mods to read (and potentially add to) the list of {@link RenderLayer}s applied
 * during entity rendering without requiring direct access to the private field.</p>
 *
 * @since 0.4.0+1.19.2
 */
@Environment(EnvType.CLIENT)
@Mixin(LivingEntityRenderer.class)
public interface LivingEntityRendererAccessor {

    /**
     * Returns the mutable list of render layers attached to this entity renderer.
     *
     * @return the render layer list
     *
     * @since 0.4.0+1.19.2
     */
    @Accessor
    @SuppressWarnings("unused")
    // API Accessor
    List<RenderLayer<?, ?>> getLayers();
}
