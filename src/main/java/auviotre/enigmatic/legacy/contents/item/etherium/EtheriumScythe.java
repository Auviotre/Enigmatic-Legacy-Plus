package auviotre.enigmatic.legacy.contents.item.etherium;

import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class EtheriumScythe extends HoeItem {
    public EtheriumScythe() {
        super(EtheriumProperties.TIER, new Item.Properties().fireResistant().attributes(createAttributes(EtheriumProperties.TIER, 3.0F, -2.0F)
        ).component(EnigmaticComponents.ETHERIUM_TOOL, 4));
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
        InteractionResult result = super.useOn(context);
        if (result.consumesAction()) {
            BlockPos pos = context.getClickedPos();
            Level level = context.getLevel();
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    for (int k = -1; k <= 1; k++) {
                        if (i == 0 && j == 0 && k == 0) continue;
                        BlockPos offset = pos.offset(i, j, k);
                        if (level.getBlockState(offset).isAir()) continue;
                        if (!level.getBlockState(offset.above()).isAir()) continue;
                        BlockState modifiedState = level.getBlockState(offset).getToolModifiedState(context, ItemAbilities.HOE_TILL, false);
                        Pair<Predicate<UseOnContext>, Consumer<UseOnContext>> pair = modifiedState == null ? null : Pair.of((ctx) -> true, changeIntoState(modifiedState, offset));
                        if (pair == null) continue;
                        if (pair.getFirst().test(context)) {
                            Consumer<UseOnContext> consumer = pair.getSecond();
                            Player player = context.getPlayer();
                            if (!level.isClientSide()) {
                                consumer.accept(context);
                                if (player != null) {
                                    context.getItemInHand().hurtAndBreak(1, player, LivingEntity.getSlotForHand(context.getHand()));
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    public boolean canPerformAction(ItemStack stack, ItemAbility ability) {
        return ItemAbilities.DEFAULT_HOE_ACTIONS.contains(ability) || ability == ItemAbilities.SWORD_SWEEP;
    }
}
