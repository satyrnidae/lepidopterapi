package dev.satyrn.lepidoptera.neoforge.data.provider.server.loot;

import dev.satyrn.lepidoptera.annotations.Api;
import dev.satyrn.lepidoptera.annotations.ModMeta;
import dev.satyrn.lepidoptera.util.ModHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Api
public abstract class ModLootTableProvider extends LootTableProvider {
    protected final ModMeta metadata;

    public ModLootTableProvider(Class<?> modClass, PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider,
            List<SubProviderEntry> subProviders) {
        super(output, Set.of(), subProviders, lookupProvider);
        this.metadata = ModHelper.metadata(modClass);
    }
}
