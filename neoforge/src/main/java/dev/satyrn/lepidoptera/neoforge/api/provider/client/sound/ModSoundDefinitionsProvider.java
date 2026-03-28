package dev.satyrn.lepidoptera.neoforge.api.provider.client.sound;

import dev.satyrn.lepidoptera.api.ModHelper;
import dev.satyrn.lepidoptera.api.ModMeta;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.CompletableFuture;

/**
 * Abstract base for mod-specific NeoForge sound definitions data providers.
 *
 * <p>Subclass this and implement {@link #registerSounds()} to add sound definitions for
 * your mod. Wire the provider into your {@code GatherDataEvent} listener.</p>
 *
 * <p>Override {@link #runModded} to perform additional data-gen work that should run
 * alongside the standard sound definitions output.</p>
 *
 * @since 1.0.0-SNAPSHOT.1+1.21.1
 */
@ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
@SuppressWarnings("unused")
public abstract class ModSoundDefinitionsProvider extends SoundDefinitionsProvider {

    /**
     * The mod metadata resolved from the mod class passed to the constructor.
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    protected final ModMeta metadata;

    /**
     * Creates a new sound definitions provider for the given mod class.
     *
     * @param modClass the mod's main class, annotated with {@link dev.satyrn.lepidoptera.api.ModMeta}
     * @param output   the data-gen pack output
     * @param helper   helper used to validate references to existing resource files
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public ModSoundDefinitionsProvider(Class<?> modClass, PackOutput output, ExistingFileHelper helper) {
        super(output, ModHelper.modId(modClass), helper);
        this.metadata = ModHelper.metadata(modClass);
    }

    /**
     * Runs both the standard sound definitions output and {@link #runModded}.
     *
     * @param output the data-gen output cache
     *
     * @return a future that completes when all outputs have been written
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @Override
    public final CompletableFuture<?> run(CachedOutput output) {
        return CompletableFuture.allOf(super.run(output), runModded(output));
    }

    /**
     * Override to perform additional data-gen work alongside the standard sound
     * definitions output. Runs concurrently with the parent {@code run} call.
     *
     * <p>Defaults to a no-op.</p>
     *
     * @param output the data-gen output cache
     *
     * @return a future that completes when all additional outputs have been written
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    protected CompletableFuture<?> runModded(CachedOutput output) {
        // Defaults to no-op.
        return CompletableFuture.runAsync(() -> {
        });
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @Override
    public String getName() {
        return "Sound Definitions Provider for " + ModHelper.friendlyName(this.metadata);
    }
}
