package dev.satyrn.lepidoptera.api;

import org.jetbrains.annotations.ApiStatus;
import net.minecraft.resources.ResourceLocation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a mod's main class with identifying metadata.
 *
 * <p>Place this annotation on the class passed to Lepidoptera API helpers such as
 * {@code ModHelper} and {@code T9n} to supply the mod ID, display name,
 * and semantic version without requiring a runtime dependency on a specific loader's
 * metadata API.</p>
 *
 * @since 1.0.0-SNAPSHOT.1+1.21.1
 */
@ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ModMeta {
    /**
     * The mod's unique identifier (e.g. {@code "mymod"}).
     *
     * <p>Used as the namespace in {@link ResourceLocation ResourceLocations} produced by
     * {@code ModHelper.resource()} and as the key prefix in translation key helpers.</p>
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    String value();

    /**
     * The mod's human-readable display name (e.g. {@code "My Mod"}).
     *
     * <p>Defaults to an empty string, in which case helpers fall back to the mod ID.</p>
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    String name() default "";

    /**
     * The mod's version as a Semantic Versioning string (e.g. {@code "1.2.3"}).
     *
     * <p>Defaults to an empty string. Parsed by {@link dev.satyrn.lepidoptera.api.SemVer#tryParse(String)}.</p>
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    String semVer() default "";
}
