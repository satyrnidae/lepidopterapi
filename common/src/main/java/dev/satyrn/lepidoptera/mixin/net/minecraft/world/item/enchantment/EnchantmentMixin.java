package dev.satyrn.lepidoptera.mixin.net.minecraft.world.item.enchantment;

import dev.satyrn.lepidoptera.api.item.enchantment.EnchantmentExtensions;
import dev.satyrn.lepidoptera.api.NotInitializable;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.level.storage.loot.LootContext;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.spongepowered.asm.mixin.*;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Consumer;

@Mixin(Enchantment.class)
@Implements({
        @Interface(iface = EnchantmentExtensions.class, prefix = "lapix$")
})
public abstract class EnchantmentMixin {
    @Shadow
    private static <T> void applyEffects(List<ConditionalEffect<T>> list, LootContext arg, Consumer<T> consumer) {}

    @Shadow public abstract <T> List<T> getEffects(DataComponentType<List<T>> arg);

    @Shadow
    @SuppressWarnings("ALL")
    private static @Nonnull LootContext itemContext(ServerLevel arg, int i, ItemStack arg2) {
        return null;
    }

    private EnchantmentMixin() {
        NotInitializable.mixinClass(this);
    }

    @Intrinsic
    public void lapix$modifyItemFilteredCount(
            DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> dataComponentType,
            RandomSource legacyRandomSource,
            int level,
            ItemStack itemStack,
            MutableFloat value) {
        // We lose conditional effect support but that's fine if we document it.
        for (var effect : this.getEffects(dataComponentType)) {
            value.setValue(effect.effect().process(level, legacyRandomSource, value.floatValue()));
        }
    }
}
