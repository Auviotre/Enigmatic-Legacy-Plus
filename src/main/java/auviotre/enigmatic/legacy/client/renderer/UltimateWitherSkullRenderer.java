package auviotre.enigmatic.legacy.client.renderer;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.entity.projectile.UltimateWitherSkull;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class UltimateWitherSkullRenderer extends EntityRenderer<UltimateWitherSkull> {
	private static final ResourceLocation TEXTURE = EnigmaticLegacy.location("textures/models/misc/ultimate_wither_armor.png");
	private static final ResourceLocation WITHER_INVULNERABLE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/wither/wither_invulnerable.png");
	private static final ResourceLocation WITHER_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/wither/wither.png");
	private final SkullModel model;

	public UltimateWitherSkullRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.model = new SkullModel(context.bakeLayer(ModelLayers.WITHER_SKULL));
	}

	protected int getBlockLightLevel(UltimateWitherSkull entity, BlockPos pos) {
		return 15;
	}

	public void render(UltimateWitherSkull entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		poseStack.pushPose();
		float inflate = entity.isDangerous() ? 1.4F : 1.0F;
		poseStack.scale(-inflate, -inflate, inflate);
		float f = Mth.rotLerp(partialTicks, entity.yRotO, entity.getYRot());
		float f1 = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
		VertexConsumer vertexconsumer = buffer.getBuffer(this.model.renderType(this.getTextureLocation(entity)));
		this.model.setupAnim(0.0F, f, f1);
		this.model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);
		poseStack.popPose();
		if (entity.isDangerous()) {
			float fullTicks = entity.tickCount + partialTicks;
			this.renderShield(poseStack, packedLight, -fullTicks, inflate + 0.1F, buffer);
			this.renderShield(poseStack, packedLight, fullTicks, inflate + 0.2F, buffer);
		}
		super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
	}

	private void renderShield(PoseStack poseStack, int packedLight, float fullTicks, float scale, MultiBufferSource buffer) {
		poseStack.pushPose();
		poseStack.scale(-scale, -scale, scale);
		VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.energySwirl(TEXTURE, Mth.cos(fullTicks * 0.02F) * 2.0F, fullTicks * 0.02F));
		this.model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);
		poseStack.popPose();
	}

	public ResourceLocation getTextureLocation(UltimateWitherSkull entity) {
		return entity.isDangerous() ? UltimateWitherSkullRenderer.WITHER_INVULNERABLE_LOCATION : UltimateWitherSkullRenderer.WITHER_LOCATION;
	}
}