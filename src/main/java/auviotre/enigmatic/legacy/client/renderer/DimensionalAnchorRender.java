package auviotre.enigmatic.legacy.client.renderer;

import auviotre.enigmatic.legacy.contents.block.DimensionalAnchor;
import auviotre.enigmatic.legacy.contents.block.entity.DimensionalAnchorEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class DimensionalAnchorRender implements BlockEntityRenderer<DimensionalAnchorEntity> {
    public DimensionalAnchorRender(BlockEntityRendererProvider.Context context) {
    }

    public void render(DimensionalAnchorEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (entity.getBlockState().getValue(DimensionalAnchor.CHARGE) > 0) {
            Matrix4f matrix4f = poseStack.last().pose();
            this.renderCube(entity, matrix4f, buffer.getBuffer(this.renderType()));
        }
    }

    private void renderCube(DimensionalAnchorEntity entity, Matrix4f pose, VertexConsumer consumer) {
        float f = this.getOffsetDown();
        float f1 = this.getOffsetUp();
        this.renderFace(entity, pose, consumer, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, Direction.SOUTH);
        this.renderFace(entity, pose, consumer, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, Direction.NORTH);
        this.renderFace(entity, pose, consumer, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.EAST);
        this.renderFace(entity, pose, consumer, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.WEST);
        this.renderFace(entity, pose, consumer, 0.0F, 1.0F, f, f, 0.0F, 0.0F, 1.0F, 1.0F, Direction.DOWN);
        this.renderFace(entity, pose, consumer, 0.1F, 0.9F, f1, f1, 0.9F, 0.9F, 0.1F, 0.1F, Direction.UP);
    }

    private void renderFace(DimensionalAnchorEntity entity, Matrix4f pPose, VertexConsumer consumer, float pX0, float pX1, float pY0, float pY1, float pZ0, float pZ1, float pZ2, float pZ3, Direction pDirection) {
        if (entity.shouldRenderFace(pDirection)) {
            consumer.addVertex(pPose, pX0, pY0, pZ0);
            consumer.addVertex(pPose, pX1, pY0, pZ1);
            consumer.addVertex(pPose, pX1, pY1, pZ2);
            consumer.addVertex(pPose, pX0, pY1, pZ3);
        }
    }

    protected float getOffsetUp() {
        return 1.0F - (1F / 16F);
    }

    protected float getOffsetDown() {
        return 0.375F;
    }

    protected RenderType renderType() {
        return RenderType.endPortal();
    }
}
