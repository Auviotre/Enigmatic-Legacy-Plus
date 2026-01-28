package auviotre.enigmatic.legacy.contents.crafting;

import auviotre.enigmatic.legacy.registries.EnigmaticBlocks;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
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
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
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
import net.neoforged.neoforge.common.util.RecipeMatcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SpellstoneTableRecipe implements Recipe<SpellstoneTableRecipe.Input> {
    final ItemStack result;
    final int debrisCount;
    final NonNullList<Ingredient> ingredients;
    final boolean allDifferent;

    public SpellstoneTableRecipe(ItemStack result, int count, NonNullList<Ingredient> ingredients, boolean flag) {
        this.result = result;
        this.debrisCount = count;
        this.ingredients = ingredients;
        this.allDifferent = flag;
    }

    public boolean matches(@NotNull Input input, Level level) {
        ItemStack core = input.core();
        if (core.isEmpty() || !core.is(EnigmaticItems.SPELLCORE)) return false;
        if (!input.noSpellstone) return false;
        if (input.debris().getCount() < this.debrisCount) return false;
        int size = input.ingredients().size();
        List<ItemStack> items = new ArrayList<>(size);
        for (ItemStack ingredient : input.ingredients())
            if (!ingredient.isEmpty()) items.add(ingredient);
        if (allDifferent) {
            Map<Item, Integer> map = new HashMap<>();
            for (ItemStack stack : items) {
                if (map.containsKey(stack.getItem())) {
                    map.put(stack.getItem(), map.get(stack.getItem()) + 1);
                } else map.put(stack.getItem(), 1);
            }
            for (Integer value : map.values()) {
                if (value > 1) return false;
            }
        }
        return RecipeMatcher.findMatches(items, this.ingredients) != null;
    }

    public NonNullList<ItemStack> getRemainingItems(Input input) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(input.size(), ItemStack.EMPTY);
        input.debris().shrink(this.debrisCount);
        nonnulllist.set(0, input.debris().copy());
        for (int i = 2; i < nonnulllist.size(); ++i) {
            ItemStack item = input.getItem(i);
            if (item.hasCraftingRemainingItem()) {
                nonnulllist.set(i, item.getCraftingRemainingItem());
            }
        }

        return nonnulllist;
    }

    public ItemStack assemble(Input input, HolderLookup.Provider provider) {
        return this.result.copy();
    }

    public boolean canCraftInDimensions(int i, int i1) {
        return true;
    }

    public int getCount() {
        return this.debrisCount;
    }

    public boolean isAllDifferent() {
        return this.allDifferent;
    }

    public NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return this.result;
    }

    public ItemStack getToastSymbol() {
        return EnigmaticBlocks.SPELLSTONE_TABLE.toStack();
    }

    public RecipeSerializer<?> getSerializer() {
        return EnigmaticRecipes.SPELLSTONE_TABLE.get();
    }

    public RecipeType<?> getType() {
        return EnigmaticRecipes.SPELLSTONE_CRAFTING.get();
    }

    public record Input(ItemStack core, ItemStack debris, List<ItemStack> ingredients, boolean noSpellstone) implements RecipeInput {
        public ItemStack getItem(int id) {
            if (id == 0) return core;
            else if (id == 1) return debris;
            else if (id >= size()) throw new IllegalArgumentException("Recipe does not contain slot " + id);
            return ingredients.get(id - 2);
        }

        public int size() {
            return ingredients.size() + 2;
        }
    }

    public static class Serializer implements RecipeSerializer<SpellstoneTableRecipe> {
        public static final StreamCodec<RegistryFriendlyByteBuf, SpellstoneTableRecipe> STREAM_CODEC = StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);
        private static final MapCodec<SpellstoneTableRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                Codec.INT.optionalFieldOf("debris_count", 0).forGetter(recipe -> recipe.debrisCount),
                Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").flatXmap(list -> {
                    Ingredient[] aingredient = list.toArray(Ingredient[]::new);
                    if (aingredient.length == 0) return DataResult.error(() -> "No ingredients for shapeless recipe");
                    else if (aingredient.length > 7)
                        return DataResult.error(() -> "Too many ingredients for shapeless recipe. The maximum is: %s".formatted(7));
                    else return DataResult.success(NonNullList.of(Ingredient.EMPTY, aingredient));
                }, DataResult::success).forGetter(recipe -> recipe.ingredients),
                Codec.BOOL.optionalFieldOf("all_different", false).forGetter(recipe -> recipe.allDifferent)
        ).apply(instance, SpellstoneTableRecipe::new));

        private static SpellstoneTableRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            int count = buffer.readInt();
            int size = buffer.readVarInt();
            NonNullList<Ingredient> list = NonNullList.withSize(size, Ingredient.EMPTY);
            list.replaceAll((ingredient) -> Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
            ItemStack result = ItemStack.STREAM_CODEC.decode(buffer);
            boolean allDifferent = buffer.readBoolean();
            return new SpellstoneTableRecipe(result, count, list, allDifferent);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, SpellstoneTableRecipe recipe) {
            buffer.writeInt(recipe.debrisCount);
            buffer.writeVarInt(recipe.ingredients.size());
            for (Ingredient ingredient : recipe.ingredients)
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
            ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
            buffer.writeBoolean(recipe.allDifferent);
        }

        public MapCodec<SpellstoneTableRecipe> codec() {
            return CODEC;
        }

        public StreamCodec<RegistryFriendlyByteBuf, SpellstoneTableRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

    public static class Builder implements RecipeBuilder {
        private final Map<String, Criterion<?>> criteria;
        private final int debrisCount;
        private final ItemStack result;
        private final NonNullList<Ingredient> ingredients;
        private boolean allDifferent = false;

        private Builder(ItemLike resultItem, int debrisCount) {
            this.result = resultItem.asItem().getDefaultInstance();
            this.debrisCount = debrisCount;
            this.criteria = new LinkedHashMap<>();
            this.ingredients = NonNullList.create();
        }

        public static Builder spell(ItemLike result, int debrisCount) {
            return new Builder(result, debrisCount);
        }

        public Builder allDifferent() {
            this.allDifferent = true;
            return this;
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

        public RecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
            this.criteria.put(name, criterion);
            return this;
        }

        public RecipeBuilder group(@Nullable String s) {
            return this;
        }

        public Item getResult() {
            return result.getItem();
        }

        public void save(RecipeOutput recipeOutput) {
            ResourceLocation location = BuiltInRegistries.ITEM.getKey(this.getResult());
            this.save(recipeOutput, ResourceLocation.fromNamespaceAndPath(location.getNamespace(), "spellstone/" + location.getPath()));
        }

        public void save(@NotNull RecipeOutput recipeOutput, ResourceLocation id) {
            Advancement.Builder builder = recipeOutput.advancement().addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id)).rewards(AdvancementRewards.Builder.recipe(id)).requirements(AdvancementRequirements.Strategy.OR);
            Objects.requireNonNull(builder);
            this.criteria.forEach(builder::addCriterion);
            SpellstoneTableRecipe recipe = new SpellstoneTableRecipe(this.result, this.debrisCount, this.ingredients, this.allDifferent);
            recipeOutput.accept(id, recipe, builder.build(id.withPrefix("recipes/spellstone_table/")));
        }
    }
}
