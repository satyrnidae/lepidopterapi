package dev.satyrn.lepidoptera.neoforge.data.provider.server.tags.item;

import dev.satyrn.lepidoptera.annotations.Api;
import dev.satyrn.lepidoptera.annotations.ModMeta;
import dev.satyrn.lepidoptera.util.ModHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

@Api
public abstract class ModItemTagsProvider extends net.minecraft.data.tags.ItemTagsProvider {
    protected final ModMeta metadata;

    protected ModItemTagsProvider(Class<?> modClass, PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider,
            CompletableFuture<TagsProvider.TagLookup<Block>> blockTagsLookup,
            @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTagsLookup, ModHelper.modId(modClass), existingFileHelper);
        this.metadata = ModHelper.metadata(modClass);
    }

    @Override
    protected final void addTags(HolderLookup.Provider provider) {
        addModTags(provider);
    }

    protected abstract void addModTags(HolderLookup.Provider provider);

    @Override
    public String getName() {
        return "Item tag provider for " + ModHelper.friendlyName(this.metadata);
    }
}
