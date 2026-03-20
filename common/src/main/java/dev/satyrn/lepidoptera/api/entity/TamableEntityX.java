package dev.satyrn.lepidoptera.api.entity;

public interface TamableEntityX {
    default boolean getFlag(int flagId) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    default void setFlag(int flagId, boolean value) {
        throw new UnsupportedOperationException("Not Implemented");
    }
}
