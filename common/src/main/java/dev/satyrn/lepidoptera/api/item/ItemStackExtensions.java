package dev.satyrn.lepidoptera.api.item;

import org.jetbrains.annotations.ApiStatus;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Contract;

import java.util.function.Consumer;

/**
 * Extension interface mixed into {@link ItemStack} to expose additional stack behavior.
 *
 * <p>Injected by {@code ItemStackMixin} at runtime. Default stub methods throw
 * {@link NotImplementedException} if the mixin was not applied.</p>
 *
 * <p>Use {@link #cast(ItemStack)} to obtain a typed reference.</p>
 */
@ApiStatus.AvailableSince("0.4.0+1.19.2")
public interface ItemStackExtensions {
    /**
     * Damages this item stack by the given amount, breaking it and calling {@code onBreak}
     * if its durability reaches zero.
     *
     * <p>Mirrors the internal {@code ItemStack#hurtAndBreak} signature used by vanilla,
     * allowing callers that lack access to a {@code LivingEntity} (e.g. entity-free
     * fuel consumption) to still trigger the break callback correctly.</p>
     *
     * @param damage             the amount of damage to apply
     * @param legacyRandomSource the random source used for unbreaking enchantment rolls
     * @param onBreak            callback invoked with the stack's item when the stack breaks
     */
    @ApiStatus.AvailableSince("0.4.0+1.19.2")
    default void hurtAndBreak(final int damage, final RandomSource legacyRandomSource, final Consumer<Item> onBreak) {
        throw new NotImplementedException("This functionality is not implemented! Did the mixin fail?");
    }

    /**
     * Casts an {@link ItemStack} to {@link ItemStackExtensions}.
     *
     * <p>Requires an intermediate cast through {@code Object} because {@code ItemStack}
     * is a final class; the mixin injects the interface at the bytecode level.</p>
     *
     * @param itemStack the stack to cast
     *
     * @return the stack as {@link ItemStackExtensions}
     */
    @SuppressWarnings("DataFlowIssue")
    @Contract(pure = true)
    static ItemStackExtensions cast(final ItemStack itemStack) {
        return (ItemStackExtensions) (Object) itemStack;
    }
}
