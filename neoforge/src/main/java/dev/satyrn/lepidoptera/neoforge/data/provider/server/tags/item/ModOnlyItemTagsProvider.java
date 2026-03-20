package dev.satyrn.lepidoptera.neoforge.data.provider.server.tags.item;

import dev.satyrn.lepidoptera.annotations.Api;
import dev.satyrn.lepidoptera.neoforge.data.provider.server.tags.block.NoOpBlockTagsProvider;
import dev.satyrn.lepidoptera.util.ModHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

@Api
public abstract class ModOnlyItemTagsProvider extends ModItemTagsProvider {
    protected ModOnlyItemTagsProvider(Class<?> modClass, PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper) {
        this(modClass, output, lookupProvider,
                new NoOpBlockTagsProvider(output, lookupProvider, ModHelper.modId(modClass), existingFileHelper),
                existingFileHelper);
    }

    private ModOnlyItemTagsProvider(Class<?> modClass, PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider,
            NoOpBlockTagsProvider noOpProvider,
            @Nullable ExistingFileHelper existingFileHelper) {
        super(modClass, output, lookupProvider, noOpProvider.contentsGetter(), existingFileHelper);
    }
}
