package dev.satyrn.lepidoptera.api.entity;

import org.jetbrains.annotations.ApiStatus;
import net.minecraft.world.entity.TamableAnimal;
import org.jetbrains.annotations.Contract;

/**
 * Extension interface mixed into {@link TamableAnimal} to expose flag-based state.
 *
 * <p>Tameable animals use a packed integer of bit-flags stored in a {@code SynchedEntityData}
 * entry. These helpers provide typed access to individual flags without requiring access to
 * Minecraft's internal data-parameter infrastructure.</p>
 *
 * <p>Use {@link #cast(TamableAnimal)} to obtain a typed reference.</p>
 *
 * @since 0.4.0+1.19.2
 */
@ApiStatus.AvailableSince("0.4.0+1.19.2")
public interface TamableAnimalExtensions {
    /**
     * Returns the value of the entity data flag at the given bit index.
     *
     * @param flagId the zero-based bit index of the flag to read
     *
     * @return {@code true} if the flag is set
     *
     * @since 0.4.0+1.19.2
     */
    @ApiStatus.AvailableSince("0.4.0+1.19.2")
    default boolean getFlag(int flagId) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    /**
     * Sets or clears the entity data flag at the given bit index.
     *
     * @param flagId the zero-based bit index of the flag to modify
     * @param value  {@code true} to set the flag, {@code false} to clear it
     *
     * @since 0.4.0+1.19.2
     */
    @ApiStatus.AvailableSince("0.4.0+1.19.2")
    default void setFlag(int flagId, boolean value) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    /**
     * Casts a {@link TamableAnimal} to {@link TamableAnimalExtensions}.
     *
     * <p>Safe because the mixin always injects this interface onto {@code TamableAnimal}.</p>
     *
     * @param tamableAnimal the entity to cast
     *
     * @return the entity as a {@link TamableAnimalExtensions}
     *
     * @since 0.4.0+1.19.2
     */
    @ApiStatus.AvailableSince("0.4.0+1.19.2")
    @Contract(value = "_ -> param1", pure = true)
    static TamableAnimalExtensions cast(TamableAnimal tamableAnimal) {
        return (TamableAnimalExtensions) tamableAnimal;
    }

}
