package auviotre.enigmatic.legacy.contents.item.misc;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.api.SubscribeConfig;
import auviotre.enigmatic.legacy.contents.item.generic.BaseCursedItem;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.handlers.TooltipHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import auviotre.enigmatic.legacy.registries.EnigmaticTags;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class GuardianHeart extends BaseCursedItem {
    public static ModConfigSpec.IntValue cooldown;
    public static ModConfigSpec.DoubleValue effectiveRange;

    public GuardianHeart() {
        super(defaultSingleProperties().fireResistant().rarity(Rarity.UNCOMMON));
    }

    @SubscribeConfig
    public static void onConfig(ModConfigSpec.Builder builder, ModConfig.Type type) {
        builder.translation("item.enigmaticlegacyplus.guardian_heart").push("cursedItems.guardianHeart");
        effectiveRange = builder.defineInRange("effectiveRange", 24.0, 4.0, 64.0);
        cooldown = builder.defineInRange("cooldown", 200, 100, 600);
        builder.pop(2);
    }

    public static boolean doesObserveEntity(Player player, LivingEntity entity) {
        Vec3 vector3d = player.getViewVector(1.0F).normalize();
        Vec3 vector3d1 = new Vec3(entity.getX() - player.getX(), entity.getEyeY() - player.getEyeY(), entity.getZ() - player.getZ());
        double d0 = vector3d1.length();
        vector3d1 = vector3d1.normalize();
        double d1 = vector3d.dot(vector3d1);

        return d1 > 1.0D - 0.025D / d0 && player.hasLineOfSight(entity);
    }

    public @Nullable
    static <T extends LivingEntity> T getClosestEntity(List<? extends T> entities, Predicate<LivingEntity> predicate, double x, double y, double z) {
        double d0 = -1.0D;
        T closest = null;
        for (T entity : entities) {
            if (predicate.test(entity)) {
                double d1 = entity.distanceToSqr(x, y, z);
                if (closest == null || d1 < d0) {
                    d0 = d1;
                    closest = entity;
                }
            }
        }
        return closest;
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        TooltipHandler.cursedOnly(list, stack);
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof Player player && !level.isClientSide()) {
            List<Monster> monsters = player.level().getEntitiesOfClass(Monster.class, player.getBoundingBox().inflate(effectiveRange.get()));
            if (EnigmaticHandler.isTheCursedOne(player) && Inventory.isHotbarSlot(slotId) && !player.getCooldowns().isOnCooldown(this)) {
                Monster oneWatched = null;
                for (Monster monster : monsters) {
                    if (doesObserveEntity(player, monster) && !monster.getType().is(EnigmaticTags.EntityTypes.GUARDIAN_HEART_EXCLUDED)) {
                        oneWatched = monster;
                        break;
                    }
                }

                if (oneWatched != null && oneWatched.isAlive()) {
                    final Monster theOne = oneWatched;
                    Vec3 vec = theOne.position();
                    List<Monster> surroundingMobs = player.level().getEntitiesOfClass(Monster.class, theOne.getBoundingBox().inflate(effectiveRange.get() * 0.6), living -> living.isAlive() && theOne.hasLineOfSight(living));
                    Monster closestMonster = getClosestEntity(surroundingMobs, (monster) -> monster != theOne, vec.x, vec.y, vec.z);

                    if (closestMonster != null) {
                        this.setAttackTarget(theOne, closestMonster);
                        theOne.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 300, 0, false, true));
                        theOne.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 300, 1, false, true));
                        theOne.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 300, 1, false, true));

                        for (Monster otherMonster : surroundingMobs)
                            this.setAttackTarget(otherMonster, theOne);

                        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.HOSTILE, 1.0F, 1.0F);
                        player.getCooldowns().addCooldown(this, cooldown.get());
                    }
                }
            }

            for (Monster monster : monsters) {
                if (monster instanceof Guardian guardian && monster.getClass() != ElderGuardian.class) {
                    if (guardian.getTarget() == null) {
                        List<Monster> surroundingMobs = player.level().getEntitiesOfClass(Monster.class, guardian.getBoundingBox().inflate(10), living -> living.isAlive() && guardian.hasLineOfSight(living));
                        Monster closestMonster = getClosestEntity(surroundingMobs, checked -> !(checked instanceof Guardian), guardian.getX(), guardian.getY(), guardian.getZ());

                        if (closestMonster != null) {
                            this.setAttackTarget(guardian, closestMonster);
                        }
                    }
                }
            }
        }
    }

    private void setAttackTarget(Monster monster, Monster otherMonster) {
        if (monster != null && otherMonster != null && monster != otherMonster) {
            if (monster instanceof AbstractPiglin) {
                monster.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, otherMonster);
                monster.setTarget(otherMonster);
                monster.setLastHurtByMob(otherMonster);
                monster.hurt(otherMonster.damageSources().mobAttack(otherMonster), 0.0F);

            } else if (monster instanceof NeutralMob neutral) {
                neutral.setTarget(otherMonster);
            } else {
                monster.setTarget(otherMonster);
                monster.setLastHurtByMob(otherMonster);
            }
        }
    }

    @Mod(value = EnigmaticLegacy.MODID)
    @EventBusSubscriber(modid = EnigmaticLegacy.MODID)
    public static class Events {
        @SubscribeEvent
        private static void onFindTarget(@NotNull LivingChangeTargetEvent event) {
            LivingEntity entity = event.getEntity();
            LivingEntity target = event.getNewAboutToBeSetTarget();
            if (event.getOriginalAboutToBeSetTarget() != null) return;
            if (EnigmaticHandler.hasItem(target, EnigmaticItems.GUARDIAN_HEART) && entity instanceof Guardian) {
                if (entity.getLastAttacker() != target && entity.getClass() != ElderGuardian.class)
                    event.setCanceled(true);
            }
        }
    }
}
