package dev.satyrn.lepidoptera.neoforge.data.provider.client.sound;

import dev.satyrn.lepidoptera.annotations.Api;
import dev.satyrn.lepidoptera.annotations.ModMeta;
import dev.satyrn.lepidoptera.util.ModHelper;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

@Api
public abstract class ModSoundDefinitionsProvider extends SoundDefinitionsProvider {
    protected final ModMeta metadata;

    public ModSoundDefinitionsProvider(Class<?> modClass, PackOutput output, ExistingFileHelper helper) {
        super(output, ModHelper.modId(modClass), helper);
        this.metadata = ModHelper.metadata(modClass);
    }

    @Override
    public String getName() {
        return "Sound Definitions Provider for " + ModHelper.friendlyName(this.metadata);
    }
}
