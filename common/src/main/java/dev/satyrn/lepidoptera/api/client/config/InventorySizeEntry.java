package dev.satyrn.lepidoptera.api.client.config;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import dev.satyrn.lepidoptera.LepidopteraAPI;
import dev.satyrn.lepidoptera.api.config.InventorySize;
import dev.satyrn.lepidoptera.api.config.InventorySizeField;
import dev.satyrn.lepidoptera.api.lang.T9n;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.gui.registry.api.GuiProvider;
import me.shedaniel.clothconfig2.gui.entries.TooltipListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * A Cloth Config GUI entry for {@link InventorySize} config fields.
 *
 * <p>Renders a horizontal slider for width, a vertical slider for height, and a live
 * preview grid of inventory slot cells. The field itself stores the value as a plain
 * {@code "WxH"} string, making it serializer-agnostic.</p>
 *
 * <p>Register the static {@link #TYPE_PROVIDER} with Cloth Config's
 * {@code GuiRegistry} in your client initializer:</p>
 * <pre>{@code
 * AutoConfig.getGuiRegistry(MyConfig.class)
 *     .registerPredicateProvider(
 *         InventorySizeEntry.TYPE_PROVIDER,
 *         field -> field.getType() == String.class
 *                && field.isAnnotationPresent(InventorySizeField.class));
 * }</pre>
 *
 * @since 1.0.0-SNAPSHOT+1.21.1
 */
@ApiStatus.AvailableSince("1.0.0-SNAPSHOT+1.21.1")
@Environment(EnvType.CLIENT)
public final class InventorySizeEntry extends TooltipListEntry<String> {

    // -- Texture --

    /**
     * A Cloth Config {@link GuiProvider} that matches any {@code String} field annotated
     * with {@link InventorySizeField} and binds it to this entry type.
     *
     * <p>Pass this to {@code GuiRegistry.registerPredicateProvider} once in your client
     * initializer (see class-level Javadoc).</p>
     *
     * @since 1.0.0-SNAPSHOT+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT+1.21.1")
    @ApiStatus.Internal
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static final GuiProvider TYPE_PROVIDER = (i18n, field, config, defaults, guiProvider) -> {
        final InventorySizeField annotation = field.getAnnotation(InventorySizeField.class);
        final int maxWidth = annotation != null ? annotation.maxWidth() : 27;
        final int maxHeight = annotation != null ? annotation.maxHeight() : 27;

        final String currentValue = readField(field, config,
                new InventorySize(InventorySize.MIN_VALUE, InventorySize.MIN_VALUE).toString());
        final String defaultVal = readField(field, defaults, currentValue);

        // Build tooltip supplier from @ConfigEntry.Gui.Tooltip(count = N) if present.
        // Cloth Config indexed tooltip keys follow the pattern: i18n + ".@Tooltip[N]"
        final ConfigEntry.Gui.Tooltip tooltipAnnotation = field.getAnnotation(ConfigEntry.Gui.Tooltip.class);
        final java.util.function.Supplier<Optional<Component[]>> tooltipSupplier;
        if (tooltipAnnotation != null) {
            final Component[] lines = new Component[tooltipAnnotation.count()];
            for (int k = 0; k < lines.length; k++) {
                lines[k] = Component.translatable(i18n + ".@Tooltip[" + k + "]");
            }
            tooltipSupplier = () -> Optional.of(lines);
        } else {
            tooltipSupplier = Optional::empty;
        }

        final InventorySizeEntry entry = new InventorySizeEntry(Component.translatable(i18n), tooltipSupplier,
                currentValue, defaultVal, maxWidth, maxHeight);
        entry.saveCallback = value -> writeField(field, config, value);
        return (List) Collections.singletonList(entry);
    };

    // -- MC slider sprites --
    private static final ResourceLocation CELL_TEXTURE = ResourceLocation.fromNamespaceAndPath("lepidoptera_api",
            "textures/gui/inventory_size.png");
    private static final ResourceLocation SLIDER_BG = ResourceLocation.withDefaultNamespace("widget/slider");
    private static final ResourceLocation SLIDER_BG_HL = ResourceLocation.withDefaultNamespace(
            "widget/slider_highlighted");
    private static final ResourceLocation SLIDER_HANDLE = ResourceLocation.withDefaultNamespace("widget/slider_handle");

    // -- Layout constants --
    private static final ResourceLocation SLIDER_HANDLE_HL = ResourceLocation.withDefaultNamespace(
            "widget/slider_handle_highlighted");
    private static final int PADDING = 4;
    private static final int LABEL_HEIGHT = 10;
    private static final int H_SLIDER_HEIGHT = 20;
    /**
     * Width of the vertical slider track — matches {@link #H_SLIDER_HEIGHT}.
     */
    private static final int V_SLIDER_WIDTH = 20;
    /**
     * Height of the draggable thumb on the vertical slider (matches MC handle proportions).
     */
    private static final int V_THUMB_HEIGHT = 8;
    /**
     * Font line height used to size the rotated "Height: N" label column.
     */
    private static final int FONT_LINE_HEIGHT = 9;
    /**
     * Gap between the rotated label and the slider track.
     */
    private static final int V_LABEL_GAP = 2;
    private static final int CELL_SIZE = 16;

    // -- Colors --
    /**
     * No gap between cells — slots are drawn edge-to-edge.
     */
    private static final int CELL_STEP = CELL_SIZE;
    private static final int COLOR_LABEL = 0xFFFFFFFF;

    // -- State --
    private static final int COLOR_DIM_VALUE = 0xFFAAAAAA;
    private final String originalValue;
    private final String defaultValue;
    private final int maxWidth;
    private final int maxHeight;
    // Widgets — positions are updated every render() call
    private final WidthSlider widthSlider;
    private final HeightSliderWidget heightSlider;
    private int currentWidth;
    private int currentHeight;
    /**
     * The widget that captured the most recent mouseClicked, or {@code null}.
     *
     * <p>{@link AbstractSliderButton#mouseDragged} responds to every left-drag regardless
     * of which widget was clicked, so drag events must only be forwarded to the widget that
     * originally captured the click.</p>
     */
    private @Nullable AbstractWidget activeWidget = null;

    // -------------------------------------------------------------------------
    // TYPE_PROVIDER
    // -------------------------------------------------------------------------
    /**
     * Entry top-Y captured each frame; used to restrict tooltip display to the label row.
     */
    private int lastRenderY = 0;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Creates an entry.
     *
     * @param label        the translated field name shown as the entry label
     * @param currentValue the initial {@code "WxH"} string from the config
     * @param defaultValue the default {@code "WxH"} string for the reset button
     * @param maxWidth     the maximum slider value for width  (must be {@code >= 1})
     * @param maxHeight    the maximum slider value for height (must be {@code >= 1})
     *
     * @since 1.0.0-SNAPSHOT+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT+1.21.1")
    @ApiStatus.Internal
    @SuppressWarnings({"deprecation", "UnstableApiUsage"})
    public InventorySizeEntry(final Component label,
                              final java.util.function.Supplier<Optional<Component[]>> tooltipSupplier,
                              final String currentValue,
                              final String defaultValue,
                              final int maxWidth,
                              final int maxHeight) {
        super(label, tooltipSupplier, false);
        this.defaultValue = defaultValue;
        this.maxWidth = Math.max(InventorySize.MIN_VALUE, maxWidth);
        this.maxHeight = Math.max(InventorySize.MIN_VALUE, maxHeight);

        final InventorySize parsed = safeParse(currentValue, defaultValue);
        this.currentWidth = Mth.clamp(parsed.width(), InventorySize.MIN_VALUE, this.maxWidth);
        this.currentHeight = Mth.clamp(parsed.height(), InventorySize.MIN_VALUE, this.maxHeight);
        this.originalValue = currentValue;

        // Widgets are positioned in render(); placeholder bounds here.
        // The initial slider value is computed here (outer this is available) and passed in,
        // because inner constructors cannot reference the enclosing instance before super().
        final double initWidthNorm = this.maxWidth == 1 ? 0.0 : (this.currentWidth - 1.0) / (this.maxWidth - 1.0);
        this.widthSlider = new WidthSlider(0, 0, this.maxWidth * CELL_SIZE, initWidthNorm);
        this.heightSlider = new HeightSliderWidget(0, 0, this.maxHeight * CELL_STEP);
    }

    // -------------------------------------------------------------------------
    // AbstractConfigListEntry / AbstractConfigEntry
    // -------------------------------------------------------------------------

    @Contract(pure = true)
    private static String readField(final Field field, final Object obj, final String fallback) {
        try {
            return (String) field.get(obj);
        } catch (final IllegalAccessException | ClassCastException ignored) {
            return fallback;
        }
    }

    @Contract(mutates = "param1")
    private static void writeField(final Field field, final Object obj, final String value) {
        try {
            field.set(obj, value);
        } catch (final IllegalAccessException ignored) {
        }
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0-SNAPSHOT+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT+1.21.1")
    @ApiStatus.Internal
    @Contract(pure = true)
    @Override
    public String getValue() {
        return new InventorySize(this.currentWidth, this.currentHeight).toString();
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0-SNAPSHOT+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT+1.21.1")
    @ApiStatus.Internal
    @Contract(pure = true)
    @Override
    public Optional<String> getDefaultValue() {
        return Optional.of(this.defaultValue);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0-SNAPSHOT+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT+1.21.1")
    @ApiStatus.Internal
    @Contract(pure = true)
    @Override
    public boolean isEdited() {
        return !getValue().equals(this.originalValue);
    }

    /**
     * Restricts tooltip display to the label row (field name + summary).
     *
     * <p>The default {@link me.shedaniel.clothconfig2.gui.entries.TooltipListEntry} behaviour
     * shows the tooltip over the entire entry area. We override this so the tooltip only
     * appears when the cursor is within the label row, keeping the sliders and grid readable.</p>
     *
     * @since 1.0.0-SNAPSHOT+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT+1.21.1")
    @ApiStatus.Internal
    @Override
    public Optional<Component[]> getTooltip(final int mouseX, final int mouseY) {
        final int labelRowBottom = this.lastRenderY + PADDING + LABEL_HEIGHT + PADDING;
        if (mouseY >= this.lastRenderY && mouseY < labelRowBottom) {
            return super.getTooltip(mouseX, mouseY);
        }
        return Optional.empty();
    }

    /**
     * Returns the fixed pixel height of this entry.
     *
     * <p>The entry height is determined by the configured {@code maxHeight} and does not
     * change as the slider is dragged.</p>
     *
     * @since 1.0.0-SNAPSHOT+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT+1.21.1")
    @ApiStatus.Internal
    @Contract(pure = true)
    @Override
    public int getItemHeight() {
        return PADDING + LABEL_HEIGHT + PADDING + H_SLIDER_HEIGHT + PADDING + this.maxHeight * CELL_STEP + PADDING;
    }

    // -------------------------------------------------------------------------
    // Rendering
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0-SNAPSHOT+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT+1.21.1")
    @ApiStatus.Internal
    @Contract(value = "-> new", pure = true)
    @Override
    public @Unmodifiable List<? extends GuiEventListener> children() {
        return List.of(this.widthSlider, this.heightSlider);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0-SNAPSHOT+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT+1.21.1")
    @ApiStatus.Internal
    @Contract(value = "-> new", pure = true)
    @Override
    public @Unmodifiable List<? extends NarratableEntry> narratables() {
        return List.of(this.widthSlider, this.heightSlider);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0-SNAPSHOT+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT+1.21.1")
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

        this.lastRenderY = y;
        final var font = Minecraft.getInstance().font;

        // Row 1 — label + current value summary
        final int labelY = y + PADDING;
        graphics.drawString(font, getFieldName(), x + PADDING, labelY, COLOR_LABEL);
        final Component summary = Component.translatable(T9n.gui(LepidopteraAPI.class, "inventory_size", "summary"),
                this.currentWidth, this.currentHeight);
        graphics.drawString(font, summary, x + entryWidth - PADDING - font.width(summary), labelY, COLOR_DIM_VALUE);

        // Right-align the entire control block to match other Cloth Config entry positions.
        // Layout (right→left from entry right edge):
        //   PADDING | grid (maxWidth * CELL_SIZE) | PADDING | vertical slider (V_SLIDER_WIDTH)
        //           | V_LABEL_GAP | "Height: N" label rotated (FONT_LINE_HEIGHT wide)
        final int trackH = this.maxHeight * CELL_STEP;
        final int gridLeft = x + entryWidth - PADDING - this.maxWidth * CELL_SIZE;
        final int sliderLeft = gridLeft - PADDING - V_SLIDER_WIDTH;
        final int labelLeft = sliderLeft - V_LABEL_GAP - FONT_LINE_HEIGHT;

        // Row 2 — horizontal width slider, fixed width = maxWidth * CELL_SIZE, aligned with grid
        final int row2Y = labelY + LABEL_HEIGHT + PADDING;
        this.widthSlider.setX(gridLeft);
        this.widthSlider.setY(row2Y);
        this.widthSlider.setWidth(this.maxWidth * CELL_SIZE);
        this.widthSlider.render(graphics, mouseX, mouseY, delta);

        // Row 3 — vertical height slider + grid preview
        final int row3Y = row2Y + H_SLIDER_HEIGHT + PADDING;

        this.heightSlider.setX(sliderLeft);
        this.heightSlider.setY(row3Y);
        this.heightSlider.setHeight(trackH);
        this.heightSlider.render(graphics, mouseX, mouseY, delta);

        // Draw the rotated "Height: N" label after the slider so it renders on top
        drawVerticalLabel(graphics, font, labelLeft, row3Y, trackH, this.currentHeight);

        drawGrid(graphics, gridLeft, row3Y);

        // Delegate to TooltipListEntry.render() so it can queue the tooltip when hovered.
        // AbstractConfigListEntry.render() only registers the hover area (no content drawing),
        // so calling super here is safe and does not conflict with our layout.
        super.render(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
    }

    // -------------------------------------------------------------------------
    // Input forwarding
    // -------------------------------------------------------------------------

    /**
     * Renders the "Height: N" label rotated 90° CCW so it reads upward alongside the vertical slider.
     *
     * @param graphics           the graphics context
     * @param font               the font renderer
     * @param labelX             the left edge of the label column (i.e. {@code x + PADDING})
     * @param trackY             the top of the track / grid row
     * @param trackH             the pixel height of the track
     * @param currentHeightValue the current height value to embed in the label
     */
    private void drawVerticalLabel(final GuiGraphics graphics,
                                   final net.minecraft.client.gui.Font font,
                                   final int labelX,
                                   final int trackY,
                                   final int trackH,
                                   final int currentHeightValue) {
        final Component full = Component.translatable(T9n.gui(LepidopteraAPI.class, "inventory_size", "height"),
                currentHeightValue);
        // Fall back to the short form if the full label is longer than the track is tall
        final Component heightLabel = font.width(full.getString()) <= trackH
                ? full
                : Component.translatable(T9n.gui(LepidopteraAPI.class, "inventory_size", "height_short"),
                        currentHeightValue);
        final String text = heightLabel.getString();
        final int textWidth = font.width(text);

        // Center the text vertically along the track.
        // After a −90° (CW) rotation, the text's X axis maps to screen-down and Y axis to screen-right.
        // Translating to (labelX + FONT_LINE_HEIGHT, trackY + (trackH + textWidth) / 2) places
        // the rotated text centered on the track height, flush within the FONT_LINE_HEIGHT column.
        final float tx = labelX + FONT_LINE_HEIGHT + (V_SLIDER_WIDTH / 2F);
        final float ty = trackY + (trackH + textWidth) / 2.0f;

        graphics.pose().pushPose();
        graphics.pose().translate(tx, ty, 0.0f);
        graphics.pose().mulPose(Axis.ZP.rotationDegrees(-90.0f));
        graphics.drawString(font, text, 0, 0, COLOR_LABEL, false);
        graphics.pose().popPose();
    }

    private void drawGrid(final GuiGraphics graphics, final int startX, final int startY) {
        // GuiGraphics.blit routes through the legacy RenderSystem.setShader pipeline which
        // does not enable blending automatically — enable it explicitly for the semi-transparent
        // cell texture, then restore the previous state afterward.
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        for (int row = 0; row < this.currentHeight; row++) {
            for (int col = 0; col < this.currentWidth; col++) {
                final int cellX = startX + col * CELL_STEP;
                final int cellY = startY + row * CELL_STEP;
                graphics.blit(CELL_TEXTURE, cellX, cellY, 0, 0.0f, 0.0f, CELL_SIZE, CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
        RenderSystem.disableBlend();
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0-SNAPSHOT+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT+1.21.1")
    @ApiStatus.Internal
    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        if (this.widthSlider.mouseClicked(mouseX, mouseY, button)) {
            this.activeWidget = this.widthSlider;
            return true;
        }
        if (this.heightSlider.mouseClicked(mouseX, mouseY, button)) {
            this.activeWidget = this.heightSlider;
            return true;
        }
        this.activeWidget = null;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * <p>Drag events are forwarded only to {@link #activeWidget} — the widget that captured
     * the most recent click — because {@link AbstractSliderButton#mouseDragged} responds to
     * any left-drag regardless of which widget initiated it.</p>
     *
     * @since 1.0.0-SNAPSHOT+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT+1.21.1")
    @ApiStatus.Internal
    @Override
    public boolean mouseDragged(final double mouseX,
                                final double mouseY,
                                final int button,
                                final double deltaX,
                                final double deltaY) {
        if (this.activeWidget != null) {
            return this.activeWidget.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0-SNAPSHOT+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT+1.21.1")
    @ApiStatus.Internal
    @Contract(mutates = "this")
    @Override
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        if (this.activeWidget != null) {
            this.activeWidget.mouseReleased(mouseX, mouseY, button);
            this.activeWidget = null;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Contract(pure = true)
    private InventorySize safeParse(final String value, final String fallback) {
        try {
            return InventorySize.parse(value);
        } catch (final IllegalArgumentException ignored) {
            try {
                return InventorySize.parse(fallback);
            } catch (final IllegalArgumentException ignored2) {
                return new InventorySize(InventorySize.MIN_VALUE, InventorySize.MIN_VALUE);
            }
        }
    }

    // =========================================================================
    // Inner widget: horizontal width slider
    // =========================================================================

    private final class WidthSlider extends AbstractSliderButton {

        WidthSlider(final int x, final int y, final int width, final double initialNorm) {
            super(x, y, width, H_SLIDER_HEIGHT, Component.empty(), initialNorm);
            updateMessage();
        }

        @ApiStatus.Internal
        @Override
        protected void applyValue() {
            currentWidth = Mth.clamp((int) Math.round(this.value * (maxWidth - 1)) + 1, InventorySize.MIN_VALUE,
                    maxWidth);
        }

        @ApiStatus.Internal
        @Override
        protected void updateMessage() {
            final var font = Minecraft.getInstance().font;
            final Component full = Component.translatable(T9n.gui(LepidopteraAPI.class, "inventory_size", "width"),
                    currentWidth);
            // AbstractSliderButton.renderScrollingString uses margin=2 each side → usable width = this.width - 4
            final Component label = font.width(full) <= this.width - 4
                    ? full
                    : Component.translatable(T9n.gui(LepidopteraAPI.class, "inventory_size", "width_short"),
                            currentWidth);
            setMessage(label);
        }
    }

    // =========================================================================
    // Inner widget: vertical height slider
    // =========================================================================
    // TODO: Vertical slider widget could be useful elsewhere
    private final class HeightSliderWidget extends AbstractWidget {

        private boolean dragging = false;
        private int trackHeight;

        HeightSliderWidget(final int x, final int y, final int trackHeight) {
            super(x, y, V_SLIDER_WIDTH, trackHeight, Component.empty());
            this.trackHeight = trackHeight;
        }

        /**
         * Updates the rendered track height (called each frame before render).
         */
        @ApiStatus.Internal
        @Contract(mutates = "this")
        @Override
        public void setHeight(final int h) {
            this.trackHeight = Math.max(V_THUMB_HEIGHT, h);
        }

        @Override
        protected void renderWidget(final GuiGraphics graphics,
                                    final int mouseX,
                                    final int mouseY,
                                    final float delta) {
            // -- Track (background) --
            // Rotate 90° CCW so the horizontal slider sprite is drawn vertically.
            // After Axis.ZP.rotationDegrees(90):
            //   screen-right  → up    (so sprite "width"  maps to physical height)
            //   screen-down   → right (so sprite "height" maps to physical width)
            // We translate the pose origin to (getX() + V_SLIDER_WIDTH, getY()) so that
            // drawing at (0, 0) in rotated space lands in the top-left corner of the track.
            final ResourceLocation bgSprite = isFocused() ? SLIDER_BG_HL : SLIDER_BG;
            graphics.pose().pushPose();
            graphics.pose().translate(getX() + V_SLIDER_WIDTH, getY(), 0.0f);
            graphics.pose().mulPose(Axis.ZP.rotationDegrees(90.0f));
            graphics.blitSprite(bgSprite, 0, 0, this.trackHeight, V_SLIDER_WIDTH);
            graphics.pose().popPose();

            // -- Thumb --
            final int thumbY = thumbY();
            final boolean hov = isMouseOverThumb(mouseX, mouseY);
            final ResourceLocation handleSprite = (hov || this.dragging) ? SLIDER_HANDLE_HL : SLIDER_HANDLE;
            graphics.pose().pushPose();
            graphics.pose().translate(getX() + V_SLIDER_WIDTH, thumbY, 0.0f);
            graphics.pose().mulPose(Axis.ZP.rotationDegrees(90.0f));
            graphics.blitSprite(handleSprite, 0, 0, V_THUMB_HEIGHT, V_SLIDER_WIDTH);
            graphics.pose().popPose();
        }

        @ApiStatus.Internal
        @Contract(pure = true)
        @Override
        public @Nullable ComponentPath nextFocusPath(final FocusNavigationEvent event) {
            return null; // not keyboard-navigable
        }

        @ApiStatus.Internal
        @Override
        public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
            if (button == 0 && isMouseOver(mouseX, mouseY)) {
                this.dragging = true;
                applyMouse(mouseY);
                return true;
            }
            return false;
        }

        @ApiStatus.Internal
        @Override
        public boolean mouseDragged(final double mouseX,
                                    final double mouseY,
                                    final int button,
                                    final double deltaX,
                                    final double deltaY) {
            if (this.dragging) {
                applyMouse(mouseY);
                return true;
            }
            return false;
        }

        @ApiStatus.Internal
        @Contract(mutates = "this")
        @Override
        public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
            this.dragging = false;
            return super.mouseReleased(mouseX, mouseY, button);
        }

        @ApiStatus.Internal
        @Contract(pure = true)
        @Override
        public boolean isMouseOver(final double mouseX, final double mouseY) {
            return mouseX >= getX() &&
                    mouseX < getX() + V_SLIDER_WIDTH &&
                    mouseY >= getY() &&
                    mouseY < getY() + this.trackHeight;
        }

        @ApiStatus.Internal
        @Override
        protected void updateWidgetNarration(final NarrationElementOutput output) {
            defaultButtonNarrationText(output);
        }

        // -- Helpers --

        @Contract(pure = true)
        private int thumbY() {
            if (maxHeight <= 1) {
                return getY();
            }
            final double norm = (currentHeight - 1.0) / (maxHeight - 1.0);
            final int range = this.trackHeight - V_THUMB_HEIGHT;
            // norm=0 (min height) → thumb at top; norm=1 (max) → thumb at bottom
            return getY() + (int) (norm * range);
        }

        @Contract(pure = true)
        private boolean isMouseOverThumb(final double mouseX, final double mouseY) {
            final int ty = thumbY();
            return mouseX >= getX() && mouseX < getX() + V_SLIDER_WIDTH && mouseY >= ty && mouseY < ty + V_THUMB_HEIGHT;
        }

        @Contract(mutates = "this")
        private void applyMouse(final double mouseY) {
            if (maxHeight <= 1) {
                currentHeight = 1;
                return;
            }
            final int range = this.trackHeight - V_THUMB_HEIGHT;
            // Map mouse Y position to a normalized value in [0..1],
            // where 0 (top of track) = minimum height and 1 (bottom) = maximum height.
            final double relY = mouseY - getY() - V_THUMB_HEIGHT / 2.0;
            final double norm = Mth.clamp(relY / range, 0.0, 1.0);
            currentHeight = Mth.clamp((int) Math.round(norm * (maxHeight - 1)) + 1, InventorySize.MIN_VALUE, maxHeight);
        }
    }
}