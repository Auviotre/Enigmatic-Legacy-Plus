package auviotre.enigmatic.legacy.contents.item.books;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.item.IItemHelper;
import auviotre.enigmatic.legacy.contents.item.generic.BaseItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import auviotre.enigmatic.legacy.registries.EnigmaticTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VoidTome extends BaseItem {
    public VoidTome() {
        super(IItemHelper.singleProperties().rarity(Rarity.UNCOMMON));
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        if (!stack.getOrDefault(EnigmaticComponents.BOOLEAN, false)) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.voidTomeUse");
            TooltipHandler.line(list);
        }
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.inInventory", ChatFormatting.GOLD);
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.voidTome1", ChatFormatting.GOLD, "16%");
        TooltipHandler.line(list, "tooltip.enigmaticlegacy.voidTome2", ChatFormatting.GOLD, "8%");
    }

    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getOrDefault(EnigmaticComponents.BOOLEAN, false)) return InteractionResultHolder.pass(stack);
        if (world.isClientSide()) {
            float width = player.getBbWidth() * 1.25F;
            for (int i = 0; i < 24; i++)
                world.addParticle(ParticleTypes.SQUID_INK, player.getRandomX(width), player.getRandomY(), player.getRandomZ(width), 0.0, 0.05, 0.0F);
        } else if (EnigmaticHandler.unlockSpecialSlot("scroll", player, IItemHelper.getLocation(this)))
            player.level().playLocalSound(player.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0F, 1.0F, true);
        stack.set(EnigmaticComponents.BOOLEAN, true);
        player.hurt(player.damageSources().fellOutOfWorld(), player.getHealth() * 0.3F);
        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 400, 0, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 400, 0, false, true));
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onDamageIncoming(@NotNull LivingIncomingDamageEvent event) {
            if (event.getAmount() >= Float.MAX_VALUE) return;
            LivingEntity entity = event.getEntity();
            DamageSource source = event.getSource();
            if (source.is(Tags.DamageTypes.IS_TECHNICAL)) return;
            if (EnigmaticHandler.hasItem(entity, EnigmaticItems.VOID_TOME)) {
                if (entity.getRandom().nextFloat() < 0.08F) {
                    event.setCanceled(true);
                    event.setAmount(0);
                } else if (source.is(EnigmaticTags.DamageTypes.ILLUSION_LANTERN_RESISTANT_TO)) {
                    event.setAmount(event.getAmount() * 0.84F);
                }
            }
        }
    }
}
