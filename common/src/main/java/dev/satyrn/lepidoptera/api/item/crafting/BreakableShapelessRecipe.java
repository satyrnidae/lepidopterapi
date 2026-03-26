package dev.satyrn.lepidoptera.api.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.satyrn.lepidoptera.api.annotations.Api;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

/**
 * A shapeless crafting recipe that applies damage to any damageable crafting-remaining items
 * instead of returning them undamaged.
 *
 * <p>Register using the serializer ID {@code lepidoptera_api:crafting_shapeless_breakable}.
 * The JSON format is identical to a standard shapeless recipe, with one additional optional field:
 * <pre>{@code
 * {
 *   "type": "lepidoptera_api:crafting_shapeless_breakable",
 *   "damage": 1,
 *   "group": "...",
 *   "category": "...",
 *   "ingredients": [...],
 *   "result": {...}
 * }
 * }</pre>
 * {@code "damage"} defaults to {@code 1} if omitted.
 *
 * @since 1.0.0-SNAPSHOT.1+1.21.1
 */
@Api("1.0.0-SNAPSHOT.1+1.21.1")
public class BreakableShapelessRecipe extends ShapelessRecipe {

    /**
     * The recipe serializer for this recipe type.
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @Api("1.0.0-SNAPSHOT.1+1.21.1") public static final Serializer SERIALIZER = new Serializer();

    // Fields are stored locally for codec access since ShapelessRecipe's fields are package-private.
    private final String group;
    private final CraftingBookCategory category;
    private final ItemStack result;
    private final NonNullList<Ingredient> ingredients;
    private final int damage;

    /**
     * Creates a breakable shapeless recipe.
     *
     * @param group       the recipe group (may be empty)
     * @param category    the crafting book category
     * @param result      the output item stack
     * @param ingredients the list of required ingredients (1–9 entries)
     * @param damage      the damage applied to damageable crafting-remaining items
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @Api("1.0.0-SNAPSHOT.1+1.21.1")
    public BreakableShapelessRecipe(final String group,
                                    final CraftingBookCategory category,
                                    final ItemStack result,
                                    final NonNullList<Ingredient> ingredients,
                                    final int damage) {
        super(group, category, result, ingredients);
        this.group = group;
        this.category = category;
        this.result = result;
        this.ingredients = ingredients;
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
     * Codec-based serializer for {@link BreakableShapelessRecipe}.
     * Handles both persistent JSON encoding and network packet encoding.
     *
     * @since 1.0.0-SNAPSHOT.1+1.21.1
     */
    @Api("1.0.0-SNAPSHOT.1+1.21.1")
    public static class Serializer implements RecipeSerializer<BreakableShapelessRecipe> {

        /**
         * {@link MapCodec} for reading and writing {@link BreakableShapelessRecipe} to/from JSON.
         *
         * @since 1.0.0-SNAPSHOT.1+1.21.1
         */
        @Api("1.0.0-SNAPSHOT.1+1.21.1") public static final MapCodec<BreakableShapelessRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(Codec.STRING.optionalFieldOf("group", "").forGetter(r -> r.group),
                                CraftingBookCategory.CODEC.fieldOf("category")
                                        .orElse(CraftingBookCategory.MISC)
                                        .forGetter(r -> r.category),
                                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(r -> r.result),
                                Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").flatXmap(list -> {
                                    Ingredient[] arr = list.toArray(Ingredient[]::new);
                                    if (arr.length == 0) {
                                        return DataResult.error(() -> "No ingredients for shapeless recipe");
                                    } else if (arr.length > 9) {
                                        return DataResult.error(() -> "Too many ingredients for shapeless recipe");
                                    }
                                    return DataResult.success(NonNullList.of(Ingredient.EMPTY, arr));
                                }, DataResult::success).forGetter(r -> r.ingredients),
                                Codec.INT.optionalFieldOf("damage", 1).forGetter(r -> r.damage))
                        .apply(instance, BreakableShapelessRecipe::new));

        /**
         * {@link StreamCodec} for reading and writing {@link BreakableShapelessRecipe} over the network.
         *
         * @since 1.0.0-SNAPSHOT.1+1.21.1
         */
        @Api("1.0.0-SNAPSHOT.1+1.21.1") public static final StreamCodec<RegistryFriendlyByteBuf, BreakableShapelessRecipe> STREAM_CODEC = StreamCodec.of(
                BreakableShapelessRecipe.Serializer::toNetwork, BreakableShapelessRecipe.Serializer::fromNetwork);


        /**
         * Gets the codec instance
         *
         * @since 1.0.0-SNAPSHOT.1+1.21.1
         */
        @Api("1.0.0-SNAPSHOT.1+1.21.1")
        public @Override MapCodec<BreakableShapelessRecipe> codec() {
            return CODEC;
        }

        /**
         * Gets the stream codec instance
         *
         * @since 1.0.0-SNAPSHOT.1+1.21.1
         */
        @Api("1.0.0-SNAPSHOT.1+1.21.1")
        public StreamCodec<RegistryFriendlyByteBuf, BreakableShapelessRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static BreakableShapelessRecipe fromNetwork(final RegistryFriendlyByteBuf buf) {
            String group = buf.readUtf();
            CraftingBookCategory category = buf.readEnum(CraftingBookCategory.class);
            int size = buf.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.withSize(size, Ingredient.EMPTY);
            ingredients.replaceAll(ignored -> Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
            ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
            int damage = buf.readVarInt();
            return new BreakableShapelessRecipe(group, category, result, ingredients, damage);
        }

        private static void toNetwork(final RegistryFriendlyByteBuf buf, final BreakableShapelessRecipe recipe) {
            buf.writeUtf(recipe.group);
            buf.writeEnum(recipe.category);
            buf.writeVarInt(recipe.ingredients.size());
            for (Ingredient ingredient : recipe.ingredients) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ingredient);
            }
            ItemStack.STREAM_CODEC.encode(buf, recipe.result);
            buf.writeVarInt(recipe.damage);
        }
    }
}
