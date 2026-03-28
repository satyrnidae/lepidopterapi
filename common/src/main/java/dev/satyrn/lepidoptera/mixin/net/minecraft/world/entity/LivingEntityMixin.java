package dev.satyrn.lepidoptera.mixin.net.minecraft.world.entity;

import dev.satyrn.lepidoptera.api.NotInitializable;
import dev.satyrn.lepidoptera.api.entity.HungryEntityRegistry;
import dev.satyrn.lepidoptera.api.entity.LivingEntityExtensions;
import dev.satyrn.lepidoptera.api.food.EntityFoodData;
import dev.satyrn.lepidoptera.api.item.EquipmentRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(net.minecraft.world.entity.LivingEntity.class)
@Implements({@Interface(iface = LivingEntityExtensions.class, prefix = "lapix$")})
public abstract class LivingEntityMixin extends Entity {

    @Unique
    private final EntityFoodData lapi$foodData = new EntityFoodData();

    private LivingEntityMixin(final EntityType<?> entityType, final Level level) {
        super(entityType, level);
        NotInitializable.mixinClass(this);
    }

    /**
     * Allows items registered in {@link EquipmentRegistry} to be drag-placed into the matching
     * inventory armor slot. Vanilla returns {@code MAINHAND} when no specific slot is found;
     * we fall through to the registry only in that case so real armor items are unaffected.
     */
    @Inject(method = "getEquipmentSlotForItem", at = @At("RETURN"), cancellable = true)
    private static void lapi$onGetEquipmentSlotForItem(final ItemStack stack,
                                                       final CallbackInfoReturnable<EquipmentSlot> cir) {
        if (cir.getReturnValue() != EquipmentSlot.MAINHAND) {
            return;
        }
        @Nullable EquipmentSlot slot = EquipmentRegistry.getSlot(stack);
        if (slot != null) {
            cir.setReturnValue(slot);
        }
    }

    public abstract @Shadow float getHealth();

    public abstract @Shadow float getMaxHealth();

    @Intrinsic
    public boolean lapix$isHurt() {
        return this.getHealth() > 0F && this.getHealth() < this.getMaxHealth();
    }

    @Intrinsic
    public EntityFoodData lapix$getFoodData() {
        return this.lapi$foodData;
    }

    @Intrinsic
    public void lapix$addExhaustion(final float exhaustion) {
        this.lapi$foodData.addExhaustion(exhaustion);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void lapi$onTick(final CallbackInfo ci) {
        if (HungryEntityRegistry.isRegistered(this.getType())) {
            this.lapi$foodData.tick((LivingEntity) (Object) this);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    private void lapi$onReadAdditionalSaveData(final CompoundTag tag, final CallbackInfo ci) {
        if (HungryEntityRegistry.isRegistered(this.getType())) {
            this.lapi$foodData.readAdditionalSaveData(tag);
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    private void lapi$onAddAdditionalSaveData(final CompoundTag tag, final CallbackInfo ci) {
        if (HungryEntityRegistry.isRegistered(this.getType())) {
            this.lapi$foodData.addAdditionalSaveData(tag);
        }
    }
}