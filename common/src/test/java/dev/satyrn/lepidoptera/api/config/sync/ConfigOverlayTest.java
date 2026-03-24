package dev.satyrn.lepidoptera.api.config.sync;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigOverlayTest {

    private ConfigOverlay<String> overlay;

    @BeforeEach
    void setUp() {
        overlay = new ConfigOverlay<>();
    }

    @Nested
    class InitialState {
        @Test
        void get_returnsEmpty_whenNeverSet() {
            assertTrue(overlay.get().isEmpty());
        }
    }

    @Nested
    class AfterSet {
        @Test
        void get_returnsValue_afterSet() {
            overlay.set("hello");
            assertEquals("hello", overlay.get().orElseThrow());
        }

        @Test
        void get_returnsLatestValue_afterMultipleSets() {
            overlay.set("first");
            overlay.set("second");
            assertEquals("second", overlay.get().orElseThrow());
        }
    }

    @Nested
    class AfterClear {
        @Test
        void get_returnsEmpty_afterClear() {
            overlay.set("value");
            overlay.clear();
            assertTrue(overlay.get().isEmpty());
        }

        @Test
        void get_returnsEmpty_afterSetThenClear() {
            overlay.set("alpha");
            overlay.clear();
            assertTrue(overlay.get().isEmpty());
        }
    }
}