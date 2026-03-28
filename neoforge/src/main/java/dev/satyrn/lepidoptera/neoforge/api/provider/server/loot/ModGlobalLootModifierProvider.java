package dev.satyrn.lepidoptera.neoforge.api.provider.server.loot;

import dev.satyrn.lepidoptera.api.ModHelper;
import dev.satyrn.lepidoptera.api.ModMeta;
import dev.satyrn.lepidoptera.api.WithLocation;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.CompletableFuture;

/**
 * Abstract base for mod-specific NeoForge global loot modifier data providers.
 *
 * <p>Subclass this and implement {@link #start()} to register global loot modifiers for
 * your mod. Wire the provider into your {@code GatherDataEvent} listener.</p>
 *
 * @since 1.0.0-SNAPSHOT.1+1.21.1
 */
@ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
@SuppressWarnings("unused")
public abstract class ModGlobalLootModifierProvider extends GlobalLootModifierProvider implements WithLocation {

    /**
     * The mod metadata resolved from the mod class passed to the constructor.
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    protected final ModMeta metadata;

    /**
     * Creates a new global loot modifier provider for the given mod class.
     *
     * @param modClass       the mod's main class, annotated with {@link dev.satyrn.lepidoptera.api.ModMeta}
     * @param output         the data-gen pack output
     * @param lookupProvider a future providing the registry lookup context
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public ModGlobalLootModifierProvider(Class<?> modClass,
                                         PackOutput output,
                                         CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, ModHelper.modId(modClass));
        this.metadata = ModHelper.metadata(modClass);
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
        return ModHelper.resource(this.metadata, "providers/global_loot_modifier");
    }
}
