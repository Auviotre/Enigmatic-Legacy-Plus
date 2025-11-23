package auviotre.enigmatic.legacy.client.renderer.model;

import auviotre.enigmatic.legacy.contents.entity.PiglinWanderer;
import net.minecraft.client.model.PiglinModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.monster.piglin.PiglinArmPose;

public class PiglinWandererModel extends PiglinModel<PiglinWanderer> {
    public PiglinWandererModel(ModelPart part) {
        super(part);
    }

    public void setupAnim(PiglinWanderer entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        PiglinArmPose pose = entity.getArmPose();
        if (pose == PiglinArmPose.CROSSBOW_HOLD && entity.isCharging()) {
            if (entity.isLeftHanded()) {
                this.leftArm.xRot = this.leftArm.xRot * 0.16F - (float) Math.PI;
                this.leftArm.yRot = 0.0F;
                this.rightArm.xRot = this.rightArm.xRot * 0.5F - (float) (Math.PI / 10);
                this.rightArm.yRot = 0.0F;
            } else {
                this.rightArm.xRot = this.rightArm.xRot * 0.16F - (float) Math.PI;
                this.rightArm.yRot = 0.0F;
                this.leftArm.xRot = this.leftArm.xRot * 0.5F - (float) (Math.PI / 10);
                this.leftArm.yRot = 0.0F;
            }
            this.leftSleeve.copyFrom(this.leftArm);
            this.rightSleeve.copyFrom(this.rightArm);
        }
    }
}
