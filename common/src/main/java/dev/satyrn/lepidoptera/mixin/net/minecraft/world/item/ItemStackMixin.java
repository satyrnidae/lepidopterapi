package dev.satyrn.lepidoptera.mixin.net.minecraft.world.item;

import dev.satyrn.lepidoptera.api.NotInitializable;
import dev.satyrn.lepidoptera.api.item.ItemStackExtensions;
import dev.satyrn.lepidoptera.api.item.enchantment.EnchantmentHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.*;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
@Implements({@Interface(iface = ItemStackExtensions.class, prefix = "lapix$")})
public abstract class ItemStackMixin {
    @Shadow
    public abstract boolean isDamageableItem();

    @Shadow
    public abstract int getDamageValue();

    @Shadow
    public abstract int getMaxDamage();

    @Shadow
    public abstract Item getItem();

    @Shadow
    public abstract void shrink(int i);

    @Shadow
    public abstract void setDamageValue(int damage);

    private ItemStackMixin() {
        NotInitializable.mixinClass(this);
    }

    @Intrinsic
    public void lapix$hurtAndBreak(int damage, RandomSource randomSource, Consumer<Item> onBreak) {
        if (this.isDamageableItem()) {
            if (damage > 0) {
                // Processes enchantments through a custom call chain.
                // Enchantments are applied non-conditionally because we don't have a server level to build context.
                damage = EnchantmentHelper.processDurabilityChange(randomSource, (ItemStack) (Object) this, damage);
                if (damage <= 0) {
                    return;
                }
            }

            // Ignore advancement criteria

            int newDamage = this.getDamageValue() + damage;
            if (newDamage >= this.getMaxDamage()) {
                Item item = this.getItem();
                this.shrink(1);
                onBreak.accept(item);
            } else {
                this.setDamageValue(newDamage);
            }
        }
    }
}
