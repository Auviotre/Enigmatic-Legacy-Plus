package auviotre.enigmatic.legacy.contents.item.etherium;

import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import auviotre.enigmatic.legacy.registries.EnigmaticTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class EtheriumHammer extends DiggerItem {
    public EtheriumHammer() {
        super(EtheriumProperties.TIER, EnigmaticTags.Blocks.ALL_MINEABLE, new Item.Properties().fireResistant().attributes(createAttributes(EtheriumProperties.TIER, 9.0F, -3.0F)
        ).component(EnigmaticComponents.ETHERIUM_TOOL, 4));
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.etheriumHammer", ChatFormatting.GOLD, 3, 1);
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.etheriumDisable");
    }

    public boolean canPerformAction(ItemStack stack, ItemAbility ability) {
        return ItemAbilities.DEFAULT_PICKAXE_ACTIONS.contains(ability) || ability == ItemAbilities.AXE_DIG
                || ability == ItemAbilities.SHOVEL_DIG || ability == ItemAbilities.HOE_DIG || ability == ItemAbilities.SWORD_DIG;
    }

    public void spawnFlameParticles(ServerLevel level, BlockPos pos) {
        Vec3 center = pos.getCenter();
        ItemParticleOption particle = new ItemParticleOption(ParticleTypes.ITEM, EnigmaticItems.ETHERIUM_INGOT.toStack());
        level.sendParticles(particle, center.x, center.y, center.z, 6, 0.5, 0.5, 0.5, 0);
    }

    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
//        if (level instanceof ServerLevel server) this.spawnFlameParticles(server, pos);
        if (miningEntity.isCrouching()) return super.mineBlock(stack, level, state, pos, miningEntity);
        if (miningEntity instanceof Player player && isCorrectToolForDrops(stack, state) && !level.isClientSide()) {
            BlockHitResult hitResult = Item.getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                Helper.destroyBlocks(level, player, hitResult.getDirection(), pos, (objState) -> isCorrectToolForDrops(stack, objState), 3, 1, true, pos, stack, (objPos, objState) -> {
                    Tool tool = stack.get(DataComponents.TOOL);
                    stack.hurtAndBreak(tool != null ? tool.damagePerBlock() : 1, miningEntity, EquipmentSlot.MAINHAND);
                });
            }
        }
        return super.mineBlock(stack, level, state, pos, miningEntity);
    }

    public interface Helper {
        static void tryBreak(Level level, BlockPos pos, Player player, Predicate<BlockState> predicate, boolean checkHarvestLevel, ItemStack tool, BiConsumer<BlockPos, BlockState> toolDamageConsumer) {
            BlockState state = level.getBlockState(pos);
            Block block = state.getBlock();
            BlockEntity blockEntity = level.getBlockEntity(pos);

            boolean validHarvest = !checkHarvestLevel || state.canHarvestBlock(level, pos, player);
            boolean isEffective = predicate.test(state);
            boolean unbreakable = state.is(BlockTags.WITHER_IMMUNE) || block == Blocks.SPAWNER || state.getDestroySpeed(level, pos) < 0F;

            if (isEffective && !unbreakable) {
                boolean removed = state.onDestroyedByPlayer(level, pos, player, validHarvest, level.getFluidState(pos));
                if (removed) {
                    block.destroy(level, pos, state);
                    block.playerDestroy(level, player, pos, state, blockEntity, tool);
                    toolDamageConsumer.accept(pos, state);
                    if (level instanceof ServerLevel server) {
                        Vec3 center = pos.getCenter();
                        BlockParticleOption particle = new BlockParticleOption(ParticleTypes.BLOCK, state);
                        server.sendParticles(particle, center.x, center.y, center.z, 3, 0.5, 0.5, 0.5, 0);
                    }
                }
            }
        }

        static void destroyPlane(Level world, Player player, Direction dir, BlockPos pos, Predicate<BlockState> predicate, int radius, boolean check, @Nullable BlockPos excludedBlock, ItemStack tool, BiConsumer<BlockPos, BlockState> toolDamageConsumer) {
            int supRad = (radius - 1) / 2;
            for (int a = -supRad; a <= supRad; a++) {
                for (int b = -supRad; b <= supRad; b++) {
                    BlockPos target = null;
                    if (dir == Direction.UP || dir == Direction.DOWN) target = pos.offset(a, 0, b);
                    if (dir == Direction.NORTH || dir == Direction.SOUTH) target = pos.offset(a, b, 0);
                    if (dir == Direction.EAST || dir == Direction.WEST) target = pos.offset(0, a, b);
                    if (target != null && target.equals(excludedBlock)) continue;
                    tryBreak(world, target, player, predicate, check, tool, toolDamageConsumer);
                }
            }
        }

        static void destroyBlocks(Level world, Player player, Direction direction, BlockPos pos, Predicate<BlockState> predicate, int radius, int depth, boolean check, @Nullable BlockPos excludedBlock, ItemStack tool, BiConsumer<BlockPos, BlockState> toolDamageConsumer) {
            for (int a = 0; a < depth; a++) {
                destroyPlane(world, player, direction, pos.relative(direction, -a), predicate, radius, check, excludedBlock, tool, toolDamageConsumer);
            }
        }
    }
}
