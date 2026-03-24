package dev.satyrn.lepidoptera.neoforge.api.provider.server.loot;

import dev.satyrn.lepidoptera.api.WithLocation;
import dev.satyrn.lepidoptera.api.annotations.Api;
import dev.satyrn.lepidoptera.api.ModMeta;
import dev.satyrn.lepidoptera.api.ModHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;

import java.util.concurrent.CompletableFuture;

@Api
public abstract class ModGlobalLootModifierProvider
        extends GlobalLootModifierProvider
        implements WithLocation {
    protected final ModMeta metadata;
    public ModGlobalLootModifierProvider(Class<?> modClass, PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, ModHelper.modId(modClass));
        this.metadata = ModHelper.metadata(modClass);
    }

    @Override
    public String getName() {
        return location().toString();
    }

    @Override
    public ResourceLocation location() {
        return ModHelper.resource(this.metadata, "providers/global_loot_modifier");
    }
}
