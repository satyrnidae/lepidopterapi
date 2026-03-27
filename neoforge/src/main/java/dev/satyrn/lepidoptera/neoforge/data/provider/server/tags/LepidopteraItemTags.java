package dev.satyrn.lepidoptera.neoforge.data.provider.server.tags;

import dev.satyrn.lepidoptera.LepidopteraAPI;
import dev.satyrn.lepidoptera.api.item.ApiItemTags;
import dev.satyrn.lepidoptera.item.LepidopteraItems;
import dev.satyrn.lepidoptera.neoforge.api.provider.server.tags.item.ModOnlyItemTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
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
        this.tag(ApiItemTags.FEET_EQUIPMENT);
        this.tag(ApiItemTags.LEGS_EQUIPMENT);
        this.tag(ApiItemTags.CHEST_EQUIPMENT);
        this.tag(ApiItemTags.HEAD_EQUIPMENT);
        this.tag(ApiItemTags.BODY_EQUIPMENT);
        this.tag(ApiItemTags.FEET_EQUIPMENT_SHIFTABLE);
        this.tag(ApiItemTags.LEGS_EQUIPMENT_SHIFTABLE);
        this.tag(ApiItemTags.CHEST_EQUIPMENT_SHIFTABLE);
        this.tag(ApiItemTags.HEAD_EQUIPMENT_SHIFTABLE)
                .add(LepidopteraItems.DEPLETED_ALEMBIC.get())
                .add(LepidopteraItems.ALCHEMICAL_ALEMBIC.get());
    }
}
