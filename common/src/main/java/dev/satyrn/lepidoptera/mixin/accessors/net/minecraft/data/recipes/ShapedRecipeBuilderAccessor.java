package dev.satyrn.lepidoptera.mixin.accessors.net.minecraft.data.recipes;

import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

@Mixin(ShapedRecipeBuilder.class)
public interface ShapedRecipeBuilderAccessor {
    @Accessor
    Map<String, Criterion<?>> getCriteria();

    @Accessor
    int getCount();

    @Accessor
    @Nullable
    String getGroup();

    @Accessor
    List<String> getRows();

    @Accessor
    Map<Character, Ingredient> getKey();

    @Invoker
    ShapedRecipePattern invokeEnsureValid(ResourceLocation resourceLocation);
}
