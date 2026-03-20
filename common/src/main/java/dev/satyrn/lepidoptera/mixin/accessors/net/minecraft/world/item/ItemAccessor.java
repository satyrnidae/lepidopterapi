package dev.satyrn.lepidoptera.mixin.accessors.net.minecraft.world.item;

import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Item.class)
public interface ItemAccessor {
    @SuppressWarnings("unused")
    @Mutable
    @Accessor
    void setMaxDamage(int maxDamage);
}
