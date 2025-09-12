package auviotre.enigmatic.legacy.contents.item;

import auviotre.enigmatic.legacy.contents.item.generic.BaseItem;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Infinimeal extends BaseItem {
    public static final DispenseItemBehavior DISPENSE_ITEM_BEHAVIOR = new OptionalDispenseItemBehavior() {
        protected ItemStack execute(@NotNull BlockSource source, ItemStack item) {
            this.setSuccess(true);
            Level level = source.level();
            BlockPos pos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
            if (!tryApply(item, level, pos, Optional.empty(), Optional.empty())) this.setSuccess(false);
            return item;
        }
    };

    public Infinimeal() {
        super(defaultSingleProperties().rarity(Rarity.UNCOMMON));
        DispenserBlock.registerBehavior(this, DISPENSE_ITEM_BEHAVIOR);
    }

    private static boolean tryApply(@NotNull ItemStack stack, Level world, BlockPos pos, Optional<Player> optionalPlayer, Optional<Direction> clickedFace) {
        ItemStack stackCopy = new ItemStack(stack.getItem());

        if (applyVanillaBoneMeal(stackCopy, world, pos, optionalPlayer, clickedFace)) {
            if (!world.isClientSide()) world.levelEvent(1505, pos, 15);
            return true;
        }

        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (block instanceof CactusBlock || block instanceof SugarCaneBlock) {
            BlockPos topMostPos = findTopmostGrowable(world, pos, block, true);
            BlockState topMostState = world.getBlockState(topMostPos);

            if (topMostState.hasProperty(BlockStateProperties.AGE_15) && world.isEmptyBlock(topMostPos.above())) {
                int age = topMostState.getValue(BlockStateProperties.AGE_15);

                int plantHeight;
                for (plantHeight = 1; world.getBlockState(topMostPos.below(plantHeight)).is(block); ) {
                    ++plantHeight;
                }

                if (plantHeight >= 3) return false;

                if (world.isClientSide()) spawnBoneMealParticles(world, topMostPos, 15);

                age += world.random.nextInt(20);
                world.setBlock(topMostPos, topMostState.setValue(BlockStateProperties.AGE_15, Math.min(age, 15)), 4);

                if (world instanceof ServerLevel server) {
                    world.getBlockState(topMostPos).randomTick(server, topMostPos, world.random);
                }

                return true;
            }
        } else if (block instanceof VineBlock && state.isRandomlyTicking()) {
            if (world.isClientSide()) spawnBoneMealParticles(world, pos, 15);

            int cycles = 7 + world.random.nextInt(7);

            if (world instanceof ServerLevel server) {
                for (int i = 0; i <= cycles; i++) state.randomTick(server, pos, world.random);
                state.updateNeighbourShapes(server, pos, 4);
            }

            return true;
        } else if (block instanceof NetherWartBlock && state.isRandomlyTicking()) {
            if (world.isClientSide()) spawnBoneMealParticles(world, pos, 15);

            int cycles = 1 + world.random.nextInt(1);
            cycles *= 11;

            if (world instanceof ServerLevel server) {
                for (int i = 0; i <= cycles; i++) {
                    state.randomTick(server, pos, world.random);
                }
            }

            return true;
        } else if (block instanceof ChorusPlantBlock || block instanceof ChorusFlowerBlock) {
            if (world.isClientSide()) spawnBoneMealParticles(world, pos, 15);

            if (world instanceof ServerLevel server) {
                List<BlockPos> flowers = findChorusFlowers(world, pos);
                flowers.forEach(flowerPos -> {
                    int cycles = 1 + world.random.nextInt(2);
                    cycles *= 11;

                    for (int i = 0; i <= cycles; i++) {
                        BlockState flowerState = world.getBlockState(flowerPos);
                        flowerState.randomTick(server, flowerPos, world.random);
                    }
                });
            }
            return true;
        }
        return false;
    }

    public static boolean applyVanillaBoneMeal(ItemStack stack, Level level, BlockPos pos, Optional<Player> optionalPlayer, Optional<Direction> clickedFace) {
        if (!growCrop(stack, level, pos, optionalPlayer))
            return BoneMealItem.growWaterPlant(stack, level, pos, clickedFace.orElse(null));
        return true;
    }

    public static boolean growCrop(ItemStack stack, Level level, BlockPos pos, @NotNull Optional<Player> optionalPlayer) {
        if (optionalPlayer.isEmpty()) {
            if (level instanceof ServerLevel) return BoneMealItem.applyBonemeal(stack, level, pos, null);
            return false;
        }
        return BoneMealItem.applyBonemeal(stack, level, pos, optionalPlayer.get());
    }

    private static List<BlockPos> findChorusFlowers(Level level, BlockPos pos) {
        List<BlockPos> chorusTree = new ArrayList<>();
        chorusTree.add(pos);

        while (true) {
            int formerSize = chorusTree.size();
            for (BlockPos treePos : new ArrayList<>(chorusTree)) {
                chorusTree.addAll(getNeighboringBlocks(level, treePos, chorusTree, ChorusFlowerBlock.class, ChorusPlantBlock.class));
            }
            if (formerSize == chorusTree.size()) break;
        }

        return chorusTree.stream().filter(blockPos -> level.getBlockState(blockPos).getBlock() instanceof ChorusFlowerBlock).collect(Collectors.toList());
    }

    @SafeVarargs
    private static List<BlockPos> getNeighboringBlocks(Level level, BlockPos pos, List<BlockPos> exclude, Class<? extends Block>... classes) {
        BlockPos[] neighbors = new BlockPos[]{pos.above(), pos.below(), pos.east(), pos.north(), pos.south(), pos.west()};

        return Arrays.stream(neighbors).filter(neighbor -> !exclude.contains(neighbor) && Arrays.stream(classes)
                .anyMatch(theClass -> theClass.isInstance(level.getBlockState(neighbor).getBlock()))).collect(Collectors.toList());
    }

    private static BlockPos findTopmostGrowable(Level world, BlockPos pos, Block block, boolean goUp) {
        BlockPos top = pos;
        while (true) {
            world.getBlockState(top);
            if (world.getBlockState(top).getBlock() == block) {
                BlockPos nextUp = goUp ? top.above() : top.below();
                world.getBlockState(nextUp);
                if (world.getBlockState(nextUp).getBlock() != block) return top;
                else top = nextUp;
            } else return pos;
        }
    }

    public static void spawnBoneMealParticles(@NotNull Level world, BlockPos pos, int data) {
        BlockState blockstate = world.getBlockState(pos);
        if (!blockstate.isAir()) {
            double d0 = 0.5D;
            double d1;
            if (blockstate.is(Blocks.WATER)) {
                data *= 3;
                d1 = 1.0D;
                d0 = 3.0D;
            } else if (blockstate.isSolidRender(world, pos)) {
                pos = pos.above();
                data *= 3;
                d0 = 3.0D;
                d1 = 1.0D;
            } else {
                d1 = blockstate.getShape(world, pos).max(Direction.Axis.Y);
            }

            world.addParticle(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);

            RandomSource random = world.getRandom();
            for (int i = 0; i < data; ++i) {
                double d2 = random.nextGaussian() * 0.02D;
                double d3 = random.nextGaussian() * 0.02D;
                double d4 = random.nextGaussian() * 0.02D;
                double d5 = 0.5D - d0;
                double d6 = pos.getX() + d5 + random.nextDouble() * d0 * 2.0D;
                double d7 = pos.getY() + random.nextDouble() * d1;
                double d8 = pos.getZ() + d5 + random.nextDouble() * d0 * 2.0D;

                world.addParticle(ParticleTypes.HAPPY_VILLAGER, d6, d7, d8, d2, d3, d4);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.infinimeal1");
        TooltipHandler.line(list);
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.infinimeal2");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.infinimeal3");
    }

    public InteractionResult useOn(@NotNull UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (tryApply(stack, world, pos, Optional.ofNullable(context.getPlayer()), Optional.of(context.getClickedFace()))) {
            if (!world.isClientSide())
                context.getPlayer().gameEvent(GameEvent.ITEM_INTERACT_FINISH);
            return InteractionResult.sidedSuccess(world.isClientSide);
        }
        return InteractionResult.PASS;
    }
}
