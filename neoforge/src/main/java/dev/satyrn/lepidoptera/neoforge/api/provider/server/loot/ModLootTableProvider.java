package dev.satyrn.lepidoptera.neoforge.api.provider.server.loot;

import dev.satyrn.lepidoptera.api.ModHelper;
import dev.satyrn.lepidoptera.api.ModMeta;
import dev.satyrn.lepidoptera.api.WithLocation;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@ApiStatus.AvailableSince("0.4.0+1.19.2")
public abstract class ModLootTableProvider extends LootTableProvider implements WithLocation {
    protected final ModMeta metadata;

    public ModLootTableProvider(Class<?> modClass,
                                PackOutput output,
                                CompletableFuture<HolderLookup.Provider> lookupProvider,
                                List<SubProviderEntry> subProviders) {
        super(output, Set.of(), subProviders, lookupProvider);
        this.metadata = ModHelper.metadata(modClass);
    }

    @Override
    public ResourceLocation location() {
        return ModHelper.resource(metadata, "providers/loot_table");
    }
}
