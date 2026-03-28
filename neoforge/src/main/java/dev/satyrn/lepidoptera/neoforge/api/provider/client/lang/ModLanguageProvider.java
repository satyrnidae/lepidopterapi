package dev.satyrn.lepidoptera.neoforge.api.provider.client.lang;

import dev.satyrn.lepidoptera.api.ModHelper;
import dev.satyrn.lepidoptera.api.ModMeta;
import dev.satyrn.lepidoptera.api.WithLocation;
import dev.satyrn.lepidoptera.api.lang.FormattedStringBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.jetbrains.annotations.ApiStatus;

/**
 * Abstract base for mod-specific NeoForge language (translation) data providers.
 *
 * <p>Subclass this and implement {@link #addTranslations()} to add translation entries for your mod.
 * Wire the provider into your {@code GatherDataEvent} listener.</p>
 *
 * @since 1.0.0-SNAPSHOT.1+1.21.1
 */
@ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
public abstract class ModLanguageProvider extends LanguageProvider implements WithLocation {

    /**
     * The mod metadata resolved from the mod class passed to the constructor.
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    protected final ModMeta metadata;

    /**
     * The locale code this provider targets (e.g. {@code "en_us"}, {@code "fr_fr"}).
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    protected final String locale;

    /**
     * Creates a new language provider for the given mod class and locale.
     *
     * @param modClass the mod's main class, annotated with {@link dev.satyrn.lepidoptera.api.ModMeta}
     * @param output   the data-gen pack output
     * @param locale   the locale code to generate translations for (e.g. {@code "en_us"})
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public ModLanguageProvider(Class<?> modClass, PackOutput output, String locale) {
        super(output, ModHelper.modId(modClass), locale);
        this.metadata = ModHelper.metadata(modClass);
        this.locale = locale;
    }

    /**
     * Override to register translation entries via the inherited {@code add} methods.
     * The base implementation does nothing.
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @Override
    protected void addTranslations() {
        // Does nothing in the base class
    }

    /**
     * Convenience overload that calls {@link #add(String, String)} with the string value
     * of the given {@link StringBuilder}.
     *
     * @param key     the translation key
     * @param builder the builder whose {@code toString()} value is the translation
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    protected void add(String key, final StringBuilder builder) {
        this.add(key, builder.toString());
    }

    /**
     * Convenience overload that calls {@link #add(String, String)} with the built value
     * of the given {@link FormattedStringBuilder}.
     *
     * @param key     the translation key
     * @param builder the builder whose {@link FormattedStringBuilder#build()} value is the translation
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    protected void add(String key, final FormattedStringBuilder builder) {
        this.add(key, builder.toString());
    }

    /**
     * Adds a translation keyed by the enum constant's {@link Enum#name()}.
     *
     * @param anEnum the enum constant whose name is used as the key
     * @param name   the translation value
     * @param <T>    the enum type
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    protected <T extends Enum<T>> void add(final Enum<T> anEnum, String name) {
        this.add(anEnum.name(), name);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @Override
    public String getName() {
        return location().toString();
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @Override
    public ResourceLocation location() {
        return ModHelper.resource(this.metadata, "providers/lang/" + this.locale);
    }
}
