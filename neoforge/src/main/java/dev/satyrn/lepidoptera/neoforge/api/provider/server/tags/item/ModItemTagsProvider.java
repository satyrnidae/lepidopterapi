package dev.satyrn.lepidoptera.neoforge.api.provider.server.tags.item;

import dev.satyrn.lepidoptera.api.ModHelper;
import dev.satyrn.lepidoptera.api.ModMeta;
import dev.satyrn.lepidoptera.api.WithLocation;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

/**
 * Abstract base for mod-specific NeoForge item tag data providers.
 *
 * <p>Subclass this and implement {@link #addModTags(HolderLookup.Provider)} to register
 * item tags for your mod. Wire the provider into your {@code GatherDataEvent} listener.</p>
 *
 * <p>If your provider only needs to register tags for your own mod's items (and does not
 * copy or depend on block tags), prefer {@link ModOnlyItemTagsProvider}, which avoids
 * requiring a block tags lookup future.</p>
 *
 * @since 1.0.0-SNAPSHOT.1+1.21.1
 */
@ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
public abstract class ModItemTagsProvider extends ItemTagsProvider implements WithLocation {

    /**
     * The mod metadata resolved from the mod class passed to the constructor.
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    protected final ModMeta metadata;

    /**
     * Creates a new item tag provider for the given mod class.
     *
     * @param modClass           the mod's main class, annotated with {@link dev.satyrn.lepidoptera.api.ModMeta}
     * @param output             the data-gen pack output
     * @param lookupProvider     a future providing the registry lookup context
     * @param blockTagsLookup    a future providing block tag lookups (required by
     *                           {@link ItemTagsProvider} for copying block tags to item tags)
     * @param existingFileHelper helper used to validate references to existing tag files,
     *                           or {@code null} to skip validation
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    protected ModItemTagsProvider(Class<?> modClass,
                                  PackOutput output,
                                  CompletableFuture<HolderLookup.Provider> lookupProvider,
                                  CompletableFuture<TagsProvider.TagLookup<Block>> blockTagsLookup,
                                  @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTagsLookup, ModHelper.modId(modClass), existingFileHelper);
        this.metadata = ModHelper.metadata(modClass);
    }

    @Override
    protected final void addTags(HolderLookup.Provider provider) {
        addModTags(provider);
    }

    /**
     * Implement to register item tags via the inherited {@code tag()} methods.
     * Called during data generation in place of {@code addTags}.
     *
     * @param provider the registry lookup context
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    protected abstract void addModTags(HolderLookup.Provider provider);

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @Override
    public String getName() {
        return location().toString();
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @Override
    public ResourceLocation location() {
        return ModHelper.resource(this.metadata, "providers/tag/item");
    }
}
