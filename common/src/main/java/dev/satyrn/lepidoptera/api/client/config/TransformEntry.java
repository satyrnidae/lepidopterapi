package dev.satyrn.lepidoptera.api.client.config;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.satyrn.lepidoptera.LepidopteraAPI;
import dev.satyrn.lepidoptera.api.config.TransformDisplayObject;
import dev.satyrn.lepidoptera.api.config.TransformField;
import dev.satyrn.lepidoptera.api.config.transform.Offset;
import dev.satyrn.lepidoptera.api.config.transform.Rotation;
import dev.satyrn.lepidoptera.api.config.transform.Transformation;
import dev.satyrn.lepidoptera.api.lang.T9n;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.gui.registry.api.GuiProvider;
import me.shedaniel.clothconfig2.gui.ClothConfigScreen;
import me.shedaniel.clothconfig2.gui.entries.TooltipListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Supplier;


/**
 * A Cloth Config GUI entry for {@link Transformation @Transformation}-typed config fields.
 *
 * <p>Renders a live 3D viewport showing the configured item with its current transform
 * applied, axis-gizmo drag handles for interactive editing, mode-toggle tabs
 * (Rotate / Translate / Scale), and numeric {@link EditBox} fallback fields.</p>
 *
 * <p>The widget is generic over all six {@code @Transformation}-annotated types
 * ({@link dev.satyrn.lepidoptera.api.config.transform.Rotation Rotation},
 * {@link Offset Offset},
 * {@link dev.satyrn.lepidoptera.api.config.transform.RotationScale RotationScale},
 * {@link dev.satyrn.lepidoptera.api.config.transform.OffsetScale OffsetScale},
 * {@link dev.satyrn.lepidoptera.api.config.transform.RotationOffset RotationOffset},
 * {@link dev.satyrn.lepidoptera.api.config.transform.RotationOffsetScale RotationOffsetScale}).
 * The active controls are determined from the {@code @Transformation} flags on the field type.</p>
 *
 * <p>Register the static {@link #TYPE_PROVIDER} in your client initializer:</p>
 * <pre>{@code
 * AutoConfig.getGuiRegistry(MyConfig.class)
 *     .registerPredicateProvider(
 *         TransformEntry.TYPE_PROVIDER,
 *         field -> field.getType().isAnnotationPresent(Transformation.class)
 *                && field.isAnnotationPresent(TransformField.class));
 * }</pre>
 *
 * @since 1.0.1-SNAPSHOT.3+1.21.1
 */
@ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
@Environment(EnvType.CLIENT)
public final class TransformEntry extends TooltipListEntry<Object> {

    // =========================================================================
    // Constants
    // =========================================================================

    private static final int PADDING       = 4;
    private static final int LABEL_HEIGHT  = 10;
    private static final int VIEWPORT_SIZE = TransformViewport.SIZE;
    private static final int TAB_HEIGHT    = 14;
    private static final int FIELD_HEIGHT  = 18;
    private static final int FIELD_GAP     = 2;
    private static final int FIELDS_WIDTH  = 80;
    private static final int LABEL_WIDTH   = 12; // "X:" width
    private static final int INPUT_WIDTH   = FIELDS_WIDTH - LABEL_WIDTH - PADDING;

    private static final ResourceLocation SPRITE_TAB_ACTIVE   = ResourceLocation.withDefaultNamespace("widget/button_disabled");
    private static final ResourceLocation SPRITE_TAB_HOVERED  = ResourceLocation.withDefaultNamespace("widget/button_highlighted");
    private static final ResourceLocation SPRITE_TAB_INACTIVE = ResourceLocation.withDefaultNamespace("widget/button");
    private static final int COLOR_TAB_TEXT      = 0xFFFFFFFF;
    private static final int COLOR_TAB_TEXT_DIM  = 0xFF888888;
    private static final int COLOR_LABEL         = 0xFFFFFFFF;
    private static final int COLOR_AXIS_LABEL_X  = 0xFFFF7777;
    private static final int COLOR_AXIS_LABEL_Y  = 0xFF7777FF;
    private static final int COLOR_AXIS_LABEL_Z  = 0xFF77DD77;

    // =========================================================================
    // TYPE_PROVIDER
    // =========================================================================

    /**
     * A Cloth Config {@link GuiProvider} that matches any {@link Transformation @Transformation}
     * -typed field also annotated with {@link TransformField} and binds it to this entry type.
     *
     * <p>Pass this to {@code GuiRegistry.registerPredicateProvider} once in your client
     * initializer (see class-level Javadoc).</p>
     *
     * @since 1.0.1-SNAPSHOT.3+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    @ApiStatus.Internal
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static final GuiProvider TYPE_PROVIDER = (i18n, field, config, defaults, guiProvider) -> {
        final TransformField annotation = field.getAnnotation(TransformField.class);
        final Transformation caps = field.getType().getAnnotation(Transformation.class);

        TransformDisplayObject dispObj;
        try {
            dispObj = (TransformDisplayObject) annotation.displayObject()
                    .getDeclaredConstructor().newInstance();
        } catch (final Exception e) {
            LepidopteraAPI.error("TransformEntry: failed to instantiate displayObject {}: {}",
                    annotation.displayObject().getName(), e.getMessage());
            dispObj = () -> ItemStack.EMPTY;
        }

        final @Nullable Object currentVal = readField(field, config, null);
        final @Nullable Object defaultVal = readField(field, defaults, currentVal);

        // Build tooltip supplier from @ConfigEntry.Gui.Tooltip if present
        final ConfigEntry.Gui.Tooltip tooltipAnnotation = field.getAnnotation(ConfigEntry.Gui.Tooltip.class);
        final Supplier<Optional<Component[]>> tooltipSupplier;
        if (tooltipAnnotation != null) {
            final Component[] lines = new Component[tooltipAnnotation.count()];
            for (int k = 0; k < lines.length; k++) {
                lines[k] = Component.translatable(i18n + ".@Tooltip[" + k + "]");
            }
            tooltipSupplier = () -> Optional.of(lines);
        } else {
            tooltipSupplier = Optional::empty;
        }

        final TransformEntry entry = new TransformEntry(
                Component.translatable(i18n), tooltipSupplier,
                currentVal, defaultVal, caps, dispObj, field.getType());
        entry.saveCallback = value -> writeField(field, config, value);
        return (List) Collections.singletonList(entry);
    };

    // =========================================================================
    // Helpers
    // =========================================================================

    @Contract(pure = true)
    private static @Nullable Object readField(final Field field, final Object obj, final @Nullable Object fallback) {
        try {
            return field.get(obj);
        } catch (final IllegalAccessException ignored) {
            return fallback;
        }
    }

    private static void writeField(final Field field, final Object obj, final @Nullable Object value) {
        try {
            field.set(obj, value);
        } catch (final IllegalAccessException ignored) {
        }
    }

    @Contract(pure = true)
    private static boolean isValidFloat(final String s) {
        try {
            Float.parseFloat(s);
            return true;
        } catch (final NumberFormatException ignored) {
            return false;
        }
    }

    // =========================================================================
    // Fields
    // =========================================================================

    private final @Nullable Object originalValue;
    private final @Nullable Object defaultValue;
    private final Transformation caps;
    private final Class<?> fieldType;

    /** Working copy of rotation [x, y, z] in degrees; {@code null} when {@code !caps.rotation()}. */
    private final @Nullable float[] rotation;
    /** Working copy of offset [x, y, z] in pixels (1/16 block); {@code null} when {@code !caps.offset()}. */
    private final @Nullable float[] offset;
    /**
     * Single-element wrapper around the working scale value.
     * Stored as an array so the viewport can hold a reference and mutate it in-place.
     */
    private final float[] scaleHolder;

    // Originals for isEdited() — immutable snapshots taken at construction
    private final @Nullable float[] origRotation;
    private final @Nullable float[] origOffset;
    private final float origScale;

    private GizmoMode activeMode;
    private final int modeCount;

    // EditBoxes — 3 boxes reused across modes
    private final EditBox[] editBoxes = new EditBox[3];
    private boolean suppressEditUpdate = false;

    // Tab rects: updated each render [mode_index][x, y, w, h]
    private final int[][] tabRects;
    private final GizmoMode[] availableModes;

    // Last render position, used for tooltip restriction
    private int lastRenderY;
    // Last viewport top-left, used to pass to viewport mouse handlers
    private int lastVpX, lastVpY;

    /** Handles all 3-D rendering and orbit/drag interaction for the viewport area. */
    private final TransformViewport viewport;

    // =========================================================================
    // Constructor
    // =========================================================================

    /**
     * Creates a {@code TransformEntry}.
     *
     * @param label          the translated field name
     * @param tooltipSupplier tooltip lines supplier
     * @param currentValue   the current transform object from the config
     * @param defaultValue   the default transform object
     * @param caps           the {@link Transformation} annotation from the field type
     * @param displayObject  the preview display object
     * @param fieldType      the declared type of the config field
     *
     * @since 1.0.1-SNAPSHOT.3+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    @ApiStatus.Internal
    @SuppressWarnings({"deprecation", "UnstableApiUsage"})
    public TransformEntry(final Component label,
                          final Supplier<Optional<Component[]>> tooltipSupplier,
                          final @Nullable Object currentValue,
                          final @Nullable Object defaultValue,
                          final Transformation caps,
                          final TransformDisplayObject displayObject,
                          final Class<?> fieldType) {
        super(label, tooltipSupplier, false);
        this.originalValue = currentValue;
        this.defaultValue = defaultValue;
        this.caps = caps;
        this.fieldType = fieldType;

        // Count available modes and collect them in order
        int mc = 0;
        if (caps.rotation()) mc++;
        if (caps.offset()) mc++;
        if (caps.scale()) mc++;
        this.modeCount = mc;
        this.availableModes = new GizmoMode[mc];
        int mi = 0;
        if (caps.rotation()) this.availableModes[mi++] = GizmoMode.ROTATE;
        if (caps.offset())   this.availableModes[mi++] = GizmoMode.TRANSLATE;
        if (caps.scale())    this.availableModes[mi]   = GizmoMode.SCALE;

        // Default to TRANSLATE if available, otherwise the first available mode
        this.activeMode = caps.offset() ? GizmoMode.TRANSLATE : this.availableModes[0];
        this.tabRects = new int[mc][4];

        // Extract working copies and originals from current value
        @Nullable float[] rot = null;
        @Nullable float[] off = null;
        float sc = 1.0f;
        if (currentValue != null) {
            try {
                if (caps.rotation()) {
                    final Rotation r = (Rotation) currentValue.getClass().getMethod("getRotation").invoke(currentValue);
                    rot = new float[]{r.getX(), r.getY(), r.getZ()};
                }
                if (caps.offset()) {
                    final Offset o = (Offset) currentValue.getClass().getMethod("getOffset").invoke(currentValue);
                    off = new float[]{o.getX(), o.getY(), o.getZ()};
                }
                if (caps.scale()) {
                    sc = (float) currentValue.getClass().getMethod("getScale").invoke(currentValue);
                }
            } catch (final Exception e) {
                LepidopteraAPI.error("TransformEntry: failed to read initial field values", e);
            }
        }
        this.rotation = (rot != null) ? rot : (caps.rotation() ? new float[3] : null);
        this.offset   = (off != null) ? off : (caps.offset()   ? new float[3] : null);
        this.scaleHolder  = new float[]{sc};
        this.origRotation = (this.rotation != null) ? Arrays.copyOf(this.rotation, 3) : null;
        this.origOffset   = (this.offset   != null) ? Arrays.copyOf(this.offset,   3) : null;
        this.origScale    = sc;

        this.viewport = new TransformViewport(caps, displayObject,
                this.rotation, this.offset, this.scaleHolder,
                this::refreshEditBoxValues);

        // Build edit boxes (positioned in render())
        final Font font = Minecraft.getInstance().font;
        for (int i = 0; i < 3; i++) {
            final int idx = i;
            final EditBox eb = new EditBox(font, 0, 0, INPUT_WIDTH, FIELD_HEIGHT, Component.empty());
            eb.setMaxLength(12);
            eb.setFilter(s -> s.isEmpty() || s.equals("-") || s.equals(".") || s.equals("-.") || isValidFloat(s));
            eb.setResponder(text -> {
                if (!suppressEditUpdate) onEditBoxChanged(idx, text);
            });
            editBoxes[i] = eb;
        }
        refreshEditBoxValues();
    }

    // =========================================================================
    // Value
    // =========================================================================

    /**
     * {@inheritDoc}
     *
     * @since 1.0.1-SNAPSHOT.3+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    @ApiStatus.Internal
    @Contract(pure = true)
    @Override
    public Object getValue() {
        try {
            final Object result = fieldType.getDeclaredConstructor().newInstance();
            if (caps.rotation() && rotation != null) {
                final Rotation rot = (Rotation) result.getClass().getMethod("getRotation").invoke(result);
                rot.setX(rotation[0]);
                rot.setY(rotation[1]);
                rot.setZ(rotation[2]);
            }
            if (caps.offset() && offset != null) {
                final Offset off = (Offset) result.getClass().getMethod("getOffset").invoke(result);
                off.setX(offset[0]);
                off.setY(offset[1]);
                off.setZ(offset[2]);
            }
            if (caps.scale()) {
                result.getClass().getMethod("setScale", float.class).invoke(result, scaleHolder[0]);
            }
            return result;
        } catch (final Exception e) {
            LepidopteraAPI.error("TransformEntry: failed to build getValue()", e);
            return (originalValue != null) ? originalValue : new Object();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.1-SNAPSHOT.3+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    @ApiStatus.Internal
    @Contract(pure = true)
    @Override
    public Optional<Object> getDefaultValue() {
        return Optional.ofNullable(defaultValue);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.1-SNAPSHOT.3+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    @ApiStatus.Internal
    @Contract(pure = true)
    @Override
    public boolean isEdited() {
        if (caps.rotation() && rotation != null && origRotation != null
                && !Arrays.equals(rotation, origRotation)) {
            return true;
        }
        if (caps.offset() && offset != null && origOffset != null
                && !Arrays.equals(offset, origOffset)) {
            return true;
        }
        return caps.scale() && scaleHolder[0] != origScale;
    }

    // =========================================================================
    // Layout
    // =========================================================================

    /**
     * {@inheritDoc}
     *
     * @since 1.0.1-SNAPSHOT.3+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    @ApiStatus.Internal
    @Contract(pure = true)
    @Override
    public int getItemHeight() {
        final int tabRow = (modeCount > 1) ? PADDING + TAB_HEIGHT : 0;
        return PADDING + LABEL_HEIGHT + PADDING + VIEWPORT_SIZE + tabRow + PADDING;
    }

    // =========================================================================
    // Children / narratables
    // =========================================================================

    /**
     * {@inheritDoc}
     *
     * @since 1.0.1-SNAPSHOT.3+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    @ApiStatus.Internal
    @Contract(value = "-> new", pure = true)
    @Override
    public List<? extends GuiEventListener> children() {
        final List<AbstractWidget> list = new ArrayList<>();
        addVisibleEditBoxes(list);
        return list;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.1-SNAPSHOT.3+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    @ApiStatus.Internal
    @Contract(value = "-> new", pure = true)
    @Override
    public List<? extends NarratableEntry> narratables() {
        final List<AbstractWidget> list = new ArrayList<>();
        addVisibleEditBoxes(list);
        return list;
    }

    private void addVisibleEditBoxes(final List<AbstractWidget> list) {
        list.addAll(Arrays.asList(editBoxes).subList(0, activeEditBoxCount()));
    }

    // =========================================================================
    // Tooltip restriction
    // =========================================================================

    /**
     * Restricts tooltip display to the label row.
     *
     * @since 1.0.1-SNAPSHOT.3+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    @ApiStatus.Internal
    @Override
    public Optional<Component[]> getTooltip(final int mouseX, final int mouseY) {
        final int labelBottom = lastRenderY + PADDING + LABEL_HEIGHT + PADDING;
        if (mouseY >= lastRenderY && mouseY < labelBottom) {
            return super.getTooltip(mouseX, mouseY);
        }
        return Optional.empty();
    }

    // =========================================================================
    // Render
    // =========================================================================

    /**
     * {@inheritDoc}
     *
     * @since 1.0.1-SNAPSHOT.3+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    @ApiStatus.Internal
    @Override
    public void render(final GuiGraphics graphics,
                       final int index,
                       final int y,
                       final int x,
                       final int entryWidth,
                       final int entryHeight,
                       final int mouseX,
                       final int mouseY,
                       final boolean isHovered,
                       final float delta) {
        lastRenderY = y;
        final Minecraft mc   = Minecraft.getInstance();
        final Font      font = mc.font;

        // Scroll list clip region — used to prevent overflow below Save/Cancel buttons
        final ClothConfigScreen clothScreen = Objects.requireNonNull((ClothConfigScreen) getConfigScreen());
        final int listTop    = clothScreen.listWidget.top;
        final int listBottom = clothScreen.listWidget.bottom;

        // Row 1: field label
        graphics.drawString(font, getFieldName(), x + PADDING, y + PADDING, COLOR_LABEL);

        // Content row: viewport + fields
        final int contentY = y + PADDING + LABEL_HEIGHT + PADDING;

        // Right-align: fields flush against the right edge, viewport to the left of fields
        final int fieldsRight  = x + entryWidth - PADDING;
        final int fieldsX      = fieldsRight - FIELDS_WIDTH;
        final int vpRight      = fieldsX - PADDING;
        final int vpX          = vpRight - VIEWPORT_SIZE;

        lastVpX = vpX;
        lastVpY = contentY;

        // Shared scissor parameters — used by fields and tabs to clip to the scroll pane
        final double gs   = mc.getWindow().getGuiScale();
        final int    winH = mc.getWindow().getHeight();

        // Viewport — pass list bounds so its scissor intersects with the scroll pane clip
        viewport.render(graphics, vpX, contentY, mouseX, mouseY, activeMode, listTop, listBottom);

        // Numeric fields — scissored to prevent overflow below Save/Cancel buttons
        RenderSystem.enableScissor(
                (int)(fieldsX * gs),
                winH - (int)(listBottom * gs),
                (int)(FIELDS_WIDTH * gs),
                (int)((listBottom - listTop) * gs));
        renderFields(graphics, fieldsX, contentY, font, mouseX, mouseY, delta);
        RenderSystem.disableScissor();

        // Mode tabs — scissored to the list widget's visible rect to prevent overflow
        if (modeCount > 1) {
            final int tabY = contentY + VIEWPORT_SIZE + PADDING;
            RenderSystem.enableScissor(
                    (int)(x * gs),
                    winH - (int)(listBottom * gs),
                    (int)(entryWidth * gs),
                    (int)((listBottom - listTop) * gs));
            renderModeTabs(graphics, x + PADDING, tabY, entryWidth - PADDING * 2, font, mouseX, mouseY);
            RenderSystem.disableScissor();
        }

        super.render(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Fields column
    // ─────────────────────────────────────────────────────────────────────────

    private void renderFields(final GuiGraphics graphics,
                               final int fieldsX, final int fieldsY,
                               final Font font,
                               final int mouseX, final int mouseY,
                               final float delta) {
        final int count = activeEditBoxCount();
        for (int i = 0; i < count; i++) {
            final int rowY = fieldsY + i * (FIELD_HEIGHT + FIELD_GAP);
            final int textColor = axisLabelColor(i);
            final String label  = axisLabel(i);
            graphics.drawString(font, label, fieldsX, rowY + (FIELD_HEIGHT - font.lineHeight) / 2, textColor);
            final EditBox eb = editBoxes[i];
            eb.setX(fieldsX + LABEL_WIDTH);
            eb.setY(rowY);
            eb.setWidth(INPUT_WIDTH);
            eb.render(graphics, mouseX, mouseY, delta);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Mode tabs
    // ─────────────────────────────────────────────────────────────────────────

    private void renderModeTabs(final GuiGraphics graphics,
                                final int tabAreaX, final int tabAreaY,
                                final int tabAreaW, final Font font,
                                final int mouseX, final int mouseY) {
        final int totalGap = (modeCount - 1); // 1px gaps between tabs
        final int tabW = (tabAreaW - totalGap) / modeCount;
        for (int i = 0; i < modeCount; i++) {
            final int tx = tabAreaX + i * (tabW + 1);
            tabRects[i][0] = tx;
            tabRects[i][1] = tabAreaY;
            tabRects[i][2] = tabW;
            tabRects[i][3] = TAB_HEIGHT;

            final boolean active  = availableModes[i] == activeMode;
            final boolean hovered = !active
                    && mouseX >= tx && mouseX < tx + tabW
                    && mouseY >= tabAreaY && mouseY < tabAreaY + TAB_HEIGHT;
            final ResourceLocation sprite = active  ? SPRITE_TAB_ACTIVE
                                          : hovered ? SPRITE_TAB_HOVERED
                                          :           SPRITE_TAB_INACTIVE;
            graphics.blitSprite(sprite, tx, tabAreaY, tabW, TAB_HEIGHT);
            final Component label = tabLabel(availableModes[i]);
            graphics.drawCenteredString(font, label, tx + tabW / 2, tabAreaY + (TAB_HEIGHT - font.lineHeight) / 2,
                    active ? COLOR_TAB_TEXT_DIM : COLOR_TAB_TEXT);
        }
    }

    // =========================================================================
    // Input handling
    // =========================================================================

    /**
     * {@inheritDoc}
     *
     * <p>Propagates focus changes through the parent container chain and updates each
     * {@link EditBox}'s focus state to match, so only the clicked box shows a cursor.</p>
     *
     * @since 1.0.1-SNAPSHOT.3+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    @ApiStatus.Internal
    @Override
    public void setFocused(final @Nullable GuiEventListener child) {
        super.setFocused(child);
        for (final EditBox eb : editBoxes) {
            eb.setFocused(eb == child);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.1-SNAPSHOT.3+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    @ApiStatus.Internal
    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        if (button != 0) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        // Tab click
        for (int i = 0; i < modeCount; i++) {
            final int[] r = tabRects[i];
            if (mouseX >= r[0] && mouseX < r[0] + r[2]
                    && mouseY >= r[1] && mouseY < r[1] + r[3]) {
                setActiveMode(availableModes[i]);
                return true;
            }
        }

        // Viewport click (orbit or gizmo drag start)
        if (viewport.mouseClicked(mouseX, mouseY, button, lastVpX, lastVpY, activeMode)) {
            return true;
        }

        // Delegate to edit boxes
        final int count = activeEditBoxCount();
        for (int i = 0; i < count; i++) {
            if (editBoxes[i].mouseClicked(mouseX, mouseY, button)) {
                setFocused(editBoxes[i]);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.1-SNAPSHOT.3+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    @ApiStatus.Internal
    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY,
                                final int button, final double dX, final double dY) {
        if (viewport.mouseDragged(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dX, dY);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.1-SNAPSHOT.3+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    @ApiStatus.Internal
    @Override
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        viewport.mouseReleased();
        return super.mouseReleased(mouseX, mouseY, button);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.1-SNAPSHOT.3+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    @ApiStatus.Internal
    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY,
                                  final double scrollX, final double scrollY) {
        if (viewport.mouseScrolled(mouseX, mouseY, lastVpX, lastVpY, scrollY)) {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.1-SNAPSHOT.3+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    @ApiStatus.Internal
    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        final int count = activeEditBoxCount();
        for (int i = 0; i < count; i++) {
            if (editBoxes[i].isFocused() && editBoxes[i].keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.1-SNAPSHOT.3+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.1-SNAPSHOT.3+1.21.1")
    @ApiStatus.Internal
    @Override
    public boolean charTyped(final char codePoint, final int modifiers) {
        final int count = activeEditBoxCount();
        for (int i = 0; i < count; i++) {
            if (editBoxes[i].isFocused() && editBoxes[i].charTyped(codePoint, modifiers)) {
                return true;
            }
        }
        return super.charTyped(codePoint, modifiers);
    }

    // =========================================================================
    // Helpers — edit box
    // =========================================================================

    private void onEditBoxChanged(final int boxIdx, final String text) {
        if (text.isEmpty() || text.equals("-") || text.equals(".") || text.equals("-.")) return;
        try {
            final float v = Float.parseFloat(text);
            switch (activeMode) {
                case ROTATE    -> {
                    if (rotation != null) {
                        final float clamped = ((v % 360f) + 360f) % 360f;
                        rotation[boxIdx] = clamped;
                        if (clamped != v) {
                            suppressEditUpdate = true;
                            editBoxes[boxIdx].setValue(String.valueOf(clamped));
                            suppressEditUpdate = false;
                        }
                    }
                }
                case TRANSLATE -> { if (offset   != null) offset[boxIdx]   = v; }
                case SCALE     -> { if (boxIdx == 0) scaleHolder[0] = Math.max(0.01f, v); }
            }
        } catch (final NumberFormatException ignored) {
        }
    }

    private void refreshEditBoxValues() {
        suppressEditUpdate = true;
        try {
            final int count = activeEditBoxCount();
            for (int i = 0; i < count; i++) {
                final float v = switch (activeMode) {
                    case ROTATE    -> (rotation != null) ? rotation[i] : 0f;
                    case TRANSLATE -> (offset   != null) ? offset[i]   : 0f;
                    case SCALE     -> scaleHolder[0];
                };
                final String text = switch (activeMode) {
                    case ROTATE, SCALE -> String.format("%.2f", v);
                    case TRANSLATE     -> String.format("%.3f", v);
                };
                if (!editBoxes[i].getValue().equals(text)) {
                    editBoxes[i].setValue(text);
                }
            }
        } finally {
            suppressEditUpdate = false;
        }
    }

    private void setActiveMode(final GizmoMode mode) {
        activeMode = mode;
        refreshEditBoxValues();
    }

    private int activeEditBoxCount() {
        return (activeMode == GizmoMode.SCALE) ? 1 : 3;
    }

    // =========================================================================
    // Helpers — labels / colors
    // =========================================================================

    private String axisLabel(final int boxIdx) {
        if (activeMode == GizmoMode.SCALE) return "";
        return switch (boxIdx) {
            case 0 -> "X";
            case 1 -> "Y";
            case 2 -> "Z";
            default -> "";
        };
    }

    private int axisLabelColor(final int boxIdx) {
        if (activeMode == GizmoMode.SCALE) return COLOR_LABEL;
        return switch (boxIdx) {
            case 0 -> COLOR_AXIS_LABEL_X;
            case 1 -> COLOR_AXIS_LABEL_Y;
            case 2 -> COLOR_AXIS_LABEL_Z;
            default -> COLOR_LABEL;
        };
    }

    private Component tabLabel(final GizmoMode mode) {
        return switch (mode) {
            case ROTATE    -> Component.translatable(T9n.gui(LepidopteraAPI.class, "transform", "mode", "rotate"));
            case TRANSLATE -> Component.translatable(T9n.gui(LepidopteraAPI.class, "transform", "mode", "translate"));
            case SCALE     -> Component.translatable(T9n.gui(LepidopteraAPI.class, "transform", "mode", "scale"));
        };
    }
}
