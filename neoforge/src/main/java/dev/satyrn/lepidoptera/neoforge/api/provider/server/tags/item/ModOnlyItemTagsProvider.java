package dev.satyrn.lepidoptera.neoforge.api.provider.server.tags.item;

import dev.satyrn.lepidoptera.api.annotations.Api;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Api
public abstract class ModOnlyItemTagsProvider
        extends ModItemTagsProvider {
    @Api
    protected ModOnlyItemTagsProvider(final Class<?> modClass, final PackOutput output,
            final CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable final ExistingFileHelper existingFileHelper) {
        super(modClass, output, lookupProvider,
                CompletableFuture.completedFuture(tag -> Optional.empty()),
                existingFileHelper);
    }

    @Override
    public ResourceLocation location() {
        return super.location().withSuffix("_only");
    }
}
