package dev.satyrn.lepidoptera.neoforge.api.provider.server.tags.block;

import dev.satyrn.lepidoptera.api.WithLocation;
import dev.satyrn.lepidoptera.api.annotations.Api;
import dev.satyrn.lepidoptera.api.ModMeta;
import dev.satyrn.lepidoptera.api.ModHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

@Api
public abstract class ModBlockTagsProvider
        extends BlockTagsProvider
        implements WithLocation {
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
        return location().toString();
    }

    @Override
    public ResourceLocation location() {
        return ModHelper.resource(this.metadata, "providers/tag/block");
    }
}
