package dev.satyrn.lepidoptera.neoforge.api.provider.server.loot;

import dev.satyrn.lepidoptera.api.ModHelper;
import dev.satyrn.lepidoptera.api.ModMeta;
import dev.satyrn.lepidoptera.api.WithLocation;
import org.jetbrains.annotations.ApiStatus;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;

import java.util.concurrent.CompletableFuture;

@ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
public abstract class ModGlobalLootModifierProvider extends GlobalLootModifierProvider implements WithLocation {
    protected final ModMeta metadata;

    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public ModGlobalLootModifierProvider(Class<?> modClass,
                                         PackOutput output,
                                         CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, ModHelper.modId(modClass));
        this.metadata = ModHelper.metadata(modClass);
    }

    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public @Override String getName() {
        return location().toString();
    }

    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public @Override ResourceLocation location() {
        return ModHelper.resource(this.metadata, "providers/global_loot_modifier");
    }
}
