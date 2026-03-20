package dev.satyrn.lepidoptera.forge.data.provider.client.lang;

import dev.satyrn.lepidoptera.annotations.Api;
import dev.satyrn.lepidoptera.annotations.ModMeta;
import dev.satyrn.lepidoptera.util.ModHelper;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.LanguageProvider;

import java.util.Arrays;
import java.util.function.Supplier;

@Api
public abstract class ModLanguageProvider extends LanguageProvider {
    protected final ModMeta metadata;
    protected final String locale;

    public ModLanguageProvider(Class<?> modClass, DataGenerator gen, String locale) {
        super(gen, ModHelper.modId(modClass), locale);
        this.metadata = ModHelper.metadata(modClass);
        this.locale = locale;
    }

    @Override
    protected void addTranslations() {
        // Does nothing in the base class
    }

    @SuppressWarnings("unused")
    protected void add(final Supplier<? extends Item> key, final String name) {
        this.add(key.get(), name);
    }

    @SuppressWarnings("unused")
    protected void addConfigKey(String key, String name) {
        this.add("text.autoconfig." + this.metadata.value() + "." + key, name);
    }

    @SuppressWarnings("unused")
    protected void addConfigOption(String key, String name) {
        this.addConfigKey("option." + key, name);
    }

    @SuppressWarnings("unused")
    protected void addNestedConfigOption(String name, String...keys) {
        this.addConfigOption("option." + String.join(",", Arrays.stream(keys).toList()), name);
    }

    @SuppressWarnings("unused")
    protected void addTip(String key, String name) {
        this.add(this.metadata.value() + ".tip." + key, name);
    }

    @SuppressWarnings("unused")
    protected <T extends Enum<T>> void addEnum(final Enum<T> anEnum, String name) {
        this.add(anEnum.name(), name);
    }

    @Override
    public String getName() {
        return this.locale.toUpperCase() + " Language Provider for " + ModHelper.friendlyName(this.metadata);
    }
}
