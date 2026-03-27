package dev.satyrn.lepidoptera.api.config;

import dev.satyrn.lepidoptera.api.config.serializers.YamlComment;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

/**
 * Provides config enum values for tool effectiveness.
 *
 * @since 1.0.0-SNAPSHOT.1+1.21.1
 */
public enum ToolEffectivenessTier {
    /**
     * Tool effectiveness equivalent to wooden tools
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @YamlComment("Equivalent to wooden tools (Effectiveness = 0)")
    WOOD(0),
    /**
     * Tool effectiveness equivalent to stone tools
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @YamlComment("Equivalent to stone tools (Effectiveness = 1)")
    STONE(1),
    /**
     * Tool effectiveness equivalent to iron tools
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @YamlComment("Equivalent to iron tools (Effectiveness = 2)")
    IRON(2),
    /**
     * Tool effectiveness equivalent to golden tools
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @YamlComment("Equivalent to golden tools (Effectiveness = 0)")
    GOLD(0),
    /**
     * Tool effectiveness equivalent to diamond tools
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @YamlComment("Equivalent to diamond tools (Effectiveness = 3)")
    DIAMOND(3),
    /**
     * Tool effectiveness equivalent to netherite tools
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @YamlComment("Equivalent to netherite tools (Effectiveness = 4)")
    NETHERITE(4);

    private final int effectiveness;

    @Contract(pure = true)
    ToolEffectivenessTier(int effectiveness) {
        this.effectiveness = effectiveness;
    }

    /**
     * Gets the integer effectiveness value of the entry
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @Contract(pure = true)
    public int getEffectiveness() {
        return this.effectiveness;
    }
}

