package dev.satyrn.lepidoptera.mixin.net.minecraft.world.entity;

import dev.satyrn.lepidoptera.api.NotInitializable;
import dev.satyrn.lepidoptera.api.entity.TamableAnimalExtensions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Accessor;

import javax.annotation.Nullable;
import java.util.Objects;

@Mixin(TamableAnimal.class)
@Implements({@Interface(iface = TamableAnimalExtensions.class, prefix = "lapix$")})
public abstract class TamableAnimalMixin {

    private TamableAnimalMixin() {
        NotInitializable.mixinClass(this);
    }

    @Unique
    private SynchedEntityData lapi$entityData() {
        return ((Entity) (Object) this).getEntityData();
    }

    @Accessor("DATA_FLAGS_ID")
    public static @Nullable EntityDataAccessor<Byte> lapi$getDataFlagsId() {
        return null;
    }

    @Intrinsic
    public boolean lapix$getFlag(int flagId) {
        return (lapi$entityData().get(Objects.requireNonNull(lapi$getDataFlagsId())) & flagId) != 0;
    }

    @Intrinsic
    public void lapix$setFlag(int flagId, boolean value) {
        byte flags = lapi$entityData().get(Objects.requireNonNull(lapi$getDataFlagsId()));
        if (value) {
            lapi$entityData().set(lapi$getDataFlagsId(), (byte) (flags | flagId));
        } else {
            lapi$entityData().set(lapi$getDataFlagsId(), (byte) (flags & ~flagId));
        }
    }
}
