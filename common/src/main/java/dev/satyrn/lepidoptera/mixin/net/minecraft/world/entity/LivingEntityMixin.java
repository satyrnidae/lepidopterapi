package dev.satyrn.lepidoptera.mixin.net.minecraft.world.entity;

import dev.satyrn.lepidoptera.api.entity.LivingEntityX;
import dev.satyrn.lepidoptera.util.NotInitializable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.*;

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

    // NOTE: LivingEntity.getEquipmentSlotForItem was removed in MC 1.20.5.
    // Equipment slot routing via the Equipment interface and EquipmentRegistry now requires
    // platform-specific integration (NeoForge: Item.getEquipmentSlot(ItemStack),
    // Fabric: FabricItem.getEquipmentSlot(ItemStack)).
}
