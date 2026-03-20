package dev.satyrn.lepidoptera.forge.data.provider.server.tags.item;

import dev.satyrn.lepidoptera.annotations.Api;
import dev.satyrn.lepidoptera.forge.data.provider.server.tags.block.NoOpBlockTagsProvider;
import dev.satyrn.lepidoptera.util.ModHelper;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

@Api
public abstract class ModOnlyItemTagsProvider extends ModItemTagsProvider {
    protected ModOnlyItemTagsProvider(Class<?> modClass, DataGenerator generator,
            @Nullable ExistingFileHelper existingFileHelper) {
        super(modClass, generator,
                new NoOpBlockTagsProvider(generator, ModHelper.modId(modClass), existingFileHelper),
                existingFileHelper);
    }
}
