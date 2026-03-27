package dev.satyrn.lepidoptera.api.lang;

import net.minecraft.ChatFormatting;
import org.jetbrains.annotations.ApiStatus;

/**
 * A fluent builder for strings that contain Minecraft legacy section-sign ({@code §}) color and
 * formatting codes.
 *
 * <p>Works like {@link StringBuilder} but each {@link #append(String, ChatFormatting...)} call
 * prefixes the text with the requested format codes and automatically appends
 * {@link ChatFormatting#RESET} afterwards, keeping each section self-contained.</p>
 *
 * <p>Intended for use wherever a plain {@code String} is required but formatting is desired -
 * for example, Cloth Config tooltip strings passed to
 * {@code LanguageProvider.add(String, String)}.</p>
 *
 * <p>Example:</p>
 * <pre>{@code
 * String tooltip = new FormattedStringBuilder()
 *     .append("WARNING:", ChatFormatting.RED, ChatFormatting.BOLD)
 *     .append(" Handle with care.")
 *     .build();
 * // → "§c§lWARNING:§r Handle with care."
 * }</pre>
 *
 * @since 1.0.0-SNAPSHOT.1+1.21.1
 */
@ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
public final class FormattedStringBuilder {
    private final StringBuilder sb = new StringBuilder();

    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public FormattedStringBuilder() {
    }

    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public FormattedStringBuilder(String text) {
        this.append(text);
    }

    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public FormattedStringBuilder(String text, ChatFormatting... formats) {
        this.append(text, formats);
    }

    /**
     * Appends plain text with no formatting codes.
     *
     * @param text the text to append
     *
     * @return {@code this}, for chaining
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public FormattedStringBuilder append(final String text) {
        sb.append(text);
        return this;
    }

    /**
     * Appends text prefixed with the given formatting codes, followed by an automatic
     * {@link ChatFormatting#RESET}.
     *
     * @param text    the text to append
     * @param formats one or more {@link ChatFormatting} values to apply before the text
     *
     * @return {@code this}, for chaining
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public FormattedStringBuilder append(final String text, final ChatFormatting... formats) {
        for (final ChatFormatting format : formats) {
            sb.append(format);
        }
        sb.append(text);
        sb.append(ChatFormatting.RESET);
        return this;
    }

    /**
     * Explicitly appends a {@link ChatFormatting#RESET} code ({@code §r}).
     *
     * <p>Normally not needed since {@link #append(String, ChatFormatting...)} auto-resets, but
     * useful when a plain-text segment should clear residual formatting from surrounding
     * context.</p>
     *
     * @return {@code this}, for chaining
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public FormattedStringBuilder reset() {
        sb.append(ChatFormatting.RESET);
        return this;
    }

    /**
     * Returns the accumulated formatted string.
     *
     * @return the built string containing all appended text and formatting codes
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    public String build() {
        return sb.toString();
    }

    /**
     * Delegates to {@link #build()}.
     *
     * @return the built string
     */
    public @Override String toString() {
        return build();
    }
}
