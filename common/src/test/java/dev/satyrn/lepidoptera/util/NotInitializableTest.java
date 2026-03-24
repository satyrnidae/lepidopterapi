package dev.satyrn.lepidoptera.util;

import dev.satyrn.lepidoptera.api.NotInitializable;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

class NotInitializableTest {

    private static class Foo {}

    @Test
    void staticClass_throwsAssertionError() {
        AssertionError ex = assertThrows(AssertionError.class,
                () -> NotInitializable.staticClass(Foo.class));
        assertTrue(ex.getMessage().contains(Foo.class.getName()));
    }

    @Test
    void mixinClass_throwsAssertionError() {
        AssertionError ex = assertThrows(AssertionError.class,
                () -> NotInitializable.mixinClass(Foo.class));
        assertTrue(ex.getMessage().contains(Foo.class.getName()));
    }

    @Test
    void reflectiveInstantiation_throwsAssertionError() throws Exception {
        Constructor<NotInitializable> ctor = NotInitializable.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        InvocationTargetException ex = assertThrows(InvocationTargetException.class, ctor::newInstance);
        assertInstanceOf(AssertionError.class, ex.getCause());
        assertTrue(ex.getCause().getMessage().contains(NotInitializable.class.getName()));
    }
}