package dev.satyrn.lepidoptera.item.crafting;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.satyrn.lepidoptera.LepidopteraAPI;
import dev.satyrn.lepidoptera.api.item.crafting.BreakableShapedRecipe;
import dev.satyrn.lepidoptera.api.item.crafting.BreakableShapelessRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * Holds references to all recipe serializers registered by Lepidoptera API.
 * Call {@link #register()} on Fabric/Quilt, or register {@link #RECIPE_SERIALIZERS} directly on NeoForge.
 */
public final class LepidopteraRecipeSerializers {
    public static final RegistrySupplier<RecipeSerializer<BreakableShapedRecipe>> CRAFTING_SHAPED_BREAKABLE;
    public static final RegistrySupplier<RecipeSerializer<BreakableShapelessRecipe>> CRAFTING_SHAPELESS_BREAKABLE;

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(LepidopteraAPI.MOD_ID, Registries.RECIPE_SERIALIZER);

    static {
        CRAFTING_SHAPED_BREAKABLE = RECIPE_SERIALIZERS.register(
                "crafting_shaped_breakable", () -> BreakableShapedRecipe.SERIALIZER);
        CRAFTING_SHAPELESS_BREAKABLE = RECIPE_SERIALIZERS.register(
                "crafting_shapeless_breakable", () -> BreakableShapelessRecipe.SERIALIZER);
    }

    private LepidopteraRecipeSerializers() {}

    public static void register() {
        RECIPE_SERIALIZERS.register();
    }
}