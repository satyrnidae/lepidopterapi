package dev.satyrn.lepidoptera.client.color.item;

import dev.satyrn.lepidoptera.annotations.Api;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;

@Api
@Environment(EnvType.CLIENT)
public class DyeableLeatherColor implements ItemColor {
    @Override
    public int getColor(final ItemStack itemStack, final int i) {
        return DyedItemColor.getOrDefault(itemStack, i > 0 ? 0xFFFFFFFF : 0xA06540);
    }
}
