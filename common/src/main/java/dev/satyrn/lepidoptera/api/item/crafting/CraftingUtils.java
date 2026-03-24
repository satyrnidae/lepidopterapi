package dev.satyrn.lepidoptera.api.item.crafting;

import dev.satyrn.lepidoptera.api.NotInitializable;
import dev.satyrn.lepidoptera.api.item.ItemExtensions;
import dev.satyrn.lepidoptera.api.item.ItemStackExtensions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.mutable.MutableBoolean;

import javax.annotation.Nullable;

/**
 * Utility methods for breakable crafting and fuel depletion logic.
 *
 * <p>Used internally by {@link BreakableShapedRecipe} and {@link BreakableShapelessRecipe}
 * to compute the item stack that should remain in a crafting slot after a recipe is crafted,
 * accounting for damage applied to tool-like ingredients.</p>
 */
public final class CraftingUtils {
    private static final RandomSource RANDOM = RandomSource.create();

    private CraftingUtils() {
        NotInitializable.staticClass(this);
    }

    /**
     * Computes the crafting remainder for a source ingredient stack after applying damage.
     *
     * <p>If the source item has a crafting-remaining item and it is damageable, the remainder
     * is returned with the source's current damage value plus {@code damage} applied. If the
     * remainder breaks under that damage, the depletion-remaining item (if any) is returned
     * instead. Returns {@link ItemStack#EMPTY} if there is no remainder or the stack is fully
     * depleted.</p>
     *
     * @param source the ingredient stack being consumed
     * @param damage the amount of damage to apply to any damageable remainder
     * @return the resulting remainder stack, or {@link ItemStack#EMPTY}
     */
    public static ItemStack damageAndDepleteCrafting(final ItemStack source, final int damage) {
        if (!source.getItem().hasCraftingRemainingItem()) return ItemStack.EMPTY;

        @Nullable Item remainItem = source.getItem().getCraftingRemainingItem();
        if (remainItem == null) return ItemStack.EMPTY;

        ItemStack remainder = new ItemStack(remainItem);

        if (remainder.isDamageableItem()) {
            remainder.setDamageValue(source.getDamageValue());

            MutableBoolean hasBroken = new MutableBoolean(false);
            ItemStackExtensions.cast(remainder).hurtAndBreak(damage, RANDOM, item -> hasBroken.setTrue());

            if (hasBroken.isTrue()) {
                final @Nullable Item depletionRemainder = ItemExtensions.cast(remainItem).getCraftingDepletionRemainingItem();
                if (depletionRemainder == null) return ItemStack.EMPTY;
                remainder = new ItemStack(depletionRemainder);
            }
        }

        return remainder;
    }

    /**
     * Applies fuel-use damage to a fuel remainder stack, substituting the fuel-depletion
     * remainder item if the stack breaks.
     *
     * <p>If {@code remainder} is empty or not damageable, it is returned unchanged. Otherwise,
     * the damage-on-fuel-use value from the remainder item's {@link ItemExtensions} is applied.
     * If the stack breaks, the fuel-depletion remainder item replaces it, or
     * {@link ItemStack#EMPTY} if none is registered.</p>
     *
     * @param source    the original fuel source stack (used only to copy the initial damage value)
     * @param remainder the pre-computed fuel remainder stack to damage
     * @return the resulting remainder stack after damage, or {@link ItemStack#EMPTY}
     */
    public static ItemStack damageAndDepleteFuel(final ItemStack source, final ItemStack remainder) {
        if (!remainder.isEmpty() && remainder.isDamageableItem()) {
            final Item remainItem = remainder.getItem();
            final int damage = ItemExtensions.cast(remainItem).getDamageOnFuelUse();
            remainder.setDamageValue(source.getDamageValue());

            MutableBoolean hasBroken = new MutableBoolean(false);
            ItemStackExtensions.cast(remainder).hurtAndBreak(damage, RANDOM, item -> hasBroken.setTrue());

            if (hasBroken.isTrue()) {
                final @Nullable Item depletionRemainder = ItemExtensions.cast(remainItem)
                        .getFuelDepletionRemainingItem();
                return depletionRemainder == null ? ItemStack.EMPTY : new ItemStack(depletionRemainder);
            }
        }

        return remainder;
    }
}
