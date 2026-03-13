package auviotre.enigmatic.legacy.contents.item.misc;

import auviotre.enigmatic.legacy.contents.entity.misc.PermanentItemEntity;
import auviotre.enigmatic.legacy.contents.item.generic.BaseCursedItem;
import auviotre.enigmatic.legacy.contents.item.rings.RedemptionRing;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.List;

public class BlessStone extends BaseCursedItem {
    public BlessStone() {
        super(defaultSingleProperties().rarity(Rarity.RARE).fireResistant(), true);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.blessStone1");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.blessStone2");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.blessStone3");
        TooltipHandler.line(list);
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.canUndone");
        if (flag.isAdvanced()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.blessLevel", ChatFormatting.DARK_GRAY, RedemptionRing.Helper.getPossibleLevel(Minecraft.getInstance().player));
        }
        TooltipHandler.line(list);
        ChatFormatting color = EnigmaticHandler.isTheCursedOne(Minecraft.getInstance().player) ? ChatFormatting.GOLD : ChatFormatting.DARK_RED;
        list.add(Component.translatable("tooltip.enigmaticlegacy.cursedOnesOnly1").withStyle(color));
        list.add(Component.translatable("tooltip.enigmaticlegacy.cursedOnesOnly2").withStyle(color));
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack item = player.getItemInHand(hand);
        if (level.getLevelData().isHardcore() && EnigmaticHandler.isTheCursedOne(player)) {
            player.level().playSound(null, player.blockPosition(), SoundEvents.WITHER_DEATH, SoundSource.PLAYERS, 1.0F, 0.5F);
            CuriosApi.getCuriosInventory(player).ifPresent((handler) -> {
                IItemHandlerModifiable curios = handler.getEquippedCurios();
                for (int i = 0; i < handler.getSlots(); ++i) {
                    ItemStack stackInSlot = curios.getStackInSlot(i);
                    if (stackInSlot.is(EnigmaticItems.CURSED_RING)) {
                        ItemStack stack = EnigmaticItems.REDEMPTION_RING.toStack();
                        stack.set(EnigmaticComponents.REDEMPTION_LEVEL, RedemptionRing.Helper.getPossibleLevel(player));
                        curios.setStackInSlot(i, stack);
                    }
                }
                for (int i = 0; i < handler.getSlots(); ++i) {
                    ItemStack stackInSlot = curios.getStackInSlot(i);
                    if (EnigmaticHandler.isCursedItem(stackInSlot)) {
                        PermanentItemEntity itemEntity = new PermanentItemEntity(player.level(), player.getRandomX(4), player.getRandomY(), player.getRandomZ(4), stackInSlot);
                        itemEntity.setGlowingTag(true);
                        player.level().addFreshEntity(itemEntity);
                        curios.setStackInSlot(i, ItemStack.EMPTY);
                    }
                }
            });
            player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 200));
            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 10));
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200));
            player.swing(hand);
            player.getInventory().removeItem(item);
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.END_ROD, player.getX(), player.getY(0.75), player.getZ(), 24, 0.0, 0.0, 0.0, 0.1D);
                serverLevel.sendParticles(ParticleTypes.WITCH, player.getX(), player.getY(0.75), player.getZ(), 24, 0.0, 0.0, 0.0, 0.05D);
            }
            return InteractionResultHolder.success(item);
        }
        return InteractionResultHolder.pass(item);
    }
}
