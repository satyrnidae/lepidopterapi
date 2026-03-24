package dev.satyrn.lepidoptera.api.lang;

import dev.satyrn.lepidoptera.api.ModHelper;
import dev.satyrn.lepidoptera.api.NotInitializable;
import dev.satyrn.lepidoptera.api.annotations.Api;
import dev.satyrn.lepidoptera.api.ModMeta;
import dev.satyrn.lepidoptera.api.config.ConfigHelper;
import me.shedaniel.autoconfig.annotation.Config;
import net.minecraft.Util;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Contract;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Utility for building mod-scoped translation keys.
 *
 * <p>All methods that accept a {@link ModMeta} will scope keys to the mod's ID
 * ({@link ModMeta#value()}), so keys are automatically namespaced per mod.</p>
 *
 * <p>Use these helpers from your platform-specific language provider (e.g.
 * NeoForge's {@code ModLanguageProvider} or Fabric's {@code FabricLanguageProvider})
 * to stay consistent with Lepidoptera API key conventions.</p>
 */
@Api
public final class T9n {
    private T9n() {
        NotInitializable.staticClass(this);
    }

    //#region Configuration

    //#region Title

    /**
     * Returns the Cloth Config GUI title key: {@code text.autoconfig.<configName>.title}.
     *
     * @param configName the value of {@link Config#name()} on the config class
     */
    @Contract(pure = true)
    @Api public static String configTitle(final String configName) {
        return String.format("text.autoconfig.%s.title", configName);
    }

    /**
     * Returns the Cloth Config GUI title key for a config annotation.
     *
     * @see #configTitle(String)
     */
    @Contract(pure = true)
    @Api public static String configTitle(final Config config) {
        return configTitle(config.name());
    }

    /**
     * Returns the Cloth Config GUI title key for a config class.
     * The class must be annotated with {@link Config}.
     *
     * @see #configTitle(String)
     */
    @Contract(pure = true)
    @Api public static String configTitle(final Class<?> configClass) {
        return configTitle(ConfigHelper.name(configClass));
    }

    //#endregion Title

    //#region Keys

    /**
     * Returns an arbitrary Cloth Config key: {@code text.autoconfig.<configName>.<key>}.
     *
     * <p>Use this for keys that don't fit the {@code option} or {@code tooltip} conventions
     * (e.g. section headers or custom sub-screens).</p>
     *
     * @param configName the value of {@link Config#name()} on the config class
     * @param key        the key suffix within the config's namespace
     */
    @Contract(pure = true)
    @Api public static String configKey(final String configName, final String key) {
        return String.format("text.autoconfig.%s.%s", configName, key);
    }

    /**
     * Returns an arbitrary Cloth Config key for a config annotation.
     *
     * @see #configKey(String, String)
     */
    @Contract(pure = true)
    @Api public static String configKey(final Config config, final String key) {
        return configKey(config.name(), key);
    }

    /**
     * Returns an arbitrary Cloth Config key for a config class.
     * The class must be annotated with {@link Config}.
     *
     * @see #configKey(String, String)
     */
    @Contract(pure = true)
    @Api public static String configKey(final Class<?> configClass, final String key) {
        return configKey(ConfigHelper.name(configClass), key);
    }

    /**
     * Returns a nested Cloth Config key: {@code text.autoconfig.<configName>.<parent>.<key>}.
     *
     * @param configName the value of {@link Config#name()} on the config class
     * @param parent     the intermediate path segment (e.g. a sub-section name)
     * @param key        the leaf key
     */
    @Contract(pure = true)
    @Api public static String configKey(final String configName, final String parent, final String key) {
        return String.format("text.autoconfig.%s.%s.%s", configName, parent, key);
    }

    /**
     * Returns a nested Cloth Config key for a config annotation.
     *
     * @see #configKey(String, String, String)
     */
    @Contract(pure = true)
    @Api public static String configKey(final Config config, final String parent, final String key) {
        return configKey(config.name(), parent, key);
    }

    /**
     * Returns a nested Cloth Config key for a config class.
     * The class must be annotated with {@link Config}.
     *
     * @see #configKey(String, String, String)
     */
    @Contract(pure = true)
    @Api public static String configKey(final Class<?> configClass, final String parent, final String key) {
        return configKey(ConfigHelper.name(configClass), parent, key);
    }

    /**
     * Returns a deeply nested Cloth Config key: {@code text.autoconfig.<configName>.<parents...>.<key>}.
     * Parent segments are joined with {@code .}.
     *
     * @param configName the value of {@link Config#name()} on the config class
     * @param parents    ordered path segments above {@code key}
     * @param key        the leaf key
     */
    @Contract(pure = true)
    @Api public static String configKey(final String configName, final String[] parents, final String key) {
        return configKey(configName, String.join(".", parents), key);
    }

    /**
     * Returns a deeply nested Cloth Config key for a config annotation.
     *
     * @see #configKey(String, String[], String)
     */
    @Contract(pure = true)
    @Api public static String configKey(final Config config, final String[] parents, final String key) {
        return configKey(config.name(), parents, key);
    }

    /**
     * Returns a deeply nested Cloth Config key for a config class.
     * The class must be annotated with {@link Config}.
     *
     * @see #configKey(String, String[], String)
     */
    @Contract(pure = true)
    @Api public static String configKey(final Class<?> configClass, final String[] parents, final String key) {
        return configKey(ConfigHelper.name(configClass), parents, key);
    }

    //#endregion Keys

    //#region Options

    /**
     * Returns a Cloth Config option key: {@code text.autoconfig.<configName>.option.<option>}.
     *
     * <p>The {@code option} value should match the field name exactly as declared in the config
     * class, since Cloth Config derives option keys from field names.</p>
     *
     * @param configName the value of {@link Config#name()} on the config class
     * @param option     the config field name
     */
    @Contract(pure = true)
    @Api public static String configOption(final String configName, final String option) {
        return String.format("text.autoconfig.%s.option.%s", configName, option);
    }

    /**
     * Returns a Cloth Config option key for a config annotation.
     *
     * @see #configOption(String, String)
     */
    @Contract(pure = true)
    @Api public static String configOption(final Config config, final String option) {
        return configOption(config.name(), option);
    }

    /**
     * Returns a Cloth Config option key for a config class.
     * The class must be annotated with {@link Config}.
     *
     * @see #configOption(String, String)
     */
    @Contract(pure = true)
    @Api public static String configOption(final Class<?> configClass, final String option) {
        return configOption(ConfigHelper.name(configClass), option);
    }

    /**
     * Returns a nested Cloth Config option key:
     * {@code text.autoconfig.<configName>.option.<parent>.<option>}.
     *
     * <p>Use for options inside a {@link me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.CollapsibleObject}
     * sub-section, where {@code parent} is the field name of the sub-section.</p>
     *
     * @param configName the value of {@link Config#name()} on the config class
     * @param parent     the sub-section field name
     * @param option     the config field name within the sub-section
     */
    @Contract(pure = true)
    @Api public static String configOption(final String configName, final String parent, final String option) {
        return String.format("text.autoconfig.%s.option.%s.%s", configName, parent, option);
    }

    /**
     * Returns a nested Cloth Config option key for a config annotation.
     *
     * @see #configOption(String, String, String)
     */
    @Contract(pure = true)
    @Api public static String configOption(final Config config, final String parent, final String option) {
        return configOption(config.name(), parent, option);
    }

    /**
     * Returns a nested Cloth Config option key for a config class.
     * The class must be annotated with {@link Config}.
     *
     * @see #configOption(String, String, String)
     */
    @Contract(pure = true)
    @Api public static String configOption(final Class<?> configClass, final String parent, final String option) {
        return configOption(ConfigHelper.name(configClass), parent, option);
    }

    /**
     * Returns a deeply nested Cloth Config option key:
     * {@code text.autoconfig.<configName>.option.<parents...>.<option>}.
     * Parent segments are joined with {@code .}.
     *
     * @param configName the value of {@link Config#name()} on the config class
     * @param parents    ordered sub-section field names above {@code option}
     * @param option     the config field name at the leaf
     */
    @Contract(pure = true)
    @Api public static String configOption(final String configName, final String[] parents, final String option) {
        return configOption(configName, String.join(".", parents), option);
    }

    /**
     * Returns a deeply nested Cloth Config option key for a config annotation.
     *
     * @see #configOption(String, String[], String)
     */
    @Contract(pure = true)
    @Api public static String configOption(final Config config, final String[] parents, final String option) {
        return configOption(config.name(), parents, option);
    }

    /**
     * Returns a deeply nested Cloth Config option key for a config class.
     * The class must be annotated with {@link Config}.
     *
     * @see #configOption(String, String[], String)
     */
    @Contract(pure = true)
    @Api public static String configOption(final Class<?> configClass, final String[] parents, final String option) {
        return configOption(ConfigHelper.name(configClass), parents, option);
    }

    //#endregion Options

    //#region Tooltips

    /**
     * Returns a Cloth Config single-line tooltip key:
     * {@code text.autoconfig.<configName>.option.<option>.@Tooltip}.
     *
     * <p>Use with {@link me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Tooltip} when
     * {@code count = 1} (the default). For multi-line tooltips use
     * {@link #configTooltip(String, String, int)}.</p>
     *
     * @param configName the value of {@link Config#name()} on the config class
     * @param option     the config field name
     */
    @Contract(pure = true)
    @Api public static String configTooltip(final String configName, final String option) {
        return String.format("text.autoconfig.%s.option.%s.@Tooltip", configName, option);
    }

    /**
     * Returns a Cloth Config single-line tooltip key for a config annotation.
     *
     * @see #configTooltip(String, String)
     */
    @Contract(pure = true)
    @Api public static String configTooltip(final Config config, final String option) {
        return configTooltip(config.name(), option);
    }

    /**
     * Returns a Cloth Config single-line tooltip key for a config class.
     * The class must be annotated with {@link Config}.
     *
     * @see #configTooltip(String, String)
     */
    @Contract(pure = true)
    @Api public static String configTooltip(final Class<?> configClass, final String option) {
        return configTooltip(ConfigHelper.name(configClass), option);
    }

    /**
     * Returns an indexed Cloth Config tooltip key:
     * {@code text.autoconfig.<configName>.option.<option>.@Tooltip[N]}.
     *
     * <p>Cloth Config supports multi-line tooltips via {@link me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Tooltip#count()}.
     * Lines are indexed from {@code 0}.</p>
     *
     * @param configName the value of {@link Config#name()} on the config class
     * @param option     the config field name
     * @param line       the 0-based tooltip line index
     */
    @Contract(pure = true)
    @Api public static String configTooltip(final String configName, final String option, final int line) {
        return String.format("text.autoconfig.%s.option.%s.@Tooltip[%d]", configName, option, line);
    }

    /**
     * Returns an indexed Cloth Config tooltip key for a config annotation.
     *
     * @see #configTooltip(String, String, int)
     */
    @Contract(pure = true)
    @Api public static String configTooltip(final Config config, final String option, final int line) {
        return configTooltip(config.name(), option, line);
    }

    /**
     * Returns an indexed Cloth Config tooltip key for a config class.
     * The class must be annotated with {@link Config}.
     *
     * @see #configTooltip(String, String, int)
     */
    @Contract(pure = true)
    @Api public static String configTooltip(final Class<?> configClass, final String option, final int line) {
        return configTooltip(ConfigHelper.name(configClass), option, line);
    }

    /**
     * Returns a nested single-line Cloth Config tooltip key:
     * {@code text.autoconfig.<configName>.option.<parent>.<option>.@Tooltip}.
     *
     * @param configName the value of {@link Config#name()} on the config class
     * @param parent     the sub-section field name
     * @param option     the config field name within the sub-section
     */
    @Contract(pure = true)
    @Api public static String configTooltip(final String configName, final String parent, final String option) {
        return String.format("text.autoconfig.%s.option.%s.%s.@Tooltip", configName, parent, option);
    }

    /**
     * Returns a nested single-line Cloth Config tooltip key for a config annotation.
     *
     * @see #configTooltip(String, String, String)
     */
    @Contract(pure = true)
    @Api public static String configTooltip(final Config config, final String parent, final String option) {
        return configTooltip(config.name(), parent, option);
    }

    /**
     * Returns a nested single-line Cloth Config tooltip key for a config class.
     * The class must be annotated with {@link Config}.
     *
     * @see #configTooltip(String, String, String)
     */
    @Contract(pure = true)
    @Api public static String configTooltip(final Class<?> configClass, final String parent, final String option) {
        return configTooltip(ConfigHelper.name(configClass), parent, option);
    }

    /**
     * Returns a deeply nested single-line Cloth Config tooltip key:
     * {@code text.autoconfig.<configName>.option.<parents...>.<option>.@Tooltip}.
     * Parent segments are joined with {@code .}.
     *
     * @param configName the value of {@link Config#name()} on the config class
     * @param parents    ordered sub-section field names above {@code option}
     * @param option     the config field name at the leaf
     */
    @Contract(pure = true)
    @Api public static String configTooltip(final String configName, final String[] parents, final String option) {
        return configTooltip(configName, String.join(".", parents), option);
    }

    /**
     * Returns a deeply nested single-line Cloth Config tooltip key for a config annotation.
     *
     * @see #configTooltip(String, String[], String)
     */
    @Contract(pure = true)
    @Api public static String configTooltip(final Config config, final String[] parents, final String option) {
        return configTooltip(config.name(), parents, option);
    }

    /**
     * Returns a deeply nested single-line Cloth Config tooltip key for a config class.
     * The class must be annotated with {@link Config}.
     *
     * @see #configTooltip(String, String[], String)
     */
    @Contract(pure = true)
    @Api public static String configTooltip(final Class<?> configClass, final String[] parents, final String option) {
        return configTooltip(ConfigHelper.name(configClass), parents, option);
    }

    /**
     * Returns a nested indexed Cloth Config tooltip key:
     * {@code text.autoconfig.<configName>.option.<parent>.<option>.@Tooltip[N]}.
     *
     * @param configName the value of {@link Config#name()} on the config class
     * @param parent     the sub-section field name
     * @param option     the config field name within the sub-section
     * @param line       the 0-based tooltip line index
     */
    @Contract(pure = true)
    @Api public static String configTooltip(final String configName,
                                            final String parent,
                                            final String option,
                                            final int line) {
        return String.format("text.autoconfig.%s.option.%s.%s.@Tooltip[%d]", configName, parent, option, line);
    }

    /**
     * Returns a nested indexed Cloth Config tooltip key for a config annotation.
     *
     * @see #configTooltip(String, String, String, int)
     */
    @Contract(pure = true)
    @Api public static String configTooltip(final Config config,
                                            final String parent,
                                            final String option,
                                            final int line) {
        return configTooltip(config.name(), parent, option, line);
    }

    /**
     * Returns a nested indexed Cloth Config tooltip key for a config class.
     * The class must be annotated with {@link Config}.
     *
     * @see #configTooltip(String, String, String, int)
     */
    @Contract(pure = true)
    @Api public static String configTooltip(final Class<?> configClass,
                                            final String parent,
                                            final String option,
                                            final int line) {
        return configTooltip(ConfigHelper.name(configClass), parent, option, line);
    }

    /**
     * Returns a deeply nested indexed Cloth Config tooltip key:
     * {@code text.autoconfig.<configName>.option.<parents...>.<option>.@Tooltip[N]}.
     * Parent segments are joined with {@code .}.
     *
     * @param configName the value of {@link Config#name()} on the config class
     * @param parents    ordered sub-section field names above {@code option}
     * @param option     the config field name at the leaf
     * @param line       the 0-based tooltip line index
     */
    @Contract(pure = true)
    @Api public static String configTooltip(final String configName,
                                            final String[] parents,
                                            final String option,
                                            final int line) {
        return configTooltip(configName, String.join(".", parents), option, line);
    }

    /**
     * Returns a deeply nested indexed Cloth Config tooltip key for a config annotation.
     *
     * @see #configTooltip(String, String[], String, int)
     */
    @Contract(pure = true)
    @Api public static String configTooltip(final Config config,
                                            final String[] parents,
                                            final String option,
                                            final int line) {
        return configTooltip(config.name(), parents, option, line);
    }

    /**
     * Returns a deeply nested indexed Cloth Config tooltip key for a config class.
     * The class must be annotated with {@link Config}.
     *
     * @see #configTooltip(String, String[], String, int)
     */
    @Contract(pure = true)
    @Api public static String configTooltip(final Class<?> configClass,
                                            final String[] parents,
                                            final String option,
                                            final int line) {
        return configTooltip(ConfigHelper.name(configClass), parents, option, line);
    }

    //#endregion Tooltips

    //#endregion Configuration

    //#region Network Message

    /**
     * Returns a mod network message translation key: {@code network.<modId>.<key>}.
     *
     * <p>Used for player-facing messages sent over the network (e.g. version mismatch warnings).</p>
     *
     * @param modId the mod's ID
     * @param key   the message identifier within the mod
     */
    @Contract(pure = true)
    @Api public static String netMsg(final String modId, final String key) {
        return String.format("network.%s.%s", modId, key);
    }

    /**
     * Returns a mod network message key scoped to the given mod's ID.
     *
     * @see #netMsg(String, String)
     */
    @Contract(pure = true)
    @Api public static String netMsg(final ModMeta metadata, final String key) {
        return netMsg(metadata.value(), key);
    }

    /**
     * Returns a mod network message key scoped to the mod class's {@link ModMeta} annotation.
     *
     * @see #netMsg(String, String)
     */
    @Contract(pure = true)
    @Api public static String netMsg(final Class<?> modClass, final String key) {
        return netMsg(ModHelper.modId(modClass), key);
    }

    /**
     * Returns a mod network message key from a ResourceLocation.
     * The namespace becomes the mod ID and the path becomes the key, with {@code /} replaced by {@code .}.
     *
     * @see #netMsg(String, String)
     */
    @Contract(pure = true)
    @Api public static String netMsg(final ResourceLocation location) {
        return netMsg(location.getNamespace(), location.getPath().replace('/', '.'));
    }

    /**
     * Returns a mod network message key from a ResourceKey.
     *
     * @see #netMsg(ResourceLocation)
     */
    @Contract(pure = true)
    @Api public static String netMsg(final ResourceKey<?> key) {
        return netMsg(key.location());
    }

    /**
     * Returns a mod network message key from a resource location string.
     *
     * @throws net.minecraft.ResourceLocationException if {@code location} is not a valid resource location
     * @see #netMsg(ResourceLocation)
     */
    @Contract(pure = true)
    @Api public static String netMsg(final String location) {
        return netMsg(ResourceLocation.parse(location));
    }

    //#endregion Network Message

    //#region Tips Mod

    /**
     * Returns a tip translation key for the Tips mod: {@code <modId>.tip.<key>}.
     *
     * @param modId the mod's ID
     * @param key   the tip identifier within the mod
     */
    @Contract(pure = true)
    @Api public static String tip(final String modId, final String key) {
        return String.format("%s.tip.%s", modId, key);
    }

    /**
     * Returns a tip key scoped to the given mod's ID.
     *
     * @see #tip(String, String)
     */
    @Contract(pure = true)
    @Api public static String tip(final ModMeta metadata, final String key) {
        return tip(metadata.value(), key);
    }

    /**
     * Returns a tip key scoped to the mod class's {@link ModMeta} annotation.
     *
     * @see #tip(String, String)
     */
    @Contract(pure = true)
    @Api public static String tip(final Class<?> modClass, final String key) {
        return tip(ModHelper.modId(modClass), key);
    }

    /**
     * Returns a tip key from a ResourceLocation.
     * The namespace becomes the mod ID and the path becomes the key, with {@code /} replaced by {@code .}.
     *
     * @see #tip(String, String)
     */
    @Contract(pure = true)
    @Api public static String tip(final ResourceLocation location) {
        return tip(location.getNamespace(), location.getPath().replace('/', '.'));
    }

    /**
     * Returns a tip key from a ResourceKey.
     *
     * @see #tip(ResourceLocation)
     */
    @Contract(pure = true)
    @Api public static String tip(final ResourceKey<?> key) {
        return tip(key.location());
    }

    /**
     * Returns a tip key from a resource location string.
     *
     * @throws net.minecraft.ResourceLocationException if {@code location} is not a valid resource location
     * @see #tip(ResourceLocation)
     */
    @Contract(pure = true)
    @Api public static String tip(final String location) {
        return tip(ResourceLocation.parse(location));
    }

    //#endregion Tips Mod

    //#region Items

    /**
     * Returns an item translation key: {@code item.<modId>.<key>}.
     *
     * @param modId the mod's ID
     * @param key   the item's registry name within the mod
     */
    @Contract(pure = true)
    @Api public static String item(final String modId, final String key) {
        return String.format("item.%s.%s", modId, key);
    }

    /**
     * Returns an item translation key scoped to the given mod's ID.
     *
     * @see #item(String, String)
     */
    @Contract(pure = true)
    @Api public static String item(final ModMeta metadata, final String key) {
        return item(metadata.value(), key);
    }

    /**
     * Returns an item translation key scoped to the mod class's {@link ModMeta} annotation.
     *
     * @see #item(String, String)
     */
    @Contract(pure = true)
    @Api public static String item(final Class<?> modClass, final String key) {
        return item(ModHelper.modId(modClass), key);
    }

    /**
     * Returns an item translation key from a ResourceLocation via
     * {@link Util#makeDescriptionId(String, ResourceLocation)}: {@code item.<namespace>.<path>}.
     */
    @Contract(pure = true)
    @Api public static String item(final ResourceLocation location) {
        return Util.makeDescriptionId("item", location);
    }

    /**
     * Returns an item translation key from a ResourceKey.
     *
     * @see #item(ResourceLocation)
     */
    @Contract(pure = true)
    @Api public static String item(final ResourceKey<?> key) {
        return item(key.location());
    }

    /**
     * Returns an item translation key from a resource location string.
     *
     * @throws net.minecraft.ResourceLocationException if {@code location} is not a valid resource location
     * @see #item(ResourceLocation)
     */
    @Contract(pure = true)
    @Api public static String item(final String location) {
        return item(ResourceLocation.parse(location));
    }

    /**
     * Returns the translation key for an item via its own {@link net.minecraft.world.item.Item#getDescriptionId()}.
     */
    @Contract(pure = true)
    @Api public static String item(final ItemLike itemLike) {
        return itemLike.asItem().getDescriptionId();
    }

    /**
     * Returns the translation key for an item obtained from a supplier.
     */
    @Api public static String item(final Supplier<? extends ItemLike> supplier) {
        return supplier.get().asItem().getDescriptionId();
    }

    //#region Descriptions

    /**
     * Returns a Music Disc (or generic item description) key: {@code item.<modId>.<key>.desc}.
     *
     * @param modId the mod's ID
     * @param key   the item's registry name within the mod
     */
    @Contract(pure = true)
    @Api public static String itemDesc(final String modId, final String key) {
        return String.format("item.%s.%s.desc", modId, key);
    }

    /**
     * Returns an item description key scoped to the given mod's ID.
     *
     * @see #itemDesc(String, String)
     */
    @Contract(pure = true)
    @Api public static String itemDesc(final ModMeta metadata, final String key) {
        return itemDesc(metadata.value(), key);
    }

    /**
     * Returns an item description key scoped to the mod class's {@link ModMeta} annotation.
     *
     * @see #itemDesc(String, String)
     */
    @Contract(pure = true)
    @Api public static String itemDesc(final Class<?> modClass, final String key) {
        return itemDesc(ModHelper.modId(modClass), key);
    }

    /**
     * Returns an item description key from a ResourceLocation: {@code item.<namespace>.<path>.desc}.
     *
     * @see #itemDesc(String, String)
     */
    @Contract(pure = true)
    @Api public static String itemDesc(final ResourceLocation key) {
        return String.format("%s.desc", Util.makeDescriptionId("item", key));
    }

    /**
     * Returns an item description key from a ResourceKey.
     *
     * @see #itemDesc(ResourceLocation)
     */
    @Contract(pure = true)
    @Api public static String itemDesc(final ResourceKey<?> key) {
        return itemDesc(key.location());
    }

    /**
     * Returns an item description key from a resource location string.
     *
     * @throws net.minecraft.ResourceLocationException if {@code location} is not a valid resource location
     * @see #itemDesc(ResourceLocation)
     */
    @Contract(pure = true)
    @Api public static String itemDesc(final String location) {
        return itemDesc(ResourceLocation.parse(location));
    }

    /**
     * Returns the description key for an item via its own
     * {@link net.minecraft.world.item.Item#getDescriptionId()}, appending {@code .desc}.
     */
    @Contract(pure = true)
    @Api public static String itemDesc(final ItemLike itemLike) {
        return String.format("%s.desc", itemLike.asItem().getDescriptionId());
    }

    /**
     * Returns the description key for an item obtained from a supplier, appending {@code .desc}.
     */
    @Api public static String itemDesc(final Supplier<? extends ItemLike> supplier) {
        return String.format("%s.desc", supplier.get().asItem().getDescriptionId());
    }

    //#endregion Descriptions

    //#endregion Items

    //#region Gamerules

    /**
     * Returns a game rule translation key: {@code gamerule.<key>}.
     *
     * @param key the game rule's registered name (e.g. {@code "doAnimalStarvation"})
     */
    @Contract(pure = true)
    @Api public static String gamerule(final String key) {
        return String.format("gamerule.%s", key);
    }

    /**
     * Returns a game rule description key: {@code gamerule.<key>.description}.
     *
     * @param key the game rule's registered name (e.g. {@code "doAnimalStarvation"})
     */
    @Contract(pure = true)
    @Api public static String gameruleDesc(final String key) {
        return String.format("gamerule.%s.description", key);
    }

    //#endregion Gamerules
}
