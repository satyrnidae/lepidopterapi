package dev.satyrn.lepidoptera.neoforge.api.provider.server.datapack;

import dev.satyrn.lepidoptera.api.ModHelper;
import dev.satyrn.lepidoptera.api.ModMeta;
import dev.satyrn.lepidoptera.api.WithLocation;
import dev.satyrn.lepidoptera.api.lang.T9n;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.JukeboxSong;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Abstract base for mod-specific NeoForge jukebox song data providers.
 *
 * <p>Subclass this and implement {@link #addSongs()} to register jukebox songs for your
 * mod. Each song is registered via {@link #register(ResourceKey, float, int)} or
 * {@link #register(ResourceKey, ResourceKey, float, int)}.
 * Wire the provider into your {@code GatherDataEvent} listener.</p>
 *
 * @since 1.0.0-SNAPSHOT.1+1.21.1
 */
@ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
@SuppressWarnings("unused")
public abstract class ModJukeboxSongProvider extends DatapackBuiltinEntriesProvider implements WithLocation {
    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder();

    private final ModMeta metadata;
    private final PackOutput output;

    private @Nullable BootstrapContext<JukeboxSong> bootstrapContext;
    private @Nullable HolderGetter<SoundEvent> sounds;

    /**
     * Creates a new jukebox song provider for the given mod class.
     *
     * @param modClass the mod's main class, annotated with {@link dev.satyrn.lepidoptera.api.ModMeta}
     * @param output   the data-gen pack output
     * @param provider a future providing the registry lookup context
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public ModJukeboxSongProvider(Class<?> modClass,
                                  PackOutput output,
                                  CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, BUILDER, Set.of(ModHelper.modId(modClass)));
        this.metadata = ModHelper.metadata(modClass);
        this.output = output;
        BUILDER.add(Registries.JUKEBOX_SONG, this::bootstrap);
    }

    private void bootstrap(BootstrapContext<JukeboxSong> ctx) {
        this.bootstrapContext = ctx;
        this.sounds = ctx.lookup(Registries.SOUND_EVENT);
        this.addSongs();
    }

    /**
     * Implement to register jukebox songs via {@link #register}.
     * Called during the bootstrap phase of data generation.
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    protected abstract void addSongs();

    /**
     * Registers a jukebox song whose sound event shares the same
     * {@link ResourceLocation} as the song key.
     *
     * @param key              the registry key for the song
     * @param lengthInSeconds  the duration of the song in seconds
     * @param comparatorOutput the redstone comparator output level (0–15) when this disc
     *                         is playing
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    protected final void register(final ResourceKey<JukeboxSong> key,
                                  final float lengthInSeconds,
                                  final int comparatorOutput) {
        ResourceKey<SoundEvent> soundKey = ResourceKey.create(Registries.SOUND_EVENT, key.location());
        register(key, soundKey, lengthInSeconds, comparatorOutput);
    }

    /**
     * Registers a jukebox song with an explicit sound event key distinct from the song key.
     *
     * <p>The song's description component is derived from {@link T9n#itemDesc(ResourceKey)}
     * applied to {@code songKey}.</p>
     *
     * @param songKey          the registry key for the song
     * @param soundKey         the registry key for the sound event to play
     * @param lengthInSeconds  the duration of the song in seconds
     * @param comparatorOutput the redstone comparator output level (0–15) when this disc
     *                         is playing
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    protected final void register(final ResourceKey<JukeboxSong> songKey,
                                  final ResourceKey<SoundEvent> soundKey,
                                  final float lengthInSeconds,
                                  final int comparatorOutput) {
        Objects.requireNonNull(this.bootstrapContext);
        Objects.requireNonNull(this.sounds);
        Holder.Reference<SoundEvent> sound = this.sounds.getOrThrow(soundKey);
        this.bootstrapContext.register(songKey,
                new JukeboxSong(sound, Component.translatable(T9n.itemDesc(songKey)), lengthInSeconds,
                        comparatorOutput));
    }

    /**
     * Returns the mod metadata for this provider.
     *
     * @return the {@link ModMeta} resolved from the constructor's mod class
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    protected ModMeta getMetadata() {
        return this.metadata;
    }

    /**
     * Returns the {@link PackOutput} for this provider.
     *
     * @return the pack output passed to the constructor
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    protected PackOutput getOutput() {
        return this.output;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @Override
    public String getName() {
        return location().toString();
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @Override
    public ResourceLocation location() {
        return ModHelper.resource(this.metadata, "providers/jukebox_song");
    }
}
