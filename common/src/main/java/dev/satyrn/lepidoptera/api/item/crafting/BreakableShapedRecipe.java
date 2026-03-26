package dev.satyrn.lepidoptera.api.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.satyrn.lepidoptera.api.annotations.Api;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.Contract;

/**
 * A shaped crafting recipe that applies damage to any damageable crafting-remaining items
 * instead of returning them undamaged.
 *
 * <p>Register using the serializer ID {@code lepidoptera_api:crafting_shaped_breakable}.
 * The JSON format is identical to a standard shaped recipe, with one additional optional field:
 * <pre>{@code
 * {
 *   "type": "lepidoptera_api:crafting_shaped_breakable",
 *   "damage": 1,
 *   "group": "...",
 *   "category": "...",
 *   "pattern": [...],
 *   "key": {...},
 *   "result": {...}
 * }
 * }</pre>
 * {@code "damage"} defaults to {@code 1} if omitted.
 *
 * @since 1.0.0-SNAPSHOT.1+1.21.1
 */
@Api("1.0.0-SNAPSHOT.1+1.21.1")
public class BreakableShapedRecipe extends ShapedRecipe {

    /**
     * The recipe serializer for this recipe type.
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @Api("1.0.0-SNAPSHOT.1+1.21.1")
    public static final RecipeSerializer<BreakableShapedRecipe> SERIALIZER = new Serializer();

    // Fields are stored locally for codec access since ShapedRecipe's fields are not accessible to subclasses.
    private final String group;
    private final CraftingBookCategory category;
    private final ShapedRecipePattern pattern;
    private final ItemStack result;
    private final int damage;

    /**
     * Creates a breakable shaped recipe with the default notification setting ({@code true}).
     *
     * @param group    the recipe group (may be empty)
     * @param category the crafting book category
     * @param pattern  the shaped pattern
     * @param result   the output item stack
     * @param damage   the damage applied to damageable crafting-remaining items
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @Api("1.0.0-SNAPSHOT.1+1.21.1")
    public BreakableShapedRecipe(final String group,
                                 final CraftingBookCategory category,
                                 final ShapedRecipePattern pattern,
                                 final ItemStack result,
                                 final int damage) {
        this(group, category, pattern, result, damage, true);
    }

    /**
     * Creates a breakable shaped recipe.
     *
     * @param group            the recipe group (may be empty)
     * @param category         the crafting book category
     * @param pattern          the shaped pattern
     * @param result           the output item stack
     * @param damage           the damage applied to damageable crafting-remaining items
     * @param showNotification whether to show the recipe unlock toast notification
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @Api("1.0.0-SNAPSHOT.1+1.21.1")
    public BreakableShapedRecipe(final String group,
                                 final CraftingBookCategory category,
                                 final ShapedRecipePattern pattern,
                                 final ItemStack result,
                                 final int damage,
                                 final boolean showNotification) {
        super(group, category, pattern, result, showNotification);
        this.group = group;
        this.category = category;
        this.pattern = pattern;
        this.result = result;
        this.damage = damage;
    }

    /**
     * Returns remaining items after crafting. For any ingredient whose crafting-remaining item
     * is damageable, the remaining item is returned with the source item's damage value plus
     * {@link #damage} applied. If the item would break, the slot is left empty.
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @Api("1.0.0-SNAPSHOT.1+1.21.1")
    public @Override NonNullList<ItemStack> getRemainingItems(final CraftingInput input) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(input.size(), ItemStack.EMPTY);

        for (int i = 0; i < remaining.size(); i++) {
            ItemStack source = input.getItem(i);

            ItemStack remainStack = CraftingUtils.damageAndDepleteCrafting(source, this.damage);

            if (!remainStack.isEmpty()) {
                remaining.set(i, remainStack);
            }
        }

        return remaining;
    }

    /**
     * Returns the {@link #SERIALIZER} for this recipe type.
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @Api("1.0.0-SNAPSHOT.1+1.21.1")
    public @Override RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    /**
     * Codec-based serializer for {@link BreakableShapedRecipe}.
     * Handles both persistent JSON encoding and network packet encoding.
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @Api("1.0.0-SNAPSHOT.1+1.21.1")
    public static class Serializer implements RecipeSerializer<BreakableShapedRecipe> {

        /**
         * {@link MapCodec} for reading and writing {@link BreakableShapedRecipe} to/from JSON.
         *
         * @since 1.0.0-SNAPSHOT.1+1.21.1
         */
        @Api("1.0.0-SNAPSHOT.1+1.21.1") public static final MapCodec<BreakableShapedRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(Codec.STRING.optionalFieldOf("group", "").forGetter(r -> r.group),
                                CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.MISC)
                                        .forGetter(r -> r.category), ShapedRecipePattern.MAP_CODEC.forGetter(r -> r.pattern),
                                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(r -> r.result),
                                Codec.INT.optionalFieldOf("damage", 1).forGetter(r -> r.damage),
                                Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(ShapedRecipe::showNotification))
                        .apply(instance, BreakableShapedRecipe::new));

        /**
         * {@link StreamCodec} for reading and writing {@link BreakableShapedRecipe} over the network.
         *
         * @since 1.0.0-SNAPSHOT.1+1.21.1
         */
        @Api("1.0.0-SNAPSHOT.1+1.21.1") public static final StreamCodec<RegistryFriendlyByteBuf, BreakableShapedRecipe> STREAM_CODEC = StreamCodec.of(
                BreakableShapedRecipe.Serializer::toNetwork, BreakableShapedRecipe.Serializer::fromNetwork);

        /**
         * Gets the codec instance.
         *
         * @since 1.0.0-SNAPSHOT.1+1.21.1
         */
        @Api("1.0.0-SNAPSHOT.1+1.21.1")
        public @Override MapCodec<BreakableShapedRecipe> codec() {
            return CODEC;
        }

        /**
         * Gets the stream codec instance.
         *
         * @since 1.0.0-SNAPSHOT.1+1.21.1
         */
        @Api("1.0.0-SNAPSHOT.1+1.21.1")
        public @Override StreamCodec<RegistryFriendlyByteBuf, BreakableShapedRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        @Contract("_ -> new")
        private static BreakableShapedRecipe fromNetwork(final RegistryFriendlyByteBuf buffer) {
            String group = buffer.readUtf();
            CraftingBookCategory craftingBookCategory = buffer.readEnum(CraftingBookCategory.class);
            ShapedRecipePattern shapedRecipePattern = ShapedRecipePattern.STREAM_CODEC.decode(buffer);
            ItemStack itemStack = ItemStack.STREAM_CODEC.decode(buffer);
            int damage = buffer.readInt();
            boolean showNotification = buffer.readBoolean();
            return new BreakableShapedRecipe(group, craftingBookCategory, shapedRecipePattern, itemStack, damage,
                    showNotification);
        }

        private static void toNetwork(final RegistryFriendlyByteBuf buffer, final BreakableShapedRecipe recipe) {
            buffer.writeUtf(recipe.group);
            buffer.writeEnum(recipe.category);
            ShapedRecipePattern.STREAM_CODEC.encode(buffer, recipe.pattern);
            ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
            buffer.writeInt(recipe.damage);
            buffer.writeBoolean(recipe.showNotification());
        }
    }
}
