package auviotre.enigmatic.legacy.mixin.entity.dragon;

import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.boss.enderdragon.phases.AbstractDragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonChargePlayerPhase;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DragonChargePlayerPhase.class)
public abstract class MixinChargePhase extends AbstractDragonPhaseInstance {
    @Unique
    private int flameTicks = 0;
    @Unique
    private int flameAmount = 0;

    public MixinChargePhase() {
        super(null);
    }

    @Inject(method = "doServerTick", at = @At("RETURN"))
    public void mixServerTick(CallbackInfo info) {
        if (EnigmaticHandler.isCurseBoosted(this.dragon) && this.flameAmount < 9) {
            ++this.flameTicks;
            if (this.flameTicks % 5 == 0) {
                Vec3 vec3 = (new Vec3(this.dragon.head.getX() - this.dragon.getX(), 0.0F, this.dragon.head.getZ() - this.dragon.getZ())).normalize();
                double dx = this.dragon.head.getX() + vec3.x * 2.5F;
                double dz = this.dragon.head.getZ() + vec3.z * 2.5F;
                double dy = this.dragon.head.getY(0.5F);
                double ty = dy;
                BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(dx, dy, dz);
                while (this.dragon.level().isEmptyBlock(pos)) {
                    if (--ty < 0.0F) {
                        ty = dy;
                        break;
                    }
                    pos.set(dx, ty, dz);
                }
                ty = Mth.floor(ty) + 1;
                AreaEffectCloud flame = new AreaEffectCloud(this.dragon.level(), dx, ty, dz);
                flame.setOwner(this.dragon);
                flame.setRadius(4.0F);
                flame.setDuration(160);
                flame.setParticle(ParticleTypes.DRAGON_BREATH);
                flame.addEffect(new MobEffectInstance(MobEffects.HARM));
                ++this.flameAmount;
                this.dragon.level().addFreshEntity(flame);
            }
        }
    }

    @Inject(method = "getFlySpeed", at = @At("RETURN"), cancellable = true)
    public void getFlySpeed(CallbackInfoReturnable<Float> info) {
        if (EnigmaticHandler.isCurseBoosted(this.dragon))
            info.setReturnValue(info.getReturnValue() * 6.0F);
    }
}
