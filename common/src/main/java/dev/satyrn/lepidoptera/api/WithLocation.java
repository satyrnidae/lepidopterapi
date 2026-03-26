package dev.satyrn.lepidoptera.api;

import dev.satyrn.lepidoptera.api.annotations.Api;
import net.minecraft.resources.ResourceLocation;

/**
 * Provides a {@link ResourceLocation} identity for data-gen providers and other
 * components that need a stable locator string for logging or registration.
 *
 * @since 1.0.0-SNAPSHOT.1+1.21.1
 */
@Api("1.0.0-SNAPSHOT.1+1.21.1")
public interface WithLocation {
    /**
     * Returns the {@link ResourceLocation} that identifies this component.
     *
     * @return the component's resource location
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @Api("1.0.0-SNAPSHOT.1+1.21.1")
    ResourceLocation location();
}
