package dev.satyrn.lepidoptera.neoforge.data.provider.server.tags;

import dev.satyrn.lepidoptera.LepidopteraAPI;
import dev.satyrn.lepidoptera.api.ModHelper;
import dev.satyrn.lepidoptera.api.item.ApiItemTags;
import dev.satyrn.lepidoptera.item.LepidopteraItems;
import dev.satyrn.lepidoptera.neoforge.api.provider.server.tags.item.ModOnlyItemTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class LepidopteraItemTags extends ModOnlyItemTagsProvider {
    public LepidopteraItemTags(PackOutput arg,
                               CompletableFuture<HolderLookup.Provider> completableFuture,
                               @Nullable ExistingFileHelper existingFileHelper) {
        super(LepidopteraAPI.class, arg, completableFuture, existingFileHelper);
    }

    @Override
    protected void addModTags(final HolderLookup.Provider provider) {
        TagKey<Item> alembics = TagKey.create(Registries.ITEM, ModHelper.resource(LepidopteraAPI.class, "alembics"));
        TagKey<Item> accessoriesHat = TagKey.create(Registries.ITEM,
                ResourceLocation.fromNamespaceAndPath("accessories", "hat"));

        this.tag(alembics)
                .add(LepidopteraItems.DEPLETED_ALEMBIC.get())
                .add(LepidopteraItems.ALCHEMICAL_ALEMBIC.get());
        this.tag(accessoriesHat).addTag(alembics);

        this.tag(ApiItemTags.FEET_EQUIPMENT);
        this.tag(ApiItemTags.LEGS_EQUIPMENT);
        this.tag(ApiItemTags.CHEST_EQUIPMENT);
        this.tag(ApiItemTags.HEAD_EQUIPMENT);
        this.tag(ApiItemTags.BODY_EQUIPMENT);
        this.tag(ApiItemTags.FEET_EQUIPMENT_SHIFTABLE);
        this.tag(ApiItemTags.LEGS_EQUIPMENT_SHIFTABLE);
        this.tag(ApiItemTags.CHEST_EQUIPMENT_SHIFTABLE);
        this.tag(ApiItemTags.HEAD_EQUIPMENT_SHIFTABLE).addTag(alembics);
    }
}
