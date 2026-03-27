package dev.satyrn.lepidoptera.api;

import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.ApiStatus;

/**
 * Lifecycle interface for mods built on the Lepidoptera API.
 *
 * <p>Implement this interface on your mod's main entrypoint class and call
 * each phase method from the appropriate platform-specific hook to participate
 * in Lepidoptera's three-phase initialization sequence.</p>
 *
 * @since 0.4.1-alpha.1+1.19.2
 */
@ApiStatus.AvailableSince("0.4.1-alpha.1+1.19.2")
public interface LepidopteraMod {
    /**
     * Called during the pre-initialization phase, before the main init phase.
     *
     * <p>Use this phase for tasks that must complete before {@link #init()}, such as
     * registering capabilities or early configuration loading.</p>
     *
     * @since 0.4.1-alpha.1+1.19.2
     */
    @ApiStatus.AvailableSince("0.4.1-alpha.1+1.19.2")
    default void preInit() {

    }

    /**
     * Called during the main initialization phase.
     *
     * <p>The primary hook for registering blocks, items, entities, and other
     * game content.</p>
     *
     * @since 0.4.1-alpha.1+1.19.2
     */
    @ApiStatus.AvailableSince("0.4.1-alpha.1+1.19.2")
    default void init() {

    }

    /**
     * Called during the post-initialization phase, after {@link #init()} has completed.
     *
     * <p>Use this phase for tasks that depend on all mods having finished their main
     * initialization, such as cross-mod compatibility setup.</p>
     *
     * @since 0.4.1-alpha.1+1.19.2
     */
    @ApiStatus.AvailableSince("0.4.1-alpha.1+1.19.2")
    default void postInit() {

    }

    /**
     * Called when the Minecraft server has fully started.
     *
     * <p>Use this to start background tasks that require a running server, such as
     * config file watchers that need to dispatch work onto the server thread.</p>
     *
     * @param server the running server instance
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    default void serverStarted(MinecraftServer server) {

    }

    /**
     * Called when the Minecraft server is stopping.
     *
     * <p>Use this to stop any background tasks that were started in {@link #serverStarted}.</p>
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    default void serverStopped() {

    }

    /**
     * Called after server data packs and tags have been (re-)loaded, including on {@code /reload}.
     *
     * <p>Use this to rebuild any caches that depend on tag contents, such as entity type or
     * item tag registries. Not called on the client - only fires for the server-side tag load.</p>
     *
     * @param registryAccess the registry access for the current data load
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    default void onTagsLoaded(final RegistryAccess registryAccess) {

    }
}
