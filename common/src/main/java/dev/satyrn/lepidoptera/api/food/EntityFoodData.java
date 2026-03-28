package dev.satyrn.lepidoptera.api.food;

import dev.satyrn.lepidoptera.LepidopteraAPI;
import dev.satyrn.lepidoptera.api.entity.LivingEntityExtensions;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Custom hunger/saturation system for non-player living entities.
 *
 * <p>Mirrors vanilla's {@code FoodData} semantics: food level drains exhaustion, exhaustion
 * drains saturation, and saturation depletion eventually reduces food level. Starvation
 * damage is gated on the {@code doAnimalStarvation} game rule and world difficulty.</p>
 *
 * <p>Attach one instance per entity (e.g. as an extra field via mixin) and call
 * {@link #tick(LivingEntity)} each game tick from the entity's tick method.</p>
 *
 * @since 0.4.0+1.19.2
 */
@ApiStatus.AvailableSince("0.4.0+1.19.2")
public class EntityFoodData {
    final static int MAX_FOOD_LEVEL = 20;

    private int foodLevel = MAX_FOOD_LEVEL;
    private float saturationLevel = 5.0F;
    private float exhaustionLevel;
    private int tickTimer;
    private int lastFoodLevel = MAX_FOOD_LEVEL;

    // Food damage and starvation are difficulty-dependent now

    /**
     * Adds food and saturation directly, as if the entity ate food with the given stats.
     * Food level is capped at 20; saturation is capped at the current food level.
     *
     * @param level      the food units to add
     * @param saturation the saturation modifier (effective saturation added = {@code level * saturation * 2})
     *
     * @since 0.4.0+1.19.2
     */
    @ApiStatus.AvailableSince("0.4.0+1.19.2")
    @Contract(mutates = "this")
    public void eat(final int level, final float saturation) {
        this.foodLevel = Math.min(level + this.foodLevel, MAX_FOOD_LEVEL);
        this.saturationLevel = Math.min(this.saturationLevel + (level * saturation * 2F), this.foodLevel);
    }

    /**
     * Feeds the entity the given item stack, deriving food and saturation from its
     * {@link DataComponents#FOOD} component. Does nothing if the item has no food component.
     *
     * @param stack the item stack being eaten
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @ApiStatus.AvailableSince("1.0.0-SNAPSHOT.1+1.21.1")
    @Contract(mutates = "this")
    public boolean eat(final ItemStack stack) {
        final @Nullable var foodProperties = stack.get(DataComponents.FOOD);
        if (foodProperties != null) {
            this.eat(foodProperties.nutrition(), foodProperties.saturation());
            return true;
        }
        return false;
    }

    /**
     * Feeds the entity the given item stack
     *
     * @param item  The item
     * @param stack The item stack
     *
     * @since 0.4.0+1.19.2
     */
    @ApiStatus.AvailableSince("0.4.0+1.19.2")
    @ApiStatus.Obsolete(since = "1.0.0-SNAPSHOT.1+1.21.1")
    @Deprecated(since = "1.0.0-SNAPSHOT.1+1.21.1", forRemoval = true)
    public void eat(final Item item, final ItemStack stack) {
        eat(stack);
    }

    /**
     * Advances the food simulation by one game tick for the given entity.
     *
     * <p>Handles exhaustion drain, saturation depletion, natural regeneration
     * (respecting the {@code naturalRegeneration} game rule), and starvation damage
     * (respecting the {@code doAnimalStarvation} game rule and world difficulty).</p>
     *
     * @param entity the entity whose food data is being ticked
     *
     * @since 0.4.0+1.19.2
     */
    @ApiStatus.AvailableSince(value = "0.4.0+1.19.2")
    @Contract(mutates = "this, param")
    public void tick(final LivingEntity entity) {
        final var difficulty = entity.level().getDifficulty();
        this.lastFoodLevel = this.foodLevel;
        if (this.exhaustionLevel > 4F) {
            this.exhaustionLevel -= 4F;
            if (this.saturationLevel > 0F) {
                this.saturationLevel = Math.max(this.saturationLevel - 1F, 0F);
            } else if (difficulty != Difficulty.PEACEFUL) {
                this.foodLevel = Math.max(this.foodLevel - 1, 0);
            }
        }

        final boolean isRegenEnabled = entity.level().getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION);
        final boolean isEntityStarvationEnabled = entity.level()
                .getGameRules()
                .getBoolean(Objects.requireNonNull(LepidopteraAPI.RULE_ENTITY_STARVATION));
        if (isRegenEnabled &&
                this.saturationLevel > 0F &&
                ((LivingEntityExtensions) entity).isHurt() &&
                this.foodLevel >= 20) {
            this.tickTimer++;
            if (this.tickTimer >= 10) {
                float healAmount = Math.min(this.saturationLevel, 6F);
                entity.heal(healAmount / 6F);
                this.addExhaustion(healAmount);
                this.tickTimer = 0;
            }
        } else if (isRegenEnabled && this.foodLevel >= 18 && ((LivingEntityExtensions) entity).isHurt()) {
            this.tickTimer++;
            if (this.tickTimer >= 80) {
                entity.heal(1F);
                this.addExhaustion(6F);
                this.tickTimer = 0;
            }
        } else if (this.foodLevel <= 0) {
            this.tickTimer++;
            if (this.tickTimer >= 80) {
                if (isEntityStarvationEnabled &&
                        (entity.getHealth() > 10F ||
                                difficulty == Difficulty.HARD ||
                                entity.getHealth() > 1F && difficulty == Difficulty.NORMAL)) {
                    entity.hurt(entity.damageSources().starve(), 1.0F);
                }

                this.tickTimer = 0;
            }
        } else {
            this.tickTimer = 0;
        }
    }

    /**
     * Reads food state from a {@link CompoundTag}, restoring food level, tick timer,
     * saturation, and exhaustion saved by {@link #addAdditionalSaveData(CompoundTag)}.
     *
     * @param compoundTag the tag to read from
     *
     * @since 0.4.0+1.19.2
     */
    @ApiStatus.AvailableSince("0.4.0+1.19.2")
    @Contract(mutates = "this")
    public void readAdditionalSaveData(final CompoundTag compoundTag) {
        if (compoundTag.contains("foodLevel", 99)) {
            this.foodLevel = compoundTag.getInt("foodLevel");
            this.tickTimer = compoundTag.getInt("foodTickTimer");
            this.saturationLevel = compoundTag.getFloat("foodSaturationLevel");
            this.exhaustionLevel = compoundTag.getFloat("foodExhaustionLevel");
        }
    }

    /**
     * Writes food state into a {@link CompoundTag} for persistence.
     *
     * @param compoundTag the tag to write into
     *
     * @since 0.4.0+1.19.2
     */
    @ApiStatus.AvailableSince("0.4.0+1.19.2")
    @Contract(mutates = "param")
    public void addAdditionalSaveData(final CompoundTag compoundTag) {
        compoundTag.putInt("foodLevel", this.foodLevel);
        compoundTag.putInt("foodTickTimer", this.tickTimer);
        compoundTag.putFloat("foodSaturationLevel", this.saturationLevel);
        compoundTag.putFloat("foodExhaustionLevel", this.exhaustionLevel);
    }

    /**
     * Gets the current food level.
     *
     * @return The food level.
     *
     * @since 0.4.0+1.19.2
     */
    @ApiStatus.AvailableSince("0.4.0+1.19.2")
    @Contract(pure = true)
    public int getFoodLevel() {
        return this.foodLevel;
    }

    /**
     * Directly sets the food level without triggering any side effects.
     *
     * @param foodLevel the new food level
     *
     * @since 0.4.0+1.19.2
     */
    @ApiStatus.AvailableSince("0.4.0+1.19.2")
    @Contract(mutates = "this")
    public void setFoodLevel(int foodLevel) {
        this.foodLevel = foodLevel;
    }

    /**
     * Returns the food level from the previous tick, used to detect changes.
     *
     * @return the food level recorded at the start of the last {@link #tick(LivingEntity)} call
     *
     * @since 0.4.0+1.19.2
     */
    @ApiStatus.AvailableSince("0.4.0+1.19.2")
    @Contract(pure = true)
    public int getLastFoodLevel() {
        return this.lastFoodLevel;
    }

    /**
     * Returns {@code true} if the entity's food level is below the maximum (20).
     *
     * @return {@code true} if food level is less than 20
     *
     * @since 0.4.0+1.19.2
     */
    @ApiStatus.AvailableSince("0.4.0+1.19.2")
    @Contract(pure = true)
    public boolean needsFood() {
        return this.foodLevel < MAX_FOOD_LEVEL;
    }

    /**
     * Adds to the exhaustion level, capped at 40.
     * Exhaustion above 4 per tick drains saturation and eventually food level.
     *
     * @param exhaustion the exhaustion amount to add
     *
     * @since 0.4.0+1.19.2
     */
    @ApiStatus.AvailableSince("0.4.0+1.19.2")
    @Contract(mutates = "this")
    public void addExhaustion(float exhaustion) {
        this.exhaustionLevel = Math.min(this.exhaustionLevel + exhaustion, 40F);
    }

    /**
     * Returns the current exhaustion level.
     *
     * @return exhaustion in the range {@code [0, 40]}
     *
     * @since 0.4.0+1.19.2
     */
    @ApiStatus.AvailableSince("0.4.0+1.19.2")
    @Contract(pure = true)
    public float getExhaustionLevel() {
        return this.exhaustionLevel;
    }

    /**
     * Returns the current saturation level.
     * Saturation acts as a buffer: it depletes before the food level itself decreases.
     *
     * @return the saturation level
     *
     * @since 0.4.0+1.19.2
     */
    @ApiStatus.AvailableSince("0.4.0+1.19.2")
    @Contract(pure = true)
    public float getSaturationLevel() {
        return this.saturationLevel;
    }

    /**
     * Directly sets the saturation level without triggering any side effects.
     *
     * @param saturationLevel the new saturation level
     *
     * @since 0.4.0+1.19.2
     */
    @ApiStatus.AvailableSince("0.4.0+1.19.2")
    @Contract(mutates = "this")
    public void setSaturation(float saturationLevel) {
        this.saturationLevel = saturationLevel;
    }

    /**
     * Directly sets the exhaustion level without triggering any side effects.
     *
     * @param exhaustionLevel the new exhaustion level
     *
     * @since 0.4.0+1.19.2
     */
    @ApiStatus.AvailableSince("0.4.0+1.19.2")
    @Contract(mutates = "this")
    public void setExhaustion(float exhaustionLevel) {
        this.exhaustionLevel = exhaustionLevel;
    }
}
