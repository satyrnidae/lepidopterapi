package dev.satyrn.lepidoptera.neoforge.data.provider.server.tags.block;

import dev.satyrn.lepidoptera.annotations.Api;
import dev.satyrn.lepidoptera.annotations.ModMeta;
import dev.satyrn.lepidoptera.util.ModHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

@Api
public abstract class ModBlockTagsProvider extends BlockTagsProvider {
    protected final ModMeta metadata;

    protected ModBlockTagsProvider(Class<?> modClass, PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, ModHelper.modId(modClass), existingFileHelper);
        this.metadata = ModHelper.metadata(modClass);
    }

    @Override
    protected final void addTags(HolderLookup.Provider provider) {
        addModTags(provider);
    }

    protected abstract void addModTags(HolderLookup.Provider provider);

    @Override
    public String getName() {
        return "Block tag provider for " + ModHelper.friendlyName(this.metadata);
    }
}
