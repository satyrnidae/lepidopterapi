package dev.satyrn.lepidoptera.api.world.item;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EquipmentSlot;

import javax.annotation.Nullable;

public interface Equipment {
    /**
     * When an item or block is cast to this interface, returns the preferred equipment slot
     *
     * @return The preferred equipment slot.  Defaults to HEAD.
     */
    @SuppressWarnings("unused")
    default EquipmentSlot getPreferredSlot() {
        return EquipmentSlot.HEAD;
    }

    /**
     * When an item or block is cast to this interface, returns the sound to play when the item is equipped.
     *
     * @return The sound event to play.  Defaults to null.
     */
    @SuppressWarnings("unused")
    default @Nullable SoundEvent getEquipSound() {
        return null;
    }
}
