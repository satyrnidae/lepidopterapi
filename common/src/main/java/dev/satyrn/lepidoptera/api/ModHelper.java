package dev.satyrn.lepidoptera.api;

import dev.satyrn.lepidoptera.LepidopteraAPI;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.util.StackLocatorUtil;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Static helpers for reading {@link ModMeta} information from a mod's annotated class.
 *
 * <p>All no-argument overloads use stack introspection to determine the caller's class
 * automatically; the class-based overloads are preferred when the caller class is already
 * known to avoid the overhead of stack walking.</p>
 */
@ApiStatus.AvailableSince("0.4.0+1.19.2")
public final class ModHelper {

    @Contract("-> fail")
    private ModHelper() {
        NotInitializable.staticClass(this);
    }

    /**
     * Returns a friendly display string (name + version) for the mod of the calling class.
     *
     * @return the friendly name, e.g. {@code "My Mod v1.2.3"}
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public static String friendlyName() {
        return friendlyName(StackLocatorUtil.getCallerClass(2));
    }

    /**
     * Returns a friendly display string (name + version) derived from {@code modClass}'s
     * {@link ModMeta} annotation.
     *
     * @param modClass the class annotated with {@link ModMeta}
     *
     * @return the friendly name, or the class's canonical name if no annotation is present
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public static String friendlyName(final Class<?> modClass) {
        final @Nullable ModMeta meta = findMetadata(modClass);
        if (meta == null) {
            return modClass.getName();
        }
        return friendlyName(meta);
    }

    /**
     * Returns a friendly display string built from the supplied {@link ModMeta}.
     *
     * <p>Format: {@code "<name> v<semVer>"} where {@code name} falls back to
     * {@link ModMeta#value()} when {@link ModMeta#name()} is blank, and the version
     * suffix is omitted when the version string cannot be parsed.</p>
     *
     * @param meta the metadata annotation
     *
     * @return the friendly name string
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
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

    /**
     * Returns the mod ID of the calling class's {@link ModMeta} annotation.
     *
     * @return the mod ID (i.e. {@link ModMeta#value()})
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @SuppressWarnings("unused")
    public static String modId() {
        return modId(StackLocatorUtil.getCallerClass(2));
    }

    /**
     * Returns the mod ID from the {@link ModMeta} annotation on {@code modClass}.
     *
     * @param modClass the class annotated with {@link ModMeta}
     *
     * @return the mod ID
     *
     * @throws NullPointerException if {@code modClass} has no {@link ModMeta} annotation
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public static String modId(final Class<?> modClass) {
        return Objects.requireNonNull(findModId(modClass));
    }

    /**
     * Returns the mod display name of the calling class's {@link ModMeta} annotation.
     *
     * @return the value of {@link ModMeta#name()}
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public static String name() {
        return name(StackLocatorUtil.getCallerClass(2));
    }

    /**
     * Returns the mod display name from the {@link ModMeta} annotation on {@code modClass}.
     *
     * @param modClass the class annotated with {@link ModMeta}
     *
     * @return the value of {@link ModMeta#name()}
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public static String name(final Class<?> modClass) {
        return Objects.requireNonNull(findName(modClass));
    }

    /**
     * Creates a {@link ResourceLocation} scoped to the calling class's mod ID.
     *
     * @param path the resource path
     *
     * @return a {@link ResourceLocation} with the mod's namespace and the given path
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @SuppressWarnings("unused")
    public static ResourceLocation resource(String path) {
        return resource(StackLocatorUtil.getCallerClass(2), path);
    }

    /**
     * Creates a {@link ResourceLocation} scoped to the mod ID of {@code modClass}.
     *
     * @param modClass the class annotated with {@link ModMeta}
     * @param path     the resource path
     *
     * @return a {@link ResourceLocation} with the mod's namespace and the given path
     *
     * @throws NullPointerException if {@code modClass} has no {@link ModMeta} annotation
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public static ResourceLocation resource(Class<?> modClass, String path) throws NullPointerException {
        final @Nullable String modId = modId(modClass);
        return resource(modId, path);
    }

    /**
     * Creates a {@link ResourceLocation} scoped to the mod ID in {@code meta}.
     *
     * @param meta the metadata annotation supplying the namespace
     * @param path the resource path
     *
     * @return a {@link ResourceLocation} with the mod's namespace and the given path
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public static ResourceLocation resource(ModMeta meta, String path) {
        final @Nullable String modId = meta.value();
        return resource(modId, path);
    }

    /**
     * Creates a {@link ResourceLocation} from an explicit mod ID and path.
     *
     * @param modId the namespace (mod ID)
     * @param path  the resource path
     *
     * @return the constructed {@link ResourceLocation}
     *
     * @throws NullPointerException if {@code modId} or {@code path} produce an invalid location
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public static ResourceLocation resource(String modId, String path) throws NullPointerException {
        LepidopteraAPI.debug("Registering new resource location {}:{}", modId, path);
        return Objects.requireNonNull(ResourceLocation.tryBuild(modId, path));
    }

    /**
     * Returns the {@link SemVer} of the calling class's mod.
     *
     * @return the parsed semantic version
     *
     * @throws NullPointerException if the annotation is absent or the version cannot be parsed
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public static SemVer version() {
        return version(StackLocatorUtil.getCallerClass(2));
    }

    /**
     * Returns the {@link ModMeta} annotation of the calling class's mod.
     *
     * @return the annotation
     *
     * @throws NullPointerException if the calling class has no {@link ModMeta} annotation
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @SuppressWarnings("unused")
    public static ModMeta metadata() {
        return metadata(StackLocatorUtil.getCallerClass(2));
    }

    /**
     * Returns the {@link ModMeta} annotation present on {@code modClass}.
     *
     * @param modClass the annotated class
     *
     * @return the annotation
     *
     * @throws NullPointerException if the class has no {@link ModMeta} annotation
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public static ModMeta metadata(final Class<?> modClass) {
        return Objects.requireNonNull(findMetadata(modClass));
    }

    /**
     * Returns the {@link SemVer} parsed from {@code modClass}'s {@link ModMeta} annotation.
     *
     * @param modClass the class annotated with {@link ModMeta}
     *
     * @return the parsed version
     *
     * @throws NullPointerException if the annotation is absent or the version cannot be parsed
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
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

    /**
     * Parses the version from the given {@link ModMeta}.
     *
     * @param modMeta the metadata annotation
     *
     * @return the parsed {@link SemVer}, or {@code null} if {@link ModMeta#semVer()} is unparseable
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public static @Nullable SemVer version(final ModMeta modMeta) {
        return SemVer.tryParse(modMeta.semVer());
    }

    private static @Nullable ModMeta findMetadata(final Class<?> modClass) {
        return modClass.getAnnotation(ModMeta.class);
    }
}
