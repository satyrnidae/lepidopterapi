package dev.satyrn.lepidoptera.neoforge.api.provider.server.tags.entity;

import dev.satyrn.lepidoptera.api.ModHelper;
import dev.satyrn.lepidoptera.api.ModMeta;
import dev.satyrn.lepidoptera.api.WithLocation;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

@ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
public abstract class ModEntityTypeTagsProvider extends EntityTypeTagsProvider implements WithLocation {
    protected final ModMeta metadata;

    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public ModEntityTypeTagsProvider(Class<?> modClass,
                                     PackOutput arg,
                                     CompletableFuture<HolderLookup.Provider> completableFuture,
                                     @Nullable ExistingFileHelper existingFileHelper) {
        super(arg, completableFuture, ModHelper.modId(modClass), existingFileHelper);
        this.metadata = ModHelper.metadata(modClass);
    }

    @Override
    protected final void addTags(HolderLookup.Provider provider) {
        addModTags(provider);
    }

    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    protected abstract void addModTags(HolderLookup.Provider provider);

    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @Override
    public String getName() {
        return location().toString();
    }

    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @Override
    public ResourceLocation location() {
        return ModHelper.resource(this.metadata, "providers/tag/entity_type");
    }
}
