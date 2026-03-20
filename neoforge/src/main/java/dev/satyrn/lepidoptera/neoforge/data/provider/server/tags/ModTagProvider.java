package dev.satyrn.lepidoptera.neoforge.data.provider.server.tags;

import dev.satyrn.lepidoptera.annotations.Api;
import dev.satyrn.lepidoptera.annotations.ModMeta;
import dev.satyrn.lepidoptera.util.ModHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

@Api
public abstract class ModTagProvider<T> extends net.minecraft.data.tags.TagsProvider<T> {
    protected final ModMeta metadata;

    protected ModTagProvider(Class<?> modClass, PackOutput output,
            ResourceKey<? extends Registry<T>> registryKey,
            CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper) {
        super(output, registryKey, lookupProvider, ModHelper.modId(modClass), existingFileHelper);
        this.metadata = ModHelper.metadata(modClass);
    }

    @Override
    protected final void addTags(HolderLookup.Provider provider) {
        addModTags(provider);
    }

    protected abstract void addModTags(HolderLookup.Provider provider);

    @Override
    public String getName() {
        return "Tag provider for " + ModHelper.friendlyName(this.metadata);
    }
}
