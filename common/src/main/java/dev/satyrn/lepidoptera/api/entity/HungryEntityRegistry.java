package dev.satyrn.lepidoptera.api.entity;

import dev.satyrn.lepidoptera.api.NotInitializable;
import dev.satyrn.lepidoptera.api.annotations.Api;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Contract;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Registry that enables the hunger system for specific entity types.
 *
 * <p>Register entity types or entity type tags here so that the Lepidoptera mixin on
 * {@code LivingEntity} will tick {@link dev.satyrn.lepidoptera.api.food.EntityFoodData}
 * and persist food data for those entity types. Registration is first-come, first-served:
 * subsequent calls for an already-registered type or tag are silently ignored.</p>
 *
 * <p>Tag registrations are resolved to concrete entity types on server data load and on
 * {@code /reload}. Direct {@link EntityType} registrations take effect immediately.</p>
 *
 * <h2>Usage</h2>
 * <pre>{@code
 * // Register a single type
 * HungryEntityRegistry.register(EntityType.WOLF);
 *
 * // Register all types in a tag (resolved at data load)
 * HungryEntityRegistry.register(MyModTags.HUNGRY_ANIMALS);
 * }</pre>
 */
@Api
public final class HungryEntityRegistry {

    private static final Set<EntityType<?>> TYPE_REGISTRY = new HashSet<>();
    private static final Set<TagKey<EntityType<?>>> TAG_REGISTRY = new HashSet<>();
    private static final Set<TagKey<EntityType<?>>> PROTECTED_TAGS = new HashSet<>();

    /**
     * Flattened lookup set rebuilt on every {@link #onTagsLoaded} call.
     * {@code volatile} ensures visibility across the server-thread write and game-thread reads.
     */
    private static volatile Set<EntityType<?>> RESOLVED_TYPES = new HashSet<>();

    @Contract("-> fail")
    private HungryEntityRegistry() {
        NotInitializable.staticClass(this);
    }

    /**
     * Registers an entity type for hunger simulation.
     *
     * <p>The type is added to the lookup set immediately. If the type is already
     * registered, this call is a no-op.</p>
     *
     * @param type the entity type to register
     */
    @Api public static void register(final EntityType<?> type) {
        if (TYPE_REGISTRY.add(type)) {
            final Set<EntityType<?>> updated = new HashSet<>(RESOLVED_TYPES);
            updated.add(type);
            RESOLVED_TYPES = updated;
        }
    }

    /**
     * Registers all entity types in a tag for hunger simulation.
     *
     * <p>The tag is resolved to concrete types on the next call to
     * {@link #onTagsLoaded} (i.e. at server data load or {@code /reload}).
     * If the tag is already registered, this call is a no-op.</p>
     *
     * @param tag the entity type tag to register
     */
    @Api public static void register(final TagKey<EntityType<?>> tag) {
        TAG_REGISTRY.add(tag);
    }

    /**
     * Removes a directly-registered entity type from the hunger system.
     *
     * <p>Takes effect immediately: the type is removed from the lookup set. If the type
     * is also present in a registered tag, it will re-appear at the next
     * {@link #onTagsLoaded} call.</p>
     *
     * @param type the entity type to remove
     */
    @Api public static void unregister(final EntityType<?> type) {
        if (TYPE_REGISTRY.remove(type)) {
            final Set<EntityType<?>> updated = new HashSet<>(RESOLVED_TYPES);
            updated.remove(type);
            RESOLVED_TYPES = updated;
        }
    }

    /**
     * Removes a tag registration from the hunger system.
     *
     * <p>Tag-resolved entries in the lookup set are not immediately purged; they will be
     * removed on the next {@link #onTagsLoaded} call (i.e. server data load or
     * {@code /reload}).</p>
     *
     * @param tag the tag to remove
     * @throws UnsupportedOperationException if the tag is protected (registered by Lepidoptera itself)
     */
    @Api public static void unregister(final TagKey<EntityType<?>> tag) {
        if (PROTECTED_TAGS.contains(tag)) {
            throw new UnsupportedOperationException("Cannot unregister a protected tag: " + tag.location());
        }
        TAG_REGISTRY.remove(tag);
    }

    /**
     * Marks a tag as protected, preventing it from being removed via {@link #unregister(TagKey)}.
     * Not part of the public API — called by {@code LepidopteraAPI} during post-initialization.
     *
     * @param tags the tag or tags to protect
     */
    @SafeVarargs
    public static void protect(final TagKey<EntityType<?>>... tags) {
        PROTECTED_TAGS.addAll(Arrays.asList(tags));
    }

    /**
     * Returns {@code true} if the given entity type participates in the hunger system.
     *
     * <p>This is an O(1) lookup against a pre-flattened set. It is safe to call on every
     * entity tick.</p>
     *
     * @param type the entity type to check
     * @return {@code true} if the type is registered, either directly or via a tag
     */
    @Contract(pure = true)
    @Api public static boolean isRegistered(final EntityType<?> type) {
        return RESOLVED_TYPES.contains(type);
    }

    /**
     * Resolves all registered tags to concrete entity types and rebuilds the O(1) lookup set.
     * Package-private — called by {@code LepidopteraAPI} on server data load and {@code /reload}.
     *
     * @param registryAccess the current registry access
     */
    public static void onTagsLoaded(final RegistryAccess registryAccess) {
        final Set<EntityType<?>> resolved = new HashSet<>(TYPE_REGISTRY);
        final var entityTypeLookup = registryAccess.lookupOrThrow(Registries.ENTITY_TYPE);
        for (final TagKey<EntityType<?>> tag : TAG_REGISTRY) {
            entityTypeLookup.get(tag).ifPresent(holders ->
                    holders.forEach(h -> resolved.add(h.value())));
        }
        RESOLVED_TYPES = resolved;
    }
}