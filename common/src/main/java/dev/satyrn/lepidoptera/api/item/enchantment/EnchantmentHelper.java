package dev.satyrn.lepidoptera.api.item.enchantment;

import dev.satyrn.lepidoptera.api.NotInitializable;
import dev.satyrn.lepidoptera.api.annotations.Api;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.apache.commons.lang3.mutable.MutableFloat;

/**
 * Utilities for processing enchantment effects on item stacks.
 *
 * <p>Provides Lepidoptera-specific enchantment iteration and durability-change processing
 * that hooks into the {@link EnchantmentExtensions} mixin interface.</p>
 *
 * @since 1.0.0-SNAPSHOT.1+1.21.1
 */
@Api("1.0.0-SNAPSHOT.1+1.21.1")
public final class EnchantmentHelper {
    private EnchantmentHelper() {
        NotInitializable.staticClass(this);
    }

    /**
     * Applies all durability-modifying enchantments on {@code itemStack} to the given
     * damage value and returns the result.
     *
     * <p>Iterates over every enchantment on the stack and calls
     * {@link EnchantmentExtensions#modifyDurabilityChange} on each one, allowing
     * enchantments like Unbreaking to reduce the effective damage.</p>
     *
     * @param legacyRandomSource the random source for value-effect rolls
     * @param itemStack          the stack whose enchantments are checked
     * @param i                  the initial damage value
     *
     * @return the damage value after all enchantment modifications
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @Api("1.0.0-SNAPSHOT.1+1.21.1")
    public static int processDurabilityChange(RandomSource legacyRandomSource, ItemStack itemStack, int i) {
        MutableFloat mutableFloat = new MutableFloat(i);
        //noinspection DataFlowIssue - Mixin soft-applies interface
        runIterationOnItem(itemStack,
                (enchantment, lvl) -> ((EnchantmentExtensions) (Object) enchantment.value()).modifyDurabilityChange(
                        legacyRandomSource, lvl, itemStack, mutableFloat));
        return mutableFloat.intValue();
    }

    /**
     * Iterates over every enchantment on {@code itemStack}, invoking {@code visitor} for
     * each enchantment holder and its level.
     *
     * @param itemStack the stack to iterate
     * @param visitor   the callback to invoke for each enchantment
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @Api("1.0.0-SNAPSHOT.1+1.21.1")
    public static void runIterationOnItem(ItemStack itemStack, EnchantmentVisitor visitor) {
        ItemEnchantments itemEnchantments = itemStack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

        for (var entry : itemEnchantments.entrySet()) {
            visitor.accept(entry.getKey(), entry.getIntValue());
        }
    }

    /**
     * Callback interface for iterating enchantments on an item stack.
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @Api("1.0.0-SNAPSHOT.1+1.21.1")
    @FunctionalInterface
    public interface EnchantmentVisitor {
        /**
         * Called for each enchantment on a stack.
         *
         * @param holder the enchantment holder
         * @param i      the enchantment level
         *
         * @since 1.0.0-SNAPSHOT.1+1.21.1
         */
        @Api("1.0.0-SNAPSHOT.1+1.21.1")
        void accept(Holder<Enchantment> holder, int i);
    }
}
