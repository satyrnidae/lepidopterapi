package dev.satyrn.lepidoptera.mixin.net.minecraft.world.item.crafting;

import dev.satyrn.lepidoptera.LepidopteraAPI;
import dev.satyrn.lepidoptera.api.NotInitializable;
import dev.satyrn.lepidoptera.api.item.Repairable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RepairItemRecipe;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RepairItemRecipe.class)
public abstract class RepairItemRecipeMixin extends CustomRecipe {

    private RepairItemRecipeMixin(final CraftingBookCategory category) {
        super(category);
        NotInitializable.mixinClass(this);
    }

    @Inject(method = "matches(Lnet/minecraft/world/item/crafting/CraftingInput;Lnet/minecraft/world/level/Level;)Z", at = @At("HEAD"), cancellable = true)
    public void lapi$matches(final CraftingInput input, final Level level, final CallbackInfoReturnable<Boolean> ci) {
        for (int slot = 0; slot < input.size(); ++slot) {
            final ItemStack item = input.getItem(slot);

            if (!item.isEmpty() &&
                    item.getItem() instanceof final Repairable repairable &&
                    repairable.preventRepair()) {
                LepidopteraAPI.debug("Repairable API: Prevented repair of item in recipe " + item);
                ci.setReturnValue(false);
                ci.cancel();
            }
        }
    }
}
