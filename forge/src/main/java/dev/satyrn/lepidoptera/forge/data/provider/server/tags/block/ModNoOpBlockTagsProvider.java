package dev.satyrn.lepidoptera.forge.data.provider.server.tags.block;

import dev.satyrn.lepidoptera.annotations.Api;
import dev.satyrn.lepidoptera.util.ModHelper;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

@Api
public abstract class ModNoOpBlockTagsProvider extends NoOpBlockTagsProvider {
    protected ModNoOpBlockTagsProvider(Class<?> modClass, DataGenerator generator,
            @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, ModHelper.modId(modClass), existingFileHelper);
    }
}
