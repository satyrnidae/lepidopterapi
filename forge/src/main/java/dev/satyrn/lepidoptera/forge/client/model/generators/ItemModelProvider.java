package dev.satyrn.lepidoptera.forge.client.model.generators;

import dev.satyrn.lepidoptera.annotations.Api;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Supplier;

@Api
public abstract class ItemModelProvider extends net.minecraftforge.client.model.generators.ItemModelProvider {

    public ItemModelProvider(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
        super(generator, modid, existingFileHelper);
    }

    @SuppressWarnings("unused")
    protected ItemModelBuilder handheldItem(Supplier<? extends Item> item) {
        final @NotNull ResourceLocation id = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item.get()));
        return this.withExistingParent(id.toString(), "item/handheld")
                .texture("layer0", new ResourceLocation(id.getNamespace(), "item/" + id.getPath()));
    }

    @SuppressWarnings("unused")
    protected @NotNull ItemModelBuilder basicItem(Supplier<? extends Item> item) {
        return this.basicItem(item.get());
    }
}
