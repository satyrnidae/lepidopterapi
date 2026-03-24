package dev.satyrn.lepidoptera.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a type or member as part of the Lepidoptera public API.
 *
 * <p>Elements annotated with {@code @Api} are intended for use by downstream mods.
 * Non-annotated types and members are considered implementation details and may
 * change without notice between versions.</p>
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.SOURCE)
public @interface Api {
}
