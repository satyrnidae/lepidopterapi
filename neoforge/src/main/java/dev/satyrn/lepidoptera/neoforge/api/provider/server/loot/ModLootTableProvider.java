package dev.satyrn.lepidoptera.neoforge.api.provider.server.loot;

import dev.satyrn.lepidoptera.api.ModHelper;
import dev.satyrn.lepidoptera.api.ModMeta;
import dev.satyrn.lepidoptera.api.WithLocation;
import dev.satyrn.lepidoptera.api.annotations.Api;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Api
public abstract class ModLootTableProvider extends LootTableProvider implements WithLocation {
    protected final ModMeta metadata;

    public ModLootTableProvider(Class<?> modClass,
                                PackOutput output,
                                CompletableFuture<HolderLookup.Provider> lookupProvider,
                                List<SubProviderEntry> subProviders) {
        super(output, Set.of(), subProviders, lookupProvider);
        this.metadata = ModHelper.metadata(modClass);
    }

    public @Override ResourceLocation location() {
        return ModHelper.resource(metadata, "providers/loot_table");
    }
}
