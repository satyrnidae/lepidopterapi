package dev.satyrn.lepidoptera.api.item.enchantment;

import org.jetbrains.annotations.ApiStatus;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.mutable.MutableFloat;

import java.util.List;

/**
 * Extension interface mixed into {@link Enchantment} to support custom durability-modification
 * effects driven by the data-component enchantment system introduced in 1.21.
 *
 * <p>Use {@link #cast(Enchantment)} to obtain a typed reference to the injected methods.</p>
 */
@ApiStatus.AvailableSince("0.4.0+1.19.2")
public interface EnchantmentExtensions {
    /**
     * Modifies the durability change value for this enchantment using its
     * {@link EnchantmentEffectComponents#ITEM_DAMAGE} component.
     *
     * <p>This is a convenience overload of {@link #modifyItemFilteredCount} pre-wired to the
     * item damage component type.</p>
     *
     * @param legacyRandomSource the random source for value-effect rolls
     * @param level              the enchantment level
     * @param itemStack          the item stack being damaged
     * @param value              the mutable damage value to modify in place
     */
    @ApiStatus.AvailableSince("0.4.0+1.19.2")
    default void modifyDurabilityChange(RandomSource legacyRandomSource,
                                        int level,
                                        ItemStack itemStack,
                                        MutableFloat value) {
        modifyItemFilteredCount(EnchantmentEffectComponents.ITEM_DAMAGE, legacyRandomSource, level, itemStack, value);
    }

    /**
     * Applies a list of conditional value effects from the given data component type to
     * {@code value}, running each effect whose conditions are met.
     *
     * @param dataComponentType  the enchantment effect component holding the value effects
     * @param legacyRandomSource the random source for value-effect rolls
     * @param level              the enchantment level
     * @param itemStack          the item stack the enchantment is on
     * @param value              the mutable float to modify in place
     */
    @ApiStatus.AvailableSince("0.4.0+1.19.2")
    default void modifyItemFilteredCount(DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> dataComponentType,
                                         RandomSource legacyRandomSource,
                                         int level,
                                         ItemStack itemStack,
                                         MutableFloat value) {
        throw new NotImplementedException("Enchantment extensions mixin apply failed!");
    }

    /**
     * Casts an {@link Enchantment} to {@link EnchantmentExtensions}.
     *
     * <p>Requires an intermediate cast through {@code Object} because {@code Enchantment}
     * is a record in 1.21; the mixin injects the interface at the bytecode level.</p>
     *
     * @param enchantment the enchantment to cast
     *
     * @return the enchantment as {@link EnchantmentExtensions}
     */
    @ApiStatus.AvailableSince("0.4.0+1.19.2")
    @SuppressWarnings("DataFlowIssue")
    static EnchantmentExtensions cast(Enchantment enchantment) {
        return (EnchantmentExtensions) (Object) enchantment;
    }
}
