package dev.satyrn.lepidoptera.neoforge.data.provider.server.loot;

import dev.satyrn.lepidoptera.annotations.Api;
import dev.satyrn.lepidoptera.annotations.ModMeta;
import dev.satyrn.lepidoptera.util.ModHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;

import java.util.concurrent.CompletableFuture;

@Api
public abstract class ModGlobalLootModifierProvider extends GlobalLootModifierProvider {
    protected final ModMeta metadata;
    public ModGlobalLootModifierProvider(Class<?> modClass, PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, ModHelper.modId(modClass));
        this.metadata = ModHelper.metadata(modClass);
    }

    @Override
    public String getName() {
        return "Global loot modifier provider for " + ModHelper.friendlyName(this.metadata);
    }
}
