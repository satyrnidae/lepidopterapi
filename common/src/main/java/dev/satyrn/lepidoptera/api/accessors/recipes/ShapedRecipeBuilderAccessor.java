package dev.satyrn.lepidoptera.api.accessors.recipes;

import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * Mixin accessor interface exposing private fields and methods of
 * {@link ShapedRecipeBuilder} to {@code BreakableShapedRecipeBuilder}.
 *
 * <p>Allows the breakable recipe builder to reuse vanilla's pattern validation
 * logic ({@link #invokeEnsureValid}) without duplicating it.</p>
 *
 * @since 0.4.0+1.19.2
 */
@ApiStatus.AvailableSince("0.4.0+1.19.2")
@ApiStatus.Experimental
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
    @ApiStatus.AvailableSince(value = "0.4.0+1.19.2")
    @ApiStatus.Experimental
    Map<String, Criterion<?>> getCriteria();

    /**
     * Returns the output item count.
     *
     * @return the number of result items
     *
     * @since 0.4.0+1.19.2
     */
    @Accessor
    @ApiStatus.AvailableSince(value = "0.4.0+1.19.2")
    @ApiStatus.Experimental
    int getCount();

    /**
     * Returns the recipe group, or {@code null} if none was set.
     *
     * @return the group name
     *
     * @since 0.4.0+1.19.2
     */
    @Accessor
    @ApiStatus.AvailableSince(value = "0.4.0+1.19.2")
    @ApiStatus.Experimental
    @Nullable
    String getGroup();

    /**
     * Returns the list of pattern row strings.
     *
     * @return the pattern rows
     *
     * @since 0.4.0+1.19.2
     */
    @Accessor
    @ApiStatus.AvailableSince(value = "0.4.0+1.19.2")
    @ApiStatus.Experimental
    @SuppressWarnings("unused")
    // API Accessor
    List<String> getRows();

    /**
     * Returns the symbol-to-ingredient mapping for the pattern.
     *
     * @return the ingredient key map
     *
     * @since 0.4.0+1.19.2
     */
    @Accessor
    @ApiStatus.AvailableSince(value = "0.4.0+1.19.2")
    @ApiStatus.Experimental
    Map<Character, Ingredient> getKey();

    /**
     * Invokes the private {@code ensureValid(ResourceLocation)} method to validate
     * the pattern and produce a {@link ShapedRecipePattern}.
     *
     * @param resourceLocation the recipe ID (used in error messages)
     *
     * @return the compiled pattern
     *
     * @throws IllegalStateException if the pattern is invalid
     * @since 0.4.0+1.19.2
     */
    @ApiStatus.AvailableSince(value = "0.4.0+1.19.2")
    @ApiStatus.Experimental
    @Invoker
    @SuppressWarnings("unused")
    // API Invoker
    ShapedRecipePattern invokeEnsureValid(ResourceLocation resourceLocation);
}
