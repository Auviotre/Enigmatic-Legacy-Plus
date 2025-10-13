package auviotre.enigmatic.legacy.client.renderer.model;

import auviotre.enigmatic.legacy.EnigmaticLegacy;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;

public class SpellstoneModel extends Model {
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(EnigmaticLegacy.location("spellstone"), "main");
	private final ModelPart root;
	private final ModelPart main;

	public SpellstoneModel(ModelPart root) {
		super(RenderType::entitySolid);
		this.root = root;
        this.main = root.getChild("main");
	}

	public static LayerDefinition createLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		partdefinition.addOrReplaceChild("main", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(-1.0F))
				.texOffs(32, 32).addBox(4.0F, 4.0F, 4.0F, -8.0F, -8.0F, -8.0F, new CubeDeformation(0.5F))
				, PartPose.ZERO);

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
		this.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
	}

	public void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
		this.root.render(poseStack, buffer, packedLight, packedOverlay, color);
	}

	public void setupAnim(float timer, float yRot) {
	}
}