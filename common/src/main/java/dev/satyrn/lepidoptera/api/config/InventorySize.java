package dev.satyrn.lepidoptera.api.config;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nullable;

/**
 * An immutable inventory dimension value representing a grid of {@code width × height} slots.
 *
 * <p>The minimum value for both dimensions is {@link #MIN_VALUE} (1). Maximum values are
 * unconstrained at the type level; GUI controls impose additional upper bounds via
 * {@link InventorySizeField}.</p>
 *
 * <p>Serializes to and from the compact string form {@code "WxH"} (e.g. {@code "9x6"}).
 * Config beans should store this as a {@code String} field so that any Cloth Config serializer
 * handles it natively — use {@link #parse} and {@link #toString} at the use site.</p>
 *
 * @param width The width of the inventory
 * @param height The height of the inventory
 *
 * @since 1.0.0-SNAPSHOT+1.21.1
 */
@ApiStatus.AvailableSince("1.0.0-SNAPSHOT+1.21.1")
public record InventorySize(int width, int height) {

    /**
     * The minimum allowed value for either dimension (inclusive).
     *
     * @since 1.0.0-SNAPSHOT+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT+1.21.1")
    public static final int MIN_VALUE = 1;

    /**
     * Creates an inventory size with the given dimensions.
     *
     * @param width  the number of columns; must be {@code >= } {@link #MIN_VALUE}
     * @param height the number of rows; must be {@code >= } {@link #MIN_VALUE}
     *
     * @throws IllegalArgumentException if either dimension is below {@link #MIN_VALUE}
     * @since 1.0.0-SNAPSHOT+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT+1.21.1")
    public InventorySize {
        if (width < MIN_VALUE) {
            throw new IllegalArgumentException("Inventory width must be >= " + MIN_VALUE + ", got " + width);
        }
        if (height < MIN_VALUE) {
            throw new IllegalArgumentException("Inventory height must be >= " + MIN_VALUE + ", got " + height);
        }
    }

    /**
     * Parses an inventory size from the compact {@code "WxH"} string format.
     *
     * @param s a string of the form {@code "<width>x<height>"} where both values are
     *          positive integers {@code >= } {@link #MIN_VALUE}
     *
     * @return the parsed size
     *
     * @throws IllegalArgumentException if the string is null, empty, malformed, or contains
     *                                  values below {@link #MIN_VALUE}
     * @since 1.0.0-SNAPSHOT+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT+1.21.1")
    @Contract(value = "_ -> new", pure = true)
    public static InventorySize parse(final @Nullable String s) {
        if (s == null || s.isEmpty()) {
            throw new IllegalArgumentException("Cannot parse null or empty string as inventory size");
        }
        final int sep = s.indexOf('x');
        if (sep < 1 || sep >= s.length() - 1) {
            throw new IllegalArgumentException("Invalid inventory size \"" + s + "\": expected \"<width>x<height>\"");
        }
        try {
            final int width = Integer.parseInt(s.substring(0, sep));
            final int height = Integer.parseInt(s.substring(sep + 1));
            return new InventorySize(width, height);
        } catch (final NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Invalid inventory size \"" + s + "\": width and height must be integers", e);
        }
    }

    /**
     * Returns the number of columns.
     *
     * @return The width of the inventory
     *
     * @since 1.0.0-SNAPSHOT+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT+1.21.1")
    @Contract(pure = true)
    @Override
    public int width() {
        return this.width;
    }

    /**
     * Returns the number of rows.
     *
     * @return The height of the inventory
     *
     * @since 1.0.0-SNAPSHOT+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT+1.21.1")
    @Contract(pure = true)
    @Override
    public int height() {
        return this.height;
    }

    /**
     * Returns the compact {@code "WxH"} string representation, e.g. {@code "9x6"}.
     *
     * @since 1.0.0-SNAPSHOT+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT+1.21.1")
    @Contract(pure = true)
    @Override
    public String toString() {
        return this.width + "x" + this.height;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0-SNAPSHOT+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT+1.21.1")
    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(final @Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InventorySize(int width1, int height1))) {
            return false;
        }
        return this.width == width1 && this.height == height1;
    }

}
