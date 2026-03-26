package dev.satyrn.lepidoptera.neoforge.api.provider.client.sound;

import dev.satyrn.lepidoptera.api.ModHelper;
import dev.satyrn.lepidoptera.api.ModMeta;
import dev.satyrn.lepidoptera.api.annotations.Api;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

import java.util.concurrent.CompletableFuture;

@Api("1.0.0-SNAPSHOT.1+1.21.1")
public abstract class ModSoundDefinitionsProvider extends SoundDefinitionsProvider {
    protected final ModMeta metadata;

    @Api("1.0.0-SNAPSHOT.1+1.21.1")
    public ModSoundDefinitionsProvider(Class<?> modClass, PackOutput output, ExistingFileHelper helper) {
        super(output, ModHelper.modId(modClass), helper);
        this.metadata = ModHelper.metadata(modClass);
    }

    @Api("1.0.0-SNAPSHOT.1+1.21.1")
    public @Override final CompletableFuture<?> run(CachedOutput output) {
        return CompletableFuture.allOf(super.run(output), runModded(output));
    }

    @Api("1.0.0-SNAPSHOT.1+1.21.1")
    protected CompletableFuture<?> runModded(CachedOutput output) {
        // Defaults to no-op.
        return CompletableFuture.runAsync(() -> {
        });
    }

    @Api("1.0.0-SNAPSHOT.1+1.21.1")
    public @Override String getName() {
        return "Sound Definitions Provider for " + ModHelper.friendlyName(this.metadata);
    }
}
