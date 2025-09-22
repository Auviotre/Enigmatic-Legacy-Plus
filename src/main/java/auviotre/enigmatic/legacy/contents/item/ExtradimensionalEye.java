package auviotre.enigmatic.legacy.contents.item;

import auviotre.enigmatic.legacy.contents.item.generic.BaseItem;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ExtradimensionalEye extends BaseItem {
    public ExtradimensionalEye() {
        super(defaultSingleProperties());
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.extradimensionalEye1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.extradimensionalEye2");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.extradimensionalEye3");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.extradimensionalEye4");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.extradimensionalEye5");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.extradimensionalEye6");
        } else TooltipHandler.holdShift(list);
        if (stack.has(EnigmaticComponents.DIMENSIONAL_POS)) {
            GlobalPos globalPos = stack.get(EnigmaticComponents.DIMENSIONAL_POS);
            if (globalPos == null) return;
            BlockPos pos = globalPos.pos();
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.extradimensionalEyeLocation");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.extradimensionalEyePos", ChatFormatting.GOLD, pos.getX(), pos.getY(), pos.getZ());
            ResourceKey<Level> dimension = globalPos.dimension();
            MutableComponent component = Component.translatable(dimension.location().toLanguageKey("dimension"));
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.extradimensionalEyeDimension", ChatFormatting.GOLD, component);
        }
    }

    public boolean isFoil(@NotNull ItemStack stack) {
        return stack.has(EnigmaticComponents.DIMENSIONAL_POS);
    }

    public InteractionResultHolder<ItemStack> use(Level level, @NotNull Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isCrouching()) {
            GlobalPos globalPos = new GlobalPos(player.level().dimension(), player.blockPosition());
            stack.set(EnigmaticComponents.DIMENSIONAL_POS, globalPos);
            player.playSound(EnigmaticSounds.CHARGED_ON.get(), 1.0F, player.getRandom().nextFloat() * 0.2F + 0.9F);
            return InteractionResultHolder.success(stack);
        }
        return super.use(level, player, hand);
    }

    public InteractionResult interactLivingEntity(@NotNull ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (stack.has(EnigmaticComponents.DIMENSIONAL_POS)) {
            GlobalPos globalPos = stack.get(EnigmaticComponents.DIMENSIONAL_POS);
            if (globalPos != null && target.level().dimension() == globalPos.dimension()) {
                Vec3 pos = globalPos.pos().getBottomCenter();
                target.teleportTo(pos.x, pos.y, pos.z);
                stack.consume(1, player);
                player.playSound(SoundEvents.PLAYER_TELEPORT, 1.0F, player.getRandom().nextFloat() * 0.2F + 0.9F);
                return InteractionResult.sidedSuccess(player.level().isClientSide());
            }
        }
        return super.interactLivingEntity(stack, player, target, hand);
    }
}
