package dev.satyrn.lepidoptera.api.compatibility;

import dev.satyrn.lepidoptera.api.NotInitializable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import java.util.Comparator;

/**
 * An interface for compatibility providers which can outrank each other in priority.
 *
 * <p>Implement this interface (typically via {@link CompatibilityProvider}) and register
 * instances through {@link Compatibility#register(String)} so that the registry can
 * resolve conflicts using {@link #getPriority()}.</p>
 *
 * <p>Priority is also used by {@link dev.satyrn.lepidoptera.api.item.EquipmentRegistry}
 * and {@link dev.satyrn.lepidoptera.api.entity.HungryEntityRegistry} when two registrations
 * conflict for the same item or entity type. Use {@link Priority} constants as the priority
 * argument to those methods.</p>
 *
 * @since 1.0.0-SNAPSHOT.1+1.21.1
 */
@ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
public interface Provider {

    /**
     * Gets the provider's priority. Higher values take precedence over lower values when
     * providers conflict. Defaults to {@link Priority#LOWEST}.
     *
     * @return the provider's priority
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    default short getPriority() {
        return Priority.LOWEST;
    }

    /**
     * Standard priority levels for compatibility providers and registry registrations.
     *
     * <p>Use these constants as the {@code priority} argument to
     * {@link dev.satyrn.lepidoptera.api.item.EquipmentRegistry#registerEquipment} and
     * {@link dev.satyrn.lepidoptera.api.entity.HungryEntityRegistry#register} overloads,
     * or override {@link Provider#getPriority()} to return one of these values.</p>
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    final class Priority {

        /**
         * The highest possible priority ({@value Short#MAX_VALUE}).
         * Use when your registration must always win regardless of load order.
         *
         * @since 1.0.0-SNAPSHOT.1+1.21.1
         */
        @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
        public static final short HIGHEST = Short.MAX_VALUE;
        /**
         * High priority ({@code 100}).
         *
         * @since 1.0.0-SNAPSHOT.1+1.21.1
         */
        @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
        public static final short HIGH = 100;
        /**
         * Normal (default) priority ({@code 0}).
         * This is the priority used by all no-priority overloads of
         * {@code registerEquipment} and {@code register}.
         *
         * @since 1.0.0-SNAPSHOT.1+1.21.1
         */
        @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
        public static final short NORMAL = 0;
        /**
         * Low priority ({@code -100}).
         *
         * @since 1.0.0-SNAPSHOT.1+1.21.1
         */
        @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
        public static final short LOW = -100;
        /**
         * The lowest possible priority ({@value Short#MIN_VALUE}).
         * This is the default priority returned by {@link Provider#getPriority()}.
         *
         * @since 1.0.0-SNAPSHOT.1+1.21.1
         */
        @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
        public static final short LOWEST = Short.MIN_VALUE;

        @Contract("-> fail")
        private Priority() {
            NotInitializable.staticClass(this);
        }
    }

    /**
     * Compares two compatibility providers by ascending priority.
     * Use {@link Comparator#reversed()} to sort highest-priority-first.
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    final class ProviderComparator implements Comparator<Provider> {

        /**
         * Compares two providers by priority (ascending: lower priority = smaller value).
         *
         * @param o1 the first provider
         * @param o2 the second provider
         *
         * @return a negative integer, zero, or a positive integer as {@code o1}'s priority
         * is less than, equal to, or greater than {@code o2}'s
         *
         * @throws NullPointerException if either argument is {@code null}
         * @since 1.0.0-SNAPSHOT.1+1.21.1
         */
        @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
        public @Override int compare(final Provider o1, final Provider o2) {
            if (o1 == null || o2 == null) {
                throw new NullPointerException("One or both of the providers to compare were null.");
            }
            return Short.compare(o1.getPriority(), o2.getPriority());
        }
    }
}
