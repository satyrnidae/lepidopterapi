package dev.satyrn.lepidoptera.neoforge.client.model.generators;

import dev.satyrn.lepidoptera.annotations.Api;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Supplier;

@Api
public abstract class ItemModelProvider extends net.neoforged.neoforge.client.model.generators.ItemModelProvider {

    public ItemModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, existingFileHelper);
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
