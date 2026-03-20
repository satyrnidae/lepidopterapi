package dev.satyrn.lepidoptera.forge.data.provider.client.sound;

import dev.satyrn.lepidoptera.annotations.Api;
import dev.satyrn.lepidoptera.annotations.ModMeta;
import dev.satyrn.lepidoptera.util.ModHelper;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SoundDefinitionsProvider;

@Api
public abstract class ModSoundDefinitionsProvider extends SoundDefinitionsProvider {
    protected final ModMeta metadata;

    public ModSoundDefinitionsProvider(Class<?> modClass, DataGenerator generator, ExistingFileHelper helper) {
        super(generator, ModHelper.modId(modClass), helper);
        this.metadata = ModHelper.metadata(modClass);
    }

    @Override
    public String getName() {
        return "Sound Definitions Provider for " + ModHelper.friendlyName(this.metadata);
    }
}
