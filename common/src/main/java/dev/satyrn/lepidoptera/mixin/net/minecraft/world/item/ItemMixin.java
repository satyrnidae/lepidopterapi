package dev.satyrn.lepidoptera.mixin.net.minecraft.world.item;

import dev.satyrn.lepidoptera.api.world.item.Equipment;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin implements ItemLike {
    @Inject(method = "getEquipSound()Lnet/minecraft/sounds/SoundEvent;", at = @At("HEAD"), cancellable = true)
    void lapi$getEquipSound(final CallbackInfoReturnable<SoundEvent> ci) {
        if (this instanceof Equipment equipment) {
            ci.setReturnValue(equipment.getEquipSound());
            ci.cancel();
        }
        // fix in case BlockItem.getEquipSound is excluded due to non-unique implementation
        else //noinspection ConstantValue
            if ((Object)this instanceof BlockItem block && block.getBlock() instanceof Equipment equipment) {
            ci.setReturnValue(equipment.getEquipSound());
            ci.cancel();
        }
    }
}
