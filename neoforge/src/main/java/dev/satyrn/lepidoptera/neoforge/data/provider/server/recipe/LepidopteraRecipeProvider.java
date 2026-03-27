package dev.satyrn.lepidoptera.neoforge.data.provider.server.recipe;

import dev.satyrn.lepidoptera.LepidopteraAPI;
import dev.satyrn.lepidoptera.api.ModHelper;
import dev.satyrn.lepidoptera.api.item.crafting.builder.BreakableShapedRecipeBuilder;
import dev.satyrn.lepidoptera.api.item.crafting.builder.BreakableShapelessRecipeBuilder;
import dev.satyrn.lepidoptera.item.LepidopteraItems;
import dev.satyrn.lepidoptera.neoforge.api.provider.server.recipe.ConditionalDataBuilder;
import dev.satyrn.lepidoptera.neoforge.api.provider.server.recipe.ModRecipeProvider;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class LepidopteraRecipeProvider extends ModRecipeProvider {

    public LepidopteraRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(LepidopteraAPI.class, output, lookupProvider);
    }

    /**
     * Creates an {@link InventoryChangeTrigger} criterion matching any slot containing the given item.
     */
    private static Criterion<?> inventoryChange(final ItemPredicate predicate) {
        return CriteriaTriggers.INVENTORY_CHANGED.createCriterion(
                new InventoryChangeTrigger.TriggerInstance(Optional.empty(),
                        InventoryChangeTrigger.TriggerInstance.Slots.ANY, List.of(predicate)));
    }

    @Override
    protected CompletableFuture<?> runModded(final CachedOutput cacheOutput,
                                                       final HolderLookup.Provider registryAccess) {
        // Shared criteria - reused across multiple recipes
        final Criterion<?> hasAlembic = inventoryChange(
                ItemPredicate.Builder.item().of(LepidopteraItems.ALCHEMICAL_ALEMBIC.get()).build());
        final Criterion<?> hasDepletedAlembic = inventoryChange(
                ItemPredicate.Builder.item().of(LepidopteraItems.DEPLETED_ALEMBIC.get()).build());

        final List<CompletableFuture<?>> futures = new ArrayList<>();

        // Alchemical Alembic: soul fire base + depleted alembic + candle + glowstone dust
        futures.add(saveWithConditions(cacheOutput, ConditionalDataBuilder.wrapRecipeBuilder(
                                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, LepidopteraItems.ALCHEMICAL_ALEMBIC.get())
                                        .requires(ItemTags.SOUL_FIRE_BASE_BLOCKS)
                                        .requires(LepidopteraItems.DEPLETED_ALEMBIC.get())
                                        .requires(ItemTags.CANDLES)
                                        .requires(Items.GLOWSTONE_DUST)
                                        .requires(Items.BLAZE_POWDER)
                                        .group("alchemical_alembic")
                                        .unlockedBy("has_the_alembic", hasAlembic)
                                        .unlockedBy("has_the_blaze_powder",
                                                inventoryChange(ItemPredicate.Builder.item().of(Items.BLAZE_POWDER).build()))
                                        .unlockedBy("has_the_soul_soil", inventoryChange(
                                                ItemPredicate.Builder.item().of(ItemTags.SOUL_FIRE_BASE_BLOCKS).build()))
                                        .unlockedBy("has_the_candle",
                                                inventoryChange(ItemPredicate.Builder.item().of(ItemTags.CANDLES).build()))
                                        .unlockedBy("has_the_glowstone_dust",
                                                inventoryChange(ItemPredicate.Builder.item().of(Items.GLOWSTONE_DUST).build()))
                                        .unlockedBy("has_the_depleted_alembic", hasDepletedAlembic))
                        .addCondition(LepidopteraAPI.ALCHEMICAL_ALEMBIC_RECIPES_CONDITION),
                ModHelper.resource(metadata, "alchemical_alembic"), registryAccess));

        // Depleted Alembic: iron nugget + iron ingot / glass bottle / iron ingot _ iron ingot
        //  "NI "
        //  " B "
        //  "I I"
        futures.add(saveWithConditions(cacheOutput, ConditionalDataBuilder.wrapRecipeBuilder(
                                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, LepidopteraItems.DEPLETED_ALEMBIC.get())
                                        .define('B', Items.GLASS_BOTTLE)
                                        .define('I', Items.IRON_INGOT)
                                        .define('N', Items.IRON_NUGGET)
                                        .pattern("NI ")
                                        .pattern(" B ")
                                        .pattern("I I")
                                        .group("depleted_alembic")
                                        .unlockedBy("has_the_depleted_alembic", hasDepletedAlembic)
                                        .unlockedBy("has_the_flask",
                                                inventoryChange(ItemPredicate.Builder.item().of(Items.GLASS_BOTTLE).build())))
                        .addCondition(LepidopteraAPI.ALCHEMICAL_ALEMBIC_RECIPES_CONDITION),
                ModHelper.resource(metadata, "depleted_alembic"), registryAccess));

        // Shapeless: Alchemical Alembic + Iron Ingot -> Gold Ingot (damage=1)
        futures.add(saveWithConditions(cacheOutput, ConditionalDataBuilder.wrapRecipeBuilder(
                                BreakableShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.GOLD_INGOT, 1, 1)
                                        .requires(LepidopteraItems.ALCHEMICAL_ALEMBIC.get())
                                        .requires(Items.IRON_INGOT)
                                        .group("gold_ingot")
                                        .unlockedBy("has_the_alembic", hasAlembic))
                        .addCondition(LepidopteraAPI.ALCHEMICAL_ALEMBIC_RECIPES_CONDITION),
                ResourceLocation.fromNamespaceAndPath(LepidopteraAPI.MOD_ID, "alembic/gold_from_iron"),
                registryAccess));

        // Shapeless: Alchemical Alembic + Coal -> Gold Ingot (damage=3)
        futures.add(saveWithConditions(cacheOutput, ConditionalDataBuilder.wrapRecipeBuilder(
                                BreakableShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.GOLD_INGOT, 1, 3)
                                        .requires(LepidopteraItems.ALCHEMICAL_ALEMBIC.get())
                                        .requires(Items.COAL)
                                        .group("gold_ingot")
                                        .unlockedBy("has_the_alembic", hasAlembic))
                        .addCondition(LepidopteraAPI.ALCHEMICAL_ALEMBIC_RECIPES_CONDITION),
                ModHelper.resource(metadata, "alembic/gold_from_coal"), registryAccess));

        // Shapeless: Alchemical Alembic + Emerald -> Gold Ingot (damage=0)
        futures.add(saveWithConditions(cacheOutput, ConditionalDataBuilder.wrapRecipeBuilder(
                                BreakableShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.GOLD_INGOT, 1, 0)
                                        .requires(LepidopteraItems.ALCHEMICAL_ALEMBIC.get())
                                        .requires(Items.EMERALD)
                                        .group("gold_ingot")
                                        .unlockedBy("has_the_alembic", hasAlembic))
                        .addCondition(LepidopteraAPI.ALCHEMICAL_ALEMBIC_RECIPES_CONDITION),
                ModHelper.resource(metadata, "alembic/gold_from_emerald"), registryAccess));

        // Shaped: Soul sand cross with alembic center -> Gunpowder (damage=1)
        //  " S "
        //  "SAS"
        //  " S "
        futures.add(saveWithConditions(cacheOutput, ConditionalDataBuilder.wrapRecipeBuilder(
                                BreakableShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.GUNPOWDER, 1, 1)
                                        .define('S', Items.SOUL_SAND)
                                        .define('A', LepidopteraItems.ALCHEMICAL_ALEMBIC.get())
                                        .pattern(" S ")
                                        .pattern("SAS")
                                        .pattern(" S ")
                                        .group("gunpowder")
                                        .unlockedBy("has_the_alembic", hasAlembic))
                        .addCondition(LepidopteraAPI.ALCHEMICAL_ALEMBIC_RECIPES_CONDITION),
                ResourceLocation.fromNamespaceAndPath(LepidopteraAPI.MOD_ID, "alembic/gunpowder"), registryAccess));

        // Shaped: Surrounded alembic with coal -> Diamond (damage=max)
        // "CCC"
        // "CAC"
        // "CCC"
        futures.add(saveWithConditions(cacheOutput, ConditionalDataBuilder.wrapRecipeBuilder(
                                BreakableShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.DIAMOND, 1, Short.MAX_VALUE)
                                        .define('C', Items.COAL)
                                        .define('A', LepidopteraItems.ALCHEMICAL_ALEMBIC.get())
                                        .pattern("CCC")
                                        .pattern("CAC")
                                        .pattern("CCC")
                                        .group("diamond")
                                        .unlockedBy("has_the_alembic", hasAlembic))
                        .addCondition(LepidopteraAPI.ALCHEMICAL_ALEMBIC_RECIPES_CONDITION),
                ModHelper.resource(metadata, "alembic/diamond"), registryAccess));

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }
}
