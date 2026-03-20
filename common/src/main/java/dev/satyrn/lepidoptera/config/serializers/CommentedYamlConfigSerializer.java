package dev.satyrn.lepidoptera.config.serializers;

import com.google.common.collect.Maps;
import dev.satyrn.lepidoptera.LepidopteraAPI;
import dev.satyrn.lepidoptera.annotations.Api;
import dev.satyrn.lepidoptera.mixin.accessors.me.shedaniel.autoconfig.serializer.YamlConfigSerializerAccessor;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.YamlConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.DumperOptions;
import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.Yaml;
import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.constructor.Constructor;
import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.representer.Representer;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;

@Api
public class CommentedYamlConfigSerializer<T extends ConfigData> extends YamlConfigSerializer<T> {
    public static final int DEFAULT_LINE_LENGTH = YamlCommentInjector.DEFAULT_LINE_LENGTH;
    public static final int MIN_LINE_LENGTH = YamlCommentInjector.MIN_LINE_LENGTH;

    private static final String YAML_DIRECTIVE_PREFIX = "!!";
    private static final int INDENT = 4;

    private final YamlConfigSerializerAccessor accessor = (YamlConfigSerializerAccessor) this;
    private final YamlCommentInjector injector;

    public CommentedYamlConfigSerializer(Config definition, Class<T> configClass, int lineLength) {
        super(definition, configClass, getYaml(configClass));
        this.injector = new YamlCommentInjector(lineLength);
    }

    @SuppressWarnings("unused")
    public CommentedYamlConfigSerializer(Config definition, Class<T> configClass) {
        this(definition, configClass, DEFAULT_LINE_LENGTH);
    }

    private static <T> Yaml getYaml(Class<T> configClass) {
        final DumperOptions dumperOptions = getDumperOptions();
        final Representer representer = new Representer(dumperOptions);
        representer.getPropertyUtils().setSkipMissingProperties(true);
        final Constructor constructor = new Constructor(configClass);
        constructor.setPropertyUtils(representer.getPropertyUtils());

        return new Yaml(constructor, representer, dumperOptions);
    }

    private static @NotNull DumperOptions getDumperOptions() {
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

    @Override
    public void serialize(T config) {
        final Path configPath = this.accessor.callGetConfigPath();

        try {
            final Map<String, String> commentMap = this.injector.buildNestedCommentMap(config.getClass());
            final String yaml = this.injector.injectComments(
                    this.accessor.getYaml().dump(config), commentMap, config.getClass());
            Files.createDirectories(configPath.getParent());
            Files.writeString(configPath, yaml, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception e) {
            LepidopteraAPI.error("Failed to save mod configuration file " + configPath, e);
        }
    }

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
