package dev.satyrn.lepidoptera.mixin.net.minecraft.world.entity;

import dev.satyrn.lepidoptera.api.world.item.Equipment;
import dev.satyrn.lepidoptera.api.entity.LivingEntityX;
import dev.satyrn.lepidoptera.api.world.item.EquipmentRegistry;
import dev.satyrn.lepidoptera.util.NotInitializable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(net.minecraft.world.entity.LivingEntity.class)
@Implements({
        @Interface(iface = LivingEntityX.class, prefix = "lapix$")
})
public abstract class LivingEntityMixin extends Entity {

    private LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
        NotInitializable.mixinClass(LivingEntityMixin.class);
    }

    @Shadow
    public abstract float getHealth();

    @Shadow
    public abstract float getMaxHealth();

    @Intrinsic
    public boolean lapix$isHurt() {
        return this.getHealth() > 0F && this.getHealth() < this.getMaxHealth();
    }

    @Inject(method = "getEquipmentSlotForItem", at = @At("HEAD"), cancellable = true)
    private static void lapi$getEquipmentSlotForItem(final ItemStack itemStack,
                                                     final CallbackInfoReturnable<EquipmentSlot> ci) {
        if (itemStack.getItem() instanceof Equipment equipment) {
            ci.setReturnValue(equipment.getPreferredSlot());
            ci.cancel();
        } else if (itemStack.getItem() instanceof BlockItem block && block.getBlock() instanceof Equipment equipment) {
            ci.setReturnValue(equipment.getPreferredSlot());
            ci.cancel();
        } else {
            final @Nullable EquipmentSlot slot = EquipmentRegistry.getSlot(itemStack);
            if (slot != null) {
                ci.setReturnValue(slot);
                ci.cancel();
            }
        }
    }
}
