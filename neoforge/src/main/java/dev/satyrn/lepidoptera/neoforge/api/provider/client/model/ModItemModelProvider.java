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

@ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
public abstract class ModItemModelProvider extends ItemModelProvider {
    protected final ModMeta metadata;

    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public ModItemModelProvider(Class<?> modClass, PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, ModHelper.modId(modClass), existingFileHelper);
        this.metadata = ModHelper.metadata(modClass);
    }

    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @SuppressWarnings("unused")
    protected ItemModelBuilder handheldItem(Supplier<? extends Item> item) {
        final @Nonnull ResourceLocation id = Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item.get()));
        return this.withExistingParent(id.toString(), "item/handheld")
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "item/" + id.getPath()));
    }

    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @SuppressWarnings("unused")
    protected @Nonnull ItemModelBuilder basicItem(Supplier<? extends Item> item) {
        return this.basicItem(item.get());
    }
}
