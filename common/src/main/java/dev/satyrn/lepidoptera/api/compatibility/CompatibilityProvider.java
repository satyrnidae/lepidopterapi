package dev.satyrn.lepidoptera.api.compatibility;

import dev.architectury.platform.Platform;
import org.jetbrains.annotations.ApiStatus;

/**
 * Abstract base class for optional third-party mod compatibility providers.
 *
 * <p>Subclass this to implement an integration with another mod that may or may not be
 * present at runtime. Register instances using {@link Compatibility#register(String)},
 * passing the fully-qualified class name as a string, to ensure graceful degradation
 * when the target mod is not installed  the class will not be loaded at all if it
 * cannot be found.</p>
 *
 * <p>Override {@link #onPreInit()}, {@link #onInit()}, and/or {@link #onPostInit()} to
 * run integration code at the appropriate phase of your mod's
 * {@link dev.satyrn.lepidoptera.api.LepidopteraMod} lifecycle. These hooks are only
 * invoked when the target mod reported by {@link #getModId()} is loaded.</p>
 *
 * <p>Example implementation:</p>
 * <pre>{@code
 * public class JeiCompatProvider extends CompatibilityProvider {
 *     @Override
 *     public String getModId() { return "jei"; }
 *
 *     @Override
 *     public void onPostInit() {
 *         // register JEI recipe categories here
 *     }
 * }
 * }</pre>
 *
 * <p>Then in your mod's {@code preInit()}:</p>
 * <pre>{@code
 * Compatibility.register("com.example.mymod.compat.JeiCompatProvider");
 * Compatibility.preInit();
 * }</pre>
 *
 * <p>If your integration has client-only code, guard it with
 * {@code Platform.getEnvironment() == Env.CLIENT} or use Architectury's
 * {@code @Environment} annotation on the relevant methods.</p>
 *
 * @since 1.0.0-SNAPSHOT.1+1.21.1
 */
@ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
public abstract class CompatibilityProvider implements Provider {

    /**
     * Returns the mod ID of the mod this provider integrates with.
     * {@link Compatibility#register(String)} will discard this provider if the mod is
     * not loaded.
     *
     * @return the target mod ID
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public abstract String getModId();

    /**
     * Returns {@code true} if the target mod is currently loaded.
     *
     * @return {@code true} if {@link #getModId()} identifies a loaded mod
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public boolean isModLoaded() {
        return Platform.isModLoaded(getModId());
    }

    /**
     * Called during your mod's pre-initialization phase (before items and recipes are
     * registered). Override to perform early setup that must run before other mods
     * initialize.
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public void onPreInit() {}

    /**
     * Called during your mod's main initialization phase. Override to register cross-mod
     * content such as item registrations or tag bindings that depend on the target mod.
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public void onInit() {}

    /**
     * Called during your mod's post-initialization phase, after all mods have
     * completed their own initialization. Override to finalize integrations that require
     * the target mod to have fully registered its content.
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public void onPostInit() {}
}
