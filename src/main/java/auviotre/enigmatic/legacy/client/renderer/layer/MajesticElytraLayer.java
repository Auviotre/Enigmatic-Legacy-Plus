package auviotre.enigmatic.legacy.client.renderer.layer;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.item.tools.MajesticElytra;
import auviotre.enigmatic.legacy.handlers.EnigmaticHandler;
import auviotre.enigmatic.legacy.registries.EnigmaticItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.concurrent.atomic.AtomicBoolean;

@OnlyIn(Dist.CLIENT)
public class MajesticElytraLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
	private static final ResourceLocation TEXTURE = EnigmaticLegacy.location("textures/models/misc/majestic_elytra.png");
	private final ElytraModel<T> elytraModel;

	public MajesticElytraLayer(RenderLayerParent<T, M> layerParent, EntityModelSet modelSet) {
		super(layerParent);
		this.elytraModel = new ElytraModel<>(modelSet.bakeLayer(ModelLayers.ELYTRA));
	}

	public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T livingEntity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
		ItemStack elytra = MajesticElytra.get(livingEntity);
		if (EnigmaticHandler.hasCurio(livingEntity, EnigmaticItems.MAJESTIC_ELYTRA)) {
			AtomicBoolean notRender = new AtomicBoolean(false);
			CuriosApi.getCuriosInventory(livingEntity).flatMap(handler -> handler.findFirstCurio((stack) -> stack.is(EnigmaticItems.MAJESTIC_ELYTRA))).ifPresent(curio -> {
                if (!curio.slotContext().visible()) notRender.set(true);
            });
			if (notRender.get()) return;
		}
		if (!elytra.isEmpty()) {
			poseStack.pushPose();
			poseStack.translate(0.0D, 0.0D, 0.125D);
			this.getParentModel().copyPropertiesTo(this.elytraModel);
			this.elytraModel.setupAnim(livingEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
			VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.armorCutoutNoCull(TEXTURE), elytra.hasFoil());
			this.elytraModel.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);
			poseStack.popPose();
		}
	}
}