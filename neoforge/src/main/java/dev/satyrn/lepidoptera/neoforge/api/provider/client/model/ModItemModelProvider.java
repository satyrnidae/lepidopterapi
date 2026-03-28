package dev.satyrn.lepidoptera.neoforge.api.provider.client.model;

import dev.satyrn.lepidoptera.api.ModHelper;
import dev.satyrn.lepidoptera.api.ModMeta;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Abstract base for mod-specific NeoForge item model data providers.
 *
 * <p>Subclass this and override {@link #registerModels()} to add item model definitions
 * for your mod. Wire the provider into your {@code GatherDataEvent} listener.</p>
 *
 * @since 1.0.0-SNAPSHOT.1+1.21.1
 */
@ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
@SuppressWarnings("unused")
public abstract class ModItemModelProvider extends ItemModelProvider {

    /**
     * The mod metadata resolved from the mod class passed to the constructor.
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    protected final ModMeta metadata;

    /**
     * Creates a new item model provider for the given mod class.
     *
     * @param modClass           the mod's main class, annotated with {@link dev.satyrn.lepidoptera.api.ModMeta}
     * @param output             the data-gen pack output
     * @param existingFileHelper helper used to validate references to existing resource files
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public ModItemModelProvider(Class<?> modClass, PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, ModHelper.modId(modClass), existingFileHelper);
        this.metadata = ModHelper.metadata(modClass);
    }

    /**
     * Convenience method that generates a standard {@code item/handheld} model for the
     * given item, using the item's registry key to derive the texture path
     * ({@code <namespace>:item/<path>}).
     *
     * @param item supplier for the item to generate a model for
     *
     * @return the {@link ItemModelBuilder} for further customization
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @SuppressWarnings("unused")
    protected ItemModelBuilder handheldItem(Supplier<? extends Item> item) {
        final @Nonnull ResourceLocation id = Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item.get()));
        return this.withExistingParent(id.toString(), "item/handheld")
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "item/" + id.getPath()));
    }

    /**
     * Convenience overload of {@link #basicItem(Item)} that accepts a supplier.
     *
     * @param item supplier for the item to generate a model for
     *
     * @return the {@link ItemModelBuilder} for further customization
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @SuppressWarnings("unused")
    protected @Nonnull ItemModelBuilder basicItem(Supplier<? extends Item> item) {
        return this.basicItem(item.get());
    }
}
