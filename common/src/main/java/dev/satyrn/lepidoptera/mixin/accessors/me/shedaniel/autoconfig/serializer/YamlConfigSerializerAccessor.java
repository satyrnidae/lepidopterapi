package dev.satyrn.lepidoptera.mixin.accessors.me.shedaniel.autoconfig.serializer;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.YamlConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.Yaml;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.nio.file.Path;

@Mixin(value = YamlConfigSerializer.class, remap = false)
public interface YamlConfigSerializerAccessor {
	@Accessor Config getDefinition();
	@Accessor Class<ConfigData> getConfigClass();
	@Accessor Yaml getYaml();

	@Invoker
	Path callGetConfigPath();
}
