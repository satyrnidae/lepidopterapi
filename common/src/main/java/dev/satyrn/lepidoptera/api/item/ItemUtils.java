package dev.satyrn.lepidoptera.api.item;

import dev.satyrn.lepidoptera.api.NotInitializable;
import dev.satyrn.lepidoptera.api.annotations.Api;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;

/**
 * Utility methods for item stack manipulation.
 */
@Api
public final class ItemUtils {

    private ItemUtils() {
        NotInitializable.staticClass(this);
    }

    /**
     * Copies all enchantments from {@code source} onto {@code target}.
     * If {@code source} has no enchantments, this is a no-op.
     *
     * @param source the item stack to copy enchantments from
     * @param target the item stack to copy enchantments onto
     */
    @Api
    public static void copyEnchantments(ItemStack source, ItemStack target) {
        ItemEnchantments enchantments = source.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        if (!enchantments.isEmpty()) {
            target.set(DataComponents.ENCHANTMENTS, enchantments);
        }
    }
}
