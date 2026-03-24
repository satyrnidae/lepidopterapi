package dev.satyrn.lepidoptera.neoforge.api.provider.client.lang;

import dev.satyrn.lepidoptera.api.annotations.Api;
import dev.satyrn.lepidoptera.api.ModMeta;
import dev.satyrn.lepidoptera.api.ModHelper;
import dev.satyrn.lepidoptera.api.lang.FormattedStringBuilder;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

@Api
public abstract class ModLanguageProvider extends LanguageProvider {
    protected final ModMeta metadata;
    protected final String locale;

    @Api
    public ModLanguageProvider(Class<?> modClass, PackOutput output, String locale) {
        super(output, ModHelper.modId(modClass), locale);
        this.metadata = ModHelper.metadata(modClass);
        this.locale = locale;
    }

    @Api
    @Override
    protected void addTranslations() {
        // Does nothing in the base class
    }

    @Api
    protected void add(String key, final StringBuilder builder) {
        this.add(key, builder.toString());
    }

    @Api
    protected void add(String key, final FormattedStringBuilder builder) {
        this.add(key, builder.toString());
    }

    @Api
    protected <T extends Enum<T>> void add(final Enum<T> anEnum, String name) {
        this.add(anEnum.name(), name);
    }

    @Override
    public String getName() {
        return this.locale.toUpperCase() + " Language Provider for " + ModHelper.friendlyName(this.metadata);
    }
}
