package dev.satyrn.lepidoptera.api.accessors.item;

import dev.satyrn.lepidoptera.api.annotations.Api;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Accessor mixin for {@link Item}.
 * Note: setMaxDamage was removed in MC 1.21.1 - maxDamage is now a data component (DataComponents.MAX_DAMAGE).
 */
@Api
@Mixin(Item.class)
public interface ItemAccessor {
}
