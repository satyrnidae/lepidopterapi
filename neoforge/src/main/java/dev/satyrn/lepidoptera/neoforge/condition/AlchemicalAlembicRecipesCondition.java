package dev.satyrn.lepidoptera.neoforge.condition;

import com.mojang.serialization.MapCodec;
import dev.satyrn.lepidoptera.LepidopteraAPI;
import net.neoforged.neoforge.common.conditions.ICondition;

/**
 * NeoForge resource load condition for the alchemical alembic breakable recipes.
 * Registered under {@link LepidopteraAPI#ALCHEMICAL_ALEMBIC_RECIPES_CONDITION}.
 *
 * <p>Evaluates to {@code true} when {@code enableAlchemicalAlembicRecipes} is set in the
 * active Lepidoptera config (local or server-synced). Because conditions are checked during
 * resource loading, the server's local config value governs whether the recipes load; the
 * synced overlay then informs connected clients.</p>
 */
public final class AlchemicalAlembicRecipesCondition implements ICondition {

    public static final AlchemicalAlembicRecipesCondition INSTANCE = new AlchemicalAlembicRecipesCondition();

    /**
     * No-field codec - this condition carries no additional JSON data.
     */
    public static final MapCodec<AlchemicalAlembicRecipesCondition> CODEC = MapCodec.unit(INSTANCE);

    private AlchemicalAlembicRecipesCondition() {
    }

    public @Override boolean test(IContext context) {
        return LepidopteraAPI.alchemicalAlembicRecipesEnabled();
    }

    public @Override MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}
