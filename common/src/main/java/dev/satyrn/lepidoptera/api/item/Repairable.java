package dev.satyrn.lepidoptera.api.item;

/**
 * Marks an item as optionally preventing repair in an anvil.
 *
 * <p>Implement this interface on an {@code Item} subclass and return {@code true}
 * from {@link #preventRepair()} to block the item from being repaired via
 * Lepidoptera's {@code RepairItemRecipe} mixin.</p>
 */
public interface Repairable {
    /**
     * Returns {@code true} if this item should be prevented from being repaired in an anvil.
     *
     * <p>Defaults to {@code false} (repair is allowed).</p>
     *
     * @return {@code true} to block repair, {@code false} to allow it
     */
    default boolean preventRepair() {
        return false;
    }
}
