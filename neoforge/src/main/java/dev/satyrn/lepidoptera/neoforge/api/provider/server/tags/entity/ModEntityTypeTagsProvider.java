package dev.satyrn.lepidoptera.neoforge.api.provider.server.tags.entity;

import dev.satyrn.lepidoptera.api.ModHelper;
import dev.satyrn.lepidoptera.api.ModMeta;
import dev.satyrn.lepidoptera.api.WithLocation;
import dev.satyrn.lepidoptera.api.annotations.Api;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

@Api
public abstract class ModEntityTypeTagsProvider
        extends EntityTypeTagsProvider
        implements WithLocation {
    protected final ModMeta metadata;

    public ModEntityTypeTagsProvider(Class<?> modClass,
                                     PackOutput arg,
                                     CompletableFuture<HolderLookup.Provider> completableFuture,
                                     @Nullable ExistingFileHelper existingFileHelper) {
        super(arg, completableFuture, ModHelper.modId(modClass), existingFileHelper);
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
        return ModHelper.resource(this.metadata, "providers/tag/entity_type");
    }
}
