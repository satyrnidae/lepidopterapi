package dev.satyrn.lepidoptera.api.annotations;

import java.lang.annotation.*;

/**
 * Marks a type or member as part of the Lepidoptera public API.
 *
 * <p>Elements annotated with {@code @Api} are intended for use by downstream mods.
 * Non-annotated types and members are considered implementation details and may
 * change without notice between versions.</p>
 *
 * @since 0.4.0+1.19.2
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR})
public @interface Api {

    /**
     * Notes the version that the function was added to the public API.
     *
     * <p>Version is in simple SemVer: {@code <major>.<minor>.<patch>[-<pre-release>][+<minecraft>]}.
     * API changes are technically considered agnostic of Minecraft version, unless the separate Minecraft
     * version identifier is included.</p>
     *
     * <p>Version may not be included on all public API members.</p>
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    String value() default "";

    /**
     * Notes that this version is only applicable to the specified Minecraft version,
     * and may be subject to change with future Minecraft versions.
     *
     * <p>This is especially relevant for mixin accessors provided by the API.</p>
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    String minecraft() default "";

    /**
     * Marks public API functions as deprecated and subject to removal.
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    String deprecated() default "";
}
