package dev.satyrn.lepidoptera.api.config;

import dev.satyrn.lepidoptera.api.NotInitializable;
import dev.satyrn.lepidoptera.api.annotations.Api;
import me.shedaniel.autoconfig.annotation.Config;
import org.apache.logging.log4j.util.StackLocatorUtil;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Provides lookup methods for the Cloth Config {@code Config} annotation
 *
 * @since 1.0.0-SNAPSHOT.1+1.21.1
 */
@Api("1.0.0-SNAPSHOT.1+1.21.1")
public class ConfigHelper {

    @Contract("-> fail")
    private ConfigHelper() {
        NotInitializable.staticClass(this);
    }

    /**
     * Returns the mod display name of the calling class's {@link Config} annotation.
     *
     * @return the value of {@link Config#name()}
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @Api("1.0.0-SNAPSHOT.1+1.21.1")
    @Contract(pure = true)
    public static String name(final Class<?> configClass) {
        return Objects.requireNonNull(findName(configClass));
    }

    /**
     * Returns the mod display name of the calling class's {@link Config} annotation.
     *
     * @return the value of {@link Config#name()}
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @Api("1.0.0-SNAPSHOT.1+1.21.1")
    @Contract(pure = true)
    public static String name() {
        return name(StackLocatorUtil.getCallerClass(2));
    }

    /**
     * Returns the {@link Config} annotation present on {@code configClass}.
     *
     * @param configClass the annotated class
     *
     * @return the annotation
     *
     * @throws NullPointerException if the class has no {@link Config} annotation
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @Api("1.0.0-SNAPSHOT.1+1.21.1")
    @Contract(pure = true)
    public static Config config(final Class<?> configClass) {
        return Objects.requireNonNull(findConfig(configClass));
    }

    /**
     * Returns the {@link Config} annotation of the calling class's mod.
     *
     * @return the annotation
     *
     * @throws NullPointerException if the calling class has no {@link Config} annotation
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @Api("1.0.0-SNAPSHOT.1+1.21.1")
    @Contract(pure = true)
    public static Config config() {
        return config(StackLocatorUtil.getCallerClass(2));
    }

    @Contract(pure = true)
    private static @Nullable Config findConfig(final Class<?> configClass) {
        return configClass.getAnnotation(Config.class);
    }

    @Contract(pure = true)
    private static @Nullable String findName(final Class<?> configClass) {
        final @Nullable Config modId = findConfig(configClass);
        return modId != null ? modId.name() : null;
    }
}
