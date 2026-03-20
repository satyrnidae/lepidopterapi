package dev.satyrn.lepidoptera.mixin.net.minecraft.world.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.*;

// NOTE: Item.getEquipSound() was removed in MC 1.20.5. The equip sound is now
// provided via the Equippable data component. The Equipment.getEquipSound() API
// has been removed accordingly.
@Mixin(BlockItem.class)
public abstract class BlockItemMixin extends Item {

    @Shadow public abstract Block getBlock();

    private BlockItemMixin(Properties properties) {
        super(properties);
        throw new AssertionError();
    }
}
