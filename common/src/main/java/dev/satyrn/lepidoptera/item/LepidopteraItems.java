package dev.satyrn.lepidoptera.item;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.satyrn.lepidoptera.LepidopteraAPI;
import dev.satyrn.lepidoptera.api.item.ItemExtensions;
import dev.satyrn.lepidoptera.api.item.Repairable;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

/**
 * Holds references to all items registered by Lepidoptera API.
 * Call {@link #register()} on Fabric/Quilt, or register {@link #ITEMS} directly on NeoForge.
 */
public final class LepidopteraItems {
    public static final RegistrySupplier<Item> ALCHEMICAL_ALEMBIC;
    public static final RegistrySupplier<Item> DEPLETED_ALEMBIC;

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(LepidopteraAPI.MOD_ID, Registries.ITEM);

    static {
        DEPLETED_ALEMBIC = ITEMS.register("depleted_alembic", () -> new Item(new Item.Properties().stacksTo(16)));
        ALCHEMICAL_ALEMBIC = ITEMS.register("alchemical_alembic", () -> new AlchemicalAlembicItem(
                new Item.Properties().durability(5).rarity(Rarity.RARE)).setRemainsInCraftingTable()
                .setDamageOnFuelUse(2)
                .setFuelDepletionRemainingItem(DEPLETED_ALEMBIC.get())
                .setCraftingDepletionRemainingItem(DEPLETED_ALEMBIC.get())
                .asItem());
    }

    private LepidopteraItems() {
    }

    public static void register() {
        ITEMS.register();
    }

    static final class AlchemicalAlembicItem extends Item implements Repairable, ItemExtensions {
        AlchemicalAlembicItem(Properties props) {
            super(props);
        }

        @Override
        public boolean preventRepair() {
            return true;
        }
    }
}
