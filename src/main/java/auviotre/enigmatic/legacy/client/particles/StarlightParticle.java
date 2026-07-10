package auviotre.enigmatic.legacy.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StarlightParticle extends SimpleAnimatedParticle {
    protected StarlightParticle(ClientLevel level, double posX, double posY, double posZ, double dx, double dy, double dz, SpriteSet spriteSet) {
        super(level, posX, posY, posZ, spriteSet, 0.0F);
        this.xd = dx;
        this.yd = dy;
        this.zd = dz;
        this.quadSize *= 0.64F;
        this.friction = 0.9F;
        this.hasPhysics = false;
        this.lifetime = 128;
        this.setSpriteFromAge(spriteSet);
        this.setFadeColor(0x22FFFFFF);
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public int getLightColor(float partialTick) {
        return 15728880;
    }

    public void move(double dx, double dy, double dz) {
        this.setBoundingBox(this.getBoundingBox().move(dx, dy, dz));
        this.setLocationFromBoundingbox();
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        public Particle createParticle(SimpleParticleType type, ClientLevel level, double posX, double posY, double posZ, double speedX, double speedY, double speedZ) {
            return new StarlightParticle(level, posX, posY, posZ, speedX, speedY, speedZ, this.sprite);
        }
    }
}
