package auviotre.enigmatic.legacy.contents.block;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.registries.EnigmaticBlocks;
import auviotre.enigmatic.legacy.registries.EnigmaticSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

public class CosmicCake extends CakeBlock {
    public static final FoodProperties FOOD_PROPERTIES = Foods.GOLDEN_CARROT;
    protected static final VoxelShape[] SHAPE_BY_BITE = new VoxelShape[]{
            Block.box(1.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D),
            Block.box(1.0D, 0.0D, 2.5D, 15.0D, 8.0D, 15.0D),
            Block.box(1.0D, 0.0D, 4.5D, 15.0D, 8.0D, 15.0D),
            Block.box(1.0D, 0.0D, 6.5D, 15.0D, 8.0D, 15.0D),
            Block.box(1.0D, 0.0D, 8.5D, 15.0D, 8.0D, 15.0D),
            Block.box(1.0D, 0.0D, 10.5D, 15.0D, 8.0D, 15.0D),
            Block.box(1.0D, 0.0D, 12.0D, 15.0D, 8.0D, 15.0D)
    };

    public CosmicCake() {
        super(Properties.ofFullCopy(Blocks.CAKE));
    }

    protected static InteractionResult eat(LevelAccessor level, BlockPos pos, BlockState state, Player player) {
        if (!player.canEat(false))
            return InteractionResult.PASS;
        else if (state.getValue(BITES) > 5) return InteractionResult.PASS;
        else {
            player.awardStat(Stats.EAT_CAKE_SLICE);
            player.getFoodData().eat(FOOD_PROPERTIES.nutrition(), FOOD_PROPERTIES.saturation());
            int i = state.getValue(BITES);
            level.gameEvent(player, GameEvent.EAT, pos);
            level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, pos, Block.getId(state));
            level.setBlock(pos, state.setValue(BITES, i + 1), UPDATE_ALL);
            level.playSound(null, player.blockPosition(), SoundEvents.GENERIC_EAT, SoundSource.BLOCKS, 1.0F, 0.5F + player.getRandom().nextFloat() * 0.5F);
            return InteractionResult.SUCCESS;
        }
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE_BY_BITE[pState.getValue(BITES)];
    }

    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            if (eat(level, pos, state, player).consumesAction()) return InteractionResult.SUCCESS;
            if (player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) return InteractionResult.CONSUME;
        }
        return eat(level, pos, state, player);
    }

    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int bites;
        if ((bites = state.getValue(BITES)) > 0) {
            level.setBlock(pos, state.setValue(BITES, bites - 1), UPDATE_ALL);
            level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, pos, Block.getId(state));
            level.playSound(null, pos, EnigmaticSounds.COSMIC_CAKE_RESTORE.get(), SoundSource.BLOCKS, 1F, 0.5F + (float) Math.random() * 0.5F);
        }
    }

    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(BITES) > 0;
    }

    public static class Item extends BlockItem {
        public Item(Block block) {
            super(block, new Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
        }
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onCakeInteraction(PlayerInteractEvent.@NotNull RightClickBlock event) {
            Player player = event.getEntity();
            ItemStack toolStack = player.getItemInHand(event.getHand());
            Level level = event.getLevel();
            BlockPos pos = event.getPos();
            BlockState state = event.getLevel().getBlockState(pos);
            if (toolStack.isEmpty() && player.isCrouching() && event.getHand().equals(InteractionHand.MAIN_HAND)) {
                if (state.is(EnigmaticBlocks.COSMIC_CAKE) && state.getValue(CakeBlock.BITES) == 0) {
                    ItemStack stack = new ItemStack(EnigmaticBlocks.COSMIC_CAKE);
                    level.destroyBlock(pos, false, player);
                    player.setItemSlot(EquipmentSlot.MAINHAND, stack);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                }
            }
        }
    }
}
