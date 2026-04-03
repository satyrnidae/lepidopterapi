package dev.satyrn.lepidoptera.neoforge.api.provider.client.lang;

import dev.satyrn.lepidoptera.api.ModHelper;
import dev.satyrn.lepidoptera.api.ModMeta;
import dev.satyrn.lepidoptera.api.WithLocation;
import dev.satyrn.lepidoptera.api.lang.FormattedStringBuilder;
import dev.satyrn.lepidoptera.api.lang.T9n;
import me.shedaniel.autoconfig.annotation.Config;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

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

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void add(Supplier<? extends ItemLike> key, String value) {
        this.add(key.get().asItem(), value);
    }

    /**
     * Convenience overload that calls {@link #add(String, String)} with the string value
     * of the given {@link StringBuilder}.
     *
     * @param key     the translation key
     * @param value the builder whose {@code toString()} value is the translation
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    protected void add(String key, final StringBuilder value) {
        this.add(key, value.toString());
    }

    /**
     * Convenience overload that calls {@link #add(String, String)} with the built value
     * of the given {@link FormattedStringBuilder}.
     *
     * @param key     the translation key
     * @param value the builder whose {@link FormattedStringBuilder#build()} value is the translation
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    protected final void add(String key, final FormattedStringBuilder value) {
        this.add(key, value.toString());
    }

    /**
     * Adds a translation keyed by the enum constant's {@link Enum#name()}.
     *
     * @param key the enum constant whose name is used as the key
     * @param value   the translation value
     * @param <T>    the enum type
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    protected final <T extends Enum<T>> void add(final Enum<T> key, String value) {
        this.add(T9n.enumKey(key), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final <T extends Enum<T>> void add(final Enum<T> key, final StringBuilder value) {
        this.add(T9n.enumKey(key), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final <T extends Enum<T>> void add(final Enum<T> key, final FormattedStringBuilder value) {
        this.add(T9n.enumKey(key), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigTitle(final String configName, final String value) {
        this.add(T9n.configTitle(configName), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigTitle(final Config config, final String value) {
        this.add(T9n.configTitle(config), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigTitle(final Class<?> config, final String value) {
        this.add(T9n.configTitle(config), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigTitle(final String configName, final StringBuilder value) {
        this.add(T9n.configTitle(configName), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigTitle(final Config config, final StringBuilder value) {
        this.add(T9n.configTitle(config), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigTitle(final Class<?> config, final StringBuilder value) {
        this.add(T9n.configTitle(config), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigTitle(final String configName, final FormattedStringBuilder value) {
        this.add(T9n.configTitle(configName), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigTitle(final Config config, final FormattedStringBuilder value) {
        this.add(T9n.configTitle(config), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigTitle(final Class<?> config, final FormattedStringBuilder value) {
        this.add(T9n.configTitle(config), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigKey(final String configName, final String key, final String value) {
        this.add(T9n.configKey(configName, key), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigKey(final Config config, final String key, final String value) {
        this.add(T9n.configKey(config, key), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigKey(final Class<?> config, final String key, final String value) {
        this.add(T9n.configKey(config, key), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigKey(final String configName, final String key, final StringBuilder value) {
        this.add(T9n.configKey(configName, key), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigKey(final Config config, final String key, final StringBuilder value) {
        this.add(T9n.configKey(config, key), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigKey(final Class<?> config, final String key, final StringBuilder value) {
        this.add(T9n.configKey(config, key), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigKey(final String configName, final String key, final FormattedStringBuilder value) {
        this.add(T9n.configKey(configName, key), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigKey(final Config config, final String key, final FormattedStringBuilder value) {
        this.add(T9n.configKey(config, key), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigKey(final Class<?> config, final String key, final FormattedStringBuilder value) {
        this.add(T9n.configKey(config, key), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigKey(final String configName, final String parent, final String key, final String value) {
        this.add(T9n.configKey(configName, parent, key), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigKey(final Config config, final String parent, final String key, final String value) {
        this.add(T9n.configKey(config, parent, key), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigKey(final Class<?> config, final String parent, final String key, final String value) {
        this.add(T9n.configKey(config, parent, key), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigKey(final String configName, final String parent, final String key, final StringBuilder value) {
        this.add(T9n.configKey(configName, parent, key), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigKey(final Config config, final String parent, final String key, final StringBuilder value) {
        this.add(T9n.configKey(config, parent, key), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigKey(final Class<?> config, final String parent, final String key, final StringBuilder value) {
        this.add(T9n.configKey(config, parent, key), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigKey(final String configName, final String parent, final String key, final FormattedStringBuilder value) {
        this.add(T9n.configKey(configName, parent, key), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigKey(final Config config, final String parent, final String key, final FormattedStringBuilder value) {
        this.add(T9n.configKey(config, parent, key), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigKey(final Class<?> config, final String parent, final String key, final FormattedStringBuilder value) {
        this.add(T9n.configKey(config, parent, key), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigKey(final String configName, final String[] parents, final String key, final String value) {
        this.add(T9n.configKey(configName, parents, key), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigKey(final Config config, final String[] parents, final String key, final String value) {
        this.add(T9n.configKey(config, parents, key), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigKey(final Class<?> config, final String[] parents, final String key, final String value) {
        this.add(T9n.configKey(config, parents, key), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigKey(final String configName, final String[] parents, final String key, final StringBuilder value) {
        this.add(T9n.configKey(configName, parents, key), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigKey(final Config config, final String[] parents, final String key, final StringBuilder value) {
        this.add(T9n.configKey(config, parents, key), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigKey(final Class<?> config, final String[] parents, final String key, final StringBuilder value) {
        this.add(T9n.configKey(config, parents, key), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigKey(final String configName, final String[] parents, final String key, final FormattedStringBuilder value) {
        this.add(T9n.configKey(configName, parents, key), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigKey(final Config config, final String[] parents, final String key, final FormattedStringBuilder value) {
        this.add(T9n.configKey(config, parents, key), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigKey(final Class<?> config, final String[] parents, final String key, final FormattedStringBuilder value) {
        this.add(T9n.configKey(config, parents, key), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigOption(final String configName, final String option, final String value) {
        this.add(T9n.configOption(configName, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigOption(final Config config, final String option, final String value) {
        this.add(T9n.configOption(config, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigOption(final Class<?> config, final String option, final String value) {
        this.add(T9n.configOption(config, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigOption(final String configName, final String option, final StringBuilder value) {
        this.add(T9n.configOption(configName, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigOption(final Config config, final String option, final StringBuilder value) {
        this.add(T9n.configOption(config, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigOption(final Class<?> config, final String option, final StringBuilder value) {
        this.add(T9n.configOption(config, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigOption(final String configName, final String option, final FormattedStringBuilder value) {
        this.add(T9n.configOption(configName, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigOption(final Config config, final String option, final FormattedStringBuilder value) {
        this.add(T9n.configOption(config, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigOption(final Class<?> config, final String option, final FormattedStringBuilder value) {
        this.add(T9n.configOption(config, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigOption(final String configName, final String parent, final String option, final String value) {
        this.add(T9n.configOption(configName, parent, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigOption(final Config config, final String parent, final String option, final String value) {
        this.add(T9n.configOption(config, parent, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigOption(final Class<?> config, final String parent, final String option, final String value) {
        this.add(T9n.configOption(config, parent, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigOption(final String configName, final String parent, final String option, final StringBuilder value) {
        this.add(T9n.configOption(configName, parent, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigOption(final Config config, final String parent, final String option, final StringBuilder value) {
        this.add(T9n.configOption(config, parent, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigOption(final Class<?> config, final String parent, final String option, final StringBuilder value) {
        this.add(T9n.configOption(config, parent, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigOption(final String configName, final String parent, final String option, final FormattedStringBuilder value) {
        this.add(T9n.configOption(configName, parent, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigOption(final Config config, final String parent, final String option, final FormattedStringBuilder value) {
        this.add(T9n.configOption(config, parent, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigOption(final Class<?> config, final String parent, final String option, final FormattedStringBuilder value) {
        this.add(T9n.configOption(config, parent, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigOption(final String configName, final String[] parents, final String option, final String value) {
        this.add(T9n.configOption(configName, parents, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigOption(final Config config, final String[] parents, final String option, final String value) {
        this.add(T9n.configOption(config, parents, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigOption(final Class<?> config, final String[] parents, final String option, final String value) {
        this.add(T9n.configOption(config, parents, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigOption(final String configName, final String[] parents, final String option, final StringBuilder value) {
        this.add(T9n.configOption(configName, parents, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigOption(final Config config, final String[] parents, final String option, final StringBuilder value) {
        this.add(T9n.configOption(config, parents, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigOption(final Class<?> config, final String[] parents, final String option, final StringBuilder value) {
        this.add(T9n.configOption(config, parents, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigOption(final String configName, final String[] parents, final String option, final FormattedStringBuilder value) {
        this.add(T9n.configOption(configName, parents, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigOption(final Config config, final String[] parents, final String option, final FormattedStringBuilder value) {
        this.add(T9n.configOption(config, parents, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigOption(final Class<?> config, final String[] parents, final String option, final FormattedStringBuilder value) {
        this.add(T9n.configOption(config, parents, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigCategory(final String configName, final String category, final String value) {
        this.add(T9n.configCategory(configName, category), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigCategory(final Config config, final String category, final String value) {
        this.add(T9n.configCategory(config, category), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigCategory(final Class<?> config, final String category, final String value) {
        this.add(T9n.configCategory(config, category), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigCategory(final String configName, final String category, final StringBuilder value) {
        this.add(T9n.configCategory(configName, category), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigCategory(final Config config, final String category, final StringBuilder value) {
        this.add(T9n.configCategory(config, category), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigCategory(final Class<?> config, final String category, final StringBuilder value) {
        this.add(T9n.configCategory(config, category), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigCategory(final String configName, final String category, final FormattedStringBuilder value) {
        this.add(T9n.configCategory(configName, category), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigCategory(final Config config, final String category, final FormattedStringBuilder value) {
        this.add(T9n.configCategory(config, category), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigCategory(final Class<?> config, final String category, final FormattedStringBuilder value) {
        this.add(T9n.configCategory(config, category), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigCategory(final String configName, final String parent, final String category, final String value) {
        this.add(T9n.configCategory(configName, parent, category), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigCategory(final Config config, final String parent, final String category, final String value) {
        this.add(T9n.configCategory(config, parent, category), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigCategory(final Class<?> config, final String parent, final String category, final String value) {
        this.add(T9n.configCategory(config, parent, category), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigCategory(final String configName, final String parent, final String category, final StringBuilder value) {
        this.add(T9n.configCategory(configName, parent, category), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigCategory(final Config config, final String parent, final String category, final StringBuilder value) {
        this.add(T9n.configCategory(config, parent, category), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigCategory(final Class<?> config, final String parent, final String category, final StringBuilder value) {
        this.add(T9n.configCategory(config, parent, category), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigCategory(final String configName, final String parent, final String category, final FormattedStringBuilder value) {
        this.add(T9n.configCategory(configName, parent, category), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigCategory(final Config config, final String parent, final String category, final FormattedStringBuilder value) {
        this.add(T9n.configCategory(config, parent, category), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigCategory(final Class<?> config, final String parent, final String category, final FormattedStringBuilder value) {
        this.add(T9n.configCategory(config, parent, category), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigCategory(final String configName, final String[] parents, final String category, final String value) {
        this.add(T9n.configCategory(configName, parents, category), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigCategory(final Config config, final String[] parents, final String category, final String value) {
        this.add(T9n.configCategory(config, parents, category), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigCategory(final Class<?> config, final String[] parents, final String category, final String value) {
        this.add(T9n.configCategory(config, parents, category), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigCategory(final String configName, final String[] parents, final String category, final StringBuilder value) {
        this.add(T9n.configCategory(configName, parents, category), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigCategory(final Config config, final String[] parents, final String category, final StringBuilder value) {
        this.add(T9n.configCategory(config, parents, category), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigCategory(final Class<?> config, final String[] parents, final String category, final StringBuilder value) {
        this.add(T9n.configCategory(config, parents, category), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigCategory(final String configName, final String[] parents, final String category, final FormattedStringBuilder value) {
        this.add(T9n.configCategory(configName, parents, category), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigCategory(final Config config, final String[] parents, final String category, final FormattedStringBuilder value) {
        this.add(T9n.configCategory(config, parents, category), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigCategory(final Class<?> config, final String[] parents, final String category, final FormattedStringBuilder value) {
        this.add(T9n.configCategory(config, parents, category), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigTooltip(final String configName, final String option, final String value) {
        this.add(T9n.configTooltip(configName, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigTooltip(final Config config, final String option, final String value) {
        this.add(T9n.configTooltip(config, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigTooltip(final Class<?> config, final String option, final String value) {
        this.add(T9n.configTooltip(config, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigTooltip(final String configName, final String option, final String[] value) {
        for (int i = 0; i < value.length; i++) {
            this.add(T9n.configTooltip(configName, option, i), value[i]);
        }
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigTooltip(final Config config, final String option, final String[] value) {
        for (int i = 0; i < value.length; i++) {
            this.add(T9n.configTooltip(config, option, i), value[i]);
        }
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigTooltip(final Class<?> config, final String option, final String[] value) {
        for (int i = 0; i < value.length; i++) {
            this.add(T9n.configTooltip(config, option, i), value[i]);
        }
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigTooltip(final String configName, final String option, final StringBuilder value) {
        this.add(T9n.configTooltip(configName, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigTooltip(final Config config, final String option, final StringBuilder value) {
        this.add(T9n.configTooltip(config, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigTooltip(final Class<?> config, final String option, final StringBuilder value) {
        this.add(T9n.configTooltip(config, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigTooltip(final String configName, final String option, final FormattedStringBuilder value) {
        this.add(T9n.configTooltip(configName, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigTooltip(final Config config, final String option, final FormattedStringBuilder value) {
        this.add(T9n.configTooltip(config, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigTooltip(final Class<?> config, final String option, final FormattedStringBuilder value) {
        this.add(T9n.configTooltip(config, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigTooltip(final String configName, final String parent, final String option, final String value) {
        this.add(T9n.configTooltip(configName, parent, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigTooltip(final Config config, final String parent, final String option, final String value) {
        this.add(T9n.configTooltip(config, parent, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigTooltip(final Class<?> config, final String parent, final String option, final String value) {
        this.add(T9n.configTooltip(config, parent, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigTooltip(final String configName, final String parent, final String option, final String[] value) {
        for (int i = 0; i < value.length; i++) {
            this.add(T9n.configTooltip(configName, parent, option, i), value[i]);
        }
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigTooltip(final Config config, final String parent, final String option, final String[] value) {
        for (int i = 0; i < value.length; i++) {
            this.add(T9n.configTooltip(config, parent, option, i), value[i]);
        }
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigTooltip(final Class<?> config, final String parent, final String option, final String[] value) {
        for (int i = 0; i < value.length; i++) {
            this.add(T9n.configTooltip(config, parent, option, i), value[i]);
        }
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigTooltip(final String configName, final String parent, final String option, final StringBuilder value) {
        this.add(T9n.configTooltip(configName, parent, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigTooltip(final Config config, final String parent, final String option, final StringBuilder value) {
        this.add(T9n.configTooltip(config, parent, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigTooltip(final Class<?> config, final String parent, final String option, final StringBuilder value) {
        this.add(T9n.configTooltip(config, parent, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigTooltip(final String configName, final String parent, final String option, final FormattedStringBuilder value) {
        this.add(T9n.configTooltip(configName, parent, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigTooltip(final Config config, final String parent, final String option, final FormattedStringBuilder value) {
        this.add(T9n.configTooltip(config, parent, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigTooltip(final Class<?> config, final String parent, final String option, final FormattedStringBuilder value) {
        this.add(T9n.configTooltip(config, parent, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigTooltip(final String configName, final String[] parents, final String option, final String value) {
        this.add(T9n.configTooltip(configName, parents, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigTooltip(final Config config, final String[] parents, final String option, final String value) {
        this.add(T9n.configTooltip(config, parents, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigTooltip(final Class<?> config, final String[] parents, final String option, final String value) {
        this.add(T9n.configTooltip(config, parents, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigTooltip(final String configName, final String[] parents, final String option, final String[] value) {
        for (int i = 0; i < value.length; i++) {
            this.add(T9n.configTooltip(configName, parents, option, i), value[i]);
        }
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigTooltip(final Config config, final String[] parents, final String option, final String[] value) {
        for (int i = 0; i < value.length; i++) {
            this.add(T9n.configTooltip(config, parents, option, i), value[i]);
        }
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigTooltip(final Class<?> config, final String[] parents, final String option, final String[] value) {
        for (int i = 0; i < value.length; i++) {
            this.add(T9n.configTooltip(config, parents, option, i), value[i]);
        }
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigTooltip(final String configName, final String[] parents, final String option, final StringBuilder value) {
        this.add(T9n.configTooltip(configName, parents, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigTooltip(final Config config, final String[] parents, final String option, final StringBuilder value) {
        this.add(T9n.configTooltip(config, parents, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigTooltip(final Class<?> config, final String[] parents, final String option, final StringBuilder value) {
        this.add(T9n.configTooltip(config, parents, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigTooltip(final String configName, final String[] parents, final String option, final FormattedStringBuilder value) {
        this.add(T9n.configTooltip(configName, parents, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigTooltip(final Config config, final String[] parents, final String option, final FormattedStringBuilder value) {
        this.add(T9n.configTooltip(config, parents, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addConfigTooltip(final Class<?> config, final String[] parents, final String option, final FormattedStringBuilder value) {
        this.add(T9n.configTooltip(config, parents, option), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addNetMsg(final String key, final String value) {
        this.add(T9n.netMsg(this.metadata, key), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addNetMsg(final String key, final StringBuilder value) {
        this.add(T9n.netMsg(this.metadata, key), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addNetMsg(final String key, final FormattedStringBuilder value) {
        this.add(T9n.netMsg(this.metadata, key), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addTip(final String key, final String value) {
        this.add(T9n.tip(this.metadata, key), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addTip(final String key, final StringBuilder value) {
        this.add(T9n.tip(this.metadata, key), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addTip(final String key, final FormattedStringBuilder value) {
        this.add(T9n.tip(this.metadata, key), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addItemDesc(final ItemLike item, final String value) {
        this.add(T9n.itemDesc(item), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addItemDesc(final ItemLike item, final StringBuilder value) {
        this.add(T9n.itemDesc(item), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addItemDesc(final ItemLike item, final FormattedStringBuilder value) {
        this.add(T9n.itemDesc(item), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addItemDesc(final Supplier<? extends ItemLike> item, final String value) {
        this.add(T9n.itemDesc(item), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addGamerule(final String key, final String value) {
        this.add(T9n.gamerule(key), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addGamerule(final String key, final StringBuilder value) {
        this.add(T9n.gamerule(key), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addGamerule(final String key, final FormattedStringBuilder value) {
        this.add(T9n.gamerule(key), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addGameruleDesc(final String key, final String value) {
        this.add(T9n.gameruleDesc(key), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addGameruleDesc(final String key, final StringBuilder value) {
        this.add(T9n.gameruleDesc(key), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addGameruleDesc(final String key, final FormattedStringBuilder value) {
        this.add(T9n.gameruleDesc(key), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addGui(final String key, final String value) {
        this.add(T9n.gui(this.metadata, key), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addGui(final String key, final StringBuilder value) {
        this.add(T9n.gui(this.metadata, key), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addGui(final String key, final FormattedStringBuilder value) {
        this.add(T9n.gui(this.metadata, key), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addModName(final String value) {
        this.add(T9n.modMenuName(this.metadata), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addModName(final StringBuilder value) {
        this.add(T9n.modMenuName(this.metadata), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addModName(final FormattedStringBuilder value) {
        this.add(T9n.modMenuName(this.metadata), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addModDesc(final String value) {
        this.add(T9n.modMenuDesc(this.metadata), value);
        this.add(T9n.fmlDesc(this.metadata), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addModDesc(final StringBuilder value) {
        this.add(T9n.modMenuDesc(this.metadata), value);
        this.add(T9n.fmlDesc(this.metadata), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addModDesc(final FormattedStringBuilder value) {
        this.add(T9n.modMenuDesc(this.metadata), value);
        this.add(T9n.fmlDesc(this.metadata), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addModSummary(final String value) {
        this.add(T9n.modMenuSummary(this.metadata), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addModSummary(final StringBuilder value) {
        this.add(T9n.modMenuSummary(this.metadata), value);
    }

    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    protected final void addModSummary(final FormattedStringBuilder value) {
        this.add(T9n.modMenuSummary(this.metadata), value);
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
