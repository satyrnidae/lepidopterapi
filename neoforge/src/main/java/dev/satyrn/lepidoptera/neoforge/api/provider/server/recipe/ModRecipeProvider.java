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
    protected final ModMeta metadata;
    private final PackOutput packOutput;

    protected ModRecipeProvider(Class<?> modClass,
                                PackOutput output,
                                CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
        this.packOutput = output;
        this.metadata = ModHelper.metadata(modClass);
    }

    private static <T> CompletableFuture<?> buildRecipeAchievement(CachedOutput cachedOutput,
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

    protected final @Override CompletableFuture<?> run(CachedOutput arg, HolderLookup.Provider arg2) {
        return CompletableFuture.allOf(super.run(arg, arg2), runModded(arg, arg2));
    }

    protected final @Override void buildRecipes(RecipeOutput output) {
        buildModRecipes(output);
    }

    protected void buildModRecipes(RecipeOutput output) {
    }

    /**
     * Returns the {@link PackOutput} for this provider, for use with
     * {@link ConditionalDataBuilder#save}.
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    protected PackOutput packOutput() {
        return this.packOutput;
    }

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

    @ApiStatus.AvailableSince("0.4.0+1.19.2")
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

    public @Override ResourceLocation location() {
        return ModHelper.resource(metadata, "providers/recipe");
    }
}