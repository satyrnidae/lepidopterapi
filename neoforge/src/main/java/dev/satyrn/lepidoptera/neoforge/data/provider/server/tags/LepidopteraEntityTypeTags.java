package dev.satyrn.lepidoptera.neoforge.data.provider.server.tags;

import dev.satyrn.lepidoptera.LepidopteraAPI;
import dev.satyrn.lepidoptera.api.entity.ApiEntityTags;
import dev.satyrn.lepidoptera.neoforge.api.provider.server.tags.entity.ModEntityTypeTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class LepidopteraEntityTypeTags extends ModEntityTypeTagsProvider {
    public LepidopteraEntityTypeTags(PackOutput arg,
                                     CompletableFuture<HolderLookup.Provider> completableFuture,
                                     @Nullable ExistingFileHelper existingFileHelper) {
        super(LepidopteraAPI.class, arg, completableFuture, existingFileHelper);
    }

    @Override
    protected void addModTags(final HolderLookup.Provider provider) {
        this.tag(ApiEntityTags.TICKS_FOOD);
    }
}
