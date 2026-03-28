package dev.satyrn.lepidoptera.neoforge.api.provider.client.sound;

import dev.satyrn.lepidoptera.api.ModHelper;
import dev.satyrn.lepidoptera.api.ModMeta;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.CompletableFuture;

@ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
public abstract class ModSoundDefinitionsProvider extends SoundDefinitionsProvider {
    protected final ModMeta metadata;

    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public ModSoundDefinitionsProvider(Class<?> modClass, PackOutput output, ExistingFileHelper helper) {
        super(output, ModHelper.modId(modClass), helper);
        this.metadata = ModHelper.metadata(modClass);
    }

    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @Override
    public final CompletableFuture<?> run(CachedOutput output) {
        return CompletableFuture.allOf(super.run(output), runModded(output));
    }

    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    protected CompletableFuture<?> runModded(CachedOutput output) {
        // Defaults to no-op.
        return CompletableFuture.runAsync(() -> {
        });
    }

    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public @Override String getName() {
        return "Sound Definitions Provider for " + ModHelper.friendlyName(this.metadata);
    }
}
