package dev.satyrn.lepidoptera.neoforge.api.provider.client.sound;

import dev.satyrn.lepidoptera.api.annotations.Api;
import dev.satyrn.lepidoptera.api.ModMeta;
import dev.satyrn.lepidoptera.api.ModHelper;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

import java.util.concurrent.CompletableFuture;

@Api
public abstract class ModSoundDefinitionsProvider extends SoundDefinitionsProvider {
    protected final ModMeta metadata;

    public ModSoundDefinitionsProvider(Class<?> modClass, PackOutput output, ExistingFileHelper helper) {
        super(output, ModHelper.modId(modClass), helper);
        this.metadata = ModHelper.metadata(modClass);
    }

    @Override
    public final CompletableFuture<?> run(CachedOutput output) {
        return CompletableFuture.allOf(
                super.run(output),
                runModded(output)
        );
    }

    protected CompletableFuture<?> runModded(CachedOutput output) {
        // Defaults to no-op.
        return CompletableFuture.runAsync(() -> {});
    }

    @Override
    public String getName() {
        return "Sound Definitions Provider for " + ModHelper.friendlyName(this.metadata);
    }
}
