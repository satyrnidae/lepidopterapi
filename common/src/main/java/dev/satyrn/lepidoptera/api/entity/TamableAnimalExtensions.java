package dev.satyrn.lepidoptera.api.entity;

import dev.satyrn.lepidoptera.api.annotations.Api;
import net.minecraft.world.entity.TamableAnimal;

/**
 * Extension interface mixed into {@link TamableAnimal} to expose flag-based state.
 *
 * <p>Tameable animals use a packed integer of bit-flags stored in a {@code SynchedEntityData}
 * entry. These helpers provide typed access to individual flags without requiring access to
 * Minecraft's internal data-parameter infrastructure.</p>
 *
 * <p>Use {@link #cast(TamableAnimal)} to obtain a typed reference.</p>
 */
@Api
public interface TamableAnimalExtensions {
    /**
     * Returns the value of the entity data flag at the given bit index.
     *
     * @param flagId the zero-based bit index of the flag to read
     * @return {@code true} if the flag is set
     */
    default boolean getFlag(int flagId) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    /**
     * Sets or clears the entity data flag at the given bit index.
     *
     * @param flagId the zero-based bit index of the flag to modify
     * @param value  {@code true} to set the flag, {@code false} to clear it
     */
    default void setFlag(int flagId, boolean value) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    /**
     * Casts a {@link TamableAnimal} to {@link TamableAnimalExtensions}.
     *
     * <p>Safe because the mixin always injects this interface onto {@code TamableAnimal}.</p>
     *
     * @param tamableAnimal the entity to cast
     * @return the entity as a {@link TamableAnimalExtensions}
     */
    static TamableAnimalExtensions cast(TamableAnimal tamableAnimal) {
        return (TamableAnimalExtensions) tamableAnimal;
    }

}
