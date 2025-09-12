package auviotre.enigmatic.legacy.contents.crafting;

import auviotre.enigmatic.legacy.registries.EnigmaticRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class ShapelessNoRemainRecipe extends ShapelessRecipe {
    private final String group;
    private final ItemStack result;
    private final CraftingBookCategory category;
    private final NonNullList<Ingredient> ingredients;

    public ShapelessNoRemainRecipe(String group, CraftingBookCategory category, ItemStack result, NonNullList<Ingredient> ingredients) {
        super(group, category, result, ingredients);
        this.group = group;
        this.result = result;
        this.category = category;
        this.ingredients = ingredients;
    }

    public RecipeSerializer<?> getSerializer() {
        return EnigmaticRecipes.SHAPELESS_NO_REMAIN.get();
    }

    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        return NonNullList.withSize(input.size(), ItemStack.EMPTY);
    }

    public static class Serializer implements RecipeSerializer<ShapelessNoRemainRecipe> {
        public static final StreamCodec<RegistryFriendlyByteBuf, ShapelessNoRemainRecipe> STREAM_CODEC = StreamCodec.of(ShapelessNoRemainRecipe.Serializer::toNetwork, ShapelessNoRemainRecipe.Serializer::fromNetwork);
        private static final MapCodec<ShapelessNoRemainRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> recipe.group),
                CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(recipe -> recipe.category),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").flatXmap(list -> {
                    Ingredient[] aingredient = list.toArray(Ingredient[]::new);
                    if (aingredient.length == 0) return DataResult.error(() -> "No ingredients for shapeless recipe");
                    else if (aingredient.length > 9)
                        return DataResult.error(() -> "Too many ingredients for shapeless recipe. The maximum is: %s".formatted(9));
                    else return DataResult.success(NonNullList.of(Ingredient.EMPTY, aingredient));
                }, DataResult::success).forGetter(recipe -> recipe.ingredients)
        ).apply(instance, ShapelessNoRemainRecipe::new));

        private static ShapelessNoRemainRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            String group = buffer.readUtf();
            CraftingBookCategory category = buffer.readEnum(CraftingBookCategory.class);
            int size = buffer.readVarInt();
            NonNullList<Ingredient> list = NonNullList.withSize(size, Ingredient.EMPTY);
            list.replaceAll((ingredient) -> Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
            ItemStack itemstack = ItemStack.STREAM_CODEC.decode(buffer);
            return new ShapelessNoRemainRecipe(group, category, itemstack, list);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, ShapelessNoRemainRecipe recipe) {
            buffer.writeUtf(recipe.group);
            buffer.writeEnum(recipe.category);
            buffer.writeVarInt(recipe.ingredients.size());
            for (Ingredient ingredient : recipe.ingredients) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
            }
            ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
        }

        public MapCodec<ShapelessNoRemainRecipe> codec() {
            return CODEC;
        }

        public StreamCodec<RegistryFriendlyByteBuf, ShapelessNoRemainRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

    public static class Builder implements RecipeBuilder {
        private final RecipeCategory category;
        private final Item result;
        private final ItemStack resultStack;
        private final NonNullList<Ingredient> ingredients;
        private final Map<String, Criterion<?>> criteria;
        @Nullable
        private String group;

        public Builder(RecipeCategory category, ItemLike result, int count) {
            this(category, new ItemStack(result, count));
        }

        public Builder(RecipeCategory category, ItemStack result) {
            this.ingredients = NonNullList.create();
            this.criteria = new LinkedHashMap<>();
            this.category = category;
            this.result = result.getItem();
            this.resultStack = result;
        }

        public static Builder shapeless(RecipeCategory category, ItemLike result) {
            return new Builder(category, result, 1);
        }

        public static Builder shapeless(RecipeCategory category, ItemLike result, int count) {
            return new Builder(category, result, count);
        }

        public static Builder shapeless(RecipeCategory category, ItemStack result) {
            return new Builder(category, result);
        }

        public Builder requires(TagKey<Item> tag) {
            return this.requires(Ingredient.of(tag));
        }

        public Builder requires(ItemLike item) {
            return this.requires(item, 1);
        }

        public Builder requires(ItemLike item, int quantity) {
            for (int i = 0; i < quantity; ++i) this.requires(Ingredient.of(item));
            return this;
        }

        public Builder requires(Ingredient ingredient) {
            return this.requires(ingredient, 1);
        }

        public Builder requires(Ingredient ingredient, int quantity) {
            for (int i = 0; i < quantity; ++i) this.ingredients.add(ingredient);
            return this;
        }

        public Builder unlockedBy(String name, Criterion<?> criterion) {
            this.criteria.put(name, criterion);
            return this;
        }

        public Builder group(@Nullable String groupName) {
            this.group = groupName;
            return this;
        }

        public Item getResult() {
            return this.result;
        }

        public void save(RecipeOutput recipeOutput, ResourceLocation id) {
            if (this.criteria.isEmpty()) throw new IllegalStateException("No way of obtaining recipe " + id);
            Advancement.Builder advancement$builder = recipeOutput.advancement().addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id)).rewards(AdvancementRewards.Builder.recipe(id)).requirements(AdvancementRequirements.Strategy.OR);
            Objects.requireNonNull(advancement$builder);
            this.criteria.forEach(advancement$builder::addCriterion);
            ShapelessNoRemainRecipe recipe = new ShapelessNoRemainRecipe(Objects.requireNonNullElse(this.group, ""), RecipeBuilder.determineBookCategory(this.category), this.resultStack, this.ingredients);
            recipeOutput.accept(id, recipe, advancement$builder.build(id.withPrefix("recipes/" + this.category.getFolderName() + "/")));
        }
    }
}
