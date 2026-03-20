package dev.satyrn.lepidoptera.neoforge.data.provider.server.tags.block;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class NoOpBlockTagsProvider extends BlockTagsProvider {
    public NoOpBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
            String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
    }
}
