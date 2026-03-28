package dev.satyrn.lepidoptera.api.config.serializers;

import com.google.common.collect.Maps;
import dev.satyrn.lepidoptera.LepidopteraAPI;
import dev.satyrn.lepidoptera.api.accessors.autoconfig.YamlConfigSerializerAccessor;
import dev.satyrn.lepidoptera.config.serializers.YamlCommentInjector;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.YamlConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.DumperOptions;
import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.Yaml;
import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.constructor.Constructor;
import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.representer.Representer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;

/**
 * A {@link YamlConfigSerializer} that injects human-readable comments derived from
 * {@link YamlComment} annotations into the
 * serialized YAML output.
 *
 * <p>Comments are built by {@link YamlCommentInjector} and spliced into the YAML string
 * before it is written to disk. During deserialization, YAML directive lines (e.g.
 * {@code !!java.lang.Object}) are stripped so they do not confuse SnakeYAML's parser.</p>
 *
 * <p>The config file path is determined by the {@code name} attribute of the
 * {@link me.shedaniel.autoconfig.annotation.Config @Config} annotation on the config class:
 * {@code <configFolder>/<name>.yaml}. Use {@code name = "subdir/filename"} to nest the file
 * in a subdirectory.</p>
 *
 * @param <T> the config data type
 *
 * @since 0.4.0+1.19.2
 */
@ApiStatus.AvailableSince("0.4.0+1.19.2")
public final class CommentedYamlConfigSerializer<T extends ConfigData> extends YamlConfigSerializer<T> {

    /**
     * The default maximum line length for word-wrapped comments (120 characters).
     *
     * @since 0.4.0+1.19.2
     */
    @ApiStatus.AvailableSince("0.4.0+1.19.2")
    public static final int DEFAULT_LINE_LENGTH = YamlCommentInjector.DEFAULT_LINE_LENGTH;

    /**
     * The minimum allowable line length for word-wrapped comments (60 characters).
     *
     * @since 0.4.0+1.19.2
     */
    @ApiStatus.AvailableSince("0.4.0+1.19.2")
    @ApiStatus.Obsolete
    @Deprecated(since = "1.0.0-SNAPSHOT.1+1.21.1")
    @SuppressWarnings("unused") // Public API member
    public static final int MIN_LINE_LENGTH = YamlCommentInjector.MIN_LINE_LENGTH;

    private static final String YAML_DIRECTIVE_PREFIX = "!!";
    private static final int INDENT = 4;

    private final YamlConfigSerializerAccessor accessor = (YamlConfigSerializerAccessor) (Object) this;
    private final YamlCommentInjector injector;

    /**
     * Creates a serializer with a custom comment line length.
     *
     * <p>The config file is written to {@code <configFolder>/<name>.yaml} where {@code name}
     * comes from the {@link Config @Config} annotation on the config class.</p>
     *
     * @param definition  the Cloth Config {@code @Config} definition for this config
     * @param configClass the config data class
     * @param lineLength  the maximum comment line length; clamped to at least {@link #MIN_LINE_LENGTH}
     *
     * @since 0.4.0+1.19.2
     */
    @ApiStatus.AvailableSince("0.4.0+1.19.2")
    public CommentedYamlConfigSerializer(final Config definition, final Class<T> configClass, final int lineLength) {
        super(definition, configClass, getYaml(configClass));
        this.injector = new YamlCommentInjector(lineLength);
    }

    /**
     * Creates a serializer with {@link #DEFAULT_LINE_LENGTH}.
     *
     * @param definition  the Cloth Config {@code @Config} definition for this config
     * @param configClass the config data class
     *
     * @since 0.4.0+1.19.2
     */
    @ApiStatus.AvailableSince("0.4.0+1.19.2")
    public CommentedYamlConfigSerializer(final Config definition, final Class<T> configClass) {
        this(definition, configClass, DEFAULT_LINE_LENGTH);
    }

    @Contract("_ -> new")
    private static <T> Yaml getYaml(final Class<T> configClass) {
        final DumperOptions dumperOptions = getDumperOptions();
        final Representer representer = new Representer(dumperOptions);
        representer.getPropertyUtils().setSkipMissingProperties(true);
        final Constructor constructor = new Constructor(configClass);
        constructor.setPropertyUtils(representer.getPropertyUtils());

        return new Yaml(constructor, representer, dumperOptions);
    }

    @Contract("-> new")
    private static DumperOptions getDumperOptions() {
        final DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setPrettyFlow(true);
        dumperOptions.setCanonical(false);
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dumperOptions.setNonPrintableStyle(DumperOptions.NonPrintableStyle.ESCAPE);
        dumperOptions.setAllowReadOnlyProperties(false);
        dumperOptions.setIndent(INDENT);
        dumperOptions.setTags(Maps.newHashMap());
        dumperOptions.setExplicitStart(false);
        dumperOptions.setExplicitEnd(false);
        return dumperOptions;
    }

    /**
     * Serializes {@code config} to YAML, injects comments, and writes the result to disk.
     * Logs an error (without throwing) if the write fails.
     *
     * @param config the config object to serialize
     *
     * @since 0.4.0+1.19.2
     */
    @ApiStatus.AvailableSince("0.4.0+1.19.2")
    @Override
    public void serialize(final T config) {
        final Path configPath = this.accessor.callGetConfigPath();

        try {
            final Map<String, String> commentMap = this.injector.buildNestedCommentMap(config.getClass());
            final String yaml = this.injector.injectComments(this.accessor.getYaml().dump(config), commentMap,
                    config.getClass());
            Files.createDirectories(configPath.getParent());
            Files.writeString(configPath, yaml, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception e) {
            LepidopteraAPI.error("Failed to save mod configuration file " + configPath, e);
        }
    }

    /**
     * Reads and deserializes the config from disk, stripping YAML directive lines before parsing.
     * Returns a default config instance if the file does not exist or cannot be read.
     *
     * @return the deserialized config, or a default instance on failure
     *
     * @since 0.4.0+1.19.2
     */
    @ApiStatus.AvailableSince("0.4.0+1.19.2")
    @Override
    public T deserialize() {
        Path configPath = this.accessor.callGetConfigPath();
        if (Files.exists(configPath)) {
            try {
                List<String> lines = Files.readAllLines(configPath);

                // Strip YAML Directives from config
                for (var i = 0; i < lines.size(); ++i) {
                    if (lines.get(i).trim().startsWith(YAML_DIRECTIVE_PREFIX)) {
                        lines.remove(i--);
                    }
                }

                return this.accessor.getYaml().load(String.join("\n", lines));
            } catch (Exception e) {
                LepidopteraAPI.error("Failed to load mod configuration file " + configPath, e);
            }
        }
        LepidopteraAPI.warn("The mod configuration for the {} file could not be loaded and will be defaulted!",
                this.accessor.getDefinition().name());
        return this.createDefault();
    }
}
