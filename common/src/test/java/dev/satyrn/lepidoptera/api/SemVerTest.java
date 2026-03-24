package dev.satyrn.lepidoptera.api;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SemVerTest {

    // -------------------------------------------------------------------------
    // tryParse - valid inputs
    // -------------------------------------------------------------------------

    @Nested
    class TryParse {
        @Test
        void fullVersion_parsesAllComponents() {
            SemVer v = SemVer.tryParse("1.2.3");
            assertNotNull(v);
            assertEquals(1, v.major());
            assertEquals(2, v.minor());
            assertEquals(3, v.patch());
            assertEquals("", v.preRelease());
            assertEquals("", v.metadata());
        }

        @Test
        void withPreRelease_parsesPreRelease() {
            SemVer v = SemVer.tryParse("1.2.3-alpha");
            assertNotNull(v);
            assertEquals("alpha", v.preRelease());
            assertEquals("", v.metadata());
        }

        @Test
        void withMetadata_parsesMetadata() {
            SemVer v = SemVer.tryParse("1.2.3+build.1");
            assertNotNull(v);
            assertEquals("", v.preRelease());
            assertEquals("build.1", v.metadata());
        }

        @Test
        void withPreReleaseAndMetadata_parsesBoth() {
            SemVer v = SemVer.tryParse("1.2.3-alpha+build.1");
            assertNotNull(v);
            assertEquals("alpha", v.preRelease());
            assertEquals("build.1", v.metadata());
        }

        @Test
        void missingPatch_defaultsToZero() {
            SemVer v = SemVer.tryParse("2.0");
            assertNotNull(v);
            assertEquals(2, v.major());
            assertEquals(0, v.minor());
            assertEquals(0, v.patch());
        }

        @Test
        void majorOnly_defaultsMinorAndPatchToZero() {
            SemVer v = SemVer.tryParse("1");
            assertNotNull(v);
            assertEquals(1, v.major());
            assertEquals(0, v.minor());
            assertEquals(0, v.patch());
        }

        @Test
        void nonNumericComponent_returnsNull() {
            assertNull(SemVer.tryParse("not.a.version"));
        }
    }

    // -------------------------------------------------------------------------
    // toString
    // -------------------------------------------------------------------------

    @Nested
    class ToString {
        @Test
        void basicVersion_roundTrips() {
            assertEquals("1.2.3", SemVer.tryParse("1.2.3").toString());
        }

        @Test
        void withPreRelease_includesDash() {
            assertEquals("1.2.3-alpha", SemVer.tryParse("1.2.3-alpha").toString());
        }

        @Test
        void withMetadata_includesPlus() {
            assertEquals("1.2.3+meta", SemVer.tryParse("1.2.3+meta").toString());
        }

        @Test
        void withBoth_roundTrips() {
            assertEquals("1.2.3-alpha+meta", SemVer.tryParse("1.2.3-alpha+meta").toString());
        }

        @Test
        void emptyConstant_producesZeroZeroZero() {
            assertEquals("0.0.0", SemVer.EMPTY.toString());
        }
    }

    // -------------------------------------------------------------------------
    // equals
    // -------------------------------------------------------------------------

    @Nested
    class Equals {
        @Test
        void sameVersion_isEqual() {
            assertEquals(SemVer.create(1, 2, 3), SemVer.create(1, 2, 3));
        }

        @Test
        void metadataIgnored_inEquality() {
            SemVer a = SemVer.tryParse("1.2.3+build.1");
            SemVer b = SemVer.tryParse("1.2.3+build.2");
            assertNotNull(a);
            assertNotNull(b);
            assertEquals(a, b);
        }

        @Test
        void differentPreRelease_notEqual() {
            assertNotEquals(SemVer.tryParse("1.2.3-alpha"), SemVer.tryParse("1.2.3-beta"));
        }

        @Test
        void releaseVsPreRelease_notEqual() {
            assertNotEquals(SemVer.tryParse("1.2.3"), SemVer.tryParse("1.2.3-alpha"));
        }
    }

    // -------------------------------------------------------------------------
    // compareTo
    // -------------------------------------------------------------------------

    @Nested
    class CompareTo {
        @Test
        void higherMajor_isGreater() {
            assertTrue(SemVer.create(2).compareTo(SemVer.create(1)) > 0);
        }

        @Test
        void higherMinor_isGreater() {
            assertTrue(SemVer.create(1, 2).compareTo(SemVer.create(1, 1)) > 0);
        }

        @Test
        void higherPatch_isGreater() {
            assertTrue(SemVer.create(1, 0, 2).compareTo(SemVer.create(1, 0, 1)) > 0);
        }

        @Test
        void release_isGreaterThanPreRelease() {
            // "1.2.3" > "1.2.3-alpha" per SemVer spec
            assertTrue(SemVer.create(1, 2, 3).compareTo(SemVer.create(1, 2, 3, "alpha")) > 0);
        }

        @Test
        void equalVersions_compareToZero() {
            assertEquals(0, SemVer.create(1, 2, 3).compareTo(SemVer.create(1, 2, 3)));
        }
    }

    // -------------------------------------------------------------------------
    // create overloads
    // -------------------------------------------------------------------------

    @Nested
    class CreateOverloads {
        @Test
        void majorOnly_setsMinorAndPatchToZero() {
            SemVer v = SemVer.create(3);
            assertEquals(3, v.major());
            assertEquals(0, v.minor());
            assertEquals(0, v.patch());
        }

        @Test
        void majorMinor_setsPatchToZero() {
            SemVer v = SemVer.create(1, 4);
            assertEquals(1, v.major());
            assertEquals(4, v.minor());
            assertEquals(0, v.patch());
        }

        @Test
        void majorMinorPatch_allSet() {
            SemVer v = SemVer.create(1, 2, 3);
            assertEquals(1, v.major());
            assertEquals(2, v.minor());
            assertEquals(3, v.patch());
        }

        @Test
        void withPreRelease_setsPreRelease() {
            SemVer v = SemVer.create(1, 0, 0, "rc.1");
            assertEquals("rc.1", v.preRelease());
        }
    }
}
