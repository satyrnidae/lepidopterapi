package dev.satyrn.lepidoptera.api.compatibility;

import dev.architectury.utils.Env;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import static dev.architectury.platform.Platform.getEnvironment;

/**
 * Abstract base class for client-only third-party mod compatibility providers.
 *
 * <p>Extends {@link CompatibilityProvider} with client-side lifecycle hooks. Use this class
 * instead of {@link CompatibilityProvider} when your integration requires access to
 * client-only APIs (e.g. recipe viewers, rendering, GUI). Concrete subclasses that import
 * {@code net.minecraft.client.*} or other client-only APIs should be annotated with
 * {@code @Environment(EnvType.CLIENT)}.</p>
 *
 * <p>Register instances using {@link Compatibility#registerClient(String)}  or declare them
 * in the {@code client} field of {@link CompatibilityProviders @CompatibilityProviders} and
 * call {@link Compatibility#registerAll(Class)}. Client providers are silently skipped on a
 * dedicated server.</p>
 *
 * <p>Example:</p>
 * <pre>{@code
 * @Environment(EnvType.CLIENT)
 * public class JeiClientProvider extends ClientCompatibilityProvider {
 *     @Override
 *     public String getModId() { return "jei"; }
 *
 *     @Override
 *     public void onClientPostInit() {
 *         // register JEI recipe categories here
 *     }
 * }
 * }</pre>
 *
 * @since 1.0.1-SNAPSHOT.2+1.21.1
 */
@ApiStatus.AvailableSince("1.0.1-SNAPSHOT.2+1.21.1")
public abstract class ClientCompatibilityProvider extends CompatibilityProvider {

    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Constructs a new client compatibility provider. Logs a warning if instantiated on a
     * dedicated server — client providers should only be registered via
     * {@link Compatibility#registerClient(String)}, which skips registration server-side.
     *
     * @since 1.0.1-SNAPSHOT.2+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.2+1.21.1")
    protected ClientCompatibilityProvider() {
        if (getEnvironment() != Env.CLIENT) {
            LOGGER.warn("Client compatibility provider {} was instantiated on a dedicated server. "
                    + "Use Compatibility.registerClient() to register client providers.",
                    getClass().getName());
        }
    }

    /**
     * Called during the client pre-initialization phase. Override to perform early
     * client-side setup that must run before other mods initialize on the client.
     *
     * @since 1.0.1-SNAPSHOT.2+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.2+1.21.1")
    public void onClientPreInit() {
    }

    /**
     * Called during the client main initialization phase. Override to register
     * client-side content such as GUI extensions or recipe viewer integrations.
     *
     * @since 1.0.1-SNAPSHOT.2+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.2+1.21.1")
    public void onClientInit() {
    }

    /**
     * Called during the client post-initialization phase, after all mods have completed
     * their own client initialization. Override to finalize client-side integrations.
     *
     * @since 1.0.1-SNAPSHOT.2+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.2+1.21.1")
    public void onClientPostInit() {
    }
}
