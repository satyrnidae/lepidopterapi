package dev.satyrn.lepidoptera.api.item.crafting.builder;

import dev.satyrn.lepidoptera.api.annotations.Api;
import dev.satyrn.lepidoptera.api.item.crafting.BreakableShapedRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Builds a {@link BreakableShapedRecipe} for use in data providers.
 *
 * <p>Usage mirrors {@code ShapedRecipeBuilder}: call {@link #define}, {@link #pattern},
 * and optionally {@link #group}, {@link #damage}, then {@link #save}.
 */
@Api
public final class BreakableShapedRecipeBuilder implements RecipeBuilder {

    private final RecipeCategory category;
    private final Item result;
    private final int count;
    private final List<String> rows = new ArrayList<>();
    private final Map<Character, Ingredient> key = new LinkedHashMap<>();
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    private @Nullable String group;
    private final int damage;
    private final boolean showNotification;

    private BreakableShapedRecipeBuilder(RecipeCategory category, ItemLike result, int count, int damage, boolean showNotification) {
        this.category = category;
        this.result = result.asItem();
        this.count = count;
        this.damage = damage;
        this.showNotification = showNotification;
    }

    /**
     * Creates a builder for a single-output, 1-damage breakable shaped recipe.
     *
     * @param category the recipe category
     * @param result   the result item
     * @return a new builder
     */
    public static BreakableShapedRecipeBuilder shaped(RecipeCategory category, ItemLike result) {
        return shaped(category, result, 1, 1, true);
    }

    /**
     * Creates a builder for a multi-output, 1-damage breakable shaped recipe.
     *
     * @param category the recipe category
     * @param result   the result item
     * @param count    the number of result items
     * @return a new builder
     */
    public static BreakableShapedRecipeBuilder shaped(RecipeCategory category, ItemLike result, int count) {
        return shaped(category, result, count, 1, true);
    }

    /**
     * Creates a builder for a breakable shaped recipe with a custom damage value.
     *
     * @param category the recipe category
     * @param result   the result item
     * @param count    the number of result items
     * @param damage   the damage applied to damageable crafting-remaining items
     * @return a new builder
     */
    public static BreakableShapedRecipeBuilder shaped(RecipeCategory category, ItemLike result, int count, int damage) {
        return shaped(category, result, count, damage, true);
    }

    /**
     * Creates a fully-specified builder for a breakable shaped recipe.
     *
     * @param category         the recipe category
     * @param result           the result item
     * @param count            the number of result items
     * @param damage           the damage applied to damageable crafting-remaining items
     * @param showNotification whether to show the recipe unlock toast
     * @return a new builder
     */
    public static BreakableShapedRecipeBuilder shaped(RecipeCategory category, ItemLike result, int count, int damage, boolean showNotification) {
        return new BreakableShapedRecipeBuilder(category, result, count, damage, showNotification);
    }

    /**
     * Maps a character symbol to an ingredient item.
     *
     * @param symbol the pattern character to define
     * @param item   the item matched by this symbol
     * @return {@code this}, for chaining
     * @throws IllegalArgumentException if {@code symbol} is already defined or is {@code ' '}
     */
    public BreakableShapedRecipeBuilder define(char symbol, ItemLike item) {
        return define(symbol, Ingredient.of(item));
    }

    /**
     * Maps a character symbol to an ingredient.
     *
     * @param symbol     the pattern character to define
     * @param ingredient the ingredient matched by this symbol
     * @return {@code this}, for chaining
     * @throws IllegalArgumentException if {@code symbol} is already defined or is {@code ' '}
     */
    public BreakableShapedRecipeBuilder define(char symbol, Ingredient ingredient) {
        if (key.containsKey(symbol)) {
            throw new IllegalArgumentException("Symbol '" + symbol + "' is already defined");
        }
        if (symbol == ' ') {
            throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved");
        }
        key.put(symbol, ingredient);
        return this;
    }

    /**
     * Adds a row to the shaped pattern.
     *
     * @param row a string of symbols (and spaces for empty slots) representing one row
     * @return {@code this}, for chaining
     */
    public BreakableShapedRecipeBuilder pattern(String row) {
        rows.add(row);
        return this;
    }

    /**
     * Adds an advancement criterion that unlocks this recipe.
     *
     * @param string    the criterion name
     * @param criterion the criterion
     * @return {@code this}, for chaining
     */
    @Override
    public RecipeBuilder unlockedBy(String string, Criterion<?> criterion) {
        this.criteria.put(string, criterion);
        return this;
    }

    /**
     * Sets the recipe group.
     *
     * @param group the group name, or {@code null} to clear
     * @return {@code this}, for chaining
     */
    public BreakableShapedRecipeBuilder group(@Nullable String group) {
        this.group = group;
        return this;
    }

    /**
     * Returns the result item.
     *
     * @return the item produced by this recipe
     */
    @Override
    public Item getResult() {
        return this.result;
    }

    /**
     * Validates the builder state and writes the recipe and its unlock advancement to
     * {@code output}.
     *
     * @param output the recipe output sink
     * @param id     the recipe resource location
     * @throws IllegalStateException if no unlock criteria have been added
     */
    public void save(RecipeOutput output, ResourceLocation id) {
        ShapedRecipePattern pattern = this.ensureValid(id);
        Advancement.Builder builder = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(builder::addCriterion);
        BreakableShapedRecipe shapedRecipe = new BreakableShapedRecipe(
                Objects.requireNonNullElse(this.group, ""),
                RecipeBuilder.determineBookCategory(this.category),
                pattern,
                new ItemStack(this.result, this.count),
                this.damage,
                this.showNotification);
        output.accept(id, shapedRecipe, builder.build(id.withPrefix("recipes/" + this.category.getFolderName() + "/")));
    }

    private ShapedRecipePattern ensureValid(ResourceLocation id) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + id);
        }
        return ShapedRecipePattern.of(this.key, this.rows);
    }
}
