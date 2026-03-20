package dev.satyrn.lepidoptera.mixin.accessors.net.minecraft.client.renderer.entity;

import dev.satyrn.lepidoptera.annotations.Api;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(LivingEntityRenderer.class)
public interface LivingEntityRendererAccessor {

    @Accessor
    @SuppressWarnings({"unused", "rawtypes"})
    List<RenderLayer> getLayers();
}
