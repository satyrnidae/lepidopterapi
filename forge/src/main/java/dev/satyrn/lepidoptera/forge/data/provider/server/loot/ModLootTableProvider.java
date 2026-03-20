package dev.satyrn.lepidoptera.forge.data.provider.server.loot;

import com.mojang.datafixers.util.Pair;
import dev.satyrn.lepidoptera.annotations.Api;
import dev.satyrn.lepidoptera.annotations.ModMeta;
import dev.satyrn.lepidoptera.util.ModHelper;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Api
public abstract class ModLootTableProvider extends LootTableProvider {
    protected final ModMeta metadata;
    protected final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> lootTableSuppliers;

    public ModLootTableProvider(Class<?> modClass, DataGenerator generator) {
        super(generator);
        this.metadata = ModHelper.metadata(modClass);
        this.lootTableSuppliers = this.getLootTableSuppliers();
    }

    protected abstract List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getLootTableSuppliers();

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
        return this.lootTableSuppliers;
    }

    /**
     * Override this method in your class if you want to validate your loot tables.
     */
    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationTracker) {
        // Do nothing
    }

    @Override
    public String getName() {
        return "Loot table provider for " + ModHelper.friendlyName(this.metadata);
    }
}
