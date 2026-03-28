package dev.satyrn.lepidoptera.api.accessors.recipes;

import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Mixin accessor interface exposing private fields and methods of
 * {@link ShapedRecipeBuilder} to {@code BreakableShapedRecipeBuilder}.
 *
 * @since 0.4.0+1.19.2
 */
@Mixin(ShapedRecipeBuilder.class)
public interface ShapedRecipeBuilderAccessor {

    /**
     * Returns the map of unlock criteria registered on this builder.
     *
     * @return the criteria map
     *
     * @since 0.4.0+1.19.2
     */
    @Accessor
    Map<String, Criterion<?>> getCriteria();

    /**
     * Returns the output item count.
     *
     * @return the number of result items
     *
     * @since 0.4.0+1.19.2
     */
    @Accessor
    int getCount();

    /**
     * Returns the recipe group, or {@code null} if none was set.
     *
     * @return the group name
     *
     * @since 0.4.0+1.19.2
     */
    @Accessor
    @Nullable
    String getGroup();

    /**
     * Returns the symbol-to-ingredient mapping for the pattern.
     *
     * @return the ingredient key map
     *
     * @since 0.4.0+1.19.2
     */
    @Accessor
    Map<Character, Ingredient> getKey();
}
