package dev.satyrn.lepidoptera.util;

import dev.satyrn.lepidoptera.api.NotInitializable;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

class NotInitializableTest {

    @Test
    void staticClass_throwsAssertionError() {
        AssertionError ex = assertThrows(AssertionError.class, () -> NotInitializable.staticClass(new Foo()));
        assertTrue(ex.getMessage().contains(Foo.class.getName()), "Actual message: " + ex.getMessage());
    }

    @Test
    void mixinClass_throwsAssertionError() {
        AssertionError ex = assertThrows(AssertionError.class, () -> NotInitializable.mixinClass(new Foo()));
        assertTrue(ex.getMessage().contains(Foo.class.getName()), "Actual message: " + ex.getMessage());
    }

    @Test
    void reflectiveInstantiation_throwsAssertionError() throws Exception {
        Constructor<NotInitializable> ctor = NotInitializable.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        InvocationTargetException ex = assertThrows(InvocationTargetException.class, ctor::newInstance);
        assertInstanceOf(AssertionError.class, ex.getCause());
        assertTrue(ex.getCause().getMessage().contains(NotInitializable.class.getName()));
    }

    private static class Foo {
    }
}