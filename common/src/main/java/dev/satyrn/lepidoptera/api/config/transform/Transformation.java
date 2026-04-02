package dev.satyrn.lepidoptera.api.config.transform;

public @interface Transformation {
    boolean rotation() default false;
    boolean offset() default false;
    boolean scale() default false;
}
