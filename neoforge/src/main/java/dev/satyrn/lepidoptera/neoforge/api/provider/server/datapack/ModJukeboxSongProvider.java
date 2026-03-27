package dev.satyrn.lepidoptera.neoforge.api.provider.server.datapack;

import dev.satyrn.lepidoptera.api.ModHelper;
import dev.satyrn.lepidoptera.api.ModMeta;
import dev.satyrn.lepidoptera.api.WithLocation;
import org.jetbrains.annotations.ApiStatus;
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

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
public abstract class ModJukeboxSongProvider extends DatapackBuiltinEntriesProvider implements WithLocation {
    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder();

    private final ModMeta metadata;
    private final PackOutput output;

    private @Nullable BootstrapContext<JukeboxSong> bootstrapContext;
    private @Nullable HolderGetter<SoundEvent> sounds;

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

    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    protected abstract void addSongs();

    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    protected final void register(final ResourceKey<JukeboxSong> key,
                                  final float lengthInSeconds,
                                  final int comparatorOutput) {
        ResourceKey<SoundEvent> soundKey = ResourceKey.create(Registries.SOUND_EVENT, key.location());
        register(key, soundKey, lengthInSeconds, comparatorOutput);
    }

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

    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    protected ModMeta getMetadata() {
        return this.metadata;
    }

    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    protected PackOutput getOutput() {
        return this.output;
    }

    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public @Override String getName() {
        return location().toString();
    }

    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public @Override ResourceLocation location() {
        return ModHelper.resource(this.metadata, "providers/jukebox_song");
    }
}
