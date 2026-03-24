package dev.satyrn.lepidoptera.api.entity;

import dev.satyrn.lepidoptera.LepidopteraAPI;
import dev.satyrn.lepidoptera.api.ModHelper;
import dev.satyrn.lepidoptera.api.NotInitializable;
import dev.satyrn.lepidoptera.api.annotations.Api;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Contract;

@Api
public final class ApiEntityTags {
    @Api public static final TagKey<EntityType<?>> TICKS_FOOD;

    static {
        TICKS_FOOD = register("ticks_food");
    }

    @Contract("-> fail")
    private ApiEntityTags() {
        NotInitializable.staticClass(this);
    }

    private static TagKey<EntityType<?>> register(@SuppressWarnings("SameParameterValue") final String name) {
        return TagKey.create(Registries.ENTITY_TYPE, ModHelper.resource(LepidopteraAPI.class, name));
    }
}
