package auviotre.enigmatic.legacy.client.renderer;

import auviotre.enigmatic.legacy.api.item.IPermanentCrystal;
import auviotre.enigmatic.legacy.contents.entity.PermanentItemEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import static net.minecraft.client.renderer.entity.ItemEntityRenderer.getSeedForItemStack;
import static net.minecraft.client.renderer.entity.ItemEntityRenderer.renderMultipleFromCount;

@OnlyIn(Dist.CLIENT)
public class PermanentItemRenderer extends EntityRenderer<PermanentItemEntity> {
    private final ItemRenderer itemRenderer;
    private final RandomSource random = RandomSource.create();

    public PermanentItemRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
        this.shadowRadius = 0.15F;
        this.shadowStrength = 0.75F;
    }

    public ResourceLocation getTextureLocation(PermanentItemEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    public void render(PermanentItemEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !player.isAlive() && Math.sqrt(entity.distanceToSqr(player.getX(), player.getEyeY(), player.getZ())) <= 1.0)
            return;

        poseStack.pushPose();
        ItemStack itemstack = entity.getItem();

        if (itemstack.getItem() instanceof IPermanentCrystal) {
            poseStack.scale(1.25f, 1.25f, 1.25f);
            poseStack.translate(0, -0.1125d, 0);
        }

        this.random.setSeed(getSeedForItemStack(itemstack));
        BakedModel bakedmodel = this.itemRenderer.getModel(itemstack, entity.level(), null, entity.getId());
        boolean flag = bakedmodel.isGui3d();
        boolean shouldBob = IClientItemExtensions.of(itemstack).shouldBobAsEntity(itemstack);
        float f1 = Mth.sin((entity.getAge() + partialTicks) / 10.0F + entity.hoverStart) * 0.1F + 0.1F;
        float f2 = shouldBob ? bakedmodel.getTransforms().getTransform(ItemDisplayContext.GROUND).scale.y() : 0;
        poseStack.translate(0.0D, f1 + 0.25F * f2, 0.0D);

        float f3 = entity.getItemHover(partialTicks);
        poseStack.mulPose(Axis.YP.rotation(f3));

        renderMultipleFromCount(this.itemRenderer, poseStack, buffer, packedLight, itemstack, bakedmodel, flag, this.random);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}