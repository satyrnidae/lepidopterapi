package dev.satyrn.lepidoptera.api.config;

import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.*;

/**
 * Marks a {@code String} config field as an inventory size, enabling the
 * {@code InventorySizeEntry} GUI control for that field in Cloth Config's AutoConfig screen.
 *
 * <p>The field value is the {@code "WxH"} string produced by {@link InventorySize#toString()}.
 * Annotating with this also binds the maximum width and height used by the GUI sliders.</p>
 *
 * <p>Register the GUI entry type provider once in your client initializer:</p>
 * <pre>{@code
 * AutoConfig.getGuiRegistry(MyConfig.class)
 *     .registerPredicateProvider(
 *         InventorySizeEntry.TYPE_PROVIDER,
 *         field -> field.getType() == String.class
 *                && field.isAnnotationPresent(InventorySizeField.class));
 * }</pre>
 *
 * @since 1.0.0-SNAPSHOT+1.21.1
 */
@ApiStatus.AvailableSince("1.0.0-SNAPSHOT+1.21.1")
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface InventorySizeField {

    /**
     * The maximum allowed width (number of columns). Defaults to 27.
     *
     * @since 1.0.0-SNAPSHOT+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT+1.21.1")
    int maxWidth() default 27;

    /**
     * The maximum allowed height (number of rows). Defaults to 27.
     *
     * @since 1.0.0-SNAPSHOT+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT+1.21.1")
    int maxHeight() default 27;
}
