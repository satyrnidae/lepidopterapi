package dev.satyrn.lepidoptera.mixin.net.minecraft.world.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;

// NOTE: Item.getEquipSound() was removed in MC 1.20.5. The equip sound is now
// provided via the Equippable data component. The Equipment.getEquipSound() API
// has been removed accordingly.
@Mixin(Item.class)
public abstract class ItemMixin implements ItemLike {
}
