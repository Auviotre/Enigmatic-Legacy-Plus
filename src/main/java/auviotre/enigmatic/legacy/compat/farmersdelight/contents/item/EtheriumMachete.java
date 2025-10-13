package auviotre.enigmatic.legacy.compat.farmersdelight.contents.item;

import auviotre.enigmatic.legacy.contents.item.etherium.EtheriumProperties;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticComponents;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import vectorwing.farmersdelight.common.item.KnifeItem;

import java.util.List;

public class EtheriumMachete extends KnifeItem {

    public EtheriumMachete() {
        super(EtheriumProperties.TIER, new Item.Properties().fireResistant().attributes(createAttributes(EtheriumProperties.TIER, 0.5F, -2.2F)).component(EnigmaticComponents.ETHERIUM_TOOL, 5));
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
        } else TooltipHandler.holdShift(list);
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (hand != InteractionHand.OFF_HAND && player.getOffhandItem().getUseAnimation() == UseAnim.NONE) {
            if (!player.getCooldowns().isOnCooldown(this)) {
                if (level instanceof ServerLevel server) {
                    boolean hit = this.shoot(server, player, stack);
                    player.getCooldowns().addCooldown(this, hit ? 5 + player.getRandom().nextInt(15) : 50);
                }
                stack.hurtAndBreak(2, player, EquipmentSlot.MAINHAND);
                player.awardStat(Stats.ITEM_USED.get(this));
                return InteractionResultHolder.success(stack);
            }
        }
        return InteractionResultHolder.pass(stack);
    }

    private boolean shoot(ServerLevel level, Player player, ItemStack item) {
        Vec3 pos = new Vec3(player.getX(), player.getY(0.64), player.getZ());
        Vec3 angle = player.getLookAngle();
        float damage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
        move:
        for (double d = 1; d < 16; d += 0.8) {
            Vec3 tmpPos = pos.add(angle.scale(d));
            level.sendParticles(ParticleTypes.END_ROD, tmpPos.x, tmpPos.y, tmpPos.z, 1, 0, 0, 0, 0.02 / d);

            EntityHitResult result = ProjectileUtil.getEntityHitResult(player, tmpPos.subtract(angle), tmpPos, AABB.ofSize(tmpPos, 0.25, 0.25, 0.25).expandTowards(angle), this::canHitEntity, 0.0F);
            if (result != null && result.getEntity() instanceof LivingEntity entity) {
                if (player.canAttack(entity)) {
                    DamageSource source = entity.damageSources().mobAttack(player);
                    damage = EnchantmentHelper.modifyDamage(level, item, entity, source, damage);
                    entity.hurt(source, damage);
                    return true;
                }
            }

            BlockPos blockPos = BlockPos.containing(tmpPos);
            BlockState blockstate = level.getBlockState(blockPos);
            if (!blockstate.isAir()) {
                VoxelShape voxelshape = blockstate.getCollisionShape(level, blockPos);
                if (!voxelshape.isEmpty()) {
                    for (AABB aabb : voxelshape.toAabbs()) {
                        if (aabb.move(blockPos).contains(tmpPos)) break move;
                    }
                }
            }
        }
        return false;
    }

    protected boolean canHitEntity(Entity target) {
        return target instanceof LivingEntity && target.canBeHitByProjectile();
    }

    public InteractionResult useOn(@NotNull UseOnContext context) {
        if (context.getPlayer() == null) return super.useOn(context);
        return context.getPlayer().isCrouching() ? this.use(context.getLevel(), context.getPlayer(), context.getHand()).getResult() : super.useOn(context);
    }
}
