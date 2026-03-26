package dev.satyrn.lepidoptera.api.config;

import dev.satyrn.lepidoptera.api.annotations.Api;
import me.shedaniel.autoconfig.ConfigData;

/**
 * Extension of Cloth Config's {@link ConfigData} for nested config sub-objects.
 *
 * <p>Implement this interface on config subsections to support server-pushed config
 * synchronization via {@code ServerConfigSync}: the {@link #copyFrom} method is called
 * to overlay server values onto the local config object.</p>
 *
 * @param <T> the concrete config type (self-referential)
 *
 * @since 0.4.0+1.19.2
 */
@Api("0.4.0+1.19.2")
public interface NestingConfigData<T extends NestingConfigData<T>> extends ConfigData {
    /**
     * Copies all values from {@code other} into this config object.
     *
     * <p>Used by the config sync system to apply server-authoritative values
     * over the local config without replacing the object reference.</p>
     *
     * @param other the source config to copy from
     *
     * @since 0.4.0+1.19.2
     */
    @Api("0.4.0+1.19.2")
    void copyFrom(final T other);
}
