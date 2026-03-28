package dev.satyrn.lepidoptera.api.accessors.autoconfig;

import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.YamlConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.Yaml;
import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.nio.file.Path;

/**
 * Mixin accessor interface exposing private fields and methods of
 * {@link YamlConfigSerializer} to {@code CommentedYamlConfigSerializer}.
 *
 * <p>Applied via Mixin in the {@code lepidoptera.api.mixins.json} config.
 * All members are internal infrastructure; downstream code should use
 * {@code CommentedYamlConfigSerializer} directly.</p>
 *
 * @since 0.4.0+1.19.2
 */
@ApiStatus.AvailableSince("0.4.0+1.19.2")
@ApiStatus.Internal
@Mixin(value = YamlConfigSerializer.class, remap = false)
public interface YamlConfigSerializerAccessor {

    /**
     * Returns the {@link Config} annotation that defines this serializer's config ID and name.
     *
     * @return the config definition
     *
     * @since 0.4.0+1.19.2
     */
    @Accessor
    @ApiStatus.AvailableSince("0.4.0+1.19.2")
    @ApiStatus.Internal
    Config getDefinition();

    /**
     * Returns the SnakeYAML {@link Yaml} instance used for serialization and deserialization.
     *
     * @return the YAML processor
     *
     * @since 0.4.0+1.19.2
     */
    @Accessor
    @ApiStatus.AvailableSince("0.4.0+1.19.2")
    @ApiStatus.Internal
    Yaml getYaml();

    /**
     * Invokes the private {@code getConfigPath()} method to resolve the on-disk path for
     * this config file.
     *
     * @return the resolved config file path
     *
     * @since 0.4.0+1.19.2
     */
    @ApiStatus.AvailableSince("0.4.0+1.19.2")
    @ApiStatus.Internal
    @Invoker
    Path callGetConfigPath();
}
