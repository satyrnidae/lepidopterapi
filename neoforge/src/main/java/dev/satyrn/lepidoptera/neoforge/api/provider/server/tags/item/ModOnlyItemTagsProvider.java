package dev.satyrn.lepidoptera.neoforge.api.provider.server.tags.item;

import org.jetbrains.annotations.ApiStatus;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
public abstract class ModOnlyItemTagsProvider extends ModItemTagsProvider {
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    protected ModOnlyItemTagsProvider(final Class<?> modClass,
                                      final PackOutput output,
                                      final CompletableFuture<HolderLookup.Provider> lookupProvider,
                                      @Nullable final ExistingFileHelper existingFileHelper) {
        super(modClass, output, lookupProvider, CompletableFuture.completedFuture(tag -> Optional.empty()),
                existingFileHelper);
    }

    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public @Override ResourceLocation location() {
        return super.location().withSuffix("_only");
    }
}
