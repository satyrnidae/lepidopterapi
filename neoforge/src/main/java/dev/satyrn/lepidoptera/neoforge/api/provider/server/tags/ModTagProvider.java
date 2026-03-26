package dev.satyrn.lepidoptera.neoforge.api.provider.server.tags;

import dev.satyrn.lepidoptera.api.ModHelper;
import dev.satyrn.lepidoptera.api.ModMeta;
import dev.satyrn.lepidoptera.api.WithLocation;
import dev.satyrn.lepidoptera.api.annotations.Api;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

@Api("1.0.0-SNAPSHOT.1+1.21.1")
public abstract class ModTagProvider<T> extends TagsProvider<T> implements WithLocation {
    protected final ModMeta metadata;

    @Api("1.0.0-SNAPSHOT.1+1.21.1")
    protected ModTagProvider(Class<?> modClass,
                             PackOutput output,
                             ResourceKey<? extends Registry<T>> registryKey,
                             CompletableFuture<HolderLookup.Provider> lookupProvider,
                             @Nullable ExistingFileHelper existingFileHelper) {
        super(output, registryKey, lookupProvider, ModHelper.modId(modClass), existingFileHelper);
        this.metadata = ModHelper.metadata(modClass);
    }

    protected @Override
    final void addTags(HolderLookup.Provider provider) {
        addModTags(provider);
    }

    @Api("1.0.0-SNAPSHOT.1+1.21.1")
    protected abstract void addModTags(HolderLookup.Provider provider);

    @Api("1.0.0-SNAPSHOT.1+1.21.1")
    public @Override String getName() {
        return location().toString();
    }

    @Api("1.0.0-SNAPSHOT.1+1.21.1")
    public @Override ResourceLocation location() {
        return ModHelper.resource(metadata, "tags/" + this.registryKey.location().getPath());
    }
}
