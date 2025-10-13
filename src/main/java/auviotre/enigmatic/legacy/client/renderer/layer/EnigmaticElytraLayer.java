package auviotre.enigmatic.legacy.client.renderer.layer;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.item.generic.BaseElytraItem;
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
import org.spongepowered.include.com.google.common.collect.ImmutableMap;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@OnlyIn(Dist.CLIENT)
public class EnigmaticElytraLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private static final Map<BaseElytraItem, ResourceLocation> TEXTURE_MAP = ImmutableMap.<BaseElytraItem, ResourceLocation>builder()
            .put(EnigmaticItems.MAJESTIC_ELYTRA.get(), EnigmaticLegacy.location("textures/models/misc/majestic_elytra.png"))
            .put(EnigmaticItems.CHAOS_ELYTRA.get(), EnigmaticLegacy.location("textures/models/misc/chaos_elytra.png"))
            .build();
    private final ElytraModel<T> elytraModel;

    public EnigmaticElytraLayer(RenderLayerParent<T, M> layerParent, EntityModelSet modelSet) {
        super(layerParent);
        this.elytraModel = new ElytraModel<>(modelSet.bakeLayer(ModelLayers.ELYTRA));
    }

    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack elytra = BaseElytraItem.getElytra(entity);
        AtomicBoolean notRender = new AtomicBoolean(false);
        CuriosApi.getCuriosInventory(entity).flatMap(handler -> handler.findFirstCurio(stack -> stack.getItem() instanceof BaseElytraItem)).ifPresent(curio -> {
            if (!curio.slotContext().visible()) notRender.set(true);
        });
        if (notRender.get()) return;
        if (!elytra.isEmpty() && elytra.getItem() instanceof BaseElytraItem item) {
            poseStack.pushPose();
            poseStack.translate(0.0D, 0.0D, 0.125D);
            this.getParentModel().copyPropertiesTo(this.elytraModel);
            this.elytraModel.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.armorCutoutNoCull(TEXTURE_MAP.get(item)), elytra.hasFoil());
            this.elytraModel.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }
    }
}