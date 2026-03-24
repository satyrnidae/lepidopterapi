package dev.satyrn.lepidoptera.neoforge.api.provider.client.model;

import dev.satyrn.lepidoptera.api.annotations.Api;
import dev.satyrn.lepidoptera.api.ModMeta;
import dev.satyrn.lepidoptera.api.ModHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Supplier;

@Api
public abstract class ModItemModelProvider extends ItemModelProvider {
    protected final ModMeta metadata;

    public ModItemModelProvider(Class<?> modClass, PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, ModHelper.modId(modClass), existingFileHelper);
        this.metadata = ModHelper.metadata(modClass);
    }

    @SuppressWarnings("unused")
    protected ItemModelBuilder handheldItem(Supplier<? extends Item> item) {
        final @NotNull ResourceLocation id = Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item.get()));
        return this.withExistingParent(id.toString(), "item/handheld")
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "item/" + id.getPath()));
    }

    @SuppressWarnings("unused")
    protected @NotNull ItemModelBuilder basicItem(Supplier<? extends Item> item) {
        return this.basicItem(item.get());
    }
}
