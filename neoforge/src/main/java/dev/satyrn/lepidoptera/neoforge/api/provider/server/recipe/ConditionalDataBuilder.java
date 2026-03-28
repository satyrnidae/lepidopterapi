package dev.satyrn.lepidoptera.neoforge.api.provider.server.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * A generic data-generation wrapper that appends NeoForge and Fabric load-conditions to any
 * JSON data the inner producer writes.
 *
 * <h2>Generic use</h2>
 * <pre>{@code
 * ConditionalDataBuilder.wrap(
 *     (cachedOutput, id, registryAccess, packOutput, neoConditions, fabricConditions) -> {
 *         JsonObject json = buildMyJson(registryAccess);
 *         json.add("neoforge:conditions", neoConditions);
 *         json.add("fabric:load_conditions", fabricConditions);
 *         Path path = packOutput.createPathProvider(PackOutput.Target.DATA_PACK, "my_type").json(id);
 *         return DataProvider.saveStable(cachedOutput, json, path);
 *     })
 *     .addCondition(MY_CONDITION)
 *     .save(cachedOutput, id, registryAccess, packOutput);
 * }</pre>
 *
 * <h2>Recipe builder shortcut</h2>
 * <pre>{@code
 * ConditionalDataBuilder.wrapRecipeBuilder(
 *         BreakableShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.GOLD_INGOT)
 *                 .requires(Items.IRON_INGOT)
 *                 .unlockedBy("has_iron", CriteriaTriggers.INVENTORY_CHANGED.createCriterion(...)))
 *         .addCondition(MY_CONDITION)
 *         .save(cachedOutput, id, registryAccess, packOutput);
 * }</pre>
 *
 * @since 1.0.0-SNAPSHOT.1+1.21.1
 */
@ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
public final class ConditionalDataBuilder {

    private final JsonProducer producer;
    private final List<ResourceLocation> neoForgeConditions = new ArrayList<>();
    private final List<ResourceLocation> fabricConditions = new ArrayList<>();

    private ConditionalDataBuilder(final JsonProducer producer) {
        this.producer = producer;
    }

    /**
     * Creates a {@link ConditionalDataBuilder} wrapping the given generic JSON producer.
     *
     * <p>The producer receives the pre-built condition arrays when {@link #save} is called.
     * It is responsible for injecting them into its JSON and saving to disk.
     *
     * @param producer the JSON-producing function to wrap
     *
     * @return a new builder
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @Contract("_ -> new")
    public static ConditionalDataBuilder wrap(final JsonProducer producer) {
        return new ConditionalDataBuilder(producer);
    }

    /**
     * Creates a {@link ConditionalDataBuilder} wrapping any {@link RecipeBuilder}.
     *
     * <p>When saved, this will:
     * <ol>
     *   <li>Run the inner builder against a capturing {@link RecipeOutput} to extract
     *       the {@link Recipe} and its accompanying {@link AdvancementHolder} without
     *       writing any files.</li>
     *   <li>Re-encode the recipe to JSON via its own serializer codec, then inject
     *       both {@code neoforge:conditions} and {@code fabric:load_conditions} arrays.</li>
     *   <li>Re-encode the advancement to JSON and inject the same condition arrays.</li>
     *   <li>Write both files via {@link DataProvider#saveStable}.</li>
     * </ol>
     *
     * <p>The advancement path is derived from {@link AdvancementHolder#id()}, which the
     * inner builder sets to {@code {namespace}:recipes/{category}/{name}}. This matches
     * the path convention used by {@link ModRecipeProvider}.
     *
     * @param inner the recipe builder to wrap
     *
     * @return a new builder
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @Contract("_ -> new")
    public static ConditionalDataBuilder wrapRecipeBuilder(final RecipeBuilder inner) {
        return wrap((cachedOutput, id, registryAccess, packOutput, neoForgeConditions, fabricConditions) -> {
            final CapturingRecipeOutput capture = new CapturingRecipeOutput();
            inner.save(capture, id);

            if (capture.capturedRecipe == null) {
                throw new IllegalStateException("Inner RecipeBuilder did not produce a recipe for id: " + id);
            }

            final PackOutput.PathProvider recipePaths = packOutput.createPathProvider(PackOutput.Target.DATA_PACK,
                    "recipe");
            final PackOutput.PathProvider advancementPaths = packOutput.createPathProvider(PackOutput.Target.DATA_PACK,
                    "advancement");

            final List<CompletableFuture<?>> futures = new ArrayList<>();
            futures.add(writeRecipeJson(cachedOutput, id, capture.capturedRecipe, neoForgeConditions, fabricConditions,
                    recipePaths, registryAccess));

            if (capture.capturedAdvancement != null) {
                futures.add(writeAdvancementJson(cachedOutput, capture.capturedAdvancement, neoForgeConditions,
                        fabricConditions, advancementPaths, registryAccess));
            }

            return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
        });
    }

    private static JsonArray buildNeoForgeArray(final List<ResourceLocation> conditions) {
        final JsonArray arr = new JsonArray();
        for (final ResourceLocation rl : conditions) {
            final JsonObject obj = new JsonObject();
            obj.addProperty("type", rl.toString());
            arr.add(obj);
        }
        return arr;
    }

    private static JsonArray buildFabricArray(final List<ResourceLocation> conditions) {
        final JsonArray arr = new JsonArray();
        for (final ResourceLocation rl : conditions) {
            final JsonObject obj = new JsonObject();
            obj.addProperty("condition", rl.toString());
            arr.add(obj);
        }
        return arr;
    }

    /**
     * Encodes the recipe using its own serializer codec, injects condition arrays, and
     * saves to {@code data/{namespace}/recipe/{path}.json}.
     *
     * <p>The {@code "type"} field is derived from the recipe's registered serializer in
     * {@link BuiltInRegistries#RECIPE_SERIALIZER} rather than being passed explicitly,
     * so this helper works for any registered recipe type.
     */
    @SuppressWarnings("unchecked") // RecipeSerializer always handles its own concrete Recipe type.
    private static CompletableFuture<?> writeRecipeJson(final CachedOutput cachedOutput,
                                                        final ResourceLocation id,
                                                        final Recipe<?> recipe,
                                                        final JsonArray neoForgeConditions,
                                                        final JsonArray fabricConditions,
                                                        final PackOutput.PathProvider recipePaths,
                                                        final HolderLookup.Provider registryAccess) {
        final RecipeSerializer<Recipe<?>> serializer = (RecipeSerializer<Recipe<?>>) recipe.getSerializer();
        final ResourceLocation typeId = Objects.requireNonNull(BuiltInRegistries.RECIPE_SERIALIZER.getKey(serializer),
                () -> "Recipe serializer not registered in BuiltInRegistries: " + serializer);

        final JsonObject json = serializer.codec()
                .codec()
                .encodeStart(registryAccess.createSerializationContext(JsonOps.INSTANCE), recipe)
                .getOrThrow()
                .getAsJsonObject();

        json.addProperty("type", typeId.toString());

        if (!neoForgeConditions.isEmpty()) {
            json.add("neoforge:conditions", neoForgeConditions);
        }
        if (!fabricConditions.isEmpty()) {
            json.add("fabric:load_conditions", fabricConditions);
        }

        final Path path = recipePaths.json(id);
        return DataProvider.saveStable(cachedOutput, json, path);
    }

    /**
     * Encodes the advancement using {@link Advancement#CODEC}, injects condition arrays, and
     * saves to the path encoded in {@link AdvancementHolder#id()}.
     *
     * <p>The inner recipe builder sets the advancement id to
     * {@code {namespace}:recipes/{category}/{name}} before passing it to
     * {@link RecipeOutput#accept}, so {@link AdvancementHolder#id()} already contains the
     * correct full path - no category lookup is required here.
     */
    private static CompletableFuture<?> writeAdvancementJson(final CachedOutput cachedOutput,
                                                             final AdvancementHolder holder,
                                                             final JsonArray neoForgeConditions,
                                                             final JsonArray fabricConditions,
                                                             final PackOutput.PathProvider advancementPaths,
                                                             final HolderLookup.Provider registryAccess) {
        final JsonObject json = Advancement.CODEC.encodeStart(
                        registryAccess.createSerializationContext(JsonOps.INSTANCE), holder.value())
                .getOrThrow()
                .getAsJsonObject();

        if (!neoForgeConditions.isEmpty()) {
            json.add("neoforge:conditions", neoForgeConditions);
        }
        if (!fabricConditions.isEmpty()) {
            json.add("fabric:load_conditions", fabricConditions);
        }

        // holder.id() is already "namespace:recipes/{category}/{name}"
        final Path path = advancementPaths.json(holder.id());
        return DataProvider.saveStable(cachedOutput, json, path);
    }

    /**
     * Adds a condition to the {@code neoforge:conditions} array.
     *
     * <p>The condition is identified by its registered {@link ResourceLocation}; e.g.
     * {@code mymod:my_condition}. In the output JSON this becomes
     * {@code { "type": "mymod:my_condition" }}.
     *
     * @param condition the condition identifier to add
     *
     * @return {@code this}, for chaining
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @Contract(value = "_ -> this", mutates = "this")
    public ConditionalDataBuilder addNeoForgeCondition(final ResourceLocation condition) {
        neoForgeConditions.add(condition);
        return this;
    }

    /**
     * Adds a condition to the {@code fabric:load_conditions} array.
     *
     * <p>The condition is identified by its registered {@link ResourceLocation}; e.g.
     * {@code mymod:my_condition}. In the output JSON this becomes
     * {@code { "condition": "mymod:my_condition" }}.
     *
     * @param condition the condition identifier to add
     *
     * @return {@code this}, for chaining
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @Contract(value = "_ -> this", mutates = "this")
    public ConditionalDataBuilder addFabricCondition(final ResourceLocation condition) {
        fabricConditions.add(condition);
        return this;
    }

    /**
     * Adds a condition to both the {@code neoforge:conditions} and
     * {@code fabric:load_conditions} arrays.
     *
     * <p>This is the most common call when the same {@link ResourceLocation} is registered
     * on both platforms (which is the convention in this library).
     *
     * @param condition the condition identifier to add to both lists
     *
     * @return {@code this}, for chaining
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @Contract(value = "_ -> this", mutates = "this")
    public ConditionalDataBuilder addCondition(final ResourceLocation condition) {
        neoForgeConditions.add(condition);
        fabricConditions.add(condition);
        return this;
    }

    /**
     * Builds the condition arrays from the accumulated condition lists and delegates to
     * the wrapped {@link JsonProducer}.
     *
     * @param cachedOutput   the data-gen output cache
     * @param id             the primary resource location for this data entry
     * @param registryAccess the current registry lookup context
     * @param packOutput     the data-pack output root
     *
     * @return a future that completes when all files have been written
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public CompletableFuture<?> save(final CachedOutput cachedOutput,
                                     final ResourceLocation id,
                                     final HolderLookup.Provider registryAccess,
                                     final PackOutput packOutput) {
        return producer.produce(cachedOutput, id, registryAccess, packOutput, buildNeoForgeArray(neoForgeConditions),
                buildFabricArray(fabricConditions));
    }

    /**
     * Produces one or more data-pack JSON files given the standard data-gen context and
     * pre-built condition arrays.
     *
     * <p>The producer is responsible for deciding which JSON files to write and where to
     * inject the condition arrays. Both arrays may be empty when no conditions have been
     * added to the owning {@link ConditionalDataBuilder}.
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @FunctionalInterface
    public interface JsonProducer {
        /**
         * Writes data-pack JSON, injecting the given condition arrays where appropriate.
         *
         * @param cachedOutput       the data-gen output cache
         * @param id                 the primary resource location for this data entry
         * @param registryAccess     the current registry lookup context
         * @param packOutput         the data-pack output root
         * @param neoForgeConditions pre-built {@code neoforge:conditions} array (may be empty)
         * @param fabricConditions   pre-built {@code fabric:load_conditions} array (may be empty)
         *
         * @return a future that completes when all files have been written
         *
         * @since 1.0.0-SNAPSHOT.1+1.21.1
         */
        @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
        CompletableFuture<?> produce(CachedOutput cachedOutput,
                                     ResourceLocation id,
                                     HolderLookup.Provider registryAccess,
                                     PackOutput packOutput,
                                     JsonArray neoForgeConditions,
                                     JsonArray fabricConditions);
    }

    /**
     * A minimal {@link RecipeOutput} that captures the recipe and advancement holder from
     * the inner builder's {@link RecipeOutput#accept} call without writing any files.
     *
     * <p>{@link RecipeOutput#advancement()} returns a bare {@code recipeAdvancement()} builder
     * so that builders which call it (e.g. {@link net.minecraft.data.recipes.ShapedRecipeBuilder})
     * receive a valid object to build against.
     */
    private static final class CapturingRecipeOutput implements RecipeOutput {
        @Nullable
        Recipe<?> capturedRecipe;
        @Nullable
        AdvancementHolder capturedAdvancement;

        /**
         * NeoForge patches {@link RecipeOutput} via {@code IRecipeOutputExtension} so that this
         * four-arg overload is the abstract method; the vanilla three-arg
         * {@code accept(id, recipe, advancement)} becomes a default that delegates here.
         * Conditions passed by the inner builder are intentionally ignored - {@link ConditionalDataBuilder}
         * manages all conditions independently.
         */
        @Override
        public void accept(final ResourceLocation id,
                           final Recipe<?> recipe,
                           @Nullable final AdvancementHolder advancement,
                           final ICondition... conditions) {
            this.capturedRecipe = recipe;
            this.capturedAdvancement = advancement;
        }

        @Override
        public Advancement.Builder advancement() {
            return Advancement.Builder.recipeAdvancement();
        }
    }
}
