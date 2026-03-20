package dev.satyrn.lepidoptera.beans;

import dev.satyrn.lepidoptera.util.NotInitializable;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class BeanInspection {
    private static final List<Class<?>> NON_BEAN_TYPES = Arrays.asList(String.class, Number.class, Boolean.class,
            List.class, Map.class);

    private BeanInspection() {
        NotInitializable.staticClass(BeanInspection.class);
    }

    public static boolean isBean(Class<?> type) {
        if (NON_BEAN_TYPES.stream().anyMatch(nonBeanType -> nonBeanType.isAssignableFrom(type))) {
            return false;
        }

        return hasPublicNoArgConstructor(type) && hasBeanProperties(type);
    }

    private static boolean hasPublicNoArgConstructor(Class<?> type) {
        try {
            type.getConstructor();
            return true;
        } catch (NoSuchMethodException ignored) {
            return false;
        }
    }

    private static boolean hasBeanProperties(Class<?> type) {
        try {
            PropertyDescriptor[] props = Introspector.getBeanInfo(type).getPropertyDescriptors();
            return props.length > 1;
        } catch (IntrospectionException ignored) {
            return false;
        }
    }
}
