package dev.satyrn.lepidoptera.api.client.config;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.ApiStatus;

/**
 * The active gizmo editing mode for a {@link TransformEntry}.
 *
 * @since 1.0.1-SNAPSHOT.4+1.21.1
 */
@ApiStatus.Internal
@Environment(EnvType.CLIENT)
enum GizmoMode {
    ROTATE, TRANSLATE, SCALE
}
