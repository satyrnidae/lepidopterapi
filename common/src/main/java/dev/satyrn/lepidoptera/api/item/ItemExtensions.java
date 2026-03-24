package dev.satyrn.lepidoptera.api.item;

import dev.satyrn.lepidoptera.api.annotations.Api;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * Extension interface mixed into {@link Item} to add crafting and fuel depletion behavior.
 *
 * <p>These methods are injected by the {@code ItemMixin} at runtime. The default implementations
 * throw {@link NotImplementedException} as stubs — calling them without the mixin applied
 * indicates a configuration error.</p>
 *
 * <p>Use {@link #cast(Item)} to obtain a typed reference to the injected methods.</p>
 */
public interface ItemExtensions extends ItemLike {

    /**
     * Sets the item that remains in the crafting grid after this item is consumed.
     * Pass {@code null} to clear any previously set remainder.
     *
     * @param item the item to leave behind, or {@code null} for none
     * @return {@code this}, for chaining
     */
    @Contract(value = "_ -> this", mutates = "this")
    default ItemExtensions setCraftingRemainingItem(@Nullable Item item) { throw new NotImplementedException("Mixin apply failed!"); }

    /**
     * Configures this item to remain in the crafting grid after use (i.e. it is its own
     * crafting remainder). Shorthand for {@code setCraftingRemainingItem((Item) this)}.
     *
     * @return {@code this}, for chaining
     */
    @Contract(value = "-> this", mutates = "this")
    default ItemExtensions setRemainsInCraftingTable() { return setCraftingRemainingItem((Item)this); }

    /**
     * Sets the item that replaces this item in the crafting grid when it is damaged to zero
     * during crafting (depletion remainder). Pass {@code null} to clear.
     *
     * @param item the item to substitute, or {@code null} for none
     * @return {@code this}, for chaining
     */
    @Contract(value = "_ -> this", mutates = "this")
    default ItemExtensions setCraftingDepletionRemainingItem(@Nullable Item item) { throw new NotImplementedException("Mixin apply failed!"); }

    /**
     * Sets the item that replaces this item when it is depleted as a fuel source.
     * Pass {@code null} to clear.
     *
     * @param item the item to substitute, or {@code null} for none
     * @return {@code this}, for chaining
     */
    @Contract(value = "_ -> this", mutates = "this")
    default ItemExtensions setFuelDepletionRemainingItem(@Nullable Item item) { throw new NotImplementedException("Mixin apply failed!"); }

    /**
     * Sets the amount of damage dealt to this item each time it is used as a fuel source.
     *
     * @param damage the damage per fuel use
     * @return {@code this}, for chaining
     */
    @Contract(value = "_ -> this", mutates = "this")
    default ItemExtensions setDamageOnFuelUse(int damage) { throw new NotImplementedException("Mixin apply failed!"); }

    /**
     * Returns the item set as the crafting depletion remainder, or {@code null} if none.
     *
     * @return the crafting depletion remainder item
     */
    default @Nullable Item getCraftingDepletionRemainingItem() { throw new NotImplementedException("Mixin apply failed!"); }

    /**
     * Returns the item set as the fuel depletion remainder, or {@code null} if none.
     *
     * @return the fuel depletion remainder item
     */
    default @Nullable Item getFuelDepletionRemainingItem() { throw new NotImplementedException("Mixin apply failed!"); }

    /**
     * Returns the amount of damage dealt to this item per fuel use.
     *
     * @return damage per fuel use
     */
    default int getDamageOnFuelUse() { throw new NotImplementedException("Mixin apply failed!"); }

    /**
     * Returns this object as an {@link Item}. Safe because {@code ItemExtensions} is always
     * mixed into {@code Item}.
     *
     * @return this item
     */
    @Api
    @Override
    default Item asItem() {
        return (Item)this;
    }

    /**
     * Casts an {@link Item} to {@link ItemExtensions}.
     *
     * <p>Safe because the mixin always injects this interface onto {@code Item}.</p>
     *
     * @param item the item to cast
     * @return the item as {@link ItemExtensions}
     */
    static ItemExtensions cast(Item item) {
        return (ItemExtensions) item;
    }
}
