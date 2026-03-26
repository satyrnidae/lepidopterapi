package dev.satyrn.lepidoptera.mixin.net.minecraft.world.level.block.entity;

import com.llamalad7.mixinextras.sugar.Local;
import dev.satyrn.lepidoptera.api.NotInitializable;
import dev.satyrn.lepidoptera.api.item.crafting.CraftingUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

/**
 * When the furnace consumes a fuel item and places its crafting-remaining item into the fuel
 * slot, this mixin applies 1 point of damage to any damageable remaining item. If that damage
 * destroys the item, the fuel slot is left empty instead.
 *
 * <p>The injection targets the two {@code NonNullList.set(1, ...)} calls in
 * {@code serverTick} that set the fuel slot after a new fuel item is consumed.
 */
@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin {
    @Unique private static final int LAPI$FUEL_SLOT = 1;
    @Unique private static final RandomSource LAPI$RANDOM_SOURCE = RandomSource.create();

    private AbstractFurnaceBlockEntityMixin() {
        NotInitializable.mixinClass(this);
    }

    @ModifyArgs(method = "serverTick(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;)V",
                at = @At(value = "INVOKE",
                         target = "Lnet/minecraft/core/NonNullList;set(ILjava/lang/Object;)Ljava/lang/Object;"))
    private static void lepidoptera$damageFuelRemainder(Args args,
                                                        @Local(index = 6, ordinal = 0) final ItemStack source) {
        int slotIndex = args.get(0);
        if (slotIndex != LAPI$FUEL_SLOT) {
            return;
        }
        ItemStack remainder = args.get(1);

        args.set(1, CraftingUtils.damageAndDepleteFuel(source, remainder));
    }
}
