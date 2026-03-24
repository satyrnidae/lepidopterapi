package dev.satyrn.lepidoptera.api.entity;

import dev.satyrn.lepidoptera.api.annotations.Api;
import dev.satyrn.lepidoptera.api.food.EntityFoodData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Contract;

/**
 * Extension interface mixed into {@link LivingEntity} to expose additional behavior.
 *
 * <p>Use {@link #cast(LivingEntity)} to obtain a typed reference to the injected
 * methods on any {@code LivingEntity} instance.</p>
 *
 * <p>The hunger methods ({@link #getFoodData()} and {@link #addExhaustion(float)}) are
 * always available on every living entity. Food data is only ticked for entity types
 * registered in {@link HungryEntityRegistry}.</p>
 */
@Api
public interface LivingEntityExtensions {

    /**
     * Returns {@code true} if the entity is currently hurt (i.e. its health is below
     * its maximum health).
     *
     * @return {@code true} if the entity needs healing
     */
    @Contract(pure = true)
    @Api default boolean isHurt() {
        throw new UnsupportedOperationException("Not Implemented");
    }

    /**
     * Returns the entity's {@link EntityFoodData} instance, which tracks food level,
     * saturation, exhaustion, and the starvation tick timer.
     *
     * <p>This is always available on every {@link LivingEntity}. Food data is only
     * advanced each tick for entity types registered in {@link HungryEntityRegistry}.</p>
     *
     * @return the food data for this entity
     */
    @Contract(pure = true)
    @Api default EntityFoodData getFoodData() {
        throw new UnsupportedOperationException("Not Implemented");
    }

    /**
     * Feeds the entity the given item stack, deriving food and saturation from its
     * {@link net.minecraft.core.component.DataComponents#FOOD} component.
     * Does nothing if the item has no food component.
     *
     * <p>The default implementation accepts the item only if the entity
     * {@link EntityFoodData#needsFood() needs food}, delegating to
     * {@link EntityFoodData#eat(ItemStack)}.
     * Override to restrict feeding to specific items (e.g. an entity's favourite foods)
     * and return {@code false} to signal that the item was rejected. Implementers should generally filter by failing
     * out with {@code false} then delegate to the interface default implementation at the end.</p>
     *
     * @param itemStack the item stack being eaten
     * @return {@code true} if the entity accepted and ate the food; {@code false} if it was full or rejected
     */
    @Contract(mutates = "this")
    @Api default boolean eat(final ItemStack itemStack) {
        final EntityFoodData foodData = this.getFoodData();
        if (foodData.needsFood()) {
            return getFoodData().eat(itemStack);
        }
        return false;
    }

    /**
     * Adds to the entity's exhaustion level, which drains saturation and eventually
     * food level over time.
     *
     * @param exhaustion the amount of exhaustion to add (clamped to a maximum of 40)
     */
    @Api default void addExhaustion(final float exhaustion) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    /**
     * Casts a {@link LivingEntity} to {@link LivingEntityExtensions}.
     *
     * <p>Safe because the mixin always injects this interface onto {@code LivingEntity}.</p>
     *
     * @param livingEntity the entity to cast
     * @return the entity as a {@link LivingEntityExtensions}
     */
    @Contract(pure = true)
    @Api static LivingEntityExtensions cast(final LivingEntity livingEntity) {
        return (LivingEntityExtensions) livingEntity;
    }
}
