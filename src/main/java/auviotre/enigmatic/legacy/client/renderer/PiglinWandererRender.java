package auviotre.enigmatic.legacy.client.renderer;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.client.renderer.model.PiglinWandererModel;
import auviotre.enigmatic.legacy.contents.entity.PiglinWanderer;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.PiglinModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class PiglinWandererRender extends HumanoidMobRenderer<PiglinWanderer, PiglinModel<PiglinWanderer>> {
    public PiglinWandererRender(EntityRendererProvider.Context context, ModelLayerLocation layer, ModelLayerLocation location, ModelLayerLocation location1) {
        super(context, new PiglinWandererModel(context.getModelSet().bakeLayer(layer)), 0.5F, 1.0019531F, 1.0F, 1.0019531F);
        this.addLayer(new HumanoidArmorLayer<>(
                        this,
                        new HumanoidArmorModel<>(context.bakeLayer(location)),
                        new HumanoidArmorModel<>(context.bakeLayer(location1)),
                        context.getModelManager()
                )
        );
    }

    public ResourceLocation getTextureLocation(PiglinWanderer mob) {
        return EnigmaticLegacy.location("textures/entity/piglin_wanderer.png");
    }

    protected boolean isShaking(PiglinWanderer entity) {
        return super.isShaking(entity) || entity.isConverting();
    }
}
