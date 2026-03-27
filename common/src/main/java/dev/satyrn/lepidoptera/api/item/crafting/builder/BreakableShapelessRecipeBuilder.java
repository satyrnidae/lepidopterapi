package dev.satyrn.lepidoptera.api.item.crafting.builder;

import org.jetbrains.annotations.ApiStatus;
import dev.satyrn.lepidoptera.api.item.crafting.BreakableShapelessRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Builds a {@link BreakableShapelessRecipe} for use in data providers.
 *
 * <p>Usage mirrors {@code ShapelessRecipeBuilder}: call {@link #requires},
 * and optionally {@link #group}, {@link #damage}, then {@link #save}.
 *
 * @since 1.0.0-SNAPSHOT.1+1.21.1
 */
@ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
public final class BreakableShapelessRecipeBuilder implements RecipeBuilder {

    private final RecipeCategory category;
    private final Item result;
    private final int count;
    private final NonNullList<Ingredient> ingredients = NonNullList.create();
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    private @Nullable String group;
    private final int damage;

    private BreakableShapelessRecipeBuilder(final RecipeCategory category,
                                            final ItemLike result,
                                            final int count,
                                            final int damage) {
        this.category = category;
        this.result = result.asItem();
        this.count = count;
        this.damage = damage;
    }

    /**
     * Creates a builder for a single-output, 1-damage breakable shapeless recipe.
     *
     * @param category the recipe category
     * @param result   the result item
     *
     * @return a new builder
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @Contract("_, _ -> new")
    public static BreakableShapelessRecipeBuilder shapeless(final RecipeCategory category, final ItemLike result) {
        return shapeless(category, result, 1, 1);
    }

    /**
     * Creates a builder for a multi-output, 1-damage breakable shapeless recipe.
     *
     * @param category the recipe category
     * @param result   the result item
     * @param count    the number of result items
     *
     * @return a new builder
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @Contract("_, _, _ -> new")
    public static BreakableShapelessRecipeBuilder shapeless(final RecipeCategory category,
                                                            final ItemLike result,
                                                            final int count) {
        return shapeless(category, result, count, 1);
    }

    /**
     * Creates a builder for a breakable shapeless recipe with a custom damage value.
     *
     * @param category the recipe category
     * @param result   the result item
     * @param count    the number of result items
     * @param damage   the damage applied to damageable crafting-remaining items
     *
     * @return a new builder
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @Contract("_, _, _, _ -> new")
    public static BreakableShapelessRecipeBuilder shapeless(final RecipeCategory category,
                                                            final ItemLike result,
                                                            final int count,
                                                            final int damage) {
        return new BreakableShapelessRecipeBuilder(category, result, count, damage);
    }

    /**
     * Adds an ingredient item to the recipe.
     *
     * @param item the required item
     *
     * @return {@code this}, for chaining
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @Contract(value = "_ -> this", mutates = "this")
    public BreakableShapelessRecipeBuilder requires(final ItemLike item) {
        return requires(Ingredient.of(item));
    }

    /**
     * Adds an ingredient to the recipe.
     *
     * @param ingredient the required ingredient
     *
     * @return {@code this}, for chaining
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @Contract(value = "_ -> this", mutates = "this")
    public BreakableShapelessRecipeBuilder requires(final Ingredient ingredient) {
        ingredients.add(ingredient);
        return this;
    }

    /**
     * Adds an advancement criterion that unlocks this recipe.
     *
     * @param string    the criterion name
     * @param criterion the criterion
     *
     * @return {@code this}, for chaining
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @Contract(value = "_, _ -> this", mutates = "this")
    public @Override RecipeBuilder unlockedBy(final String string, final Criterion<?> criterion) {
        this.criteria.put(string, criterion);
        return this;
    }

    /**
     * Sets the recipe group.
     *
     * @param group the group name, or {@code null} to clear
     *
     * @return {@code this}, for chaining
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @Contract(value = "_ -> this", mutates = "this")
    public @Override BreakableShapelessRecipeBuilder group(final @Nullable String group) {
        this.group = group;
        return this;
    }

    /**
     * Returns the result item.
     *
     * @return the item produced by this recipe
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @Contract(pure = true)
    public @Override Item getResult() {
        return this.result.asItem();
    }

    /**
     * Validates the builder state and writes the recipe and its unlock advancement to
     * {@code output}.
     *
     * @param output the recipe output sink
     * @param id     the recipe resource location
     *
     * @throws IllegalStateException if no unlock criteria have been added
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @Contract(mutates = "param1")
    public @Override void save(final RecipeOutput output, final ResourceLocation id) {
        this.ensureValid(id);
        Advancement.Builder builder = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(builder::addCriterion);
        BreakableShapelessRecipe shapelessRecipe = new BreakableShapelessRecipe(
                Objects.requireNonNullElse(this.group, ""), RecipeBuilder.determineBookCategory(this.category),
                new ItemStack(this.result, this.count), this.ingredients, this.damage);
        output.accept(id, shapelessRecipe,
                builder.build(id.withPrefix("recipes/" + this.category.getFolderName() + "/")));
    }

    private void ensureValid(ResourceLocation resourceLocation) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + resourceLocation);
        }
    }
}
