package dev.satyrn.lepidoptera.api.client;

import dev.satyrn.lepidoptera.api.annotations.Api;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import org.jetbrains.annotations.Contract;

/**
 * Client-only {@link ItemColor} implementation for dyeable leather items.
 *
 * <p>Returns the dyed color stored in the stack's {@code DyedItemColor} component
 * for the first tint layer (layer index 0), falling back to the natural undyed
 * leather color ({@link #LEATHER_COLOR}). For all other tint layers the fallback
 * is fully opaque white ({@code 0xFFFFFFFF}), matching vanilla's overlay behavior.</p>
 *
 * <p>Register an instance of this class as an {@code ItemColor} for any item that
 * uses the {@code DyedItemColor} data component.</p>
 */
@Environment(EnvType.CLIENT)
@Api
public class DyeableLeatherColor implements ItemColor {
    /** The default undyed leather color ({@code #A06540}). */
    @Api public static final int LEATHER_COLOR = 0xA06540;

    /**
     * Returns the tint color for the given item stack and tint layer index.
     *
     * <p>Layer {@code 0} uses the stack's dyed color (defaulting to {@link #LEATHER_COLOR});
     * all other layers default to fully opaque white ({@code 0xFFFFFFFF}).</p>
     *
     * @param itemStack  the stack being rendered
     * @param tintIndex  the tint layer index
     * @return the ARGB tint color
     */
    @Contract(pure = true)
    @Override
    @Api public int getColor(final ItemStack itemStack, final int tintIndex) {
        return DyedItemColor.getOrDefault(itemStack, tintIndex > 0 ? 0xFFFFFFFF : LEATHER_COLOR);
    }
}
