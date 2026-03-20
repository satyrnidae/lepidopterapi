package dev.satyrn.lepidoptera.config;

import dev.satyrn.lepidoptera.annotations.Api;
import me.shedaniel.autoconfig.ConfigData;

@Api
public interface NestingConfigData<T extends NestingConfigData<T>> extends ConfigData {
    @SuppressWarnings("unused")
    void copyFrom(T other);
}
