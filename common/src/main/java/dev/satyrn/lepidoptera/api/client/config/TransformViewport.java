package dev.satyrn.lepidoptera.api.client.config;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.satyrn.lepidoptera.api.config.TransformDisplayObject;
import dev.satyrn.lepidoptera.api.config.transform.Transformation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nullable;

// TODO: Rotation gizmo directional signs need to be flipped based on which side
//   of the gizmo the player is viewing it from.
// TODO: Items in viewport render in front of the ClothConfig save and exit buttons
//   Most likely, this means they need to be masked/hidden when outside the bounds
//   of the config scroll pane. Better to use a mask most likely.

/**
 * Handles all 3-D rendering and viewport interaction for {@link TransformEntry}.
 *
 * <p>Owns the orbit camera state, gizmo drag state, and all rendering helpers.
 * Working values ({@code rotation}, {@code offset}, {@code scaleHolder}) are
 * float arrays shared with the owning {@link TransformEntry}; this class
 * modifies them in-place and fires the {@code onChanged} callback afterward so
 * the entry can refresh its numeric {@link net.minecraft.client.gui.components.EditBox} fields.</p>
 *
 * @since 1.0.1-SNAPSHOT.3+1.21.1
 */
@ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
@ApiStatus.Internal
@Environment(EnvType.CLIENT)
final class TransformViewport {

    // TODO: Scale to the GUI Size option in Minecraft.
    //   Increased from 80 to 120 by default.
    /** Width and height of the viewport square, in pixels. */
    static final int SIZE = 120;

    private static final float GIZMO_LENGTH     = 0.45f;
    private static final int   GIZMO_HIT_RADIUS = 7;
    /** Base camera scale so the item fills the viewport proportionally regardless of SIZE. */
    private static final float BASE_ZOOM        = 38.0f / SIZE;
    private static final float ZOOM_SENSITIVITY = 0.15f;
    private static final float CAMERA_YAW       = 0.0f;
    /** Negative pitch so the camera looks from above (positive 3-D Y = world-up). */
    private static final float CAMERA_PITCH = -30.0f;
    private static final float ORBIT_HEIGHT = 0.5f;
    /** Degrees of orbit per pixel of drag. */
    private static final float ORBIT_SENSITIVITY = 0.4f;

    // Axis colors  (X = red, Y = blue, Z = green — Minecraft convention)
    private static final int COLOR_AXIS_X       = 0xFFFF4444;
    private static final int COLOR_AXIS_X_HOVER = 0xFFFF8888;
    private static final int COLOR_AXIS_Y       = 0xFF4444FF;
    private static final int COLOR_AXIS_Y_HOVER = 0xFF8888FF;
    private static final int COLOR_AXIS_Z       = 0xFF44CC44;
    private static final int COLOR_AXIS_Z_HOVER = 0xFF88FF88;

    private static final int COLOR_VIEWPORT_BG     = 0x44181818;
    private static final int COLOR_VIEWPORT_BORDER = 0xFF505050;

    /** Degrees of rotation change per pixel of drag. */
    private static final float ROT_DRAG_SENSITIVITY   = 1.0f;
    /** Block units of offset change per pixel of drag (offset is now in blocks, not 1/16-px). */
    private static final float OFF_DRAG_SENSITIVITY   = 0.5f / 16.0f;
    /** Scale change per pixel of drag. */
    private static final float SCALE_DRAG_SENSITIVITY = 0.01f;

    // =====================================================================
    // Fields
    // =====================================================================

    private final Transformation caps;
    private final TransformDisplayObject displayObject;

    /**
     * Shared float arrays written by both {@link TransformEntry} (edit boxes) and
     * this class (drag). Modifications are visible to both parties immediately
     * because Java arrays are reference types.
     */
    private final @Nullable float[] rotation;
    private final @Nullable float[] offset;
    /** Single-element wrapper that gives a primitive {@code scale} reference semantics. */
    private final float[] scaleHolder;

    /** Called whenever a drag modifies a value, so the entry can refresh its edit boxes. */
    private final Runnable onChanged;

    // -- Camera / zoom / pan state -----------------------------------------
    private float   cameraZoom    = 1.0f;
    private float   panX          = 0.0f;
    private float   panY          = 0.0f;

    // -- Orbit camera state -----------------------------------------------
    private float   orbitYaw      = CAMERA_YAW;
    private float   orbitPitch    = CAMERA_PITCH;
    private boolean orbitDragging = false;
    private double  orbitDragStartX;
    private double  orbitDragStartY;
    private float   orbitStartYaw;
    private float   orbitStartPitch;

    // -- Middle-mouse pan drag state ---------------------------------------
    private boolean middleDragging = false;
    private double  panDragStartX;
    private double  panDragStartY;
    private float   panStartX;
    private float   panStartY;

    // -- Gizmo drag state --------------------------------------------------
    private @Nullable Integer   dragAxisIndex = null;
    private @Nullable GizmoMode dragMode = null;
    private double dragStartMouseX;
    private double dragStartMouseY;
    private float  dragStartValue;
    /** Normalised screen-space direction of the dragged axis (for projected drag). */
    private float  dragAxisScreenDX   = 1f;
    private float  dragAxisScreenDY   = 0f;
    /** Sign multiplier for rotation drag: determined by which half of the ring was clicked. */
    private float  dragRotationSign   = 1f;

    // -- Screen position caches (written each frame, read in mouse handlers) --
    private float axisOriginScreenX;
    private float axisOriginScreenY;
    private final float[][] axisTipScreen = new float[3][2];
    /** 3 rings × 8 sample points × {sx, sy} — used for rotation-ring hit-testing. */
    private final float[][][] ringScreenPoints = new float[3][8][2];
    /** Pose matrix captured when drawing rotation rings — used to compute camera-facing sign. */
    private final Matrix4f lastRotatePoseMat = new Matrix4f();

    // =====================================================================
    // Constructor
    // =====================================================================

    @Contract(pure = true)
    TransformViewport(final Transformation caps,
                      final TransformDisplayObject displayObject,
                      final @Nullable float[] rotation,
                      final @Nullable float[] offset,
                      final float[] scaleHolder,
                      final Runnable onChanged) {
        this.caps         = caps;
        this.displayObject = displayObject;
        this.rotation     = rotation;
        this.offset       = offset;
        this.scaleHolder  = scaleHolder;
        this.onChanged    = onChanged;
    }

    // =====================================================================
    // Render
    // =====================================================================

    void render(final GuiGraphics graphics,
                final int vpX, final int vpY,
                final int mouseX, final int mouseY,
                final GizmoMode mode) {
        // Background + 1-px border
        graphics.fill(vpX,          vpY,          vpX + SIZE, vpY + SIZE,   COLOR_VIEWPORT_BG);
        graphics.fill(vpX,          vpY,          vpX + SIZE, vpY + 1,      COLOR_VIEWPORT_BORDER);
        graphics.fill(vpX,          vpY + SIZE-1, vpX + SIZE, vpY + SIZE,   COLOR_VIEWPORT_BORDER);
        graphics.fill(vpX,          vpY,          vpX + 1,    vpY + SIZE,   COLOR_VIEWPORT_BORDER);
        graphics.fill(vpX + SIZE-1, vpY,          vpX + SIZE, vpY + SIZE,   COLOR_VIEWPORT_BORDER);

        final Minecraft mc   = Minecraft.getInstance();
        final double    gs   = mc.getWindow().getGuiScale();
        final int       winH = mc.getWindow().getHeight();
        RenderSystem.enableScissor(
                (int)((vpX + 1) * gs),
                (int)(winH - (vpY + SIZE - 1) * gs),
                (int)((SIZE - 2) * gs),
                (int)((SIZE - 2) * gs));

        final var pose = graphics.pose();
        pose.pushPose();

        // Camera: translate to viewport center (+ pan), apply orbit, scale (Y negated to
        // flip screen-Y → world-Y), then shift so the orbit focus sits at screen center.
        final float cs = SIZE * BASE_ZOOM * cameraZoom;
        pose.translate(vpX + SIZE / 2.0f + panX, vpY + SIZE / 2.0f + panY, 100.0f);
        pose.mulPose(Axis.YP.rotationDegrees(orbitYaw));
        pose.mulPose(Axis.XP.rotationDegrees(orbitPitch));
        pose.scale(cs, -cs, cs);
        pose.translate(0.0f, -ORBIT_HEIGHT, 0.0f);

        renderReferenceCube(graphics, mc);
        renderItemAndGizmo(graphics, mc, mouseX, mouseY, mode);

        pose.popPose();
        RenderSystem.disableScissor();
    }

    // =====================================================================
    // Reference cube (grass block)
    // =====================================================================

    private void renderReferenceCube(final GuiGraphics graphics, final Minecraft mc) {
        final var pose = graphics.pose();
        pose.pushPose();
        // Magic number scale garbage
        pose.scale(1.75f, 1.75f, 1.75f);
        // A block naturally occupies 0–1 on each axis.  Translate so it sits at
        // x:[-½, ½], y:[-1, 0], z:[-½, ½] — top face flush with world y = 0.
        pose.translate(-0.5f, -1.0f, -0.5f);
        mc.getBlockRenderer().renderSingleBlock(
                Blocks.GRASS_BLOCK.defaultBlockState(),
                pose, graphics.bufferSource(),
                LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
        graphics.bufferSource().endBatch();
        pose.popPose();
    }

    // =====================================================================
    // Item + gizmo
    // =====================================================================

    private void renderItemAndGizmo(final GuiGraphics graphics, final Minecraft mc,
                                     final int mouseX, final int mouseY,
                                     final GizmoMode mode) {
        final var pose = graphics.pose();
        pose.pushPose();

        // Apply item transform in the same order as the live layer renderer:
        //   1. baked-in initial translation (from display object)
        //   2. configurable offset
        //   3. configurable scale
        //   4. baked-in initial rotation (from display object)
        //   5. configurable rotation axes
        final Vec3 initOff = displayObject.getInitialDisplayOffset();
        pose.translate(initOff.x, initOff.y, initOff.z);

        if (caps.offset() && offset != null) {
            pose.translate(offset[0], offset[1], offset[2]);
        }
        if (caps.scale()) {
            final float s = scaleHolder[0];
            pose.scale(s, s, s);
        }

        // Translate and scale gizmos are world-space (pre-rotation).
        pose.pushPose();
        scaleInvariant(pose, 1.75F, 1.75F, 1.75F);
        if (mode == GizmoMode.TRANSLATE || mode == GizmoMode.SCALE) {
            captureScreenPositions(pose.last().pose(), mode);
            final int hoverAxis = getHoveredAxis(mouseX, mouseY, mode);
            if (mode == GizmoMode.TRANSLATE) {
                for (int i = 0; i < 3; i++) {
                    addAxisArrow(graphics, pose, i, axisColor(i), axisHoverColor(i), hoverAxis == i);
                }
            } else {
                addScaleHandle(graphics, pose, hoverAxis == 0);
            }
            graphics.bufferSource().endBatch(RenderType.lines());
        }
        pose.popPose();

        if (caps.rotation() && rotation != null) {
            pose.mulPose(displayObject.calcConfiguredPose(rotation));
        }

        pose.mulPose(displayObject.getInitialRotation());
        // All items face away from the camera by default in the item renderer; correct for it.
        pose.mulPose(Axis.YP.rotation(Mth.PI));

        // Item preview
        final ItemStack stack = displayObject.getDisplayStack();
        if (!stack.isEmpty()) {
            mc.getItemRenderer().renderStatic(stack, displayObject.getDisplayContext(),
                    LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY,
                    pose, graphics.bufferSource(), null, 0);
            graphics.bufferSource().endBatch();
        }

        // Rotation gizmo is local-space (post-rotation): rings align with the item's current
        // axes.
        pose.pushPose();
        pose.mulPose(Axis.YP.rotation(Mth.PI).conjugate());
        pose.mulPose(new Quaternionf(displayObject.getInitialRotation()).conjugate());
        scaleInvariant(pose, 1.25F, 1.25F, 1.25F);
        if (mode == GizmoMode.ROTATE) {
            captureScreenPositions(pose.last().pose(), mode);
            final int hoverAxis = getHoveredAxis(mouseX, mouseY, mode);
            for (int i = 0; i < 3; i++) {
                addRotationRing(graphics, pose, i, axisColor(i), axisHoverColor(i), hoverAxis == i);
            }
            graphics.bufferSource().endBatch(RenderType.lines());
        }
        pose.popPose();

        pose.popPose();
    }

    // Scales an object relative to the static zoom (i.e. will not change size with cam zoom)
    private void scaleInvariant(PoseStack pose, float x, float y, float z) {
        var staticZoom = SIZE * BASE_ZOOM;
        var camZoom = staticZoom * cameraZoom;
        // Basically we don't want the gizmos to change size relative to vp while zooming
        pose.scale(1/camZoom, 1/-camZoom, 1/camZoom);
        pose.scale(staticZoom * x, -staticZoom * y, staticZoom * z);
    }

    // =====================================================================
    // Screen position capture
    // =====================================================================

    private void captureScreenPositions(final Matrix4f mat,
                                         final GizmoMode mode) {
        final Vector3f v = new Vector3f();
        mat.transformPosition(0f, 0f, 0f, v);
        axisOriginScreenX = v.x();
        axisOriginScreenY = v.y();

        if (mode == GizmoMode.TRANSLATE) {
            for (int i = 0; i < 3; i++) {
                final float dx = (i == 0) ? GIZMO_LENGTH : 0f;
                final float dy = (i == 1) ? GIZMO_LENGTH : 0f;
                final float dz = (i == 2) ? GIZMO_LENGTH : 0f;
                mat.transformPosition(dx, dy, dz, v);
                axisTipScreen[i][0] = v.x();
                axisTipScreen[i][1] = v.y();
            }
        } else if (mode == GizmoMode.ROTATE) {
            lastRotatePoseMat.set(mat);
            for (int i = 0; i < 3; i++) {
                for (int pt = 0; pt < 8; pt++) {
                    final float ca = (float) Math.cos(pt * Math.PI / 4) * GIZMO_LENGTH;
                    final float sa = (float) Math.sin(pt * Math.PI / 4) * GIZMO_LENGTH;
                    switch (i) {
                        case 0 -> v.set(0,  ca, sa);   // X ring lives in the YZ plane
                        case 1 -> v.set(ca, 0,  sa);   // Y ring lives in the XZ plane
                        default -> v.set(ca, sa, 0);   // Z ring lives in the XY plane
                    }
                    mat.transformPosition(v.x(), v.y(), v.z(), v);
                    ringScreenPoints[i][pt][0] = v.x();
                    ringScreenPoints[i][pt][1] = v.y();
                }
            }
        }
    }

    // =====================================================================
    // Gizmo drawing
    // =====================================================================

    private void addAxisArrow(final GuiGraphics g, final PoseStack ps,
                               final int axis, final int color, final int hoverColor,
                               final boolean hovered) {
        final int   c   = hovered ? hoverColor : color;
        final float a   = ((c >> 24) & 0xFF) / 255f;
        final float r   = ((c >> 16) & 0xFF) / 255f;
        final float gf  = ((c >> 8)  & 0xFF) / 255f;
        final float b   = (c & 0xFF) / 255f;
        final float len = hovered ? GIZMO_LENGTH * 1.15f : GIZMO_LENGTH;
        final float dx  = (axis == 0) ? 1f : 0f;
        final float dy  = (axis == 1) ? 1f : 0f;
        final float dz  = (axis == 2) ? 1f : 0f;
        final var   vc  = g.bufferSource().getBuffer(RenderType.lines());
        final var  last = ps.last();
        vc.addVertex(last, 0f, 0f, 0f)
          .setColor(r, gf, b, a).setNormal(last, dx, dy, dz);
        vc.addVertex(last, dx * len, dy * len, dz * len)
          .setColor(r, gf, b, a).setNormal(last, dx, dy, dz);
    }

    private void addRotationRing(final GuiGraphics g, final PoseStack ps,
                                  final int axis, final int color, final int hoverColor,
                                  final boolean hovered) {
        final int   c   = hovered ? hoverColor : color;
        final float a   = ((c >> 24) & 0xFF) / 255f;
        final float r   = ((c >> 16) & 0xFF) / 255f;
        final float gf  = ((c >> 8)  & 0xFF) / 255f;
        final float b   = (c & 0xFF) / 255f;
        final float len = hovered ? GIZMO_LENGTH * 1.15f : GIZMO_LENGTH;
        final var   vc  = g.bufferSource().getBuffer(RenderType.lines());
        final var  last = ps.last();

        for (int seg = 0; seg < 32; seg++) {
            final float a1 = (float)(seg       * 2 * Math.PI / 32);
            final float a2 = (float)((seg + 1) * 2 * Math.PI / 32);
            final float c1 = (float) Math.cos(a1) * len, s1 = (float) Math.sin(a1) * len;
            final float c2 = (float) Math.cos(a2) * len, s2 = (float) Math.sin(a2) * len;
            final float x1, y1, z1, x2, y2, z2, nx, ny, nz;
            switch (axis) {
                case 0  -> { x1=0;  y1=c1; z1=s1; x2=0;  y2=c2; z2=s2; nx=1; ny=0; nz=0; }
                case 1  -> { x1=c1; y1=0;  z1=s1; x2=c2; y2=0;  z2=s2; nx=0; ny=1; nz=0; }
                default -> { x1=c1; y1=s1; z1=0;  x2=c2; y2=s2; z2=0;  nx=0; ny=0; nz=1; }
            }
            vc.addVertex(last, x1, y1, z1).setColor(r, gf, b, a).setNormal(last, nx, ny, nz);
            vc.addVertex(last, x2, y2, z2).setColor(r, gf, b, a).setNormal(last, nx, ny, nz);
        }
    }

    /** Draws a small 3-axis cross at the origin for the SCALE mode handle. */
    private void addScaleHandle(final GuiGraphics g, final PoseStack ps, final boolean hovered) {
        final int   c   = hovered ? 0xFFFFCC44 : 0xFFCCAA22;
        final float a   = ((c >> 24) & 0xFF) / 255f;
        final float r   = ((c >> 16) & 0xFF) / 255f;
        final float gf  = ((c >> 8)  & 0xFF) / 255f;
        final float b   = (c & 0xFF) / 255f;
        final float hs  = GIZMO_LENGTH * 0.25f;
        final var   vc  = g.bufferSource().getBuffer(RenderType.lines());
        final var  last = ps.last();
        vc.addVertex(last, -hs, 0,   0  ).setColor(r, gf, b, a).setNormal(last, 1, 0, 0);
        vc.addVertex(last,  hs, 0,   0  ).setColor(r, gf, b, a).setNormal(last, 1, 0, 0);
        vc.addVertex(last,  0, -hs,  0  ).setColor(r, gf, b, a).setNormal(last, 0, 1, 0);
        vc.addVertex(last,  0,  hs,  0  ).setColor(r, gf, b, a).setNormal(last, 0, 1, 0);
        vc.addVertex(last,  0,  0,  -hs ).setColor(r, gf, b, a).setNormal(last, 0, 0, 1);
        vc.addVertex(last,  0,  0,   hs ).setColor(r, gf, b, a).setNormal(last, 0, 0, 1);
    }

    // =====================================================================
    // Input handling
    // =====================================================================

    /**
     * Handles a mouse click inside the viewport.
     * Button 0 = left (gizmo drag / orbit), button 2 = middle (pan).
     *
     * @return {@code true} if the event was consumed
     */
    boolean mouseClicked(final double mouseX, final double mouseY, final int button,
                          final int vpX, final int vpY,
                          final GizmoMode mode) {
        if (mouseX < vpX || mouseX >= vpX + SIZE || mouseY < vpY || mouseY >= vpY + SIZE) {
            return false;
        }
        if (button == 2) {
            middleDragging = true;
            panDragStartX  = mouseX;
            panDragStartY  = mouseY;
            panStartX      = panX;
            panStartY      = panY;
            return true;
        }
        if (button != 0) return false;
        final int axis = getHoveredAxis((int) mouseX, (int) mouseY, mode);
        if (axis >= 0) {
            dragAxisIndex   = axis;
            dragMode        = mode;
            dragStartMouseX = mouseX;
            dragStartMouseY = mouseY;
            dragStartValue  = currentDragValue(axis, mode);
            // In TRANSLATE mode, project drag along the axis's screen-space direction so
            // the handle moves correctly when the item is rotated.
            if (mode == GizmoMode.TRANSLATE) {
                final float adx = axisTipScreen[axis][0] - axisOriginScreenX;
                final float ady = axisTipScreen[axis][1] - axisOriginScreenY;
                final float len = (float) Math.sqrt(adx * adx + ady * ady);
                dragAxisScreenDX = (len > 0.001f) ? adx / len : 1f;
                dragAxisScreenDY = (len > 0.001f) ? ady / len : 0f;
            } else {
                // Rotation rings and scale handle: use plain horizontal drag.
                dragAxisScreenDX = 1f;
                dragAxisScreenDY = 0f;
            }
            if (mode == GizmoMode.ROTATE) {
                // "Wheel dragging": sign depends on which half of the ring was grabbed.
                // 2D cross product of (click→center) × dragDir gives the side of the
                // drag-axis line the click landed on.
                final float toCenterX = axisOriginScreenX - (float) mouseX;
                final float toCenterY = axisOriginScreenY - (float) mouseY;
                final float cross = toCenterX * dragAxisScreenDY - toCenterY * dragAxisScreenDX;
                final float crossSign = (cross >= 0f) ? 1f : -1f;

                // Camera-face sign: if the ring's normal points toward the camera (Z > 0 in
                // screen space) then the ring is viewed from its "back" face, which reverses
                // the perceived rotation direction.
                final Vector3f normal = new Vector3f(
                        axis == 0 ? 1f : 0f,
                        axis == 1 ? 1f : 0f,
                        axis == 2 ? 1f : 0f);
                lastRotatePoseMat.transformDirection(normal);
                final float cameraSign = (normal.z() >= 0f) ? -1f : 1f;

                dragRotationSign = crossSign * cameraSign * -1f;
            } else {
                dragRotationSign = 1f;
            }
            return true;
        }
        // Empty viewport space → start orbit drag.
        orbitDragging   = true;
        orbitDragStartX = mouseX;
        orbitDragStartY = mouseY;
        orbitStartYaw   = orbitYaw;
        orbitStartPitch = orbitPitch;
        return true;
    }

    boolean mouseDragged(final double mouseX, final double mouseY, final int button) {
        if (button == 2 && middleDragging) {
            panX = panStartX + (float)(mouseX - panDragStartX);
            panY = panStartY + (float)(mouseY - panDragStartY);
            return true;
        }
        if (button != 0) return false;
        if (dragAxisIndex != null && dragMode != null) {
            final float proj = (float)(
                    (mouseX - dragStartMouseX) * dragAxisScreenDX
                    + (mouseY - dragStartMouseY) * dragAxisScreenDY);
            applyDrag(dragAxisIndex, proj, dragMode);
            return true;
        }
        if (orbitDragging) {
            orbitYaw   = orbitStartYaw + (float)(mouseX - orbitDragStartX) * ORBIT_SENSITIVITY;
            orbitPitch = Math.clamp(orbitStartPitch - (float) (mouseY - orbitDragStartY) * ORBIT_SENSITIVITY, -89f,
                    89f);
            return true;
        }
        return false;
    }

    boolean mouseScrolled(final double mouseX, final double mouseY,
                           final int vpX, final int vpY,
                           final double scrollY) {
        if (mouseX < vpX || mouseX >= vpX + SIZE || mouseY < vpY || mouseY >= vpY + SIZE) {
            return false;
        }
        cameraZoom = Math.max(0.1f, cameraZoom + (float) scrollY * ZOOM_SENSITIVITY);
        return true;
    }

    void mouseReleased() {
        dragAxisIndex    = null;
        dragMode         = null;
        dragRotationSign = 1f;
        orbitDragging    = false;
        middleDragging   = false;
    }

    // =====================================================================
    // Drag helpers
    // =====================================================================

    private float currentDragValue(final int axisIdx, final GizmoMode mode) {
        return switch (mode) {
            case ROTATE    -> (rotation != null) ? rotation[axisIdx] : 0f;
            case TRANSLATE -> (offset   != null) ? offset[axisIdx]   : 0f;
            case SCALE     -> scaleHolder[0];
        };
    }

    private void applyDrag(final int axisIdx, final float totalDeltaPx,
                            final GizmoMode mode) {
        switch (mode) {
            case ROTATE -> {
                if (rotation != null) {
                    final float raw = dragStartValue + totalDeltaPx * ROT_DRAG_SENSITIVITY * dragRotationSign;
                    rotation[axisIdx] = ((raw % 360f) + 360f) % 360f;
                    onChanged.run();
                }
            }
            case TRANSLATE -> {
                if (offset != null) {
                    offset[axisIdx] = dragStartValue + totalDeltaPx * OFF_DRAG_SENSITIVITY;
                    onChanged.run();
                }
            }
            case SCALE -> {
                scaleHolder[0] = Math.max(0.01f, dragStartValue + totalDeltaPx * SCALE_DRAG_SENSITIVITY);
                onChanged.run();
            }
        }
    }

    // =====================================================================
    // Hit-testing
    // =====================================================================

    private int getHoveredAxis(final int mx, final int my,
                                final GizmoMode mode) {
        return switch (mode) {
            case ROTATE -> {
                for (int i = 0; i < 3; i++) {
                    for (int pt = 0; pt < 8; pt++) {
                        final float dx = mx - ringScreenPoints[i][pt][0];
                        final float dy = my - ringScreenPoints[i][pt][1];
                        if (dx * dx + dy * dy <= GIZMO_HIT_RADIUS * GIZMO_HIT_RADIUS) yield i;
                    }
                }
                yield -1;
            }
            case TRANSLATE -> {
                for (int i = 0; i < 3; i++) {
                    final float dx = mx - axisTipScreen[i][0];
                    final float dy = my - axisTipScreen[i][1];
                    if (dx * dx + dy * dy <= GIZMO_HIT_RADIUS * GIZMO_HIT_RADIUS) yield i;
                }
                yield -1;
            }
            case SCALE -> {
                final float dx = mx - axisOriginScreenX;
                final float dy = my - axisOriginScreenY;
                yield (dx * dx + dy * dy <= (GIZMO_HIT_RADIUS * 2) * (GIZMO_HIT_RADIUS * 2)) ? 0 : -1;
            }
        };
    }

    // =====================================================================
    // Colour helpers
    // =====================================================================

    private static int axisColor(final int axis) {
        return switch (axis) {
            case 0  -> COLOR_AXIS_X;
            case 1  -> COLOR_AXIS_Y;
            default -> COLOR_AXIS_Z;
        };
    }

    private static int axisHoverColor(final int axis) {
        return switch (axis) {
            case 0  -> COLOR_AXIS_X_HOVER;
            case 1  -> COLOR_AXIS_Y_HOVER;
            default -> COLOR_AXIS_Z_HOVER;
        };
    }
}
