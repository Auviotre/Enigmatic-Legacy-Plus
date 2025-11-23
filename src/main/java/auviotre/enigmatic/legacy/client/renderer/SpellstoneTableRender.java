package auviotre.enigmatic.legacy.client.renderer;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.client.renderer.model.SpellstoneModel;
import auviotre.enigmatic.legacy.contents.block.entity.SpellstoneTableEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;

public class SpellstoneTableRender implements BlockEntityRenderer<SpellstoneTableEntity> {
    private final SpellstoneModel model;

    public SpellstoneTableRender(BlockEntityRendererProvider.Context context) {
        this.model = new SpellstoneModel(context.bakeLayer(SpellstoneModel.LAYER));
    }

    public void render(SpellstoneTableEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (!entity.render) return;
        poseStack.pushPose();
        float timer = entity.time + partialTick;
        poseStack.translate(0.5F, 1.08F + Mth.sin(timer * 0.1F) * 0.025F, 0.5F);
        poseStack.mulPose(Axis.ZP.rotation(0.25F + Mth.lerp(partialTick, entity.oRot, entity.rot)));
        poseStack.mulPose(Axis.XP.rotation(0.1F + Mth.lerp(partialTick, -entity.oRot, -entity.rot)));
        poseStack.mulPose(Axis.YP.rotation(Mth.lerp(partialTick, entity.oRot, entity.rot)));
        VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.entityTranslucentCull(EnigmaticLegacy.location("textures/models/misc/spellstone_table_cube.png")));
        this.model.render(poseStack, vertexconsumer, packedLight, packedOverlay, -1);
        poseStack.popPose();
    }
}
