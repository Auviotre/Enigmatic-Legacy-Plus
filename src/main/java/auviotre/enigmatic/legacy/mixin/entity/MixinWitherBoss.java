package auviotre.enigmatic.legacy.mixin.entity;

import auviotre.enigmatic.legacy.contents.item.tools.ExecutionAxe;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WitherBoss.class)
public abstract class MixinWitherBoss extends Monster {
    protected MixinWitherBoss(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    public abstract boolean addEffect(MobEffectInstance effectInstance, @Nullable Entity entity);

    @Inject(method = "dropCustomDeathLoot", at = @At("TAIL"))
    public void mixDrop(ServerLevel level, DamageSource source, boolean recentlyHit, CallbackInfo info) {
        if (recentlyHit && source.getEntity() instanceof LivingEntity attacker && EnigmaticHandler.isTheCursedOne(attacker)) {
            int count = attacker.getRandom().nextInt(4) + 1;
            int lootingLevel = ExecutionAxe.getLootingLevel(attacker, this.level());
            count += attacker.getRandom().nextInt(lootingLevel);
            ItemEntity itementity = this.spawnAtLocation(new ItemStack(EnigmaticItems.EVIL_ESSENCE.get(), count));
            if (itementity != null) itementity.setExtendedLifetime();
        }
    }
}
