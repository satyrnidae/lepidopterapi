package dev.satyrn.lepidoptera.api.item;

import dev.satyrn.lepidoptera.LepidopteraAPI;
import dev.satyrn.lepidoptera.api.ModHelper;
import dev.satyrn.lepidoptera.api.NotInitializable;
import dev.satyrn.lepidoptera.api.annotations.Api;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Contract;

public final class ApiItemTags {
    @Api public static final TagKey<Item> FEET_EQUIPMENT;
    @Api public static final TagKey<Item> LEGS_EQUIPMENT;
    @Api public static final TagKey<Item> CHEST_EQUIPMENT;
    @Api public static final TagKey<Item> HEAD_EQUIPMENT;
    @Api public static final TagKey<Item> BODY_EQUIPMENT;
    @Api public static final TagKey<Item> FEET_EQUIPMENT_SHIFTABLE;
    @Api public static final TagKey<Item> LEGS_EQUIPMENT_SHIFTABLE;
    @Api public static final TagKey<Item> CHEST_EQUIPMENT_SHIFTABLE;
    @Api public static final TagKey<Item> HEAD_EQUIPMENT_SHIFTABLE;

    static {
        FEET_EQUIPMENT = register("equipment/feet");
        LEGS_EQUIPMENT = register("equipment/legs");
        CHEST_EQUIPMENT = register("equipment/chest");
        HEAD_EQUIPMENT = register("equipment/head");
        BODY_EQUIPMENT = register("equipment/body");
        FEET_EQUIPMENT_SHIFTABLE = register("equipment/feet/quick_equip");
        LEGS_EQUIPMENT_SHIFTABLE = register("equipment/legs/quick_equip");
        CHEST_EQUIPMENT_SHIFTABLE = register("equipment/chest/quick_equip");
        HEAD_EQUIPMENT_SHIFTABLE = register("equipment/head/quick_equip");
    }

    @Contract("-> fail")
    private ApiItemTags() {
        NotInitializable.staticClass(this);
    }

    private static TagKey<Item> register(final String name) {
        return TagKey.create(Registries.ITEM, ModHelper.resource(LepidopteraAPI.class, name));
    }
}
