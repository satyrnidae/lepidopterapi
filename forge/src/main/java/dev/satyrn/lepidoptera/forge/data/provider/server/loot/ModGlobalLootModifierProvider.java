package dev.satyrn.lepidoptera.forge.data.provider.server.loot;

import dev.satyrn.lepidoptera.annotations.Api;
import dev.satyrn.lepidoptera.annotations.ModMeta;
import dev.satyrn.lepidoptera.util.ModHelper;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.GlobalLootModifierProvider;

@Api
public abstract class ModGlobalLootModifierProvider extends GlobalLootModifierProvider {
    protected final ModMeta metadata;
    public ModGlobalLootModifierProvider(Class<?> modClass, DataGenerator gen) {
        super(gen, ModHelper.modId(modClass));
        this.metadata = ModHelper.metadata(modClass);
    }

    @Override
    public String getName() {
        return "Global loot modifier provider for " + ModHelper.friendlyName(this.metadata);
    }
}
