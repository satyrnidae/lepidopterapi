package dev.satyrn.lepidoptera.api.config;

import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.*;

/**
 * Marks a {@link dev.satyrn.lepidoptera.api.config.transform.Transformation @Transformation}-typed
 * config field as a 3D transform, enabling the {@code TransformEntry} GUI control for that field
 * in Cloth Config's AutoConfig screen.
 *
 * <p>The annotation names a {@link TransformDisplayObject} implementation (zero-arg constructor
 * required) that supplies the item preview and baked-in initial transforms mirroring whatever
 * the real layer renderer applies before the configurable values.</p>
 *
 * <p>Register the GUI entry type provider once in your client initializer:</p>
 * <pre>{@code
 * AutoConfig.getGuiRegistry(MyConfig.class)
 *     .registerPredicateProvider(
 *         TransformEntry.TYPE_PROVIDER,
 *         field -> field.getType().isAnnotationPresent(Transformation.class)
 *                && field.isAnnotationPresent(TransformField.class));
 * }</pre>
 *
 * @since 1.0.1-SNAPSHOT.3+1.21.1
 */
@ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TransformField {

    /**
     * The {@link TransformDisplayObject} implementation to use for the 3D viewport preview.
     * The class must have a zero-argument constructor accessible at runtime.
     *
     * <p>Declared as {@code Class<?>} (not bounded) because this annotation lives in common
     * code and the implementation class may use client-only APIs; the class is only instantiated
     * at runtime on the client inside {@code TransformEntry.TYPE_PROVIDER}.</p>
     *
     * @since 1.0.1-SNAPSHOT.3+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    Class<?> displayObject();
}
