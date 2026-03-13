package auviotre.enigmatic.legacy.client.renderer;

import auviotre.enigmatic.legacy.contents.entity.projectile.SoulFlameBall;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SoulFlameBallRenderer extends EntityRenderer<SoulFlameBall> {
    public SoulFlameBallRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    protected int getBlockLightLevel(SoulFlameBall entity, BlockPos pos) {
        return 15;
    }


    public void render(SoulFlameBall entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.tickCount >= 2 || !(this.entityRenderDispatcher.camera.getEntity().distanceToSqr(entity) < (double)12.25F)) {
            poseStack.pushPose();
//            poseStack.scale(this.scale, this.scale, this.scale);
//            poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
//            this.itemRenderer.renderStatic(((ItemSupplier)entity).getItem(), ItemDisplayContext.GROUND, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, entity.level(), entity.getId());
            poseStack.popPose();
        }
    }

    public ResourceLocation getTextureLocation(SoulFlameBall entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
