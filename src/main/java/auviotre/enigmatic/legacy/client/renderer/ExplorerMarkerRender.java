package auviotre.enigmatic.legacy.client.renderer;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.client.renderer.model.ExplorerMarkerModel;
import auviotre.enigmatic.legacy.contents.entity.misc.ExplorerMarker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ExplorerMarkerRender extends EntityRenderer<ExplorerMarker> {
    private static final ResourceLocation TEXTURE = EnigmaticLegacy.location("textures/models/misc/explorer_marker.png");
    private final ExplorerMarkerModel model;

    public ExplorerMarkerRender(EntityRendererProvider.Context context) {
        super(context);
        this.model = new ExplorerMarkerModel(context.bakeLayer(ExplorerMarkerModel.LAYER));
    }

    public void render(ExplorerMarker entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.outline(getTextureLocation(entity)));
        this.model.render(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, -1);
        poseStack.popPose();
    }


    public ResourceLocation getTextureLocation(ExplorerMarker entity) {
        return TEXTURE;
    }
}
