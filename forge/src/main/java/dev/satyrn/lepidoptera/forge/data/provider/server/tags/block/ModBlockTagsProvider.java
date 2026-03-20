package dev.satyrn.lepidoptera.forge.data.provider.server.tags.block;

import dev.satyrn.lepidoptera.annotations.Api;
import dev.satyrn.lepidoptera.annotations.ModMeta;
import dev.satyrn.lepidoptera.util.ModHelper;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

@Api
public abstract class ModBlockTagsProvider extends net.minecraft.data.tags.BlockTagsProvider {
    protected final ModMeta metadata;

    protected ModBlockTagsProvider(Class<?> modClass, DataGenerator generator,
            @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, ModHelper.modId(modClass), existingFileHelper);
        this.metadata = ModHelper.metadata(modClass);
    }

    @Override
    protected final void addTags() {
        addModTags();
    }

    protected abstract void addModTags();

    @Override
    public String getName() {
        return "Block tag provider for " + ModHelper.friendlyName(this.metadata);
    }
}
