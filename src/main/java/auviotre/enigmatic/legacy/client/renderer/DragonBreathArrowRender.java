package auviotre.enigmatic.legacy.client.renderer;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.contents.entity.DragonBreathArrow;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class DragonBreathArrowRender extends ArrowRenderer<DragonBreathArrow> {
    public static final ResourceLocation LOCATION = EnigmaticLegacy.location("textures/entity/projectiles/dragon_breath_arrow.png");
    public DragonBreathArrowRender(EntityRendererProvider.Context context) {
        super(context);
    }

    public ResourceLocation getTextureLocation(DragonBreathArrow arrow) {
        return LOCATION;
    }
}
