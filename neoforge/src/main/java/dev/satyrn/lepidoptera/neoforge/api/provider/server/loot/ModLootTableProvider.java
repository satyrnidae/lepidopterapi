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

/**
 * Abstract base for mod-specific NeoForge loot table data providers.
 *
 * <p>Subclass this and pass your {@link SubProviderEntry} list to the constructor to
 * generate loot tables for your mod. Wire the provider into your
 * {@code GatherDataEvent} listener.</p>
 *
 * @since 0.4.0+1.19.2
 */
@ApiStatus.AvailableSince("0.4.0+1.19.2")
@SuppressWarnings("unused")
public abstract class ModLootTableProvider extends LootTableProvider implements WithLocation {

    /**
     * The mod metadata resolved from the mod class passed to the constructor.
     *
     * @since 0.4.0+1.19.2
     */
    protected final ModMeta metadata;

    /**
     * Creates a new loot table provider for the given mod class.
     *
     * @param modClass        the mod's main class, annotated with {@link dev.satyrn.lepidoptera.api.ModMeta}
     * @param output          the data-gen pack output
     * @param lookupProvider  a future providing the registry lookup context
     * @param subProviders    the list of sub-providers that generate individual loot table sets
     *
     * @since 0.4.0+1.19.2
     */
    @ApiStatus.AvailableSince("0.4.0+1.19.2")
    public ModLootTableProvider(Class<?> modClass,
                                PackOutput output,
                                CompletableFuture<HolderLookup.Provider> lookupProvider,
                                List<SubProviderEntry> subProviders) {
        super(output, Set.of(), subProviders, lookupProvider);
        this.metadata = ModHelper.metadata(modClass);
    }

    /**
     * {@inheritDoc}
     *
     * @since 0.4.0+1.19.2
     */
    @ApiStatus.AvailableSince("0.4.0+1.19.2")
    @Override
    public ResourceLocation location() {
        return ModHelper.resource(metadata, "providers/loot_table");
    }
}
