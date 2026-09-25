package auviotre.enigmatic.legacy.mixin.entity.dragon;

import auviotre.enigmatic.legacy.api.entity.AbyssalHeartBearer;
import auviotre.enigmatic.legacy.contents.item.materials.AbyssalHeart;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.phases.AbstractDragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonStrafePlayerPhase;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(DragonStrafePlayerPhase.class)
public abstract class MixinStrafePhase extends AbstractDragonPhaseInstance {
    @Shadow
    @Nullable
    private LivingEntity attackTarget;

    public MixinStrafePhase() {
        super(null);
    }

    @Inject(method = "doServerTick", at = @At("RETURN"))
    public void mixServerTick(CallbackInfo info) {
        if (EnigmaticHandler.isCurseBoosted(this.dragon)) {
            if (this.attackTarget != null) {

            }
        }
    }

    @ModifyArg(method = "doServerTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    public Entity Fireball(Entity entity) {
        RandomSource random = this.dragon.getRandom();
        int modifier = AbyssalHeart.getModifier(dragon.level(), (AbyssalHeartBearer) dragon);
        if (entity instanceof DragonFireball ball && EnigmaticHandler.isCurseBoosted(this.dragon) && this.attackTarget != null) {
//            Vec3 position = ball.position().add(this.dragon.getDeltaMovement());
//            UltimateDragonFireball fireball = new UltimateDragonFireball(this.dragon.level(), this.dragon, dfb.xPower, dfb.yPower, dfb.zPower);
//            fireball.moveTo(position.x, position.y, position.z, 0.0F, 0.0F);
//            fireball.setDeltaMovement(this.dragon.getDeltaMovement());
//            Vec3 vec3 = new Vec3(ball.xPower, ball.yPower, ball.zPower);
//            for (int i = 0; i < boostLevel / 2; i++) {
//                if (random.nextInt(3) == 0) continue;
//                double x = 0.1F * (random.nextFloat() - 0.5F) * vec3.x / vec3.length();
//                double y = 0.1F * (random.nextFloat() - 0.5F) * vec3.y / vec3.length();
//                double z = 0.1F * (random.nextFloat() - 0.5F) * vec3.z / vec3.length();
//                AbstractHurtingProjectile copy;
//                if (random.nextInt(3) == 0)
//                    copy = new DragonFireball(this.dragon.level(), this.dragon, dfb.xPower + x, dfb.yPower + y, dfb.zPower + z);
//                else
//                    copy = new UltimateDragonFireball(this.dragon.level(), this.dragon, dfb.xPower + x, dfb.yPower + y, dfb.zPower + z);
//                copy.moveTo(position.x, position.y, position.z, 0.0F, 0.0F);
//                copy.setDeltaMovement(this.dragon.getDeltaMovement());
//                this.dragon.level().addFreshEntity(copy);
//            }
//            return fireball;

            Vec3 view = this.dragon.getViewVector(1.0F);
            double tx = this.dragon.head.getX() - view.x + this.dragon.getDeltaMovement().x;
            double ty = this.dragon.head.getY(0.5F) + 0.5 + this.dragon.getDeltaMovement().y;
            double tz = this.dragon.head.getZ() - view.z + this.dragon.getDeltaMovement().z;
            double dx = this.attackTarget.getX() - tx;
            double dy = this.attackTarget.getY(0.5F) - ty;
            double dz = this.attackTarget.getZ() - tz;
            Vec3 delta = new Vec3(dx, dy, dz);
            Vec3 vec3 = this.attackTarget.getDeltaMovement();
            delta.add(vec3.scale(delta.length() / 2));
            for (int i = 0; i < Math.min(4, modifier / 2); i++) {
                double x = (random.nextFloat() - 0.5F) * delta.length() / 8.0F + vec3.x;
                double y = (random.nextFloat() - 0.5F) * delta.length() / 8.0F + vec3.y;
                double z = (random.nextFloat() - 0.5F) * delta.length() / 8.0F + vec3.z;
                AbstractHurtingProjectile copy;
                copy = new DragonFireball(this.dragon.level(), this.dragon, delta.add(x, y, z).normalize());
                copy.moveTo(tx + x, ty + y, tz + z, 0.0F, 0.0F);
                copy.setDeltaMovement(delta.normalize());
                copy.accelerationPower *= 1.6F;
                this.dragon.level().addFreshEntity(copy);
            }
            DragonFireball fireball = new DragonFireball(this.dragon.level(), this.dragon, delta.normalize());
            fireball.moveTo(tx, ty, tz, 0.0F, 0.0F);
            fireball.setDeltaMovement(delta.normalize());
            fireball.accelerationPower *= 1.8F;
            return fireball;
        }
        return entity;
    }
}
