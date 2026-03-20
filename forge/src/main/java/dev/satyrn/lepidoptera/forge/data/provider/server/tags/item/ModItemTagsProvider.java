package dev.satyrn.lepidoptera.forge.data.provider.server.tags.item;

import dev.satyrn.lepidoptera.annotations.Api;
import dev.satyrn.lepidoptera.annotations.ModMeta;
import dev.satyrn.lepidoptera.util.ModHelper;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

@Api
public abstract class ModItemTagsProvider extends net.minecraft.data.tags.ItemTagsProvider {
    protected final ModMeta metadata;

    protected ModItemTagsProvider(Class<?> modClass, DataGenerator generator,
            BlockTagsProvider blockTagsProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, blockTagsProvider, ModHelper.modId(modClass), existingFileHelper);
        this.metadata = ModHelper.metadata(modClass);
    }

    @Override
    protected final void addTags() {
        addModTags();
    }

    protected abstract void addModTags();

    @Override
    public String getName() {
        return "Item tag provider for " + ModHelper.friendlyName(this.metadata);
    }
}
