package dev.satyrn.lepidoptera.neoforge.api.provider.server.tags.item;

import dev.satyrn.lepidoptera.api.ModHelper;
import dev.satyrn.lepidoptera.api.ModMeta;
import dev.satyrn.lepidoptera.api.WithLocation;
import dev.satyrn.lepidoptera.api.annotations.Api;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

@Api("1.0.0-SNAPSHOT.1+1.21.1")
public abstract class ModItemTagsProvider extends ItemTagsProvider implements WithLocation {
    protected final ModMeta metadata;

    @Api("1.0.0-SNAPSHOT.1+1.21.1")
    protected ModItemTagsProvider(Class<?> modClass,
                                  PackOutput output,
                                  CompletableFuture<HolderLookup.Provider> lookupProvider,
                                  CompletableFuture<TagsProvider.TagLookup<Block>> blockTagsLookup,
                                  @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTagsLookup, ModHelper.modId(modClass), existingFileHelper);
        this.metadata = ModHelper.metadata(modClass);
    }

    protected final @Override void addTags(HolderLookup.Provider provider) {
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
        return ModHelper.resource(this.metadata, "providers/tag/item");
    }
}
