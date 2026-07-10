package auviotre.enigmatic.legacy.client.renderer;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.entity.projectile.AngelBeam;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class AngelBeamRenderer extends EntityRenderer<AngelBeam> {
    private static final ResourceLocation TEXTURE = EnigmaticLegacy.location("textures/entity/projectiles/angel_beam.png");

    public AngelBeamRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    protected int getBlockLightLevel(AngelBeam entity, BlockPos pos) {
        return 15;
    }

    protected int getSkyLightLevel(AngelBeam entity, BlockPos pos) {
        return 15;
    }

    public boolean shouldRender(AngelBeam livingEntity, Frustum camera, double camX, double camY, double camZ) {
        return true;
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z, int alpha, float u, float v) {
        consumer.addVertex(pose, x, y, z).setColor(255, 255, 255, alpha).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880).setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    public void render(AngelBeam beam, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        Vec3 beginning = new Vec3(beam.getBeginning());
        Vec3 position = beam.getEyePosition(partialTick);
        Vec3 delta = beginning.subtract(position);
        float len = (float) delta.length();
        delta = delta.normalize();
        float phi = (float) Math.acos(delta.y);
        float theta = (float) Math.atan2(delta.z, delta.x);
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees((float) Math.toDegrees(Math.PI / 2 - theta)));
        poseStack.mulPose(Axis.XP.rotationDegrees((float) Math.toDegrees(phi)));

        float timer = (beam.tickCount + partialTick) * 8.0F;
        float f7 = timer * 0.05F * -1.5F;
        float f8 = (float) Math.pow((48.0F - beam.tickCount - partialTick) / 48.0F, 0.6);
        int alpha = Math.clamp((int) (f8 * 256), 0, 255);
        float f11 = Mth.cos(f7 + (float) (Math.PI * 3.0 / 4.0)) * 0.282F;
        float f12 = Mth.sin(f7 + (float) (Math.PI * 3.0 / 4.0)) * 0.282F;
        float f13 = Mth.cos(f7 + (float) (Math.PI / 4)) * 0.282F;
        float f14 = Mth.sin(f7 + (float) (Math.PI / 4)) * 0.282F;
        float f15 = Mth.cos(f7 + (float) (Math.PI * 5.0F / 4.0F)) * 0.282F;
        float f16 = Mth.sin(f7 + (float) (Math.PI * 5.0F / 4.0F)) * 0.282F;
        float f17 = Mth.cos(f7 + (float) (Math.PI * 7.0F / 4.0F)) * 0.282F;
        float f18 = Mth.sin(f7 + (float) (Math.PI * 7.0F / 4.0F)) * 0.282F;
        float f19 = Mth.cos(f7 + (float) Math.PI) * 0.2F;
        float f20 = Mth.sin(f7 + (float) Math.PI) * 0.2F;
        float f21 = Mth.cos(f7 + 0.0F) * 0.2F;
        float f22 = Mth.sin(f7 + 0.0F) * 0.2F;
        float f23 = Mth.cos(f7 + (float) (Math.PI / 2)) * 0.2F;
        float f24 = Mth.sin(f7 + (float) (Math.PI / 2)) * 0.2F;
        float f25 = Mth.cos(f7 + (float) (Math.PI * 3.0 / 2.0)) * 0.2F;
        float f26 = Mth.sin(f7 + (float) (Math.PI * 3.0 / 2.0)) * 0.2F;
        float v1 = 0.0F;
        float v2 = 1.0F;
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucentCull(TEXTURE));
        PoseStack.Pose pose = poseStack.last();
        vertex(consumer, pose, f19, len, f20, alpha, 0.4999F, v2);
        vertex(consumer, pose, f19, 0.0F, f20, alpha, 0.4999F, v1);
        vertex(consumer, pose, f21, 0.0F, f22, alpha, 0.0F, v1);
        vertex(consumer, pose, f21, len, f22, alpha, 0.0F, v2);
        vertex(consumer, pose, f19, len, f20, alpha, 0.4999F, v2);
        vertex(consumer, pose, f21, len, f22, alpha, 0.0F, v2);
        vertex(consumer, pose, f21, 0.0F, f22, alpha, 0.0F, v1);
        vertex(consumer, pose, f19, 0.0F, f20, alpha, 0.4999F, v1);
        vertex(consumer, pose, f23, len, f24, alpha, 0.4999F, v2);
        vertex(consumer, pose, f23, 0.0F, f24, alpha, 0.4999F, v1);
        vertex(consumer, pose, f25, 0.0F, f26, alpha, 0.0F, v1);
        vertex(consumer, pose, f25, len, f26, alpha, 0.0F, v2);
        vertex(consumer, pose, f23, len, f24, alpha, 0.4999F, v2);
        vertex(consumer, pose, f25, len, f26, alpha, 0.0F, v2);
        vertex(consumer, pose, f25, 0.0F, f26, alpha, 0.0F, v1);
        vertex(consumer, pose, f23, 0.0F, f24, alpha, 0.4999F, v1);

        float yOffset = 0.0F;
        if (beam.tickCount % 4 < 2) yOffset = 0.5F;
        vertex(consumer, pose, f11, len, f12, alpha, 0.5F, yOffset + 0.5F);
        vertex(consumer, pose, f13, len, f14, alpha, 1.0F, yOffset + 0.5F);
        vertex(consumer, pose, f17, len, f18, alpha, 1.0F, yOffset);
        vertex(consumer, pose, f15, len, f16, alpha, 0.5F, yOffset);
        poseStack.popPose();
    }

    public ResourceLocation getTextureLocation(AngelBeam angelBeam) {
        return TEXTURE;
    }
}
