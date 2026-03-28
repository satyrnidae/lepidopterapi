package dev.satyrn.lepidoptera.neoforge.api.provider.server.tags.item;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * A {@link ModItemTagsProvider} variant that does not depend on a block tags lookup.
 *
 * <p>Use this when your item tag provider only registers tags for your own mod's items
 * and never copies or references block tags. It supplies an empty block tags lookup
 * to the parent constructor, removing the need to wire up a {@code ModBlockTagsProvider}
 * dependency in your {@code GatherDataEvent} listener.</p>
 *
 * @since 1.0.0-SNAPSHOT.1+1.21.1
 */
@ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
public abstract class ModOnlyItemTagsProvider extends ModItemTagsProvider {

    /**
     * Creates a new mod-only item tag provider for the given mod class.
     *
     * @param modClass           the mod's main class, annotated with {@link dev.satyrn.lepidoptera.api.ModMeta}
     * @param output             the data-gen pack output
     * @param lookupProvider     a future providing the registry lookup context
     * @param existingFileHelper helper used to validate references to existing tag files,
     *                           or {@code null} to skip validation
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    protected ModOnlyItemTagsProvider(final Class<?> modClass,
                                      final PackOutput output,
                                      final CompletableFuture<HolderLookup.Provider> lookupProvider,
                                      @Nullable final ExistingFileHelper existingFileHelper) {
        super(modClass, output, lookupProvider, CompletableFuture.completedFuture(tag -> Optional.empty()),
                existingFileHelper);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Appends {@code _only} to the parent location to distinguish this provider's
     * data-gen log entry from a full item tags provider for the same mod.</p>
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @Override
    public ResourceLocation location() {
        return super.location().withSuffix("_only");
    }
}
