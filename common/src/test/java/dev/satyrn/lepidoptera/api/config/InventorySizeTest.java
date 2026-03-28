package dev.satyrn.lepidoptera.api.config;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventorySizeTest {

    @Nested
    class Construction {
        @Test
        void validDimensions_createsInstance() {
            final InventorySize s = new InventorySize(9, 6);
            assertEquals(9, s.width());
            assertEquals(6, s.height());
        }

        @Test
        void minValue_isAccepted() {
            final InventorySize s = new InventorySize(InventorySize.MIN_VALUE, InventorySize.MIN_VALUE);
            assertEquals(InventorySize.MIN_VALUE, s.width());
            assertEquals(InventorySize.MIN_VALUE, s.height());
        }

        @Test
        void widthBelowMin_throws() {
            assertThrows(IllegalArgumentException.class, () -> new InventorySize(0, 1));
        }

        @Test
        void heightBelowMin_throws() {
            assertThrows(IllegalArgumentException.class, () -> new InventorySize(1, 0));
        }

        @Test
        void negativeWidth_throws() {
            assertThrows(IllegalArgumentException.class, () -> new InventorySize(-5, 3));
        }

        @Test
        void negativeHeight_throws() {
            assertThrows(IllegalArgumentException.class, () -> new InventorySize(3, -1));
        }
    }

    @Nested
    class ToStringTests {
        @Test
        void producesCompactForm() {
            assertEquals("9x6", new InventorySize(9, 6).toString());
        }

        @Test
        void minValues_producesOneByOne() {
            assertEquals("1x1", new InventorySize(1, 1).toString());
        }

        @Test
        void largeValues_formatted() {
            assertEquals("27x27", new InventorySize(27, 27).toString());
        }
    }

    @Nested
    class ParseValid {
        @Test
        void roundTrip_isEqual() {
            final InventorySize original = new InventorySize(9, 6);
            assertEquals(original, InventorySize.parse(original.toString()));
        }

        @Test
        void simple_9x6() {
            final InventorySize s = InventorySize.parse("9x6");
            assertEquals(9, s.width());
            assertEquals(6, s.height());
        }

        @Test
        void minSize_1x1() {
            final InventorySize s = InventorySize.parse("1x1");
            assertEquals(1, s.width());
            assertEquals(1, s.height());
        }

        @Test
        void largeSize_27x27() {
            final InventorySize s = InventorySize.parse("27x27");
            assertEquals(27, s.width());
            assertEquals(27, s.height());
        }
    }

    @Nested
    class ParseInvalid {
        @Test
        void emptyString_throws() {
            assertThrows(IllegalArgumentException.class, () -> InventorySize.parse(""));
        }

        @Test
        void nullString_throws() {
            assertThrows(IllegalArgumentException.class, () -> InventorySize.parse(null));
        }

        @Test
        void noSeparator_throws() {
            assertThrows(IllegalArgumentException.class, () -> InventorySize.parse("96"));
        }

        @Test
        void missingHeight_throws() {
            assertThrows(IllegalArgumentException.class, () -> InventorySize.parse("9x"));
        }

        @Test
        void missingWidth_throws() {
            assertThrows(IllegalArgumentException.class, () -> InventorySize.parse("x6"));
        }

        @Test
        void nonNumericWidth_throws() {
            assertThrows(IllegalArgumentException.class, () -> InventorySize.parse("axb"));
        }

        @Test
        void zeroWidth_throws() {
            assertThrows(IllegalArgumentException.class, () -> InventorySize.parse("0x5"));
        }

        @Test
        void zeroHeight_throws() {
            assertThrows(IllegalArgumentException.class, () -> InventorySize.parse("5x0"));
        }

        @Test
        void negativeWidth_throws() {
            assertThrows(IllegalArgumentException.class, () -> InventorySize.parse("-1x5"));
        }
    }

    @Nested
    class Equality {
        @Test
        void sameValues_areEqual() {
            assertEquals(new InventorySize(9, 6), new InventorySize(9, 6));
        }

        @Test
        void differentWidth_notEqual() {
            assertNotEquals(new InventorySize(9, 6), new InventorySize(8, 6));
        }

        @Test
        void differentHeight_notEqual() {
            assertNotEquals(new InventorySize(9, 6), new InventorySize(9, 5));
        }

        @Test
        void sameValues_sameHashCode() {
            assertEquals(new InventorySize(9, 6).hashCode(), new InventorySize(9, 6).hashCode());
        }

        @Test
        void null_notEqual() {
            assertNotEquals(null, new InventorySize(9, 6));
        }

        @Test
        void differentType_notEqual() {
            assertNotEquals("9x6", new InventorySize(9, 6));
        }
    }
}
