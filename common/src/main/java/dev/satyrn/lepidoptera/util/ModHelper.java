package dev.satyrn.lepidoptera.util;

import dev.satyrn.lepidoptera.LepidopteraAPI;
import dev.satyrn.lepidoptera.annotations.Api;
import dev.satyrn.lepidoptera.annotations.ModMeta;
import dev.satyrn.lepidoptera.api.SemVer;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.util.StackLocatorUtil;

import javax.annotation.Nullable;
import java.util.Objects;

@Api
public final class ModHelper {
    private ModHelper() {
        NotInitializable.staticClass(ModHelper.class);
    }

    public static String friendlyName() {
        return friendlyName(StackLocatorUtil.getCallerClass(2));
    }

    public static String friendlyName(final Class<?> modClass) {
        final @Nullable ModMeta meta = findMetadata(modClass);
        if (meta == null) {
            return modClass.getName();
        }
        return friendlyName(meta);
    }

    public static String friendlyName(final ModMeta meta) {
        final StringBuilder modName = new StringBuilder();
        if (!meta.name().isBlank()) {
            modName.append(meta.name());
        } else {
            modName.append(meta.value());
        }
        final @Nullable SemVer semVer = version(meta);
        if (semVer != null) {
            modName.append(" v").append(semVer);
        }
        return modName.toString();
    }

    @SuppressWarnings("unused")
    public static String modId() {
        return modId(StackLocatorUtil.getCallerClass(2));
    }

    public static String modId(final Class<?> modClass) {
        return Objects.requireNonNull(findModId(modClass));
    }

    public static String name() {
        return name(StackLocatorUtil.getCallerClass(2));
    }

    private static String name(final Class<?> modClass) {
        return Objects.requireNonNull(findName(modClass));
    }

    @SuppressWarnings("unused")
    public static ResourceLocation newResource(String path) {
        return newResource(StackLocatorUtil.getCallerClass(2), path);
    }

    public static ResourceLocation newResource(Class<?> modClass, String path) throws NullPointerException {
        final @Nullable String modId = modId(modClass);
        return newResource(modId, path);
    }

    public static ResourceLocation newResource(String modId, String path) throws NullPointerException {
        LepidopteraAPI.debug("Registering new resource location {}:{}", modId, path);
        return Objects.requireNonNull(ResourceLocation.tryBuild(modId, path));
    }

    @Api
    public static SemVer version() {
        return version(StackLocatorUtil.getCallerClass(2));
    }

    @SuppressWarnings("unused")
    public static ModMeta metadata() {
        return metadata(StackLocatorUtil.getCallerClass(2));
    }

    public static ModMeta metadata(final Class<?> modClass) {
        return Objects.requireNonNull(findMetadata(modClass));
    }

    public static SemVer version(final Class<?> modClass) {
        return Objects.requireNonNull(findVersion(modClass));
    }

    private static @Nullable String findModId(final Class<?> modClass) {
        final @Nullable ModMeta modId = findMetadata(modClass);
        return modId != null ? modId.value() : null;
    }

    private static @Nullable String findName(final Class<?> modClass) {
        final @Nullable ModMeta modId = findMetadata(modClass);
        return modId != null ? modId.name() : null;
    }

    private static @Nullable SemVer findVersion(final Class<?> modClass) {
        final @Nullable ModMeta modId = findMetadata(modClass);
        return modId != null ? version(modId) : null;
    }

    public static @Nullable SemVer version(final ModMeta modMeta) {
        return SemVer.tryParse(modMeta.semVer());
    }

    private static @Nullable ModMeta findMetadata(final Class<?> modClass) {
        return modClass.getAnnotation(ModMeta.class);
    }
}
