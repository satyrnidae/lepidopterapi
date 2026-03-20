package dev.satyrn.lepidoptera.mixin.accessors.net.minecraft.world.item;

import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Accessor mixin for {@link Item}.
 * Note: setMaxDamage was removed in MC 1.21.1 — maxDamage is now a data component (DataComponents.MAX_DAMAGE).
 */
@Mixin(Item.class)
public interface ItemAccessor {
}
