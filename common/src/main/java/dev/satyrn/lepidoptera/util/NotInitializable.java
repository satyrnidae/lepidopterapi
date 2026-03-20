package dev.satyrn.lepidoptera.util;

public final class NotInitializable {
    private NotInitializable() {
        staticClass(NotInitializable.class);
    }

    public static <T> void staticClass(final Class<T> clazz) {
        throw new AssertionError(clazz.getName() + " is a static class and should not be initialized.");
    }

	public static <T> void mixinClass(final Class<T> clazz) {
		throw new AssertionError(clazz.getName() + " is a mixin and should not be initialized.");
	}
}
