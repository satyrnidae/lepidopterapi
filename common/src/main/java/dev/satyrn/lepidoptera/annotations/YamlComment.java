package dev.satyrn.lepidoptera.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
public @interface YamlComment {
	String value() default "";
	boolean sectionHeader() default false;
	String note() default "";
	String noteLeader() default NOTE_LEADER;
	String defaultValueLeader() default DEFAULT_VALUE_LEADER;
	boolean emitType() default true;
	String defaultValue() default "";
	boolean emitChildren() default true;

	String VALID_VALUES_LEADER = "Valid values: ";
	String NOTE_LEADER = "Note: ";
	String DEFAULT_VALUE_LEADER = "Default value: ";
	String SEE_ALSO_LEADER = "See also: ";
}
