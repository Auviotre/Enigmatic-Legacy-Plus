package auviotre.enigmatic.legacy.contents.item.etherium;

import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticEnchantments;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class EtheriumScythe extends HoeItem {
    public EtheriumScythe() {
        super(EtheriumProperties.TIER, new Item.Properties().fireResistant().attributes(EtheriumProperties.createAttributes(3.0F, -2.0F, 0.04F)));
    }

    public static Consumer<UseOnContext> changeIntoState(BlockState state, BlockPos pos) {
        return (context) -> {
            context.getLevel().setBlock(pos, state, Block.UPDATE_ALL_IMMEDIATE);
            context.getLevel().gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(context.getPlayer(), state));
        };
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.etheriumScythe", ChatFormatting.GOLD, 3, 3);
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.etheriumDisable");
    }

    public float getDestroySpeed(ItemStack stack, @NotNull BlockState state) {
        if (state.is(BlockTags.SWORD_EFFICIENT)) return this.getTier().getSpeed();
        return super.getDestroySpeed(stack, state);
    }

    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, @NotNull LivingEntity miningEntity) {
        if (miningEntity.isCrouching()) return super.mineBlock(stack, level, state, pos, miningEntity);
        if (miningEntity instanceof Player player && isCorrectToolForDrops(stack, state) && !level.isClientSide()) {
            BlockHitResult hitResult = Item.getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                EtheriumHammer.Helper.destroyBlocks(level, player, hitResult.getDirection(), pos.above(), (objState) -> isCorrectToolForDrops(stack, objState), 3, 3, true, pos, stack, (objPos, objState) -> {
                    Tool tool = stack.get(DataComponents.TOOL);
                    stack.hurtAndBreak(tool != null ? tool.damagePerBlock() : 1, miningEntity, EquipmentSlot.MAINHAND);
                });
            }
        }
        return super.mineBlock(stack, level, state, pos, miningEntity);
    }

    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        BlockPos pos = context.getClickedPos();
        var holder = EnigmaticHandler.get(level, Registries.ENCHANTMENT, EnigmaticEnchantments.ETHERIC_RESONANCE);
        if (stack.getEnchantmentLevel(holder) > 0) {
            BlockState state = context.getLevel().getBlockState(pos);
            if (player != null && state.getBlock() instanceof CropBlock crop && crop.getAge(state) >= crop.getMaxAge())
                player.swing(context.getHand());
            if (tryHarvest(context, level, pos)) {
                for (BlockPos offset : BlockPos.betweenClosed(pos.offset(1, 1, 1), pos.offset(-1, -1, -1))) {
                    if (!offset.equals(pos)) tryHarvest(context, level, offset);
                }
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
        }
        InteractionResult result = super.useOn(context);
        if (result.consumesAction()) {
            for (BlockPos offset : BlockPos.betweenClosed(pos.offset(1, 1, 1), pos.offset(-1, -1, -1))) {
                if (offset.equals(pos)) continue;
                if (level.getBlockState(offset).isAir()) continue;
                if (!level.getBlockState(offset.above()).isAir()) continue;
                BlockState modifiedState = level.getBlockState(offset).getToolModifiedState(context, ItemAbilities.HOE_TILL, false);
                Pair<Predicate<UseOnContext>, Consumer<UseOnContext>> pair = modifiedState == null ? null : Pair.of(ctx -> true, changeIntoState(modifiedState, offset));
                if (pair == null) continue;
                if (pair.getFirst().test(context)) {
                    Consumer<UseOnContext> consumer = pair.getSecond();
                    if (!level.isClientSide()) {
                        consumer.accept(context);
                        if (player != null)
                            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(context.getHand()));
                    }
                }
            }
        }
        return result;
    }

    private boolean tryHarvest(UseOnContext context, Level level, BlockPos pos) {
        BlockState state = context.getLevel().getBlockState(pos);
        Player player = context.getPlayer();
        if (state.getBlock() instanceof CropBlock crop && crop.getAge(state) >= crop.getMaxAge() && level instanceof ServerLevel server) {
            BlockState modifiedState = crop.getStateForAge(0);
            BlockEntity entity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
            List<ItemStack> drops = Block.getDrops(state, server, pos, entity, player, context.getItemInHand());
            List<ItemStack> needs = Block.getDrops(modifiedState, server, pos, entity, player, context.getItemInHand());
            List<ItemStack> copy = new ArrayList<>(needs);
            for (ItemStack need : needs) {
                drops.forEach(stack -> {
                    if (need.is(stack.getItem()) && copy.remove(need)) stack.shrink(need.getCount());
                });
            }
            if (copy.isEmpty()) {
                drops.forEach(stack -> Block.popResource(level, pos, stack));
                changeIntoState(modifiedState, pos).accept(context);
                server.playSound(null, pos, state.getSoundType(level, pos, player).getBreakSound(), SoundSource.BLOCKS, 0.5F, 1.0F);
                if (player != null)
                    context.getItemInHand().hurtAndBreak(1, player, LivingEntity.getSlotForHand(context.getHand()));
                return true;
            }
        }
        return false;
    }

    public boolean canPerformAction(ItemStack stack, ItemAbility ability) {
        return ItemAbilities.DEFAULT_HOE_ACTIONS.contains(ability) || ability == ItemAbilities.SWORD_SWEEP;
    }
}
