package auviotre.enigmatic.legacy.contents.crafting;

import auviotre.enigmatic.legacy.api.item.ITaintable;
import auviotre.enigmatic.legacy.registries.EnigmaticRecipes;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
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
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CursedShapedRecipe extends ShapedRecipe {
    private final String group;
    private final ItemStack result;
    private final CraftingBookCategory category;
    private final boolean showNotification;

    public CursedShapedRecipe(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack result, boolean showNotification) {
        super(group, category, pattern, result, showNotification);
        this.group = group;
        this.result = result;
        this.category = category;
        this.showNotification = showNotification;
    }

    public boolean matches(CraftingInput input, Level level) {
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.getItem() instanceof ITaintable && !ITaintable.isTainted(stack))
                return false;
        }
        return super.matches(input, level);
    }

    public RecipeSerializer<?> getSerializer() {
        return EnigmaticRecipes.CURSED_SHAPED.get();
    }

    public static class Serializer implements RecipeSerializer<CursedShapedRecipe> {
        public static final MapCodec<CursedShapedRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> recipe.group), CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter((recipe) -> recipe.category),
                ShapedRecipePattern.MAP_CODEC.forGetter(recipe -> recipe.pattern),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(recipe -> recipe.showNotification)
        ).apply(instance, CursedShapedRecipe::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, CursedShapedRecipe> STREAM_CODEC = StreamCodec.of(CursedShapedRecipe.Serializer::toNetwork, CursedShapedRecipe.Serializer::fromNetwork);

        private static CursedShapedRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            String group = buffer.readUtf();
            CraftingBookCategory category = buffer.readEnum(CraftingBookCategory.class);
            ShapedRecipePattern pattern = ShapedRecipePattern.STREAM_CODEC.decode(buffer);
            ItemStack result = ItemStack.STREAM_CODEC.decode(buffer);
            boolean flag = buffer.readBoolean();
            return new CursedShapedRecipe(group, category, pattern, result, flag);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, CursedShapedRecipe recipe) {
            ShapedRecipe.Serializer.STREAM_CODEC.encode(buffer, recipe);
        }

        public MapCodec<CursedShapedRecipe> codec() {
            return CODEC;
        }

        public StreamCodec<RegistryFriendlyByteBuf, CursedShapedRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

    public static class Builder implements RecipeBuilder {
        private final RecipeCategory category;
        private final Item result;
        private final ItemStack resultStack;
        private final List<String> rows;
        private final Map<Character, Ingredient> key;
        private final Map<String, Criterion<?>> criteria;
        @javax.annotation.Nullable
        private String group;
        private boolean showNotification;

        public Builder(RecipeCategory category, ItemLike result, int count) {
            this(category, new ItemStack(result, count));
        }

        public Builder(RecipeCategory p_249996_, ItemStack result) {
            this.rows = Lists.newArrayList();
            this.key = Maps.newLinkedHashMap();
            this.criteria = new LinkedHashMap<>();
            this.showNotification = true;
            this.category = p_249996_;
            this.result = result.getItem();
            this.resultStack = result;
        }

        public static Builder shaped(RecipeCategory category, ItemLike result) {
            return shaped(category, result, 1);
        }

        public static Builder shaped(RecipeCategory category, ItemLike result, int count) {
            return new Builder(category, result, count);
        }

        public static Builder shaped(RecipeCategory p_251325_, ItemStack result) {
            return new Builder(p_251325_, result);
        }

        public Builder define(Character symbol, TagKey<Item> tag) {
            return this.define(symbol, Ingredient.of(tag));
        }

        public Builder define(Character symbol, ItemLike item) {
            return this.define(symbol, Ingredient.of(item));
        }

        public Builder define(Character symbol, Ingredient ingredient) {
            if (this.key.containsKey(symbol)) {
                throw new IllegalArgumentException("Symbol '" + symbol + "' is already defined!");
            } else if (symbol == ' ') {
                throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
            } else {
                this.key.put(symbol, ingredient);
                return this;
            }
        }

        public Builder pattern(String pattern) {
            if (!this.rows.isEmpty() && pattern.length() != this.rows.getFirst().length()) {
                throw new IllegalArgumentException("Pattern must be the same width on every line!");
            } else {
                this.rows.add(pattern);
                return this;
            }
        }

        public Builder unlockedBy(String name, Criterion<?> criterion) {
            this.criteria.put(name, criterion);
            return this;
        }

        public Builder group(@Nullable String groupName) {
            this.group = groupName;
            return this;
        }

        public Builder showNotification(boolean showNotification) {
            this.showNotification = showNotification;
            return this;
        }

        public Item getResult() {
            return this.result;
        }

        public void save(RecipeOutput recipeOutput, ResourceLocation id) {
            ShapedRecipePattern shapedrecipepattern = this.ensureValid(id);
            Advancement.Builder advancement$builder = recipeOutput.advancement().addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id)).rewards(AdvancementRewards.Builder.recipe(id)).requirements(AdvancementRequirements.Strategy.OR);
            Objects.requireNonNull(advancement$builder);
            this.criteria.forEach(advancement$builder::addCriterion);
            CursedShapedRecipe recipe = new CursedShapedRecipe(Objects.requireNonNullElse(this.group, ""), RecipeBuilder.determineBookCategory(this.category), shapedrecipepattern, this.resultStack, this.showNotification);
            recipeOutput.accept(id, recipe, advancement$builder.build(id.withPrefix("recipes/" + this.category.getFolderName() + "/")));
        }

        private ShapedRecipePattern ensureValid(ResourceLocation location) {
            if (this.criteria.isEmpty()) {
                throw new IllegalStateException("No way of obtaining recipe " + location);
            } else {
                return ShapedRecipePattern.of(this.key, this.rows);
            }
        }
    }
}
