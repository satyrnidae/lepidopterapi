package dev.satyrn.lepidoptera.api.item;

import dev.satyrn.lepidoptera.api.annotations.Api;
import dev.satyrn.lepidoptera.api.NotInitializable;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Registry that maps items and item tags to equipment slots.
 *
 * <p>Mods register their equipment items here so that Lepidoptera's mixin on
 * {@code Item} can return the correct {@link EquipmentSlot} from
 * {@code Item#getEquipmentSlot(ItemStack)}. Registration is first-come,
 * first-served: subsequent calls for an already-registered item or tag are
 * silently ignored.</p>
 */
@Api
public final class EquipmentRegistry {
    private static final HashMap<Item, Entry> ITEM_REGISTRY = new HashMap<>();
    private static final Map<TagKey<Item>, Entry> TAG_KEY_REGISTRY = new HashMap<>();
    private static final Set<TagKey<Item>> PROTECTED_TAGS = new HashSet<>();

    /**
     * Flattened item-to-entry map rebuilt on every {@link #onTagsLoaded} call.
     * {@code volatile} ensures visibility across the server-thread write and game-thread reads.
     */
    private static volatile Map<Item, Entry> RESOLVED_TAG_ITEMS = new HashMap<>();

    private EquipmentRegistry() {
        NotInitializable.staticClass(this);
    }

    /**
     * Registers an item tag as equippable into the given slot without shift-click support.
     *
     * @param slot the equipment slot the tagged items occupy
     * @param tag  the item tag identifying equippable items
     */
    @Api
    public static void registerEquipment(EquipmentSlot slot, TagKey<Item> tag) {
        registerEquipment(slot, tag, false);
    }

    /**
     * Registers an item tag as equippable into the given slot.
     *
     * @param slot          the equipment slot the tagged items occupy
     * @param tag           the item tag identifying equippable items
     * @param canShiftClick {@code true} if items matching this tag can be equipped by shift-clicking
     */
    @Api
    public static void registerEquipment(EquipmentSlot slot, TagKey<Item> tag, boolean canShiftClick) {
        if (!TAG_KEY_REGISTRY.containsKey(tag)) {
            TAG_KEY_REGISTRY.put(tag, new Entry(slot, canShiftClick));
        }
    }

    /**
     * Registers a specific item as equippable into the given slot without shift-click support.
     *
     * @param slot the equipment slot
     * @param item the item to register
     */
    @Api public static void registerEquipment(EquipmentSlot slot, ItemLike item) {
        registerEquipment(slot, item, false);
    }

    /**
     * Registers a specific item as equippable into the given slot.
     *
     * @param slot          the equipment slot
     * @param item          the item to register
     * @param canShiftClick {@code true} if this item can be equipped by shift-clicking
     */
    @Api public static void registerEquipment(EquipmentSlot slot, ItemLike item, boolean canShiftClick) {
        var key = item.asItem();
        if (!ITEM_REGISTRY.containsKey(key)) {
            ITEM_REGISTRY.put(key, new Entry(slot, canShiftClick));
        }
    }

    /**
     * Unconditionally updates the equipment registration for a specific item.
     *
     * <p>Unlike {@link #registerEquipment(EquipmentSlot, ItemLike, boolean)}, this method
     * always overwrites an existing entry. Use this when the registration is driven by a
     * runtime config value that can change (e.g. on server config reload).</p>
     *
     * @param slot          the equipment slot
     * @param item          the item to update
     * @param canShiftClick {@code true} if this item can be equipped by shift-clicking
     */
    @Api public static void updateEquipment(EquipmentSlot slot, ItemLike item, boolean canShiftClick) {
        ITEM_REGISTRY.put(item.asItem(), new Entry(slot, canShiftClick));
    }

    /**
     * Removes a specific item's equipment registration.
     *
     * <p>Takes effect immediately. Has no effect if the item was not directly registered.</p>
     *
     * @param item the item to remove
     */
    @Api public static void unregister(final ItemLike item) {
        ITEM_REGISTRY.remove(item.asItem());
    }

    /**
     * Removes a tag's equipment registration.
     *
     * <p>Also clears the flattened tag cache ({@link #RESOLVED_TAG_ITEMS}). Items registered
     * via remaining tags will re-appear at the next {@link #onTagsLoaded} call.</p>
     *
     * @param tag the tag to remove
     * @throws UnsupportedOperationException if the tag is protected (registered by Lepidoptera itself)
     */
    @Api public static void unregister(final TagKey<Item> tag) {
        if (PROTECTED_TAGS.contains(tag)) {
            throw new UnsupportedOperationException("Cannot unregister a protected tag: " + tag.location());
        }
        if (TAG_KEY_REGISTRY.remove(tag) != null) {
            RESOLVED_TAG_ITEMS = new HashMap<>();
        }
    }

    /**
     * Marks a tag as protected, preventing it from being removed via {@link #unregister(TagKey)}.
     * Not part of the public API — called by {@code LepidopteraAPI} during post-initialization.
     *
     * @param tags the tag or tags to protect
     */
    @SafeVarargs
    public static void protect(final TagKey<Item>...tags) {
        PROTECTED_TAGS.addAll(Arrays.asList(tags));
    }

    /**
     * Returns the registered {@link EquipmentSlot} for the given item stack, or
     * {@code null} if the item (or none of its tags) is registered.
     *
     * <p>Item-level registrations are checked before tag-level registrations.</p>
     *
     * @param itemStack the stack to look up
     * @return the equipment slot, or {@code null}
     */
    public static @Nullable EquipmentSlot getSlot(final ItemStack itemStack) {
        @Nullable final Entry entry = getEntry(itemStack);
        return entry != null ? entry.slot : null;
    }

    /**
     * Returns {@code true} if the given item stack is registered as shift-click equippable.
     *
     * @param itemStack the stack to check
     * @return {@code true} if the item can be equipped via shift-click
     */
    public static boolean canShiftClickEquipment(final ItemStack itemStack) {
        @Nullable final Entry entry = getEntry(itemStack);
        return entry != null && entry.canShiftClick;
    }

    /**
     * Returns the full {@link Entry} for the given item stack, or {@code null} if
     * the item is not registered.
     *
     * <p>Checks direct item registrations first (O(1)), then the flattened tag cache (O(1)).
     * Tag registrations are only visible after the first {@link #onTagsLoaded} call.</p>
     *
     * @param itemStack the stack to look up
     * @return the registration entry, or {@code null}
     */
    public static @Nullable Entry getEntry(final ItemStack itemStack) {
        @Nullable Entry entry = ITEM_REGISTRY.get(itemStack.getItem());
        if (entry == null) entry = RESOLVED_TAG_ITEMS.get(itemStack.getItem());
        return entry;
    }

    /**
     * Resolves tag registrations to concrete items and rebuilds the O(1) lookup cache.
     * Package-private — called by {@code LepidopteraAPI} on server data load and {@code /reload}.
     *
     * @param registryAccess the current registry access
     */
    public static void onTagsLoaded(final RegistryAccess registryAccess) {
        final Map<Item, Entry> resolved = new HashMap<>();
        final var itemLookup = registryAccess.lookupOrThrow(Registries.ITEM);
        for (final var tagEntry : TAG_KEY_REGISTRY.entrySet()) {
            itemLookup.get(tagEntry.getKey()).ifPresent(holders ->
                    holders.forEach(h -> resolved.putIfAbsent(h.value(), tagEntry.getValue())));
        }
        RESOLVED_TAG_ITEMS = resolved;
    }

    /**
     * Holds the registration data for an equippable item or tag.
     *
     * @param slot          the target equipment slot
     * @param canShiftClick whether the item can be equipped by shift-clicking
     */
    public record Entry(EquipmentSlot slot, boolean canShiftClick) {}
}
