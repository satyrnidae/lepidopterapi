package dev.satyrn.lepidoptera.mixin.net.minecraft.world.item;


import dev.satyrn.lepidoptera.api.world.item.Equipment;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.*;

import javax.annotation.Nullable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin extends Item {

    @Shadow public abstract Block getBlock();

    private BlockItemMixin(Properties properties) {
        super(properties);
        throw new AssertionError();
    }

    @Unique
    @Override
    public @Nullable SoundEvent getEquipSound() {
        if (this.getBlock() instanceof Equipment equipment) {
            return equipment.getEquipSound();
        }
        return super.getEquipSound();
    }
}
