package dev.satyrn.lepidoptera.api.item;

import dev.satyrn.lepidoptera.api.NotInitializable;
import dev.satyrn.lepidoptera.api.compatibility.Provider;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Registry that maps items and item tags to equipment slots.
 *
 * <p>Mods register their equipment items here so that Lepidoptera's mixin on
 * {@code Item} can return the correct {@link EquipmentSlot} from
 * {@code Item#getEquipmentSlot(ItemStack)}. Registration is first-come,
 * first-served at equal priority: subsequent calls for an already-registered
 * item or tag at the same or lower priority are silently ignored. A
 * higher-priority registration always wins over a lower-priority one,
 * regardless of call order. Use {@link Provider.Priority} constants as the
 * {@code priority} argument to the priority-bearing overloads.</p>
 *
 * @since 1.0.0-SNAPSHOT.1+1.21.1
 */
@ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
public final class EquipmentRegistry {
    private static final HashMap<Item, PrioritizedEntry> ITEM_REGISTRY = new HashMap<>();
    private static final Map<TagKey<Item>, PrioritizedEntry> TAG_KEY_REGISTRY = new HashMap<>();
    private static final Set<TagKey<Item>> PROTECTED_TAGS = new HashSet<>();

    /**
     * Flattened item-to-entry map rebuilt on every {@link #onTagsLoaded} call.
     * {@code volatile} ensures visibility across the server-thread write and game-thread reads.
     */
    private static volatile Map<Item, PrioritizedEntry> RESOLVED_TAG_ITEMS = new HashMap<>();

    private EquipmentRegistry() {
        NotInitializable.staticClass(this);
    }

    /**
     * Registers an item tag as equippable into the given slot without shift-click support,
     * at {@link Provider.Priority#NORMAL} priority.
     *
     * @param slot the equipment slot the tagged items occupy
     * @param tag  the item tag identifying equippable items
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public static void registerEquipment(EquipmentSlot slot, TagKey<Item> tag) {
        registerEquipment(slot, tag, false, Provider.Priority.NORMAL);
    }

    /**
     * Registers an item tag as equippable into the given slot, at
     * {@link Provider.Priority#NORMAL} priority.
     *
     * @param slot          the equipment slot the tagged items occupy
     * @param tag           the item tag identifying equippable items
     * @param canShiftClick {@code true} if items matching this tag can be equipped by shift-clicking
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public static void registerEquipment(EquipmentSlot slot, TagKey<Item> tag, boolean canShiftClick) {
        registerEquipment(slot, tag, canShiftClick, Provider.Priority.NORMAL);
    }

    /**
     * Registers an item tag as equippable into the given slot without shift-click support.
     *
     * <p>If this tag is already registered at a higher or equal priority, this call is a
     * no-op. A higher-priority registration always wins, regardless of call order.</p>
     *
     * @param slot     the equipment slot the tagged items occupy
     * @param tag      the item tag identifying equippable items
     * @param priority the registration priority; use {@link Provider.Priority} constants
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public static void registerEquipment(EquipmentSlot slot, TagKey<Item> tag, short priority) {
        registerEquipment(slot, tag, false, priority);
    }

    /**
     * Registers an item tag as equippable into the given slot.
     *
     * <p>If this tag is already registered at a higher or equal priority, this call is a
     * no-op. A higher-priority registration always wins, regardless of call order.</p>
     *
     * @param slot          the equipment slot the tagged items occupy
     * @param tag           the item tag identifying equippable items
     * @param canShiftClick {@code true} if items matching this tag can be equipped by shift-clicking
     * @param priority      the registration priority; use {@link Provider.Priority} constants
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public static void registerEquipment(EquipmentSlot slot, TagKey<Item> tag, boolean canShiftClick, short priority) {
        final PrioritizedEntry existing = TAG_KEY_REGISTRY.get(tag);
        if (existing == null || priority > existing.priority()) {
            TAG_KEY_REGISTRY.put(tag, new PrioritizedEntry(slot, canShiftClick, priority));
        }
    }

    /**
     * Registers a specific item as equippable into the given slot without shift-click
     * support, at {@link Provider.Priority#NORMAL} priority.
     *
     * @param slot the equipment slot
     * @param item the item to register
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public static void registerEquipment(EquipmentSlot slot, ItemLike item) {
        registerEquipment(slot, item, false, Provider.Priority.NORMAL);
    }

    /**
     * Registers a specific item as equippable into the given slot, at
     * {@link Provider.Priority#NORMAL} priority.
     *
     * @param slot          the equipment slot
     * @param item          the item to register
     * @param canShiftClick {@code true} if this item can be equipped by shift-clicking
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public static void registerEquipment(EquipmentSlot slot, ItemLike item, boolean canShiftClick) {
        registerEquipment(slot, item, canShiftClick, Provider.Priority.NORMAL);
    }

    /**
     * Registers a specific item as equippable into the given slot without shift-click
     * support.
     *
     * <p>If this item is already registered at a higher or equal priority, this call is a
     * no-op. A higher-priority registration always wins, regardless of call order.</p>
     *
     * @param slot     the equipment slot
     * @param item     the item to register
     * @param priority the registration priority; use {@link Provider.Priority} constants
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public static void registerEquipment(EquipmentSlot slot, ItemLike item, short priority) {
        registerEquipment(slot, item, false, priority);
    }

    /**
     * Registers a specific item as equippable into the given slot.
     *
     * <p>If this item is already registered at a higher or equal priority, this call is a
     * no-op. A higher-priority registration always wins, regardless of call order.</p>
     *
     * @param slot          the equipment slot
     * @param item          the item to register
     * @param canShiftClick {@code true} if this item can be equipped by shift-clicking
     * @param priority      the registration priority; use {@link Provider.Priority} constants
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public static void registerEquipment(EquipmentSlot slot, ItemLike item, boolean canShiftClick, short priority) {
        final var key = item.asItem();
        final PrioritizedEntry existing = ITEM_REGISTRY.get(key);
        if (existing == null || priority > existing.priority()) {
            ITEM_REGISTRY.put(key, new PrioritizedEntry(slot, canShiftClick, priority));
        }
    }

    /**
     * Unconditionally updates the equipment registration for a specific item, bypassing
     * the normal priority system.
     *
     * <p>Unlike {@link #registerEquipment(EquipmentSlot, ItemLike, boolean)}, this method
     * always overwrites an existing entry. The internal priority is set to
     * {@code Short.MAX_VALUE}  one above {@link Provider.Priority#HIGHEST}  so no
     * subsequent {@code registerEquipment} call (regardless of priority) can displace
     * the update. Use this when the registration is driven by a runtime config value
     * that can change (e.g. on server config reload).</p>
     *
     * @param slot          the equipment slot
     * @param item          the item to update
     * @param canShiftClick {@code true} if this item can be equipped by shift-clicking
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public static void updateEquipment(EquipmentSlot slot, ItemLike item, boolean canShiftClick) {
        ITEM_REGISTRY.put(item.asItem(), new PrioritizedEntry(slot, canShiftClick, Short.MAX_VALUE));
    }

    /**
     * Removes a specific item's equipment registration.
     *
     * <p>Takes effect immediately. Has no effect if the item was not directly registered.</p>
     *
     * @param item the item to remove
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public static void unregister(final ItemLike item) {
        ITEM_REGISTRY.remove(item.asItem());
    }

    /**
     * Removes a tag's equipment registration.
     *
     * <p>Also clears the flattened tag cache ({@link #RESOLVED_TAG_ITEMS}). Items registered
     * via remaining tags will re-appear at the next {@link #onTagsLoaded} call.</p>
     *
     * @param tag the tag to remove
     *
     * @throws UnsupportedOperationException if the tag is protected (registered by Lepidoptera itself)
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public static void unregister(final TagKey<Item> tag) {
        if (PROTECTED_TAGS.contains(tag)) {
            throw new UnsupportedOperationException("Cannot unregister a protected tag: " + tag.location());
        }
        if (TAG_KEY_REGISTRY.remove(tag) != null) {
            RESOLVED_TAG_ITEMS = new HashMap<>();
        }
    }

    /**
     * Marks a tag as protected, preventing it from being removed via {@link #unregister(TagKey)}.
     * Not part of the public API - called by {@code LepidopteraAPI} during post-initialization.
     *
     * @param tags the tag or tags to protect
     */
    @ApiStatus.Internal
    @SafeVarargs
    public static void protect(final TagKey<Item>... tags) {
        PROTECTED_TAGS.addAll(Arrays.asList(tags));
    }

    /**
     * Returns the registered {@link EquipmentSlot} for the given item stack, or
     * {@code null} if the item (or none of its tags) is registered.
     *
     * <p>Item-level registrations are checked before tag-level registrations.</p>
     *
     * @param itemStack the stack to look up
     *
     * @return the equipment slot, or {@code null}
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public static @Nullable EquipmentSlot getSlot(final ItemStack itemStack) {
        @Nullable final Entry entry = getEntry(itemStack);
        return entry != null ? entry.slot : null;
    }

    /**
     * Returns {@code true} if the given item stack is registered as shift-click equippable.
     *
     * @param itemStack the stack to check
     *
     * @return {@code true} if the item can be equipped via shift-click
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
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
     *
     * @return the registration entry, or {@code null}
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public static @Nullable Entry getEntry(final ItemStack itemStack) {
        @Nullable PrioritizedEntry pEntry = ITEM_REGISTRY.get(itemStack.getItem());
        if (pEntry == null) {
            pEntry = RESOLVED_TAG_ITEMS.get(itemStack.getItem());
        }
        return pEntry != null ? new Entry(pEntry.slot(), pEntry.canShiftClick()) : null;
    }

    /**
     * Resolves tag registrations to concrete items and rebuilds the O(1) lookup cache.
     * If the same item is covered by multiple registered tags, the highest-priority tag
     * registration wins; ties go to the first one encountered during iteration.
     * Package-private  called by {@code LepidopteraAPI} on server data load and {@code /reload}.
     *
     * @param registryAccess the current registry access
     */
    @ApiStatus.Internal
    public static void onTagsLoaded(final RegistryAccess registryAccess) {
        final Map<Item, PrioritizedEntry> resolved = new HashMap<>();
        final var itemLookup = registryAccess.lookupOrThrow(Registries.ITEM);
        for (final var tagEntry : TAG_KEY_REGISTRY.entrySet()) {
            final PrioritizedEntry tagPEntry = tagEntry.getValue();
            itemLookup.get(tagEntry.getKey()).ifPresent(holders -> holders.forEach(h -> {
                final PrioritizedEntry existing = resolved.get(h.value());
                if (existing == null || tagPEntry.priority() > existing.priority()) {
                    resolved.put(h.value(), tagPEntry);
                }
            }));
        }
        RESOLVED_TAG_ITEMS = resolved;
    }

    /**
     * Holds the public registration data for an equippable item or tag.
     *
     * @param slot          the target equipment slot
     * @param canShiftClick whether the item can be equipped by shift-clicking
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public record Entry(EquipmentSlot slot, boolean canShiftClick) {
    }

    /**
     * Internal storage record that adds priority tracking to {@link Entry}.
     * The public {@link Entry} record is unchanged for API compatibility.
     */
    private record PrioritizedEntry(EquipmentSlot slot, boolean canShiftClick, short priority) {
    }
}
