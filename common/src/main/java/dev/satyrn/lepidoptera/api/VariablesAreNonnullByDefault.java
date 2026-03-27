package dev.satyrn.lepidoptera.api;

import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierDefault;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Applies {@link Nonnull} to all local variables in the annotated scope by default.
 *
 * <p>Place this annotation on a {@code package-info.java} alongside
 * {@code @MethodsReturnNonnullByDefault}, {@code @FieldsAreNonnullByDefault}, and
 * {@code @ParametersAreNonnullByDefault} to declare that every variable in the package
 * is non-null unless explicitly annotated with {@link javax.annotation.Nullable}.</p>
 *
 * @since 0.4.0+1.19.2
 */
@ApiStatus.AvailableSince("0.4.0+1.19.2")
@Nonnull
@TypeQualifierDefault(ElementType.LOCAL_VARIABLE)
@Retention(RetentionPolicy.RUNTIME)
public @interface VariablesAreNonnullByDefault {
}
