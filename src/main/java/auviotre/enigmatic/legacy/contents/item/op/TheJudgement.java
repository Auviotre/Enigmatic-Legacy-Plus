package auviotre.enigmatic.legacy.contents.item.op;

import auviotre.enigmatic.legacy.contents.item.generic.BaseItem;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import auviotre.enigmatic.legacy.registries.EnigmaticSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

public class TheJudgement extends BaseItem {
    public static final double ATTACK_RADIUS = 64D;

    public TheJudgement() {
        super(defaultSingleProperties().rarity(Rarity.EPIC).attributes(createAttributes(Float.POSITIVE_INFINITY - 1, 28F)));
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            int mode = this.noDrops(stack) ? 1 : 0;
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.theJudgement1");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.theJudgement2", ChatFormatting.GOLD, (int) ATTACK_RADIUS);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.theJudgement3");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.theJudgement4");
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.theJudgement5");
            TooltipHandler.line(list);
            TooltipHandler.line(list, "tooltip.enigmaticlegacy.theJudgement6", Component.translatable("tooltip.enigmaticlegacy.theJudgementMode" + mode));
        } else TooltipHandler.line(list, "tooltip.enigmaticlegacy.holdShift");
    }

    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (target.level().isClientSide()) return false;
        boolean player = attacker instanceof Player;
        AABB box = target.getBoundingBox().inflate(ATTACK_RADIUS);
        DamageSource source = player ? target.level().damageSources().playerAttack((Player) attacker) : target.level().damageSources().mobAttack(attacker);
        List<? extends LivingEntity> targets = target.level().getEntitiesOfClass(LivingEntity.class, box, entity -> entity != attacker && entity != target && entity.distanceToSqr(target) < ATTACK_RADIUS * ATTACK_RADIUS);
        targets.forEach(entity -> entity.hurt(source, Integer.MAX_VALUE));

        if (this.noDrops(stack)) {
            List<? extends Entity> drops = target.level().getEntitiesOfClass(Entity.class, box, entity -> (entity instanceof ItemEntity || entity instanceof ExperienceOrb) && entity.distanceToSqr(target) < ATTACK_RADIUS * ATTACK_RADIUS);
            drops.forEach(drop -> drop.hurt(source, Integer.MAX_VALUE));
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        boolean prevNoDrops = this.noDrops(stack);
        SoundEvent sound = prevNoDrops ? EnigmaticSounds.CHARGED_OFF.get() : EnigmaticSounds.CHARGED_ON.get();
        player.playSound(sound, 1.0F, 0.8F + player.getRandom().nextFloat() * 0.2F);
        this.setNoDrops(stack, !prevNoDrops);
        player.swing(hand);
        return super.use(level, player, hand);
    }

    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return !player.isCreative();
    }

    public boolean isFoil(ItemStack stack) {
        return this.noDrops(stack);
    }

    private boolean noDrops(ItemStack stack) {
        return stack.getOrDefault(EnigmaticComponents.NO_DROP.get(), false);
    }

    private void setNoDrops(ItemStack stack, boolean value) {
        stack.set(EnigmaticComponents.NO_DROP.get(), value);
    }
}
