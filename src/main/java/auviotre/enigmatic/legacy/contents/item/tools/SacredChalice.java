package auviotre.enigmatic.legacy.contents.item.tools;

import auviotre.enigmatic.legacy.api.item.IItemHelper;
import auviotre.enigmatic.legacy.contents.item.generic.BaseCursedItem;
import auviotre.enigmatic.legacy.contents.item.rings.RedemptionRing;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.packets.client.SacredChalicePacket;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticEffects;
import auviotre.enigmatic.legacy.registries.EnigmaticSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class SacredChalice extends BaseCursedItem {
    public SacredChalice() {
        super(IItemHelper.singleProperties().rarity(Rarity.RARE).fireResistant()
                .component(EnigmaticComponents.BOOLEAN, false).component(EnigmaticComponents.INT, 0), true);
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        if (!stack.getOrDefault(EnigmaticComponents.BOOLEAN, false)) {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.sacredChalice1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.sacredChalice2");
        } else {
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.sacredChalice3");
            if (RedemptionRing.Helper.canUseRelic(Minecraft.getInstance().player))
                TooltipHandler.line(list, "tooltip.enigmaticlegacy.sacredChalice4Alt");
            else TooltipHandler.line(list, "tooltip.enigmaticlegacy.sacredChalice4");
        }
        TooltipHandler.line(list);
        TooltipHandler.cursedOnly(list, stack);
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.getOrDefault(EnigmaticComponents.BOOLEAN, false) && player.getHealth() < player.getMaxHealth() * 0.5F)
            return InteractionResultHolder.fail(stack);
        ItemUtils.startUsingInstantly(level, player, hand);
        player.playSound(EnigmaticSounds.CHARGED_ON.get(), 0.5F, 0.8F + 0.4F * player.getRandom().nextFloat());
        return InteractionResultHolder.consume(stack);
    }

    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        boolean filled = stack.getOrDefault(EnigmaticComponents.BOOLEAN, false);
        stack.set(EnigmaticComponents.BOOLEAN, !filled);
        if (entity instanceof Player player)
            player.getCooldowns().addCooldown(this, player.hasInfiniteMaterials() ? 20 : (filled ? 1200 : 600));
        if (filled) {
            int amount = stack.getOrDefault(EnigmaticComponents.INT, 0);
            entity.setHealth(entity.getMaxHealth());
            boolean useRelic = RedemptionRing.Helper.canUseRelic(entity);
            entity.addEffect(new MobEffectInstance(EnigmaticEffects.BLOOD_CHALICE, 600 + 600 * amount, useRelic ? 1 : 0));
            entity.addEffect(new MobEffectInstance(EnigmaticEffects.PURE_RESISTANCE, 1200 + 300 * amount, useRelic ? 1 : 0));
        } else {
            AABB box = entity.getBoundingBox().inflate(5.0, 2.0, 5.0).move(0, entity.getBbHeight() * 0.4F, 0);
            if (level instanceof ServerLevel serverLevel)
                PacketDistributor.sendToPlayersNear(serverLevel, null, entity.getX(), entity.getY(), entity.getZ(), 24, new SacredChalicePacket(box));
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, box, LivingEntity::isAlive);
            double amount = 0;
            for (LivingEntity living : entities) {
                if (living == entity) continue;
                amount += Math.sqrt(living.getMaxHealth() / entity.getMaxHealth());
                living.hurt(entity.damageSources().mobAttack(entity), living.getMaxHealth() * 0.3F);
            }
            stack.set(EnigmaticComponents.INT, (int) Math.min(7, amount));
            entity.setHealth(Math.max(0.0F, entity.getHealth() - entity.getMaxHealth() * 0.5F));
            entity.hurtMarked = true;
            entity.playSound(SoundEvents.PLAYER_HURT);
        }
        return stack;
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return stack.getOrDefault(EnigmaticComponents.BOOLEAN, false) ? UseAnim.DRINK : UseAnim.BLOCK;
    }

    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 36;
    }
}
