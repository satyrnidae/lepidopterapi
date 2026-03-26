package dev.satyrn.lepidoptera.neoforge.api.provider.client.lang;

import dev.satyrn.lepidoptera.api.ModHelper;
import dev.satyrn.lepidoptera.api.ModMeta;
import dev.satyrn.lepidoptera.api.WithLocation;
import dev.satyrn.lepidoptera.api.annotations.Api;
import dev.satyrn.lepidoptera.api.lang.FormattedStringBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.LanguageProvider;

/**
 * Abstract base for mod-specific NeoForge language (translation) data providers.
 *
 * <p>Subclass this and implement {@link #addTranslations()} to add translation entries for your mod.
 * Wire the provider into your {@code GatherDataEvent} listener.</p>
 *
 * @since 1.0.0-SNAPSHOT.1+1.21.1
 */
@Api("1.0.0-SNAPSHOT.1+1.21.1")
public abstract class ModLanguageProvider extends LanguageProvider implements WithLocation {
    protected final ModMeta metadata;
    protected final String locale;

    @Api("1.0.0-SNAPSHOT.1+1.21.1")
    public ModLanguageProvider(Class<?> modClass, PackOutput output, String locale) {
        super(output, ModHelper.modId(modClass), locale);
        this.metadata = ModHelper.metadata(modClass);
        this.locale = locale;
    }

    @Api("1.0.0-SNAPSHOT.1+1.21.1")
    protected @Override void addTranslations() {
        // Does nothing in the base class
    }

    @Api("1.0.0-SNAPSHOT.1+1.21.1")
    protected void add(String key, final StringBuilder builder) {
        this.add(key, builder.toString());
    }

    @Api("1.0.0-SNAPSHOT.1+1.21.1")
    protected void add(String key, final FormattedStringBuilder builder) {
        this.add(key, builder.toString());
    }

    @Api("1.0.0-SNAPSHOT.1+1.21.1")
    protected <T extends Enum<T>> void add(final Enum<T> anEnum, String name) {
        this.add(anEnum.name(), name);
    }

    @Api("1.0.0-SNAPSHOT.1+1.21.1")
    public @Override String getName() {
        return location().toString();
    }

    @Api("1.0.0-SNAPSHOT.1+1.21.1")
    public @Override ResourceLocation location() {
        return ModHelper.resource(this.metadata, "providers/lang/" + this.locale);
    }
}
