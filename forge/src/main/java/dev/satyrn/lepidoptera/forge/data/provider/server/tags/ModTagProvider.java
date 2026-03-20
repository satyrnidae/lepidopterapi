package dev.satyrn.lepidoptera.forge.data.provider.server.tags;

import dev.satyrn.lepidoptera.annotations.Api;
import dev.satyrn.lepidoptera.annotations.ModMeta;
import dev.satyrn.lepidoptera.util.ModHelper;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

@Api
public abstract class ModTagProvider<T> extends net.minecraft.data.tags.TagsProvider<T> {
    protected final ModMeta metadata;

    protected ModTagProvider(Class<?> modClass, DataGenerator generator, Registry<T> registry,
            @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, registry, ModHelper.modId(modClass), existingFileHelper);
        this.metadata = ModHelper.metadata(modClass);
    }

    @Override
    protected final void addTags() {
        addModTags();
    }

    protected abstract void addModTags();

    @Override
    public String getName() {
        return "Tag provider for " + ModHelper.friendlyName(this.metadata);
    }
}
