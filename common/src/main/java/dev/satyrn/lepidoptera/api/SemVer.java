package dev.satyrn.lepidoptera.api;

import dev.satyrn.lepidoptera.LepidopteraAPI;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Immutable representation of a <a href="https://semver.org/">Semantic Versioning</a> identifier.
 *
 * <p>Supports the full {@code MAJOR.MINOR.PATCH[-preRelease][+metadata]} format.
 * Equality and ordering ignore build metadata, consistent with the SemVer specification.</p>
 *
 * @param major      the major version component
 * @param minor      the minor version component
 * @param patch      the patch version component
 * @param preRelease the pre-release label (empty string if absent)
 * @param metadata   the build metadata label (empty string if absent)
 */
@ApiStatus.AvailableSince("0.4.0+1.19.2")
public record SemVer(int major,
                     int minor,
                     int patch,
                     String preRelease,
                     String metadata) implements Comparable<SemVer> {
    /**
     * A zeroed-out sentinel version ({@code 0.0.0}) used as a safe "no version" default.
     */
    @SuppressWarnings("unused")
    public static final SemVer EMPTY = new SemVer(0, 0, 0, "", "");

    /**
     * Creates a version with the given major component and all other components set to zero/empty.
     *
     * @param major the major version number
     *
     * @return {@code MAJOR.0.0}
     */
    @SuppressWarnings("unused")
    public static SemVer create(int major) {
        return create(major, 0, 0, "", "");
    }

    /**
     * Creates a version with the given major and minor components.
     *
     * @param major the major version number
     * @param minor the minor version number
     *
     * @return {@code MAJOR.MINOR.0}
     */
    @SuppressWarnings("unused")
    public static SemVer create(int major, int minor) {
        return create(major, minor, 0, "", "");
    }

    /**
     * Creates a release version with no pre-release or metadata labels.
     *
     * @param major the major version number
     * @param minor the minor version number
     * @param patch the patch version number
     *
     * @return {@code MAJOR.MINOR.PATCH}
     */
    @SuppressWarnings("unused")
    public static SemVer create(int major, int minor, int patch) {
        return create(major, minor, patch, "", "");
    }

    /**
     * Creates a pre-release version with no build metadata.
     *
     * @param major      the major version number
     * @param minor      the minor version number
     * @param patch      the patch version number
     * @param preRelease the pre-release label (e.g. {@code "alpha"})
     *
     * @return {@code MAJOR.MINOR.PATCH-preRelease}
     */
    @SuppressWarnings("unused")
    public static SemVer create(int major, int minor, int patch, String preRelease) {
        return create(major, minor, patch, preRelease, "");
    }

    /**
     * Creates a fully-specified version.
     *
     * @param major      the major version number
     * @param minor      the minor version number
     * @param patch      the patch version number
     * @param preRelease the pre-release label (empty string for none)
     * @param metadata   the build metadata label (empty string for none)
     *
     * @return the constructed {@link SemVer}
     */
    public static SemVer create(int major, int minor, int patch, String preRelease, String metadata) {
        return new SemVer(major, minor, patch, preRelease, metadata);
    }

    /**
     * Parses a Semantic Versioning string, returning {@code null} if parsing fails.
     *
     * <p>Accepts the full {@code MAJOR.MINOR.PATCH[-preRelease][+metadata]} format.
     * Components beyond {@code PATCH} in the version number are ignored with a warning.
     * Returns {@code null} (and logs a warning) if the numeric components cannot be parsed.</p>
     *
     * @param semVer the version string to parse
     *
     * @return the parsed {@link SemVer}, or {@code null} on failure
     */
    public static @Nullable SemVer tryParse(String semVer) {
        int major = 0, minor = 0, patch = 0;
        String preRelease = "", metadata = "";
        final String[] splitMeta = semVer.split("\\+", 2);
        if (splitMeta.length > 1) {
            metadata = splitMeta[splitMeta.length - 1];
        }
        final String[] splitPreRelease = splitMeta[0].split("-", 2);
        if (splitPreRelease.length > 1) {
            preRelease = splitPreRelease[splitPreRelease.length - 1];
        }
        final String[] majMinPatch = splitPreRelease[0].split("\\.");
        if (majMinPatch.length > 0) {
            if (majMinPatch.length > 3) {
                LepidopteraAPI.warn("Values after the patch in SemVer identifier {} will be ignored.", semVer);
            }
            String majStr = majMinPatch[0].isBlank() ? "0" : majMinPatch[0];
            String minStr = majMinPatch.length < 2 || majMinPatch[1].isBlank() ? "0" : majMinPatch[1];
            String patchStr = majMinPatch.length < 3 || majMinPatch[2].isBlank() ? "0" : majMinPatch[2];

            try {
                major = Integer.parseInt(majStr);
                minor = Integer.parseInt(minStr);
                patch = Integer.parseInt(patchStr);
            } catch (NumberFormatException ex) {
                LepidopteraAPI.warn("Failed to parse semantic version identifier {}", semVer);
                return null;
            }
        }
        return new SemVer(major, minor, patch, preRelease, metadata);
    }

    /**
     * Returns {@code true} if {@code obj} is a {@link SemVer} with the same major, minor, patch,
     * and pre-release label (compared case-insensitively, ignoring leading/trailing whitespace).
     * Build metadata is not considered.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SemVer semVer) {
            return this.major == semVer.major &&
                    this.minor == semVer.minor &&
                    this.patch == semVer.patch &&
                    this.preRelease.trim().equalsIgnoreCase(semVer.preRelease.trim());
        }
        return false;
    }

    /**
     * Returns a hash code consistent with {@link #equals}: based on major, minor, patch,
     * and pre-release (build metadata excluded).
     */
    @Override
    public int hashCode() {
        return Objects.hash(major, minor, patch, preRelease);
    }

    /**
     * Returns the version as a string in {@code MAJOR.MINOR.PATCH[-preRelease][+metadata]} format.
     * Pre-release and metadata segments are omitted when blank.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder().append(this.major)
                .append(".")
                .append(this.minor)
                .append(".")
                .append(this.patch);
        if (!this.preRelease.isBlank()) {
            builder.append("-").append(this.preRelease);
        }
        if (!this.metadata.isBlank()) {
            builder.append("+").append(this.metadata);
        }
        return builder.toString();
    }

    /**
     * Compares this version to {@code other} by major, minor, then patch.
     * A version with a pre-release label is considered lower than the same version without one
     * (e.g. {@code 1.0.0-alpha} &lt; {@code 1.0.0}). Build metadata is ignored.
     */
    @Override
    public int compareTo(SemVer other) {
        if (this.major != other.major) {
            return Integer.compare(this.major, other.major);
        }
        if (this.minor != other.minor) {
            return Integer.compare(this.minor, other.minor);
        }
        if (this.patch != other.patch) {
            return Integer.compare(this.patch, other.patch);
        }
        if (!this.preRelease.trim().equalsIgnoreCase(other.preRelease.trim())) {
            if (this.preRelease.isBlank()) {
                return 1;
            } else if (other.preRelease.isBlank()) {
                return -1;
            }
        }
        return 0;
    }
}
