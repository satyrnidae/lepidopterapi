package dev.satyrn.lepidoptera.api.world.item;

import net.minecraft.world.entity.EquipmentSlot;

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
}
