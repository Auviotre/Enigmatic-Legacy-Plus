package auviotre.enigmatic.legacy.client.renderer.layer;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import auviotre.enigmatic.legacy.client.ClientConfig;
import auviotre.enigmatic.legacy.contents.item.etherium.EtheriumArmor;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EtheriumShieldLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    private static final ResourceLocation TEXTURE = EnigmaticLegacy.location("textures/models/misc/ultimate_wither_armor.png");
    private final PlayerModel<AbstractClientPlayer> model;

    public EtheriumShieldLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer, EntityModelSet modelSet) {
        super(renderer);
        this.model = new PlayerModel<>(modelSet.bakeLayer(ModelLayers.PLAYER), false);
    }

    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (ClientConfig.CONFIG.etheriumShieldRenderLayer.get() && EtheriumArmor.hasShield(player)) {
            float f = (float) player.tickCount + partialTick;

            PlayerModel<AbstractClientPlayer> playerModel = this.model();
            playerModel.prepareMobModel(player, limbSwing, limbSwingAmount, partialTick);

            if (player.isSpectator()) {
                this.model.setAllVisible(false);
                this.model.head.visible = true;
                this.model.hat.visible = true;
            } else {
                ItemStack mainHandItem = player.getMainHandItem();
                ItemStack offhandItem = player.getOffhandItem();
                this.model.setAllVisible(true);

                this.model.crouching = player.isCrouching();
                HumanoidModel.ArmPose mainArmPose = this.getArmPose(player, mainHandItem, offhandItem, InteractionHand.MAIN_HAND);
                HumanoidModel.ArmPose offArmPose = this.getArmPose(player, mainHandItem, offhandItem, InteractionHand.OFF_HAND);
                if (player.getMainArm() == HumanoidArm.RIGHT) {
                    this.model.rightArmPose = mainArmPose;
                    this.model.leftArmPose = offArmPose;
                } else {
                    this.model.rightArmPose = offArmPose;
                    this.model.leftArmPose = mainArmPose;
                }
            }

            float scale = 1.02F;
            poseStack.pushPose();
            poseStack.scale(scale, scale, scale);
            poseStack.translate(0, (1F - scale) / 2F, 0);

            this.getParentModel().copyPropertiesTo(playerModel);
            VertexConsumer consumer = buffer.getBuffer(RenderType.energySwirl(TEXTURE, xOffset(f), f * 0.01F));
            playerModel.setupAnim(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            playerModel.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, FastColor.ARGB32.color(255, 255, 255));
            poseStack.popPose();
        }
    }

    private HumanoidModel.ArmPose getArmPose(AbstractClientPlayer player, ItemStack mainStack, ItemStack offStack, InteractionHand hand) {
        HumanoidModel.ArmPose armPose = HumanoidModel.ArmPose.EMPTY;
        ItemStack itemstack = hand == InteractionHand.MAIN_HAND ? mainStack : offStack;
        if (!itemstack.isEmpty()) {
            armPose = HumanoidModel.ArmPose.ITEM;
            if (player.getUseItemRemainingTicks() > 0) {
                UseAnim useAnim = itemstack.getUseAnimation();
                if (useAnim == UseAnim.BLOCK) {
                    armPose = HumanoidModel.ArmPose.BLOCK;
                } else if (useAnim == UseAnim.BOW) {
                    armPose = HumanoidModel.ArmPose.BOW_AND_ARROW;
                } else if (useAnim == UseAnim.SPEAR) {
                    armPose = HumanoidModel.ArmPose.THROW_SPEAR;
                } else if (useAnim == UseAnim.CROSSBOW && hand == player.getUsedItemHand()) {
                    armPose = HumanoidModel.ArmPose.CROSSBOW_CHARGE;
                }
            } else {
                boolean flag3 = mainStack.getItem() == Items.CROSSBOW;
                boolean flag = CrossbowItem.isCharged(mainStack);
                boolean flag1 = offStack.getItem() == Items.CROSSBOW;
                boolean flag2 = CrossbowItem.isCharged(offStack);
                if (flag3 && flag) {
                    armPose = HumanoidModel.ArmPose.CROSSBOW_HOLD;
                }

                if (flag1 && flag2 && mainStack.getItem().getUseAnimation(mainStack) == UseAnim.NONE) {
                    armPose = HumanoidModel.ArmPose.CROSSBOW_HOLD;
                }
            }
        }

        return armPose;
    }

    protected float xOffset(float tickCount) {
        return Mth.cos(tickCount * 0.02F) * 2.0F;
    }

    protected PlayerModel<AbstractClientPlayer> model() {
        return this.model;
    }
}
