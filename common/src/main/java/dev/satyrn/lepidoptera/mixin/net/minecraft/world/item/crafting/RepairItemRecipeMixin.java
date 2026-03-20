package dev.satyrn.lepidoptera.mixin.net.minecraft.world.item.crafting;

import dev.satyrn.lepidoptera.LepidopteraAPI;
import dev.satyrn.lepidoptera.api.world.item.Repairable;
import dev.satyrn.lepidoptera.util.NotInitializable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RepairItemRecipe;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RepairItemRecipe.class)
public abstract class RepairItemRecipeMixin extends CustomRecipe {

    private RepairItemRecipeMixin(final ResourceLocation id) {
        super(id);
        NotInitializable.mixinClass(RepairItemRecipeMixin.class);
    }

    @Inject(method = "matches(Lnet/minecraft/world/inventory/CraftingContainer;Lnet/minecraft/world/level/Level;)Z", at = @At("HEAD"), cancellable = true)
    public void lapi$matches(final CraftingContainer container,
                             final Level level,
                             final CallbackInfoReturnable<Boolean> ci) {
        for (int slot = 0; slot < container.getContainerSize(); ++slot) {
            final ItemStack item = container.getItem(slot);

            if (!item.isEmpty() && item.getItem() instanceof final Repairable repairable && repairable.preventRepair()) {
                LepidopteraAPI.debug("Repairable API: Prevented repair of item in recipe " + item);
                ci.setReturnValue(false);
                ci.cancel();
            }
        }
    }
}
