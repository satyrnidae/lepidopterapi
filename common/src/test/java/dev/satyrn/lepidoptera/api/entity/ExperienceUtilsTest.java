package dev.satyrn.lepidoptera.api.entity;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExperienceUtilsTest {

    @Nested
    class GetXPForLevel {

        @Test
        void level0_returns0() {
            assertEquals(BigInteger.ZERO, ExperienceUtils.getXPForLevel(BigInteger.ZERO));
        }

        @Test
        void level1_returns7() {
            assertEquals(BigInteger.valueOf(7), ExperienceUtils.getXPForLevel(BigInteger.ONE));
        }

        @Test
        void level16_returns352_tier1Boundary() {
            assertEquals(BigInteger.valueOf(352), ExperienceUtils.getXPForLevel(BigInteger.valueOf(16)));
        }

        @Test
        void level17_returns394_tier2Start() {
            assertEquals(BigInteger.valueOf(394), ExperienceUtils.getXPForLevel(BigInteger.valueOf(17)));
        }

        @Test
        void level31_returns1507_tier2Boundary() {
            assertEquals(BigInteger.valueOf(1507), ExperienceUtils.getXPForLevel(BigInteger.valueOf(31)));
        }

        @Test
        void level32_returns1628_tier3Start() {
            assertEquals(BigInteger.valueOf(1628), ExperienceUtils.getXPForLevel(BigInteger.valueOf(32)));
        }

        @Test
        void intOverload_matchesBigIntegerOverload() {
            assertEquals(ExperienceUtils.getXPForLevel(BigInteger.valueOf(10)), ExperienceUtils.getXPForLevel(10));
        }
    }

    @Nested
    class GetXPSpanForLevel {

        @Test
        void level0_returns7() {
            assertEquals(BigInteger.valueOf(7), ExperienceUtils.getXPSpanForLevel(BigInteger.ZERO));
        }

        @Test
        void level15_returns37_lastTier1() {
            assertEquals(BigInteger.valueOf(37), ExperienceUtils.getXPSpanForLevel(BigInteger.valueOf(15)));
        }

        @Test
        void level16_returns42_tier2Start() {
            assertEquals(BigInteger.valueOf(42), ExperienceUtils.getXPSpanForLevel(BigInteger.valueOf(16)));
        }

        @Test
        void level30_returns112_lastTier2() {
            assertEquals(BigInteger.valueOf(112), ExperienceUtils.getXPSpanForLevel(BigInteger.valueOf(30)));
        }

        @Test
        void level31_returns121_tier3Start() {
            assertEquals(BigInteger.valueOf(121), ExperienceUtils.getXPSpanForLevel(BigInteger.valueOf(31)));
        }

        @Test
        void intOverload_matchesBigIntegerOverload() {
            assertEquals(ExperienceUtils.getXPSpanForLevel(BigInteger.valueOf(20)),
                    ExperienceUtils.getXPSpanForLevel(20));
        }
    }

    @Nested
    class GetXPWithinLevel {

        @Test
        void zeroProgress_returns0ForAllTiers() {
            assertEquals(BigInteger.ZERO, ExperienceUtils.getXPWithinLevel(BigInteger.valueOf(5), BigDecimal.ZERO));
            assertEquals(BigInteger.ZERO, ExperienceUtils.getXPWithinLevel(BigInteger.valueOf(20), BigDecimal.ZERO));
            assertEquals(BigInteger.ZERO, ExperienceUtils.getXPWithinLevel(BigInteger.valueOf(35), BigDecimal.ZERO));
        }

        @Test
        void fullProgress_equalsSpanForAllTiers() {
            assertEquals(ExperienceUtils.getXPSpanForLevel(5),
                    ExperienceUtils.getXPWithinLevel(BigInteger.valueOf(5), BigDecimal.ONE));
            assertEquals(ExperienceUtils.getXPSpanForLevel(20),
                    ExperienceUtils.getXPWithinLevel(BigInteger.valueOf(20), BigDecimal.ONE));
            assertEquals(ExperienceUtils.getXPSpanForLevel(35),
                    ExperienceUtils.getXPWithinLevel(BigInteger.valueOf(35), BigDecimal.ONE));
        }

        @Test
        void intFloatOverload_matchesBigIntegerOverload() {
            assertEquals(ExperienceUtils.getXPWithinLevel(BigInteger.valueOf(10), BigDecimal.valueOf(0.5f)),
                    ExperienceUtils.getXPWithinLevel(10, 0.5f));
        }
    }

    @Nested
    class GetTotalXP {

        @Test
        void zeroProgress_equalsXPForLevelAtAllTierBoundaries() {
            for (final int level : new int[]{0, 1, 16, 17, 31, 32}) {
                assertEquals(ExperienceUtils.getXPForLevel(BigInteger.valueOf(level)),
                        ExperienceUtils.getTotalXP(BigInteger.valueOf(level), BigDecimal.ZERO), "level " + level);
            }
        }

        @Test
        void intFloatOverload_matchesBigIntegerOverload() {
            assertEquals(ExperienceUtils.getTotalXP(BigInteger.valueOf(15), BigDecimal.ZERO),
                    ExperienceUtils.getTotalXP(15, 0.0f));
        }
    }

    @Nested
    class FromTotalXP {

        @Test
        void zero_returnsLevel0WithZeroProgress() {
            final ExperienceUtils.LevelProgress result = ExperienceUtils.fromTotalXP(BigInteger.ZERO);
            assertEquals(BigInteger.ZERO, result.level());
            assertEquals(0, result.progress().compareTo(BigDecimal.ZERO));
        }

        @Test
        void tier1Boundary_returnsLevel16WithZeroProgress() {
            final ExperienceUtils.LevelProgress result = ExperienceUtils.fromTotalXP(BigInteger.valueOf(352));
            assertEquals(BigInteger.valueOf(16), result.level());
            assertEquals(0, result.progress().compareTo(BigDecimal.ZERO));
        }

        @Test
        void tier2Boundary_returnsLevel31WithZeroProgress() {
            final ExperienceUtils.LevelProgress result = ExperienceUtils.fromTotalXP(BigInteger.valueOf(1507));
            assertEquals(BigInteger.valueOf(31), result.level());
            assertEquals(0, result.progress().compareTo(BigDecimal.ZERO));
        }

        @Test
        void tier3Start_returnsLevel32WithZeroProgress() {
            final ExperienceUtils.LevelProgress result = ExperienceUtils.fromTotalXP(BigInteger.valueOf(1628));
            assertEquals(BigInteger.valueOf(32), result.level());
            assertEquals(0, result.progress().compareTo(BigDecimal.ZERO));
        }

        @Test
        void roundTrip_levelPreservedInTier1() {
            final BigInteger total = ExperienceUtils.getTotalXP(BigInteger.valueOf(10), BigDecimal.ZERO);
            assertEquals(BigInteger.valueOf(10), ExperienceUtils.fromTotalXP(total).level());
        }

        @Test
        void roundTrip_levelPreservedInTier2() {
            final BigInteger total = ExperienceUtils.getTotalXP(BigInteger.valueOf(25), BigDecimal.ZERO);
            assertEquals(BigInteger.valueOf(25), ExperienceUtils.fromTotalXP(total).level());
        }

        @Test
        void roundTrip_levelPreservedInTier3() {
            final BigInteger total = ExperienceUtils.getTotalXP(BigInteger.valueOf(50), BigDecimal.ZERO);
            assertEquals(BigInteger.valueOf(50), ExperienceUtils.fromTotalXP(total).level());
        }
    }
}
