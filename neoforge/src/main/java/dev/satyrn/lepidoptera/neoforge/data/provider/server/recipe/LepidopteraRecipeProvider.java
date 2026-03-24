package dev.satyrn.lepidoptera.neoforge.data.provider.server.recipe;

import dev.satyrn.lepidoptera.LepidopteraAPI;
import dev.satyrn.lepidoptera.api.ModHelper;
import dev.satyrn.lepidoptera.api.item.crafting.BreakableShapedRecipe;
import dev.satyrn.lepidoptera.api.item.crafting.BreakableShapelessRecipe;
import dev.satyrn.lepidoptera.neoforge.api.provider.server.recipe.ModRecipeProvider;
import dev.satyrn.lepidoptera.item.LepidopteraItems;
import dev.satyrn.lepidoptera.item.crafting.LepidopteraRecipeSerializers;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class LepidopteraRecipeProvider extends ModRecipeProvider {

    public LepidopteraRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(LepidopteraAPI.class, output, lookupProvider);
    }

    @Override
    protected CompletableFuture<?> runModded(CachedOutput cacheOutput, HolderLookup.Provider registryAccess) {
        // Shapeless: Alchemical Alembic + Iron Ingot -> Gold Ingot (damage=1)
        BreakableShapelessRecipe shapelessRecipe = new BreakableShapelessRecipe(
                "gold_ingot",
                CraftingBookCategory.MISC,
                new ItemStack(Items.GOLD_INGOT),
                NonNullList.of(Ingredient.EMPTY,
                        Ingredient.of(LepidopteraItems.ALCHEMICAL_ALEMBIC.get()),
                        Ingredient.of(Items.IRON_INGOT)),
                1);

        // Shapeless: Alchemical Alembic + Coal -> Gold Ingot (damage=3)
        BreakableShapelessRecipe goldFromCoal = new BreakableShapelessRecipe(
                "gold_ingot",
                CraftingBookCategory.MISC,
                new ItemStack(Items.GOLD_INGOT),
                NonNullList.of(Ingredient.EMPTY,
                        Ingredient.of(LepidopteraItems.ALCHEMICAL_ALEMBIC.get()),
                        Ingredient.of(Items.COAL)),
                3);

        // Shapeless: Alchemical Alembic + Emerald -> Gold Ingot (damage=0)
        BreakableShapelessRecipe goldFromEmerald = new BreakableShapelessRecipe(
                "gold_ingot",
                CraftingBookCategory.MISC,
                new ItemStack(Items.GOLD_INGOT),
                NonNullList.of(Ingredient.EMPTY,
                        Ingredient.of(LepidopteraItems.ALCHEMICAL_ALEMBIC.get()),
                        Ingredient.of(Items.EMERALD)),
                0);

        // Alembic recipe itself
        ShapelessRecipe alembicRecipe = new ShapelessRecipe(
                "alchemical_alembic",
                CraftingBookCategory.MISC,
                new ItemStack(LepidopteraItems.ALCHEMICAL_ALEMBIC.get()),
                NonNullList.of(Ingredient.EMPTY,
                        Ingredient.of(ItemTags.SOUL_FIRE_BASE_BLOCKS),
                        Ingredient.of(LepidopteraItems.DEPLETED_ALEMBIC.get()),
                        Ingredient.of(ItemTags.CANDLES),
                        Ingredient.of(Items.GLOWSTONE_DUST)));

        // Depleted alembic recipe
        ShapedRecipePattern depletedAlembicPattern = ShapedRecipePattern.of(
                Map.of('B', Ingredient.of(Items.GLASS_BOTTLE),
                        'I', Ingredient.of(Items.IRON_INGOT),
                        'N', Ingredient.of(Items.IRON_NUGGET)),
                List.of("NI ", " B ", "I I")
        );
        ShapedRecipe depletedAlembicRecipe = new ShapedRecipe(
                "depleted_alembic",
                CraftingBookCategory.MISC,
                depletedAlembicPattern,
                new ItemStack(LepidopteraItems.DEPLETED_ALEMBIC.get()),
                true);


        // Shaped: Soul sand cross with alembic center -> Gunpowder (damage=1)
        //  " S "
        //  "SAS"
        //  " S "
        ShapedRecipePattern shapedPattern = ShapedRecipePattern.of(
                Map.of('S', Ingredient.of(Items.SOUL_SAND),
                       'A', Ingredient.of(LepidopteraItems.ALCHEMICAL_ALEMBIC.get())),
                List.of(" S ", "SAS", " S "));
        BreakableShapedRecipe shapedRecipe = new BreakableShapedRecipe(
                "gunpowder",
                CraftingBookCategory.MISC,
                shapedPattern,
                new ItemStack(Items.GUNPOWDER),
                1);

        // Shaped: Surrounded alembic with coal -> Diamond (damage = max)
        // "CCC"
        // "CAC"
        // "CCC"
        ShapedRecipePattern diamondFromCoalPattern = ShapedRecipePattern.of(
                Map.of('C', Ingredient.of(Items.COAL),
                        'A', Ingredient.of(LepidopteraItems.ALCHEMICAL_ALEMBIC.get())),
                List.of("CCC", "CAC", "CCC"));
        BreakableShapedRecipe diamondFromCoal = new BreakableShapedRecipe(
                "diamond",
                CraftingBookCategory.MISC,
                diamondFromCoalPattern,
                new ItemStack(Items.DIAMOND),
                Short.MAX_VALUE);

        List<CompletableFuture<?>> futures = new ArrayList<>();
        Map<String, Criterion<?>> criteria = new HashMap<>();
        criteria.put("has_the_alembic", CriteriaTriggers.INVENTORY_CHANGED.createCriterion(
                new InventoryChangeTrigger.TriggerInstance(
                        Optional.empty(),
                        InventoryChangeTrigger.TriggerInstance.Slots.ANY,
                        List.of(ItemPredicate.Builder.item().of(LepidopteraItems.ALCHEMICAL_ALEMBIC.get()).build())
                )));

        Map<String, Criterion<?>> alembicCriteria = new HashMap<>();
        alembicCriteria.put("has_the_alembic", CriteriaTriggers.INVENTORY_CHANGED.createCriterion(
                new InventoryChangeTrigger.TriggerInstance(
                        Optional.empty(),
                        InventoryChangeTrigger.TriggerInstance.Slots.ANY,
                        List.of(ItemPredicate.Builder.item().of(LepidopteraItems.ALCHEMICAL_ALEMBIC.get()).build())
                )));
        alembicCriteria.put("has_the_soul_soil", CriteriaTriggers.INVENTORY_CHANGED.createCriterion(
                new InventoryChangeTrigger.TriggerInstance(
                        Optional.empty(),
                        InventoryChangeTrigger.TriggerInstance.Slots.ANY,
                        List.of(ItemPredicate.Builder.item().of(ItemTags.SOUL_FIRE_BASE_BLOCKS).build())
                )));
        alembicCriteria.put("has_the_candle", CriteriaTriggers.INVENTORY_CHANGED.createCriterion(
                new InventoryChangeTrigger.TriggerInstance(
                        Optional.empty(),
                        InventoryChangeTrigger.TriggerInstance.Slots.ANY,
                        List.of(ItemPredicate.Builder.item().of(ItemTags.CANDLES).build())
                )));
        alembicCriteria.put("has_the_glowstone_dust", CriteriaTriggers.INVENTORY_CHANGED.createCriterion(
                new InventoryChangeTrigger.TriggerInstance(
                        Optional.empty(),
                        InventoryChangeTrigger.TriggerInstance.Slots.ANY,
                        List.of(ItemPredicate.Builder.item().of(Items.GLOWSTONE_DUST).build())
                )));
        alembicCriteria.put("has_the_depleted_alembic", CriteriaTriggers.INVENTORY_CHANGED.createCriterion(
                new InventoryChangeTrigger.TriggerInstance(
                        Optional.empty(),
                        InventoryChangeTrigger.TriggerInstance.Slots.ANY,
                        List.of(ItemPredicate.Builder.item().of(LepidopteraItems.DEPLETED_ALEMBIC.get()).build())
                )));

        Map<String, Criterion<?>> depletedAlembicCriteria = new HashMap<>();
        depletedAlembicCriteria.put("has_the_depleted_alembic", CriteriaTriggers.INVENTORY_CHANGED.createCriterion(
                new InventoryChangeTrigger.TriggerInstance(
                        Optional.empty(),
                        InventoryChangeTrigger.TriggerInstance.Slots.ANY,
                        List.of(ItemPredicate.Builder.item().of(LepidopteraItems.DEPLETED_ALEMBIC.get()).build())
                )));
        depletedAlembicCriteria.put("has_the_flask", CriteriaTriggers.INVENTORY_CHANGED.createCriterion(
                new InventoryChangeTrigger.TriggerInstance(
                        Optional.empty(),
                        InventoryChangeTrigger.TriggerInstance.Slots.ANY,
                        List.of(ItemPredicate.Builder.item().of(Items.GLASS_BOTTLE).build())
                )));
        futures.add(this.recipeWithConditions(cacheOutput,
                ModHelper.resource(metadata, "alchemical_alembic"),
                alembicRecipe, RecipeSerializer.SHAPELESS_RECIPE.codec(),
                ResourceLocation.withDefaultNamespace("crafting_shapeless"),
                RecipeCategory.TOOLS,
                alembicCriteria, registryAccess,
                LepidopteraAPI.ALCHEMICAL_ALEMBIC_RECIPES_CONDITION));
        futures.add(this.recipeWithConditions(cacheOutput,
                ModHelper.resource(metadata, "depleted_alembic"),
                depletedAlembicRecipe, RecipeSerializer.SHAPED_RECIPE.codec(),
                ResourceLocation.withDefaultNamespace("crafting_shaped"),
                RecipeCategory.TOOLS,
                depletedAlembicCriteria, registryAccess,
                LepidopteraAPI.ALCHEMICAL_ALEMBIC_RECIPES_CONDITION));
        futures.add(this.recipeWithConditions(cacheOutput,
                ResourceLocation.fromNamespaceAndPath(LepidopteraAPI.MOD_ID, "alembic/gold_from_iron"),
                shapelessRecipe, BreakableShapelessRecipe.Serializer.CODEC,
                LepidopteraRecipeSerializers.CRAFTING_SHAPELESS_BREAKABLE.getId(),
                RecipeCategory.MISC,
                criteria, registryAccess,
                LepidopteraAPI.ALCHEMICAL_ALEMBIC_RECIPES_CONDITION));
        futures.add(this.recipeWithConditions(cacheOutput,
                ModHelper.resource(metadata, "alembic/gold_from_coal"),
                goldFromCoal, BreakableShapelessRecipe.Serializer.CODEC,
                LepidopteraRecipeSerializers.CRAFTING_SHAPELESS_BREAKABLE.getId(),
                RecipeCategory.MISC,
                criteria, registryAccess,
                LepidopteraAPI.ALCHEMICAL_ALEMBIC_RECIPES_CONDITION));
        futures.add(this.recipeWithConditions(cacheOutput,
                ModHelper.resource(metadata, "alembic/gold_from_emerald"),
                goldFromEmerald, BreakableShapelessRecipe.Serializer.CODEC,
                LepidopteraRecipeSerializers.CRAFTING_SHAPELESS_BREAKABLE.getId(),
                RecipeCategory.MISC,
                criteria, registryAccess,
                LepidopteraAPI.ALCHEMICAL_ALEMBIC_RECIPES_CONDITION));
        futures.add(this.recipeWithConditions(cacheOutput,
                ResourceLocation.fromNamespaceAndPath(LepidopteraAPI.MOD_ID, "alembic/gunpowder"),
                shapedRecipe, BreakableShapedRecipe.Serializer.CODEC,
                LepidopteraRecipeSerializers.CRAFTING_SHAPED_BREAKABLE.getId(),
                RecipeCategory.MISC,
                criteria, registryAccess,
                LepidopteraAPI.ALCHEMICAL_ALEMBIC_RECIPES_CONDITION));
        futures.add(this.recipeWithConditions(cacheOutput,
                ModHelper.resource(metadata, "alembic/diamond"),
                diamondFromCoal, BreakableShapedRecipe.Serializer.CODEC,
                LepidopteraRecipeSerializers.CRAFTING_SHAPED_BREAKABLE.getId(),
                RecipeCategory.MISC,
                criteria, registryAccess,
                LepidopteraAPI.ALCHEMICAL_ALEMBIC_RECIPES_CONDITION));

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }
}
