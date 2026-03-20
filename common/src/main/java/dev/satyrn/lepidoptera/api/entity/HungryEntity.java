package dev.satyrn.lepidoptera.api.entity;

import dev.satyrn.lepidoptera.annotations.Api;
import dev.satyrn.lepidoptera.api.food.EntityFoodData;

@Api
public interface HungryEntity {
    @SuppressWarnings("unused")
    default EntityFoodData getFoodData() {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @SuppressWarnings("unused")
    default void addExhaustion(float exhaustion) {
        throw new UnsupportedOperationException("Not Implemented");
    }
}
