package auviotre.enigmatic.legacy.compat.farmersdelight;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.compat.farmersdelight.contents.block.DivineFruitPieBlock;
import auviotre.enigmatic.legacy.compat.farmersdelight.contents.item.AstralFruitSlice;
import auviotre.enigmatic.legacy.compat.farmersdelight.contents.item.DivineFruitPie;
import auviotre.enigmatic.legacy.compat.farmersdelight.contents.item.EtheriumMachete;
import auviotre.enigmatic.legacy.contents.block.CosmicCake;
import auviotre.enigmatic.legacy.contents.item.generic.BaseItem;
import auviotre.enigmatic.legacy.registries.EnigmaticBlocks;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;
import vectorwing.farmersdelight.common.tag.ModTags;
import vectorwing.farmersdelight.common.utility.ItemUtils;
import vectorwing.farmersdelight.data.builder.CuttingBoardRecipeBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class FDCompat {
    public static void buildRecipes(RecipeOutput output) {
        CuttingBoardRecipeBuilder.cuttingRecipe(Ingredient.of(EnigmaticItems.ASTRAL_FRUIT), Ingredient.of(ModTags.KNIVES), Items.ASTRAL_FRUIT_SLICE, 2)
                .unlockedBy("has_item", has(EnigmaticItems.ASTRAL_FRUIT)).save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Items.ETHERIUM_MACHETE)
                .pattern("D").pattern("E").pattern("R")
                .define('D', net.minecraft.world.item.Items.DIAMOND).define('E', EnigmaticItems.ETHERIUM_INGOT)
                .define('R', EnigmaticItems.ENDER_ROD)
                .unlockedBy("has_item", has(EnigmaticItems.ETHERIUM_INGOT)).save(output);
    }

    public static Criterion<InventoryChangeTrigger.TriggerInstance> has(ItemLike itemLike) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(itemLike));
    }

    protected static Criterion<InventoryChangeTrigger.TriggerInstance> inventoryTrigger(ItemPredicate.Builder... items) {
        return inventoryTrigger(Arrays.stream(items).map(ItemPredicate.Builder::build).toArray(ItemPredicate[]::new));
    }

    protected static Criterion<InventoryChangeTrigger.TriggerInstance> inventoryTrigger(ItemPredicate... predicates) {
        return CriteriaTriggers.INVENTORY_CHANGED.createCriterion(new InventoryChangeTrigger.TriggerInstance(Optional.empty(), InventoryChangeTrigger.TriggerInstance.Slots.ANY, List.of(predicates)));
    }

    public static class Items {
        public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(EnigmaticLegacy.MODID);
        public static final DeferredItem<EtheriumMachete> ETHERIUM_MACHETE = ITEMS.register("etherium_machete", EtheriumMachete::new);
        public static final DeferredItem<Item> COSMIC_CAKE_SLICE = ITEMS.register("cosmic_cake_slice", () -> new Item(BaseItem.defaultProperties().rarity(Rarity.UNCOMMON).food(CosmicCake.FOOD_PROPERTIES)));
        public static final DeferredItem<AstralFruitSlice> ASTRAL_FRUIT_SLICE = ITEMS.register("astral_fruit_slice", AstralFruitSlice::new);
        public static final DeferredItem<DivineFruitPie> DIVINE_FRUIT_PIE = ITEMS.register("divine_fruit_pie", DivineFruitPie::new);
        public static final DeferredItem<DivineFruitPie.PieBlock> DIVINE_FRUIT_PIE_BLOCK = ITEMS.register("divine_fruit_pie_block", () -> new DivineFruitPie.PieBlock(Blocks.DIVINE_FRUIT_PIE.get()));
    }

    public static class Blocks {
        public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(EnigmaticLegacy.MODID);
        public static final DeferredBlock<DivineFruitPieBlock> DIVINE_FRUIT_PIE = BLOCKS.register("divine_fruit_pie_block", DivineFruitPieBlock::new);
    }

    public static class Tabs {
        public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EnigmaticLegacy.MODID);

        public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = TABS.register("delicacy_tab", () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.enigmaticdelicacy").withStyle(ChatFormatting.DARK_PURPLE))
                .icon(Items.ASTRAL_FRUIT_SLICE::toStack)
                .displayItems((parameters, output) -> {
                    output.accept(Items.ETHERIUM_MACHETE.get());
                    output.accept(Items.COSMIC_CAKE_SLICE.get());
                    output.accept(Items.ASTRAL_FRUIT_SLICE.get());
                    output.accept(Items.DIVINE_FRUIT_PIE_BLOCK.get());
                    output.accept(Items.DIVINE_FRUIT_PIE.get());
                }).build());
    }

    public static class Handler {
        @SubscribeEvent
        public void onCakeInteraction(PlayerInteractEvent.@NotNull RightClickBlock event) {
            Player player = event.getEntity();
            ItemStack toolStack = player.getItemInHand(event.getHand());
            Level level = event.getLevel();
            BlockPos pos = event.getPos();
            BlockState state = event.getLevel().getBlockState(pos);
            if (toolStack.is(Items.ETHERIUM_MACHETE.get())) {
                if (state.is(EnigmaticBlocks.COSMIC_CAKE)) {
                    int bites = state.getValue(CakeBlock.BITES);
                    if (bites < 6) {
                        level.setBlock(pos, state.setValue(CakeBlock.BITES, bites + 1), Block.UPDATE_ALL);
                        ItemUtils.spawnItemEntity(level, new ItemStack(Items.COSMIC_CAKE_SLICE.get()), (double) pos.getX() + 0.5, (double) pos.getY() + 0.2, (double) pos.getZ() + (double) bites * 0.1, 0.0, 0.0, -0.05);
                        level.playSound(null, pos, SoundEvents.WOOL_BREAK, SoundSource.PLAYERS, 0.8F, 0.8F);
                        event.setCancellationResult(InteractionResult.SUCCESS);
                        event.setCanceled(true);
                    }
                }
            }
        }
    }
}
