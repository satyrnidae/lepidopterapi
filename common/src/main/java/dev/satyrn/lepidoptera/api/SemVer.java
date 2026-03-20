package dev.satyrn.lepidoptera.api;

import dev.satyrn.lepidoptera.LepidopteraAPI;
import dev.satyrn.lepidoptera.annotations.Api;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;

@Api
public record SemVer (int major, int minor, int patch, String preRelease, String metadata) implements Comparable<SemVer> {
    @SuppressWarnings("unused")
    public static final SemVer EMPTY = new SemVer(0, 0, 0, "", "");

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SemVer semVer) {
            return this.major == semVer.major && this.minor == semVer.minor && this.patch == semVer.patch && this.preRelease.trim().equalsIgnoreCase(semVer.preRelease.trim());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor, patch, preRelease, metadata);
    }

    @Override
    public @NotNull String toString() {
        StringBuilder builder = new StringBuilder().append(this.major).append(".").append(this.minor).append(".").append(this.patch);
        if (!this.preRelease.isBlank()) {
            builder.append("-").append(this.preRelease);
        }
        if (!this.metadata.isBlank()) {
            builder.append("+").append(this.metadata);
        }
        return builder.toString();
    }

    @Override
    public int compareTo(@NotNull SemVer other) {
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

    @SuppressWarnings("unused")
    public static SemVer create(int major) {
        return create(major, 0, 0, "", "");
    }

    @SuppressWarnings("unused")
    public static SemVer create(int major, int minor) {
        return create(major, minor, 0, "", "");
    }

    @SuppressWarnings("unused")
    public static SemVer create(int major, int minor, int patch) {
        return create(major, minor, patch, "", "");
    }

    @SuppressWarnings("unused")
    public static SemVer create(int major, int minor, int patch, String preRelease) {
        return create(major, minor, patch, preRelease, "");
    }

    public static SemVer create(int major, int minor, int patch, String preRelease, String metadata) {
        return new SemVer(major, minor, patch, preRelease, metadata);
    }

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
}
