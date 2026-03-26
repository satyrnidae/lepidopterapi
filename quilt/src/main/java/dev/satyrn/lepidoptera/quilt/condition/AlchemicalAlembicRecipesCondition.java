package dev.satyrn.lepidoptera.quilt.condition;

import com.mojang.serialization.MapCodec;
import dev.satyrn.lepidoptera.LepidopteraAPI;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.minecraft.core.HolderLookup;

/**
 * Quilt (via QFAPI) resource load condition for the alchemical alembic breakable recipes.
 * Registered under {@link LepidopteraAPI#ALCHEMICAL_ALEMBIC_RECIPES_CONDITION}.
 *
 * <p>Evaluates to {@code true} when {@code enableAlchemicalAlembicRecipes} is set in the
 * active Lepidoptera config (local or server-synced). Because conditions are checked during
 * resource loading, the server's local config value governs whether the recipes load; the
 * synced overlay then informs connected clients.</p>
 */
public final class AlchemicalAlembicRecipesCondition implements ResourceCondition {

    public static final AlchemicalAlembicRecipesCondition INSTANCE = new AlchemicalAlembicRecipesCondition();

    /**
     * No-field codec - this condition carries no additional JSON data.
     */
    public static final MapCodec<AlchemicalAlembicRecipesCondition> CODEC = MapCodec.unit(INSTANCE);

    /**
     * {@link ResourceConditionType} wrapping the condition ID and codec for registration.
     */
    public static final ResourceConditionType<AlchemicalAlembicRecipesCondition> TYPE = ResourceConditionType.create(
            LepidopteraAPI.ALCHEMICAL_ALEMBIC_RECIPES_CONDITION, CODEC);

    private AlchemicalAlembicRecipesCondition() {
    }

    public @Override ResourceConditionType<?> getType() {
        return TYPE;
    }

    public @Override boolean test(HolderLookup.Provider registryLookup) {
        return LepidopteraAPI.alchemicalAlembicRecipesEnabled();
    }
}
