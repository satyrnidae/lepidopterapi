package dev.satyrn.lepidoptera.neoforge.api.provider.server.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import dev.satyrn.lepidoptera.api.ModHelper;
import dev.satyrn.lepidoptera.api.ModMeta;
import dev.satyrn.lepidoptera.api.WithLocation;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Abstract base for mod-specific recipe data providers.
 *
 * <p>Subclass this and implement {@link #buildModRecipes} to add recipes for your mod.
 * Wire the provider into your {@code GatherDataEvent} listener via
 * {@code event.createProvider((output, lookup) -> new YourRecipeProvider(output, lookup))}.
 */
@ApiStatus.AvailableSince("0.4.0+1.19.2")
public abstract class ModRecipeProvider extends RecipeProvider implements WithLocation {
    /**
     * The mod metadata resolved from the mod class passed to the constructor.
     *
     * @since 0.4.0+1.19.2
     */
    @ApiStatus.AvailableSince("0.4.0+1.19.2")
    protected final ModMeta metadata;
    private final PackOutput packOutput;

    /**
     * Creates a new recipe provider for the given mod class.
     *
     * @param modClass       the mod's main class, annotated with {@link dev.satyrn.lepidoptera.api.ModMeta}
     * @param output         the data-gen pack output
     * @param lookupProvider a future providing the registry lookup context
     *
     * @since 0.4.0+1.19.2
     */
    @ApiStatus.AvailableSince("0.4.0+1.19.2")
    protected ModRecipeProvider(Class<?> modClass,
                                PackOutput output,
                                CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
        this.packOutput = output;
        this.metadata = ModHelper.metadata(modClass);
    }

    private static CompletableFuture<?> buildRecipeAchievement(CachedOutput cachedOutput,
                                                               ResourceLocation id,
                                                               PackOutput.PathProvider advancementPaths,
                                                               RecipeCategory category,
                                                               Map<String, Criterion<?>> criteria,
                                                               HolderLookup.Provider registryAccess) {
        Advancement.Builder builder = Advancement.Builder.recipeAdvancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);
        criteria.forEach(builder::addCriterion);
        Advancement advancement = builder.build(id).value();

        JsonObject json = Advancement.CODEC.encodeStart(registryAccess.createSerializationContext(JsonOps.INSTANCE),
                advancement).getOrThrow().getAsJsonObject();

        Path path = advancementPaths.json(id.withPrefix("recipes/" + category.getFolderName() + "/"));
        return DataProvider.saveStable(cachedOutput, json, path);
    }

    private static <T> CompletableFuture<?> buildRecipeWithConditions(CachedOutput cachedOutput,
                                                                      ResourceLocation id,
                                                                      T recipe,
                                                                      MapCodec<T> codec,
                                                                      ResourceLocation typeId,
                                                                      PackOutput.PathProvider recipePaths,
                                                                      HolderLookup.Provider registryAccess,
                                                                      ResourceLocation... conditions) {
        JsonArray fabricConditions = new JsonArray();
        JsonArray neoConditions = new JsonArray();

        for (var condition : conditions) {
            JsonObject fabricCondition = new JsonObject();
            fabricCondition.addProperty("condition", condition.toString());
            fabricConditions.add(fabricCondition);

            JsonObject neoCondition = new JsonObject();
            neoCondition.addProperty("type", condition.toString());
            neoConditions.add(neoCondition);
        }

        JsonObject json = codec.codec()
                .encodeStart(registryAccess.createSerializationContext(JsonOps.INSTANCE), recipe)
                .getOrThrow()
                .getAsJsonObject();
        json.addProperty("type", typeId.toString());
        json.add("neoforge:conditions", neoConditions);
        json.add("fabric:load_conditions", fabricConditions);

        Path path = recipePaths.json(id);
        return DataProvider.saveStable(cachedOutput, json, path);
    }

    /**
     * Runs both the standard recipe output and {@link #runModded}.
     *
     * @param cachedOutput   the data-gen output cache
     * @param registryAccess the current registry lookup context
     *
     * @return a future that completes when all outputs have been written
     *
     * @since 0.4.0+1.19.2
     */
    @ApiStatus.AvailableSince("0.4.0+1.19.2")
    @Override
    protected final CompletableFuture<?> run(CachedOutput cachedOutput, HolderLookup.Provider registryAccess) {
        return CompletableFuture.allOf(super.run(cachedOutput, registryAccess), runModded(cachedOutput, registryAccess));
    }

    /**
     * Delegates to {@link #buildModRecipes} so subclasses override the mod-specific
     * method rather than the NeoForge framework entry point.
     *
     * @param output the recipe output to write recipes into
     *
     * @since 0.4.0+1.19.2
     */
    @ApiStatus.AvailableSince("0.4.0+1.19.2")
    @Override
    protected final void buildRecipes(RecipeOutput output) {
        buildModRecipes(output);
    }

    /**
     * Override to register recipes via the standard {@link RecipeOutput} API.
     * Called in place of {@code buildRecipes} during data generation.
     *
     * <p>Defaults to a no-op.</p>
     *
     * @param output the recipe output to write recipes into
     *
     * @since 0.4.0+1.19.2
     */
    @ApiStatus.AvailableSince("0.4.0+1.19.2")
    protected void buildModRecipes(@SuppressWarnings("unused") RecipeOutput output) {
    }

    /**
     * Returns the {@link PackOutput} for this provider, for use with
     * {@link ConditionalDataBuilder#save}.
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @SuppressWarnings("unused")
    protected PackOutput packOutput() {
        return this.packOutput;
    }

    /**
     * Override to perform additional data-gen work alongside the standard recipe output.
     * Runs concurrently with the parent {@code run} call.
     *
     * <p>Defaults to a no-op.</p>
     *
     * @param cachedOutput   the data-gen output cache
     * @param registryAccess the current registry lookup context
     *
     * @return a future that completes when all additional outputs have been written
     *
     * @since 0.4.0+1.19.2
     */
    @ApiStatus.AvailableSince("0.4.0+1.19.2")
    protected CompletableFuture<?> runModded(CachedOutput cachedOutput, HolderLookup.Provider registryAccess) {
        // Defaults to no-op
        return CompletableFuture.runAsync(() -> {
        });
    }

    /**
     * Convenience overload that delegates to
     * {@link ConditionalDataBuilder#save(CachedOutput, ResourceLocation, HolderLookup.Provider, PackOutput)}
     * using this provider's {@link PackOutput}.
     *
     * @param cachedOutput   the data-gen output cache
     * @param builder        the conditional builder to save
     * @param id             the primary resource location for this data entry
     * @param registryAccess the current registry lookup context
     *
     * @return a future that completes when all files have been written
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    protected CompletableFuture<?> saveWithConditions(final CachedOutput cachedOutput,
                                                      final ConditionalDataBuilder builder,
                                                      final ResourceLocation id,
                                                      final HolderLookup.Provider registryAccess) {
        return builder.save(cachedOutput, id, registryAccess, this.packOutput);
    }

    /**
     * Writes a recipe JSON with cross-platform load conditions and its accompanying
     * unlock advancement.
     *
     * <p>Both {@code neoforge:conditions} and {@code fabric:load_conditions} arrays are
     * injected into the recipe JSON. The advancement is written alongside the recipe
     * using the standard path convention.
     *
     * <p>Prefer {@link #saveWithConditions} with a {@link ConditionalDataBuilder} for new
     * code — this method is a lower-level alternative retained for backwards compatibility.</p>
     *
     * @param cachedOutput   the data-gen output cache
     * @param id             the resource location for the recipe (and advancement)
     * @param recipe         the recipe instance to encode
     * @param codec          the {@link MapCodec} used to encode {@code recipe}
     * @param typeId         the recipe type identifier written to the {@code "type"} field
     * @param category       the recipe category, used to derive the advancement path
     * @param criteria       the criteria to add to the unlock advancement
     * @param registryAccess the current registry lookup context
     * @param conditions     zero or more condition IDs to inject into both condition arrays
     * @param <T>            the recipe type
     *
     * @return a future that completes when both the recipe and advancement have been written
     *
     * @since 0.4.0+1.19.2
     */
    @ApiStatus.AvailableSince("0.4.0+1.19.2")
    @SuppressWarnings("unused")
    protected <T> CompletableFuture<?> recipeWithConditions(CachedOutput cachedOutput,
                                                            ResourceLocation id,
                                                            T recipe,
                                                            MapCodec<T> codec,
                                                            ResourceLocation typeId,
                                                            RecipeCategory category,
                                                            Map<String, Criterion<?>> criteria,
                                                            HolderLookup.Provider registryAccess,
                                                            ResourceLocation... conditions) {
        PackOutput.PathProvider recipePaths = packOutput.createPathProvider(PackOutput.Target.DATA_PACK, "recipe");
        PackOutput.PathProvider advancementPaths = packOutput.createPathProvider(PackOutput.Target.DATA_PACK,
                "advancement");

        return CompletableFuture.allOf(
                buildRecipeWithConditions(cachedOutput, id, recipe, codec, typeId, recipePaths, registryAccess,
                        conditions),
                buildRecipeAchievement(cachedOutput, id, advancementPaths, category, criteria, registryAccess));
    }

    /**
     * {@inheritDoc}
     *
     * @since 0.4.0+1.19.2
     */
    @ApiStatus.AvailableSince("0.4.0+1.19.2")
    @Override
    public ResourceLocation location() {
        return ModHelper.resource(metadata, "providers/recipe");
    }
}