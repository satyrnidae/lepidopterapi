package dev.satyrn.lepidoptera.api.entity;

import dev.satyrn.lepidoptera.api.NotInitializable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Utility methods for Minecraft player experience point calculations.
 *
 * <p>Implements the three-tier piecewise XP formulae from the Minecraft Wiki,
 * covering level-to-XP conversion, per-level XP span, partial-level XP,
 * total accumulated XP, and the inverse conversion from a raw XP total back
 * to a level and fractional progress.</p>
 *
 * <p>All methods are pure functions with no side effects and no dependency on
 * any live game object, making them safe to call from any thread and context.</p>
 *
 * <p>The core API accepts and returns {@link BigInteger}/{@link BigDecimal} for
 * full precision. Convenience overloads accepting {@code int} level and
 * {@code float} progress are provided for direct use with vanilla player state
 * ({@code Player.experienceLevel} / {@code Player.experienceProgress}).</p>
 *
 * @since 1.0.0-SNAPSHOT.1+1.21.1
 */
@ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
public final class ExperienceUtils {

    /**
     * The math context used for all BigDecimal calculations.
     */
    private static final MathContext MATH_CONTEXT = new MathContext(34, RoundingMode.HALF_UP);

    /**
     * The maximum level at which the first-tier formula applies.
     */
    private static final BigInteger LEVEL_TIER1_MAX = BigInteger.valueOf(16);

    /**
     * The maximum level at which the second-tier formula applies.
     */
    private static final BigInteger LEVEL_TIER2_MAX = BigInteger.valueOf(31);

    /**
     * Precomputed total XP at level 16 - the first-tier boundary for the inverse formula.
     * Equal to {@code getXPForLevel(16) = 352}.
     */
    private static final BigInteger XP_TIER1_MAX = BigInteger.valueOf(352);

    /**
     * Precomputed total XP at level 31 - the second-tier boundary for the inverse formula.
     * Equal to {@code getXPForLevel(31) = 1507}.
     */
    private static final BigInteger XP_TIER2_MAX = BigInteger.valueOf(1507);

    @Contract("-> fail")
    private ExperienceUtils() {
        NotInitializable.staticClass(this);
    }

    /**
     * Immutable carrier for the result of {@link #fromTotalXP(BigInteger)}.
     *
     * @param level    the whole-number experience level
     * @param progress the fractional progress within {@code level}, in {@code [0, 1)}
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public record LevelProgress(BigInteger level, BigDecimal progress) {
    }

    /**
     * Returns the total XP required to reach the given level from zero.
     *
     * <p>Uses the vanilla three-tier piecewise polynomial from the Minecraft Wiki:</p>
     * <ul>
     *   <li>levels 0-16: {@code x² + 6x}</li>
     *   <li>levels 17-31: {@code (5/2)x² - (81/2)x + 360}</li>
     *   <li>levels 32+: {@code (9/2)x² - (325/2)x + 2220}</li>
     * </ul>
     *
     * @param level the target level (non-negative)
     *
     * @return total XP to reach {@code level}
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @Contract(pure = true)
    public static BigInteger getXPForLevel(final BigInteger level) {
        final BigDecimal x = new BigDecimal(level);
        final BigDecimal totalXP;

        if (level.compareTo(LEVEL_TIER1_MAX) <= 0) {
            totalXP = x.pow(2)
                .add(BigDecimal.valueOf(6).multiply(x));
        } else if (level.compareTo(LEVEL_TIER2_MAX) <= 0) {
            totalXP = BigDecimal.valueOf(5)
                .divide(BigDecimal.valueOf(2), MATH_CONTEXT)
                .multiply(x.pow(2))
                .subtract(BigDecimal.valueOf(81)
                    .divide(BigDecimal.valueOf(2), MATH_CONTEXT)
                    .multiply(x))
                .add(BigDecimal.valueOf(360));
        } else {
            totalXP = BigDecimal.valueOf(9)
                .divide(BigDecimal.valueOf(2), MATH_CONTEXT)
                .multiply(x.pow(2))
                .subtract(BigDecimal.valueOf(325)
                    .divide(BigDecimal.valueOf(2), MATH_CONTEXT)
                    .multiply(x))
                .add(BigDecimal.valueOf(2220));
        }

        return totalXP.setScale(0, RoundingMode.HALF_UP).toBigInteger();
    }

    /**
     * Returns the total XP required to reach the given level from zero.
     *
     * <p>Convenience overload accepting a primitive {@code int} level, for use with
     * vanilla player level values ({@code Player.experienceLevel}).</p>
     *
     * @param level the target level (non-negative)
     *
     * @return total XP to reach {@code level}
     *
     * @see #getXPForLevel(BigInteger)
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @Contract(pure = true)
    public static BigInteger getXPForLevel(final int level) {
        return getXPForLevel(BigInteger.valueOf(level));
    }

    /**
     * Returns the XP count spanning the entirety of the given level - the XP
     * distance from first entering {@code level} to first entering {@code level + 1}.
     *
     * <p>Uses the vanilla three-tier piecewise formula from the Minecraft Wiki:</p>
     * <ul>
     *   <li>levels 0-15: {@code 2x + 7}</li>
     *   <li>levels 16-30: {@code 5x - 38}</li>
     *   <li>levels 31+: {@code 9x - 158}</li>
     * </ul>
     *
     * @param level the level whose XP span is requested (non-negative)
     *
     * @return XP count spanning {@code level}
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @Contract(pure = true)
    public static BigInteger getXPSpanForLevel(final BigInteger level) {
        final BigDecimal x = new BigDecimal(level);
        final BigDecimal span;

        if (level.compareTo(LEVEL_TIER1_MAX) < 0) {
            span = x.multiply(BigDecimal.valueOf(2)).add(BigDecimal.valueOf(7));
        } else if (level.compareTo(LEVEL_TIER2_MAX) < 0) {
            span = x.multiply(BigDecimal.valueOf(5)).subtract(BigDecimal.valueOf(38));
        } else {
            span = x.multiply(BigDecimal.valueOf(9)).subtract(BigDecimal.valueOf(158));
        }

        return span.setScale(0, RoundingMode.HALF_UP).toBigInteger();
    }

    /**
     * Returns the XP count spanning the entirety of the given level.
     *
     * <p>Convenience overload accepting a primitive {@code int} level.</p>
     *
     * @param level the level whose XP span is requested (non-negative)
     *
     * @return XP count spanning {@code level}
     *
     * @see #getXPSpanForLevel(BigInteger)
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @Contract(pure = true)
    public static BigInteger getXPSpanForLevel(final int level) {
        return getXPSpanForLevel(BigInteger.valueOf(level));
    }

    /**
     * Returns the XP accumulated within the given level at the specified fractional progress.
     *
     * <p>{@code progress} is the fraction of the current level completed, in {@code [0, 1]}
     * - equivalent to {@code Player.experienceProgress}.</p>
     *
     * @param level    the current whole-number level (non-negative)
     * @param progress fractional progress within {@code level}, in {@code [0, 1]}
     *
     * @return XP accumulated within {@code level} at {@code progress}
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @Contract(pure = true)
    public static BigInteger getXPWithinLevel(final BigInteger level, final BigDecimal progress) {
        return new BigDecimal(getXPSpanForLevel(level))
            .multiply(progress)
            .setScale(0, RoundingMode.HALF_UP)
            .toBigInteger();
    }

    /**
     * Returns the XP accumulated within the given level at the specified fractional progress.
     *
     * <p>Convenience overload accepting primitive {@code int}/{@code float} values, for
     * use with vanilla player state.</p>
     *
     * @param level    the current whole-number level (non-negative)
     * @param progress fractional progress within {@code level}, in {@code [0, 1]}
     *
     * @return XP accumulated within {@code level} at {@code progress}
     *
     * @see #getXPWithinLevel(BigInteger, BigDecimal)
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @Contract(pure = true)
    public static BigInteger getXPWithinLevel(final int level, final float progress) {
        return getXPWithinLevel(BigInteger.valueOf(level), BigDecimal.valueOf(progress));
    }

    /**
     * Returns the total XP accumulated by a player at the given level and fractional progress.
     *
     * <p>Equivalent to
     * {@code getXPForLevel(level).add(getXPWithinLevel(level, progress))}.</p>
     *
     * @param level    the current whole-number level (non-negative)
     * @param progress fractional progress within {@code level}, in {@code [0, 1]}
     *
     * @return total XP at {@code level} + {@code progress}
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @Contract(pure = true)
    public static BigInteger getTotalXP(final BigInteger level, final BigDecimal progress) {
        return getXPForLevel(level).add(getXPWithinLevel(level, progress));
    }

    /**
     * Returns the total XP accumulated by a player at the given level and fractional progress.
     *
     * <p>Convenience overload accepting primitive {@code int}/{@code float} values, for
     * use with vanilla player state ({@code Player.experienceLevel} and
     * {@code Player.experienceProgress}).</p>
     *
     * @param level    the current whole-number level (non-negative)
     * @param progress fractional progress within {@code level}, in {@code [0, 1]}
     *
     * @return total XP at {@code level} + {@code progress}
     *
     * @see #getTotalXP(BigInteger, BigDecimal)
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @Contract(pure = true)
    public static BigInteger getTotalXP(final int level, final float progress) {
        return getTotalXP(BigInteger.valueOf(level), BigDecimal.valueOf(progress));
    }

    /**
     * Converts a raw total XP value into a {@link LevelProgress} holding the
     * whole-number level and fractional progress within that level.
     *
     * <p>This is the inverse of {@link #getTotalXP(BigInteger, BigDecimal)}. Uses
     * the vanilla three-tier piecewise inverse from the Minecraft Wiki:</p>
     * <ul>
     *   <li>total ≤ 352: {@code √(x + 9) - 3}</li>
     *   <li>total ≤ 1507: {@code 81/10 + √((2/5)(x - 7839/40))}</li>
     *   <li>otherwise: {@code 325/18 + √((2/9)(x - 54215/72))}</li>
     * </ul>
     *
     * @param totalXP the raw XP total (non-negative)
     *
     * @return the {@link LevelProgress} corresponding to {@code totalXP}
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @Contract(pure = true)
    public static LevelProgress fromTotalXP(final BigInteger totalXP) {
        final BigDecimal x = new BigDecimal(totalXP);
        final BigDecimal levelD;

        if (totalXP.compareTo(XP_TIER1_MAX) <= 0) {
            levelD = x.add(BigDecimal.valueOf(9))
                .sqrt(MATH_CONTEXT)
                .subtract(BigDecimal.valueOf(3));
        } else if (totalXP.compareTo(XP_TIER2_MAX) <= 0) {
            levelD = x.subtract(BigDecimal.valueOf(7839)
                    .divide(BigDecimal.valueOf(40), MATH_CONTEXT))
                .multiply(BigDecimal.valueOf(2)
                    .divide(BigDecimal.valueOf(5), MATH_CONTEXT))
                .sqrt(MATH_CONTEXT)
                .add(BigDecimal.valueOf(81)
                    .divide(BigDecimal.valueOf(10), MATH_CONTEXT));
        } else {
            levelD = x.subtract(BigDecimal.valueOf(54215)
                    .divide(BigDecimal.valueOf(72), MATH_CONTEXT))
                .multiply(BigDecimal.valueOf(2)
                    .divide(BigDecimal.valueOf(9), MATH_CONTEXT))
                .sqrt(MATH_CONTEXT)
                .add(BigDecimal.valueOf(325)
                    .divide(BigDecimal.valueOf(18), MATH_CONTEXT));
        }

        final BigInteger level = levelD.setScale(0, RoundingMode.FLOOR).toBigInteger();
        final BigDecimal progress = levelD.remainder(BigDecimal.ONE);
        return new LevelProgress(level, progress);
    }
}
