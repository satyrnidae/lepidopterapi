package dev.satyrn.lepidoptera.mixin.net.minecraft.world.entity;

import dev.satyrn.lepidoptera.api.entity.TamableEntityX;
import dev.satyrn.lepidoptera.util.NotInitializable;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import javax.annotation.Nullable;
import java.util.Objects;

@Mixin(TamableAnimal.class)
@Implements({
        @Interface(iface = TamableEntityX.class, prefix = "lapix$")
})
public abstract class TamableEntityMixin extends Animal implements OwnableEntity {

    private TamableEntityMixin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
        NotInitializable.mixinClass(TamableEntityMixin.class);
    }

    @Accessor("DATA_FLAGS_ID")
    public static @Nullable EntityDataAccessor<Byte> lapi$getDataFlagsId() {
        return null;
    }

    @Intrinsic
    public boolean lapix$getFlag(int flagId) {
        return (this.getEntityData().get(Objects.requireNonNull(lapi$getDataFlagsId())) & flagId) != 0;
    }

    @Intrinsic
    public void lapix$setFlag(int flagId, boolean value) {
        byte flags = this.getEntityData().get(Objects.requireNonNull(lapi$getDataFlagsId()));
        if (value) {
            this.getEntityData().set(lapi$getDataFlagsId(), (byte) (flags | flagId));
        } else {
            this.getEntityData().set(lapi$getDataFlagsId(), (byte) (flags & ~flagId));
        }
    }
}
