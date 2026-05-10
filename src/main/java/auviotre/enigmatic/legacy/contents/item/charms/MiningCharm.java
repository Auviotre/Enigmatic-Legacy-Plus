package auviotre.enigmatic.legacy.contents.item.charms;

import auviotre.enigmatic.legacy.api.item.IItemHelper;
import auviotre.enigmatic.legacy.contents.item.generic.BaseCurioItem;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticSounds;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.NeoForgeMod;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class MiningCharm extends BaseCurioItem {
    public MiningCharm() {
        super(IItemHelper.singleProperties().rarity(Rarity.UNCOMMON));
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.line(list);
        if (Screen.hasShiftDown()) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.miningCharm1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.miningCharm2");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.miningCharm3");
        } else TooltipHandler.holdShift(list);
        Component mode = Component.translatable("tooltip.enigmaticlegacy.disabled");
        if (stack.getOrDefault(EnigmaticComponents.BOOLEAN, false))
            mode = Component.translatable("tooltip.enigmaticlegacy.enabled");
        TooltipHandler.line(list);
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.miningCharmNightVision", ChatFormatting.GOLD, mode);
        if (stack.isEmpty()) TooltipHandler.line(list);
    }

    @OnlyIn(Dist.CLIENT)
    public List<Component> getAttributesTooltip(List<Component> tooltips, TooltipContext context, ItemStack stack) {
        List<Component> list = super.getAttributesTooltip(tooltips, context, stack);
        list.add(Component.translatable("attribute.modifier.plus.0", 1, Component.translatable("attribute.name.fortune_level")).withStyle(ChatFormatting.BLUE));
        return list;
    }

    public void curioTick(SlotContext context, ItemStack stack) {
        if (context.entity() instanceof Player player && player.tickCount % 19 == 0) {
            if (player.getY() < 50 && !player.level().dimension().equals(Level.NETHER)
                    && !player.level().dimension().equals(Level.END)
                    && !player.isEyeInFluidType(NeoForgeMod.WATER_TYPE.value())
                    && !player.level().canSeeSkyFromBelowWater(player.blockPosition())) {
                MobEffectInstance effect = player.getEffect(MobEffects.NIGHT_VISION);
                if (effect != null) effect.duration = Math.max(339, effect.getDuration());
                else player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 339, 0, true, false));
            }
        }
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        Boolean enable = stack.getOrDefault(EnigmaticComponents.BOOLEAN, false);
        stack.set(EnigmaticComponents.BOOLEAN, !enable);
        SoundEvent soundEvent = enable ? EnigmaticSounds.CHARGED_OFF.get() : EnigmaticSounds.CHARGED_ON.get();
        level.playSound(null, player.blockPosition(), soundEvent, SoundSource.PLAYERS, (float) (0.8F + (Math.random() * 0.2F)), (float) (0.8F + (Math.random() * 0.2F)));
        player.swing(hand);
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }

    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext context, ResourceLocation id, ItemStack stack) {
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = new ImmutableMultimap.Builder<>();
        builder.put(Attributes.MINING_EFFICIENCY, new AttributeModifier(IItemHelper.getLocation(this), 3, AttributeModifier.Operation.ADD_VALUE));
        builder.put(Attributes.BLOCK_INTERACTION_RANGE, new AttributeModifier(IItemHelper.getLocation(this), 2, AttributeModifier.Operation.ADD_VALUE));
        return builder.build();
    }

    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return false;
    }

    public int getFortuneLevel(SlotContext context, LootContext lootContext, ItemStack stack) {
        return super.getFortuneLevel(context, lootContext, stack) + 1;
    }
}
