package dev.satyrn.lepidoptera.api.config.serializers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Supplies YAML comment metadata for a config field, method, or type.
 *
 * <p>Processed by {@code YamlCommentInjector} during config serialization to produce
 * human-readable comments in the output YAML file. Supports section headers, inline
 * notes, default-value hints, and automatic enum value listings.</p>
 *
 * @since 0.4.0+1.19.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
public @interface YamlComment {

    /**
     * The primary comment text to emit above the YAML key. Defaults to empty (no comment).
     *
     * @since 0.4.0+1.19.2
     */
    String value() default "";

    /**
     * When {@code true}, the comment is rendered as a section header with a decorative
     * separator rather than an ordinary inline comment.
     *
     * @since 0.4.0+1.19.2
     */
    boolean sectionHeader() default false;

    /**
     * An optional supplementary note appended after the main comment text.
     *
     * @since 0.4.0+1.19.2
     */
    String note() default "";

    /**
     * The leader string prepended to {@link #note()} when it is non-empty.
     * Defaults to {@link #NOTE_LEADER}.
     *
     * @since 0.4.0+1.19.2
     */
    String noteLeader() default NOTE_LEADER;

    /**
     * The leader string prepended to the default-value hint.
     * Defaults to {@link #DEFAULT_VALUE_LEADER}.
     *
     * @since 0.4.0+1.19.2
     */
    String defaultValueLeader() default DEFAULT_VALUE_LEADER;

    /**
     * When {@code true}, the field's Java type is included in the generated comment
     * to help users understand expected values.
     *
     * @since 0.4.0+1.19.2
     */
    boolean emitType() default true;

    /**
     * An explicit default-value string to include in the comment.
     * When empty the injector may derive this from the field value at serialization time.
     *
     * @since 0.4.0+1.19.2
     */
    String defaultValue() default "";

    /**
     * When {@code true} and the annotated field is a bean type, comments are
     * recursively generated for child fields.
     *
     * @since 0.4.0+1.19.2
     */
    boolean emitChildren() default true;

    /**
     * Leader string used to introduce a valid-values list in a comment (e.g. for enums).
     *
     * @since 0.4.0+1.19.2
     */
    String VALID_VALUES_LEADER = "Valid values: ";

    /**
     * Default leader string prepended to {@link #note()} text.
     *
     * @since 0.4.0+1.19.2
     */
    String NOTE_LEADER = "Note: ";

    /**
     * Default leader string prepended to a default-value hint.
     *
     * @since 0.4.0+1.19.2
     */
    String DEFAULT_VALUE_LEADER = "Default value: ";

    /**
     * Leader string used to introduce a "see also" reference in a comment.
     *
     * @since 0.4.0+1.19.2
     */
    // TODO: Implement See also: emitting
    String SEE_ALSO_LEADER = "See also: ";
}
