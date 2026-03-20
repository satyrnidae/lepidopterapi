package dev.satyrn.lepidoptera.api.world.item;

import dev.satyrn.lepidoptera.annotations.Api;
import dev.satyrn.lepidoptera.util.NotInitializable;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;
import java.util.*;

@Api
public final class EquipmentRegistry {
    private static final HashMap<Item, EquipmentSlot> ITEM_REGISTRY = new HashMap<>();
    private static final Map<TagKey<Item>, EquipmentSlot> TAG_KEY_REGISTRY = new HashMap<>();

    private EquipmentRegistry() {
        NotInitializable.staticClass(EquipmentRegistry.class);
    }

    @SuppressWarnings("unused")
    public static void registerEquipment(EquipmentSlot slot, TagKey<Item> tag) {
        if (!TAG_KEY_REGISTRY.containsKey(tag)) {
            TAG_KEY_REGISTRY.put(tag, slot);
        }
    }

    @SuppressWarnings("unused")
    public static void registerEquipment(EquipmentSlot slot, ItemLike itemLike) {
        registerEquipment(slot, itemLike.asItem());
    }

    public static void registerEquipment(EquipmentSlot slot, Item item) {
        if (!ITEM_REGISTRY.containsKey(item)) {
            ITEM_REGISTRY.put(item, slot);
        }
    }

    @SuppressWarnings("unsafe")
    public static @Nullable EquipmentSlot getSlot(ItemStack itemStack) {
        @Nullable EquipmentSlot slot = ITEM_REGISTRY.get(itemStack.getItem());
        if (slot == null) {
            slot = TAG_KEY_REGISTRY.keySet().parallelStream().filter(itemStack::is).findFirst().map(TAG_KEY_REGISTRY::get).orElse(null);
        }

        return slot;
    }
}
