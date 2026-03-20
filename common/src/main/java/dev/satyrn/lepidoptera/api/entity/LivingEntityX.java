package dev.satyrn.lepidoptera.api.entity;

public interface LivingEntityX {

    @SuppressWarnings("unused")
    default boolean isHurt() {
        throw new UnsupportedOperationException("Not Implemented");
    }
}
