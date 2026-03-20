package dev.satyrn.lepidoptera.forge.data.provider.server.tags.block;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class NoOpBlockTagsProvider extends net.minecraft.data.tags.BlockTagsProvider {
    public NoOpBlockTagsProvider(DataGenerator generator, String modId,
            @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, modId, existingFileHelper);
    }

    @Override
    protected void addTags() {
    }
}
