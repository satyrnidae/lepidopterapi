package dev.satyrn.lepidoptera.api.config.transform;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Transformation {
    boolean rotation() default false;
    boolean offset() default false;
    boolean scale() default false;
}
