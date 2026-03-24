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
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
public @interface YamlComment {
	/** The primary comment text to emit above the YAML key. Defaults to empty (no comment). */
	String value() default "";

	/**
	 * When {@code true}, the comment is rendered as a section header with a decorative
	 * separator rather than an ordinary inline comment.
	 */
	boolean sectionHeader() default false;

	/** An optional supplementary note appended after the main comment text. */
	String note() default "";

	/**
	 * The leader string prepended to {@link #note()} when it is non-empty.
	 * Defaults to {@link #NOTE_LEADER}.
	 */
	String noteLeader() default NOTE_LEADER;

	/**
	 * The leader string prepended to the default-value hint.
	 * Defaults to {@link #DEFAULT_VALUE_LEADER}.
	 */
	String defaultValueLeader() default DEFAULT_VALUE_LEADER;

	/**
	 * When {@code true}, the field's Java type is included in the generated comment
	 * to help users understand expected values.
	 */
	boolean emitType() default true;

	/**
	 * An explicit default-value string to include in the comment.
	 * When empty the injector may derive this from the field value at serialization time.
	 */
	String defaultValue() default "";

	/**
	 * When {@code true} and the annotated field is a bean type, comments are
	 * recursively generated for child fields.
	 */
	boolean emitChildren() default true;

	/** Leader string used to introduce a valid-values list in a comment (e.g. for enums). */
	String VALID_VALUES_LEADER = "Valid values: ";

	/** Default leader string prepended to {@link #note()} text. */
	String NOTE_LEADER = "Note: ";

	/** Default leader string prepended to a default-value hint. */
	String DEFAULT_VALUE_LEADER = "Default value: ";

	/** Leader string used to introduce a "see also" reference in a comment. */
	String SEE_ALSO_LEADER = "See also: ";
}
