package dev.satyrn.lepidoptera.mixin.net.minecraft.world.item;

import dev.satyrn.lepidoptera.api.NotInitializable;
import dev.satyrn.lepidoptera.api.item.EquipmentRegistry;
import dev.satyrn.lepidoptera.api.item.ItemExtensions;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(Item.class)
@Implements({@Interface(iface = ItemExtensions.class, prefix = "lapix$")})
public class ItemMixin {
    @Unique
    private @Nullable Item lapi$craftingRemainingItemOverride;

    /**
     * The remainder item when crafting destroys an item
     */
    @Unique
    private @Nullable Item lapi$craftingDepletionRemainingItem;

    @Unique
    private @Nullable Item lapi$fuelDepletionRemainingItem;

    @Unique
    private int lapi$damageOnFuelUse = 1;

    private ItemMixin() {
        NotInitializable.mixinClass(this);
    }

    @Inject(method = "hasCraftingRemainingItem()Z", at = @At("RETURN"), cancellable = true)
    private void lapi$onHasCraftingRemainingItem(CallbackInfoReturnable<Boolean> cir) {
        if (this.lapi$craftingRemainingItemOverride != null) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @Inject(method = "getCraftingRemainingItem()Lnet/minecraft/world/item/Item;",
            at = @At("RETURN"),
            cancellable = true)
    private void lapi$onGetCraftingRemainingItem(CallbackInfoReturnable<Item> cir) {
        if (this.lapi$craftingRemainingItemOverride != null) {
            cir.setReturnValue(lapi$craftingRemainingItemOverride);
            cir.cancel();
        }
    }

    /**
     * Right-click equip for items registered in {@link EquipmentRegistry}.
     * Fires at TAIL so any existing {@code use()} behaviour (food, tools, etc.) takes priority;
     * we only act when vanilla returned {@code PASS} (nothing else claimed the interaction).
     */
    @Inject(method = "use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResultHolder;",
            at = @At("TAIL"),
            cancellable = true)
    private void lapi$onUse(Level level,
                            Player player,
                            InteractionHand hand,
                            CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        if (cir.getReturnValue().getResult() != net.minecraft.world.InteractionResult.PASS) {
            return;
        }
        ItemStack stack = player.getItemInHand(hand);
        @Nullable EquipmentRegistry.Entry entry = EquipmentRegistry.getEntry(stack);
        if (entry == null || !entry.canShiftClick()) {
            return;
        }

        if (player.isShiftKeyDown()) {
            if (!level.isClientSide()) {
                ItemStack displaced = player.getItemBySlot(entry.slot());
                if (displaced.isEmpty()) {
                    player.setItemSlot(entry.slot(), stack.copyWithCount(1));
                    stack.shrink(1);
                }
            }
            cir.setReturnValue(InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide()));
        }
    }

    @Intrinsic
    public ItemExtensions lapix$setCraftingRemainingItem(@Nullable Item item) {
        this.lapi$craftingRemainingItemOverride = item;
        return (ItemExtensions) this;
    }

    @Intrinsic
    public ItemExtensions lapix$setCraftingDepletionRemainingItem(@Nullable Item item) {
        this.lapi$craftingDepletionRemainingItem = item;
        return (ItemExtensions) this;
    }

    @Intrinsic
    public ItemExtensions lapix$setFuelDepletionRemainingItem(@Nullable Item item) {
        this.lapi$fuelDepletionRemainingItem = item;
        return (ItemExtensions) this;
    }

    @Intrinsic
    public ItemExtensions lapix$setDamageOnFuelUse(int damage) {
        this.lapi$damageOnFuelUse = damage;
        return (ItemExtensions) this;
    }

    @Intrinsic
    public @Nullable Item lapix$getCraftingDepletionRemainingItem() {
        return this.lapi$craftingDepletionRemainingItem;
    }

    @Intrinsic
    public @Nullable Item lapix$getFuelDepletionRemainingItem() {
        return this.lapi$fuelDepletionRemainingItem;
    }

    @Intrinsic
    public int lapix$getDamageOnFuelUse() {
        return this.lapi$damageOnFuelUse;
    }
}
